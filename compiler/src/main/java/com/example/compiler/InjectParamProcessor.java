package com.example.compiler;
 
import com.example.api.EventParam;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

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

