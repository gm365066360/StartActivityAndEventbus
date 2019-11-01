package com.app.plugin

import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project

class ParamInfo {
    Project project//保留当前工程的引用
    CtClass clazz//当前处理的class
    boolean isActivity = false//是否是在Activity
    CtMethod OnCreateMethod//Activity或Fragment的初始化方法
}
