package com.app.plugin.helper

import com.app.plugin.ParamInfo
import javassist.CtMethod
import javassist.CtNewMethod
 

 class ParamHelper {
    final static String EventParamAnnotation = "com.example.api.EventParam"
    static def ONCREATE  = 'onCreate'

    static def Activity_OnCreate = "\n" +
            "    protected void onCreate(Bundle savedInstanceState) {\n" +
            "        super.onCreate(savedInstanceState);\n";
 
    static void setParam(ParamInfo mParamInfo, String path) {
        if (mParamInfo.clazz.isFrozen()) mParamInfo.clazz.defrost()//解冻
        if (mParamInfo.getOnCreateMethod() == null) {//没有OnCreateMethod，创建并加上新代码
            mParamInfo.project.logger.quiet "getOnCreateMethod  null "  + mParamInfo.isActivity
            String m = Activity_OnCreate +  mParamInfo.clazz.name+"\$\$EventBean.injectParam(this);"+ "}"
            mParamInfo.project.logger.quiet m
            CtMethod mInitEventMethod = CtNewMethod.make(m, mParamInfo.clazz);
            mParamInfo.clazz.addMethod(mInitEventMethod)
        } else {//有OnCreateMethod，直接插入新代码
            mParamInfo.project.logger.quiet "OnCreateMethod not null"
            mParamInfo.OnCreateMethod.insertAt(2,
                    mParamInfo.clazz.name+"\$\$EventBean.injectParam(this);")
        }
        mParamInfo.clazz.writeFile(path)
    }
        
}
