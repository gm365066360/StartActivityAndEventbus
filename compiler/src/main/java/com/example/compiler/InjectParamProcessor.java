package com.example.compiler;

import com.example.api.EventParam;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.TypeName;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class InjectParamProcessor extends AbstractProcessor {
    //一个有对应注解的类对应一个 生成文件
    Map<TypeElement, InjectParamActivityClassInfo> targetClassMap = new LinkedHashMap<>();
    /**
     * 元素工具类
     * 包：PackageElement
     * 类：TypeElement
     * 参数：VariableElement
     * 方法：ExecuteableElement
     */
    private Types mTypeUtils;
    private Elements mElementUtils;
    private Filer mFiler;
    private Messager mMessager;
    /**
     * 初始化一些工具类
     * @param processingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //初始化我们需要的基础工具
        mTypeUtils = processingEnv.getTypeUtils();
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
    }

    /**
     * 指定该注解处理器需要处理的注解类型
     * @return 需要处理的注解类型名的集合Set<String>
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(EventParam.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
//        mMessager.printMessage(Diagnostic.Kind.NOTE , "process...");

        /*
        注: process...
注: size=2
注: params.size=3
注: Name=i1,typeName=int
注: Name=b2,typeName=boolean
注: Name=f3,typeName=float
注: params.size=3
注: Name=str1,typeName=java.lang.String
注: Name=str2,typeName=java.lang.String
注: Name=i3,typeName=int*/
//        TypeMirror stringType =
//                mElementUtils.getTypeElement(String.class.getCanonicalName()).asType();
//        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(BackParam.class);
//        mMessager.printMessage(Diagnostic.Kind.NOTE , "size="+elementsAnnotatedWith.size());
//        for (Element annotatedElement :elementsAnnotatedWith) {
//
//            ExecutableElement exeElement = (ExecutableElement) annotatedElement;
//
//            BackParam bindAnnotation = exeElement.getAnnotation(BackParam.class);
//            String value = bindAnnotation.value();
//            if (value.isEmpty()){
//                mMessager.printMessage(Diagnostic.Kind.NOTE , "BackParam.value.isEmpty()");
//            }else {
//                mMessager.printMessage(Diagnostic.Kind.NOTE , "BackParam.value="+value);
//            }
//
//            List<? extends VariableElement> params = exeElement.getParameters();
//            mMessager.printMessage(Diagnostic.Kind.NOTE , "params.size="+params.size());
//            for (VariableElement variableElement :params) {
////                TypeMirror secondArgumentType = variableElement.asType();
//                TypeName   typeName = TypeName.get(variableElement.asType());
//                String  name = variableElement.getSimpleName().toString();
//                mMessager.printMessage(Diagnostic.Kind.NOTE , "Name="+name+",typeName="+ typeName.toString());
////                if (secondArgumentType.equals(stringType)  ) {
////
////                }
//            }
//
//        }



        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(EventParam.class)) {
            parseInjectActivity(annotatedElement);
        }
        //遍历所有的类信息
        for (InjectParamActivityClassInfo classInfo:targetClassMap.values()){
            try {
                classInfo.getJavaFile().writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /** 
     * @param element
     */
    private void parseInjectActivity(Element element) {


        //判断注解参数的合法性
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.PRIVATE) || modifiers.contains(Modifier.FINAL)) {
            return;
        }
        //获取class信息
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        InjectParamActivityClassInfo classInfo = findClassInfo(enclosingElement);
        classInfo.paramInfos.add(new InjectParamActivityClassInfo.ParamInfo((VariableElement) element));
    }

    /**
     * 从缓存中寻找activity生成类信息，没有就新建
     * @param element
     * @return
     */
    private InjectParamActivityClassInfo findClassInfo(TypeElement element) {
      InjectParamActivityClassInfo classInfo =   targetClassMap.get(element);
      if (classInfo == null){
          classInfo = new InjectParamActivityClassInfo(element,mElementUtils);
          targetClassMap.put(element, classInfo);
      }
      return classInfo;
    }
}

