package com.example.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

public class InjectParamActivityClassInfo {
    public TypeElement mClassElement; //使用注解的类元素
    private Elements mElementUtils; //元素相关的辅助类
    public List<ParamInfo> paramInfos = new LinkedList<>();//方法

    public InjectParamActivityClassInfo(TypeElement mClassElement, Elements mElementUtils) {
        this.mClassElement = mClassElement;
        this.mElementUtils = mElementUtils;
    }

    public JavaFile getJavaFile() {
        //mClassElement -> AActivity
        //标记类信息
        String packageName = getPackageName(mClassElement);
        String className = getClassName(mClassElement, packageName);
        ClassName bindingClassName = ClassName.get(packageName, className);
        //生成类名
        String creatClassName = bindingClassName.simpleName() + "$$EventBean";

        /**=============================================== 内部类 MyObserver ========================================================*/

        /**
         *  内部类 MyObserver
         */
        TypeSpec.Builder builderMyObserver = TypeSpec.classBuilder("MyObserver")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addSuperinterface(ClassName.bestGuess("android.arch.lifecycle.LifecycleObserver") )
                .addField(FieldSpec.builder(TypeName.get(mClassElement.asType()), "target", Modifier.PRIVATE).build());
//                .addField(FieldSpec.builder(TypeName.BOOLEAN, "isDataByIntent", Modifier.PRIVATE).build());

        /**
         * MyObserver 构造方法
         */
        MethodSpec.Builder constructorMyObserver = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(TypeName.get(mClassElement.asType()), "target" )
                .addStatement("this.target = target")
                .addStatement("$L.getDefault().register(this)",
                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus"));

        /**
         * MyObserver  onCreate
         */
        MethodSpec.Builder onCreateBuilder = MethodSpec.methodBuilder("onCreate")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(ClassName.bestGuess("android.arch.lifecycle.OnLifecycleEvent"))
                                .addMember("value", "$L.Event.ON_CREATE",
                                        ClassName.bestGuess("android.arch.lifecycle.Lifecycle"))
                                .build() )
                .addStatement("$L.getDefault().register(this)",
                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus"));

        /**
         * MyObserver  onStop
         */
        MethodSpec.Builder onStopBuilder = MethodSpec.methodBuilder("onStop")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(
                        ClassName.bestGuess("android.arch.lifecycle.OnLifecycleEvent"))
//                        ClassName.get("android.arch.lifecycle", "OnLifecycleEvent"))
                                .addMember("value", "$L.Event.ON_STOP",
                        ClassName.bestGuess("android.arch.lifecycle.Lifecycle"))
                                .build() )
                .addStatement(" $L.getDefault().unregister(this)",
                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus"))
                .addStatement(" target=null");

        /**
         * MyObserver  onReceive
         */
        MethodSpec.Builder onReceiveBuilder = MethodSpec.methodBuilder("onReceive")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(
                        ClassName.bestGuess("org.greenrobot.eventbus.Subscribe") )
                                .addMember("threadMode", "$L.MAIN",
                                        ClassName.bestGuess("org.greenrobot.eventbus.ThreadMode"))
                                .addMember("sticky", "true")
                                .build() )
                .addParameter(ClassName.get("", creatClassName), "eventBean" );
//                .addStatement(" $L.getDefault().unregister(this)",
//                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus"))
//                .addStatement(" target=null");

        /**=============================================== 内部类 MyObserver ========================================================*/

        /**=============================================== 内部类 Callback start ========================================================*/

        /**
         *  内部类 Callback
         */
        TypeSpec.Builder builderCallback = TypeSpec.classBuilder("Callback")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC )
                .addTypeVariable(TypeVariableName.get("T"))
                .addField( TypeVariableName.get("T") , "it", Modifier.PUBLIC);
//                .build();
//                .addField(FieldSpec.builder(TypeName.BOOLEAN, "isDataByIntent", Modifier.PRIVATE).build());

        /**
         * Callback 构造方法
         */
        MethodSpec.Builder constructorCallback = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeVariableName.get("T") , "t"  )
                .addStatement("this.it = t")  ;
        /**
         * Callback 构造方法
         */
        MethodSpec.Builder constructorCallback2 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)  ;

        /**
         * Callback  onCreate
         */
        MethodSpec.Builder onCreateCallbackBuilder = MethodSpec.methodBuilder("onCreate")
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                .addStatement("$L.getDefault().register(this)",
                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus"));

        /**
         * Callback  onStop
         */
        MethodSpec.Builder onStopCallbackBuilder = MethodSpec.methodBuilder("onStop")
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                .addStatement(" $L.getDefault().unregister(this)",
                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus")) ;

        /**
         * Callback  onResult
         */
        MethodSpec.Builder onResultBuilder = MethodSpec.methodBuilder("onResult")
                .addModifiers(Modifier.PUBLIC )
                .addJavadoc("By the subclass overwrite this method and the field \"it\" has result of <T> ; \n@see #postBack(Object)" +
                        "\n" );

        /**
         * Callback  onReceive
         */
        MethodSpec.Builder onReceiveCallbackBuilder = MethodSpec.methodBuilder("onReceive")
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(
                        ClassName.bestGuess("org.greenrobot.eventbus.Subscribe") )
                        .addMember("threadMode", "$L.MAIN",
                                ClassName.bestGuess("org.greenrobot.eventbus.ThreadMode"))
                        .addMember("sticky", "true")
                        .build() )
                .addCode("if (this.getClass().getGenericSuperclass() instanceof $L &&\n" +
                        "    (($L) (this.getClass().getGenericSuperclass())).getActualTypeArguments().length > 0) {\n" +
                        "\n" +
                        "     Class mPresenterClass = (Class) (($L) (this.getClass()\n" +
                        "      .getGenericSuperclass())).getActualTypeArguments()[0];\n" +
                        "     \n" +
                        "     if (eventBean.it.getClass().getSimpleName().equals(mPresenterClass.getSimpleName())) {\n" +
                        "        it= (T) eventBean.it;\n" +
                        "        onResult();\n" +
                        "        onStop();\n" +
                        "     }\n" +
                        "}else {\n" +
                        "    try {\n" +
                        "        throw new IllegalArgumentException(\"the <T> in code block #postForResult(this,$L.Callback<T>(){}) not found!\");\n" +
                        "    }catch (Exception e){\n" +
                        "        e.printStackTrace();\n" +
                        "    }\n" +
                        "}\n"
                        ,  ClassName.bestGuess("java.lang.reflect.ParameterizedType")
                        ,  ClassName.bestGuess("java.lang.reflect.ParameterizedType")
                        ,  ClassName.bestGuess("java.lang.reflect.ParameterizedType")
                        ,creatClassName)
//                .addParameter(ClassName.get("","Callback"),
//                        "eventBean" );
                .addParameter(ParameterizedTypeName.get(
                        ClassName.get("","Callback"),
                        TypeVariableName.get("T") ),
                        "eventBean") ;
//                .addStatement(" $L.getDefault().unregister(this)",
//                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus"))
//                .addStatement(" target=null");

        /**=============================================== 内部类 Callback end========================================================*/
        /**=============================================== 内部类 Callback2 start========================================================*/

        /**
         *  内部类 Callback2
         */
        TypeSpec.Builder builderCallback2 = TypeSpec.classBuilder("Callback2")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC )
                .addField( Object[].class, "params", Modifier.PUBLIC);
//                .build();
//                .addField(FieldSpec.builder(TypeName.BOOLEAN, "isDataByIntent", Modifier.PRIVATE).build());

        /**
         * Callback2 构造方法
         */
        MethodSpec.Builder constructorCallback21 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter( Object[].class , "objects"  )
                .addStatement("this.params = objects")  ;
        /**
         * Callback2 构造方法
         */
        MethodSpec.Builder constructorCallback22 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)  ;

        /**
         * Callback2  onCreate
         */
        MethodSpec.Builder onCreateCallback2Builder = MethodSpec.methodBuilder("onCreate")
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                .addStatement("$L.getDefault().register(this)",
                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus"));

        /**
         * Callback2 onStop
         */
        MethodSpec.Builder onStopCallback2Builder = MethodSpec.methodBuilder("onStop")
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                .addStatement(" $L.getDefault().unregister(this)",
                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus")) ;

        /**
         * Callback2  onResult
         */
        MethodSpec.Builder onResult2Builder = MethodSpec.methodBuilder("onResult")
                .addModifiers(Modifier.PUBLIC )
                .addJavadoc("By the subclass overwrite this method and the field \"params\" has result callback ;" +
                        "\n@see #postBack(Object...)" +
                        "\n" );

        /**
         * Callback2  onReceive
         */
        MethodSpec.Builder onReceive2CallbackBuilder = MethodSpec.methodBuilder("onReceive")
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(
                        ClassName.bestGuess("org.greenrobot.eventbus.Subscribe") )
                        .addMember("threadMode", "$L.MAIN",
                                ClassName.bestGuess("org.greenrobot.eventbus.ThreadMode"))
                        .addMember("sticky", "true")
                        .build() )
                .addCode("params = eventBean.params;\n" +
                                "onResult();\n" +
                                "onStop();\n"  )
                .addParameter(
                        ClassName.get("","Callback2")  ,
                        "eventBean") ;
        /**=============================================== 内部类 Callback2 end========================================================*/

        /**
         * Builder数据内部类
         */
        TypeSpec.Builder builderInnerTypeBuilder = TypeSpec.classBuilder("Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

//                .addField(FieldSpec.builder(TypeNameUtils.CONTEXT, "context", Modifier.PRIVATE).build());
//                .addField(FieldSpec.builder(TypeName.BOOLEAN, "isDataByIntent", Modifier.PRIVATE).build());
//        /**
//         * Builder构造方法
//         */
//        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
//                .addModifiers(Modifier.PRIVATE)
//                .addParameter(TypeNameUtils.CONTEXT, "from", Modifier.FINAL)
//                .addStatement("this.context = from;");
        /**
         * 暴露给外部的builder的方法用于生成对象 build()
         */
        MethodSpec.Builder builderBuider = MethodSpec.methodBuilder("builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                .addParameter(TypeNameUtils.CONTEXT, "from", Modifier.FINAL)
                .returns(ClassName.get("", "Builder"))
                .addStatement("return new Builder()");
        /**
         * Builder的create方法，用于创建生成类
         */
        MethodSpec.Builder creatBuider = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("", creatClassName))
                .addStatement("return   new $L(this)", creatClassName) ;


        /**
         * 生成类构成方法
         */
        MethodSpec.Builder creatClassConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(TypeNameUtils.BUILDER, "builder", Modifier.FINAL)
                .addStatement(" this.builder = builder");
//                .addStatement("mContent = builder.context");

        //Class<T>
        ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(
                ClassName.get(Class.class),
                ClassName.bestGuess(bindingClassName.simpleName() ));
        /**
         * 跳转方法
         */
//        MethodSpec.Builder goBuilder = MethodSpec.methodBuilder("post")
//                .addModifiers(Modifier.PUBLIC)
//                .addParameter(TypeNameUtils.CONTEXT, "from", Modifier.FINAL)
//                .addParameter(ParameterSpec.builder(inputMapTypeOfRoot, "toC").build())
//                .returns(ClassName.get("", creatClassName))
//                .addStatement("from.startActivity(new $T(from, toC))",TypeNameUtils.INTENT)
//                .addStatement("$L.getDefault().postSticky(this)",
//                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus"))
//                .addStatement(" return this");
        /**
         * post
         */
        MethodSpec.Builder goBuilder = MethodSpec.methodBuilder("post")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeNameUtils.CONTEXT, "from" )
                .addStatement("$T intent = new $T(from, $T.class)", TypeNameUtils.INTENT, TypeNameUtils.INTENT, TypeName.get(mClassElement.asType()))
                .addStatement("from.startActivity(intent)")
                .addStatement("$L.getDefault().postSticky(this)",
                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus"));
        /**
         * postForResult
         */
        MethodSpec.Builder postForResultBuilder = MethodSpec.methodBuilder("postForResult")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeNameUtils.ACTIVITY, "from" )
                .addParameter( ClassName.get("","Callback"), "callback" )
                .addStatement("$T intent = new $T(from, $T.class)", TypeNameUtils.INTENT, TypeNameUtils.INTENT, TypeName.get(mClassElement.asType()))
                .addStatement("from.startActivity(intent)")
                .addStatement("$L.getDefault().postSticky(this)",
                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus"))
                .addStatement("callback.onCreate()");
        /**
         * postForResult2
         */
        MethodSpec.Builder postForResult2Builder = MethodSpec.methodBuilder("postForResult")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeNameUtils.ACTIVITY, "from" )
                .addParameter( ClassName.get("","Callback2"), "callback" )
                .addStatement("$T intent = new $T(from, $T.class)", TypeNameUtils.INTENT, TypeNameUtils.INTENT, TypeName.get(mClassElement.asType()))
                .addStatement("from.startActivity(intent)")
                .addStatement("$L.getDefault().postSticky(this)",
                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus"))
                .addStatement("callback.onCreate()");
        /**
         * postBack
         */
        MethodSpec.Builder postBackBuilder = MethodSpec.methodBuilder("postBack")
                .addModifiers(Modifier.PUBLIC ,Modifier.STATIC)
                .addParameter( ClassName.get("","Object"), "obj" )
                .addStatement("$L.getDefault().post(new Callback<>(obj))",
                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus"));

//        ParameterSpec android = ParameterSpec.builder(TypeName.get(), "android")
//                .addModifiers(Modifier.FINAL)
//                .build();

        /**
         * postBack2
         */
        MethodSpec.Builder postBack2Builder = MethodSpec.methodBuilder("postBack")
                .addModifiers(Modifier.PUBLIC ,Modifier.STATIC)
                .addParameter(TypeVariableName.get("Object...") ,   "obj" )
                .addStatement("$L.getDefault().post(new Callback2(obj))",
                        ClassName.bestGuess("org.greenrobot.eventbus.EventBus"));

  /**
         * injectParam
         */
        MethodSpec.Builder bindBuilder = MethodSpec.methodBuilder("injectParam")
                .addModifiers(Modifier.PUBLIC ,Modifier.STATIC)
                .addParameter(TypeName.get(mClassElement.asType()), "target", Modifier.FINAL)
                .addStatement("target.getLifecycle().addObserver(new MyObserver(target))") ;


        //public  String s1;
//       typeName  ->   String
//       name   ->       s1;
        for (int i = 0; i < paramInfos.size(); i++) {
            ParamInfo paramInfo = paramInfos.get(i);

            TypeName typeName = paramInfo.getTypeName();
            String name = paramInfo.getName();
            //builder类字段
            builderInnerTypeBuilder.addField(FieldSpec.builder(typeName, name, Modifier.PRIVATE).build());
            //builder类set方法
            /*
            public Builder setS1(String s1) {
            this.s1 = s1;
            return this;
            }
        */
            MethodSpec.Builder setValueMethodBuilder = MethodSpec.methodBuilder(getSetName(name))
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(typeName, name)
                    .returns(ClassName.get("", builderInnerTypeBuilder.build().name))
                    .addStatement("this.$L = $L", name, name)
                    .addStatement("return this");
            builderInnerTypeBuilder.addMethod(setValueMethodBuilder.build());

            //通过event bus 赋值
            onReceiveBuilder.addStatement("target.$L =  eventBean.builder.$L",name, name);


        }

//        builderInnerTypeBuilder.addMethod(constructor.build());
        builderInnerTypeBuilder.addMethod(creatBuider.build());


        builderMyObserver.addMethod(constructorMyObserver.build());
        builderMyObserver.addMethod(onReceiveBuilder.build());
        builderMyObserver.addMethod(onStopBuilder.build());

        builderCallback.addMethod(constructorCallback.build());
        builderCallback.addMethod(constructorCallback2.build());
        builderCallback.addMethod(onCreateCallbackBuilder.build());
        builderCallback.addMethod(onStopCallbackBuilder.build());
        builderCallback.addMethod(onResultBuilder.build());
        builderCallback.addMethod(onReceiveCallbackBuilder.build());

        builderCallback2.addMethod(constructorCallback21.build());
        builderCallback2.addMethod(constructorCallback22.build());
        builderCallback2.addMethod(onCreateCallback2Builder.build());
        builderCallback2.addMethod(onStopCallback2Builder.build());
        builderCallback2.addMethod(onResult2Builder.build());
        builderCallback2.addMethod(onReceive2CallbackBuilder.build());

//        builderMyObserver.addMethod(onCreateBuilder.build());
        //生成类
        TypeSpec finderClass = TypeSpec.classBuilder(creatClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get("com.example.base"
                        , "BaseEvent"), TypeName.get(mClassElement.asType())))
//                .addMethod(setValueByIntentBuilder.build())
                //context对象
//                .addField(FieldSpec.builder(TypeNameUtils.CONTEXT, "mContent", Modifier.PRIVATE).build())
                //Builder对象
                .addField(FieldSpec.builder(TypeNameUtils.BUILDER, "builder", Modifier.PRIVATE).build())
                //单例对象
//                .addField(FieldSpec.builder(TypeNameUtils.THIS(creatClassName), "instance", Modifier.PRIVATE, Modifier.STATIC).build())
                //构造方法
                .addMethod(creatClassConstructor.build())
//                .addMethod(creatClassConstructor1.build())
//                .addMethod(setValueByMemoryBuilder.build())
                //跳转方法
                .addMethod(goBuilder.build())
                .addMethod(postForResultBuilder.build())
                .addMethod(postForResult2Builder.build())
                //builder方法生成对象
                .addMethod(builderBuider.build())
                //使用intent携带数据
//                .addMethod(setIntentBuilder.build())
                // Builder内部类
                .addType(builderInnerTypeBuilder.build())
                .addType(builderMyObserver.build())
                .addType(builderCallback.build())
                .addType(builderCallback2.build())
                //目标类赋值方法
                .addMethod(bindBuilder.build())
                .addMethod(postBackBuilder.build())
                .addMethod(postBack2Builder.build())
                .addJavadoc("APT generate code\nfor startActivity and EventBus deliver values and receive result" +
                        "\n")
//                        ,ClassName.get("com.example.api","EventParam"))
                .build();
        return JavaFile.builder(packageName, finderClass).build();

    }

    /**
     * 一个使用注解的字段所有信息
     */
    static class ParamInfo {
        public String name;
        public TypeName typeName;

        public ParamInfo(VariableElement variableElement) {
            name = variableElement.getSimpleName().toString();
            typeName = TypeName.get(variableElement.asType());
        }

        public TypeName getTypeName() {
            return typeName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    private String getPackageName(TypeElement type) {
        return mElementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    private String getSetName(String fileName) {
        return "set" + getOneUppercaseStr(fileName);
    }

    private String getOneUppercaseStr(String str) {
        if (!Character.isUpperCase(str.charAt(0))) {
            str = (new StringBuilder()).append(Character.toUpperCase(str.charAt(0))).append(str.substring(1)).toString();
        }
        return str;
    }

    private Class getTypeClass(String strName) {
        Class clz = null;
        try {
            clz = Class.forName(strName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clz;
    }

}
