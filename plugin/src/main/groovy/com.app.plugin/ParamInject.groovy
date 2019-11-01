package com.app.plugin

import com.app.plugin.helper.ParamHelper
import com.app.plugin.helper.Utils
import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.CtMethod
import javassist.bytecode.DuplicateMemberException
import org.gradle.api.Project

import java.lang.annotation.Annotation

public class ParamInject {
    private final static ClassPool pool = ClassPool.getDefault()

    public static void injectDir(String path, String packageName, Project project) {
//        project.logger.quiet "injectDir="
//        project.logger.quiet path

        pool.appendClassPath(path)
        //project.android.bootClasspath 加入android.jar，否则找不到android相关的所有类
        pool.appendClassPath(project.android.bootClasspath[0].toString());
        Utils.importBaseClass(pool)
        File dir = new File(path)
        if (dir.isDirectory()) {
//            project.logger.quiet "isDirectory"
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath//确保当前文件是class文件，并且不是系统自动生成的class文件
//                project.logger.quiet "filePath="+filePath
                if (filePath.endsWith(".class") && !filePath.contains('R$') && !filePath.contains('$')//代理类
                        && !filePath.contains('R.class') && !filePath.contains("BuildConfig.class")) {
//                    project.logger.quiet "injectDir11"
                    // 判断当前目录是否是在我们的应用包里面
                    int index = filePath.indexOf(packageName);
                    boolean isMyPackage = index != -1;
                    if (isMyPackage) {
                        String className = Utils.getClassName(index, filePath);
                        CtClass c = pool.getCtClass(className)
                        if (c.isFrozen()) c.defrost()
                        ParamInfo mParamInfo = new ParamInfo()
                        mParamInfo.setProject(project)
                        mParamInfo.setClazz(c)
                        if (c.getName().endsWith("Activity") || c.getSuperclass().getName().endsWith("Activity")) {
                            mParamInfo.setIsActivity(true)
                            boolean isAnnotationByBus = false;

                            for (CtField ctField :  c.getFields()) {

                                for (Annotation mAnnotation : ctField.getAnnotations()) {
                                    if (mAnnotation.annotationType().canonicalName.equals(ParamHelper.EventParamAnnotation)){
                                        isAnnotationByBus = true;
//                                        project.logger.quiet "injectDir11true"
                                    }
                                }
                            }

                            //getDeclaredMethods 获取自己申明的方法，c.getMethods()会把所有父类的方法都加上
                            for (CtMethod ctmethod : c.getDeclaredMethods()) {
                                String methodName = Utils.getSimpleName(ctmethod);
                                if (ParamHelper.ONCREATE.contains(methodName)) mParamInfo.setOnCreateMethod(ctmethod)
                            }
                            if (mParamInfo != null && isAnnotationByBus) {
                                try {
                                    ParamHelper.setParam(mParamInfo, path)
                                } catch (DuplicateMemberException e) {
                                }
                            }
                        }
                            c.detach()//用完一定记得要卸载，否则pool里的永远是旧的代码
                    }
                }
            }
        }
    }
}