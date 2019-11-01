package com.app.plugin.helper

import javassist.ClassPool
import javassist.CtMethod


class Utils {
    static void importBaseClass(ClassPool pool) {
        pool.importPackage(ParamHelper.OkBusAnnotation)
        pool.importPackage(ParamHelper.OkBusRegisterAnnotation)
        pool.importPackage(ParamHelper.EventParamAnnotation)
        pool.importPackage(ParamHelper.OkBusUnRegisterAnnotation)
        pool.importPackage("android.os.Bundle")
        pool.importPackage("com.base.event.OkBus")
        pool.importPackage("com.base.event.Event")
        pool.importPackage("android.os.Message")
    }
    static String getSimpleName(CtMethod ctmethod) {
        def methodName = ctmethod.getName()
        return methodName.substring(
                methodName.lastIndexOf('.') + 1, methodName.length())
    }
    static String getClassName(int index, String filePath) {
        int end = filePath.length() - 6 // .class = 6
        return filePath.substring(index, end).replace('\\', '.').replace('/', '.')
    }
}
