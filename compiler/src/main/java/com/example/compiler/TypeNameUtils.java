package com.example.compiler;

import com.squareup.javapoet.ClassName;



public class TypeNameUtils {
    public static final ClassName CONTEXT = ClassName.get("android.content","Context");
    public static final ClassName ACTIVITY = ClassName.get("android.app","Activity");
    public static final ClassName BUILDER = ClassName.get("","Builder");
    public static final ClassName INTENT = ClassName.get("android.content","Intent");
}
