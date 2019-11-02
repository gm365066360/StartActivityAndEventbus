# 面向AOP，代替Intent、简化传值方案

### 痛点

* 定义key,想名字是个麻烦事儿!
* 目标activity需要取值、赋值俩个操作!
* 需要返回结果时需重写onActivityResult方法!
* 定义requestCode,定义resultCode,"去"和"回"代码分隔两处,逻辑不连贯!
* 自定义Bean传值需要序列化!

### 思路
* APT根据注解生成参数容器Bean、Builder模式链式set方法赋值
* Bean中对EventBus广播、订阅
* Javassist注入,订阅时参数被同步到目标activity
* Callback同理

### 简单、优雅
**Step 1. 给目标Activity需要传递的属性加注解 @EventParam**
```
class TargetActivity extends AppCompatActivity {
    @EventParam
    public  Data data;
```
**Step 2. `Build Project` 生成 $$EventBean**

<img src=http://chuantu.xyz/t6/702/1572617764x2073530527.png  width="40%" />

APT generated $$EventBean.java

<img src=http://chuantu.xyz/t6/702/1572620778x2073530529.png  width="40%" />

**Step 3. 跳转传值代码**
```
TargetActivity$$EventBean
                .builder()
                .setData(new Data("sss"))//根据所加注解自动生成对应属性的set方法
                .create()
                .post(this);
```
**others**

* `postBack` 代替 `setResult`

```
TargetActivity$$EventBean.postBack( new Data("data") );
```

* `postForResult` 代替 `startActivityForResult`

```
TargetActivity$$EventBean
                .builder()
                .setData(new Data("sss"))//根据所加注解自动生成对应属性的set方法
                .create()
                .postForResult(this, new TargetActivity$$EventBean.Callback<Data>() {//泛型<Data>为$$EventBean.postBack所传类型
                    @Override
                    public void onResult() {
                        //it即是传回的结果,泛型<Data>
                        Log.e(" onResult= ", it.getString1());
                    }
                });
```

* 返回可变参数

```
 TargetActivity$$EventBean.postBack("data1","data2",33,true ,new Data("cData"));
```

```
 TargetActivity$$EventBean
                .builder()
                .setName( "sss" )
                .create()
                .postForResult(this, new TargetActivity$$EventBean.Callback2(){//注意:这里换成Callback2来接收可变参数数组
                    @Override
                    public void onResult() {
                        //打印出返回的数组
                        Log.e("gaom onResult= ", Arrays.toString(params));
                    }
                });
```

### 添加依赖：
**项目-build.gradle**
```
buildscript {
    repositories {
       ...
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        ...
        classpath 'com.github.gm365066360.StartActivityAndEventbus:plugin:1.4'
        classpath "io.realm:realm-gradle-plugin:3.4.0"
    }
}
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
       ...
    }
}
```
**app-build.gradle**
```
import com.app.plugin.JavassistPlugin

apply plugin: 'com.android.application'
apply plugin: 'realm-android'
apply plugin: JavassistPlugin

...

dependencies {
    ...
    implementation 'org.greenrobot:eventbus:3.1.1'
    implementation 'com.github.gm365066360.StartActivityAndEventbus:api:1.4'
    annotationProcessor 'com.github.gm365066360.StartActivityAndEventbus:compiler:1.4'
}
```
### 对照
|$$EventBean|Activity
|---|---
|post(this)|startActivity(intent)
|postForResult(this,callback)|startActivityForResult(intent,requestCode)
|.builder().setParam()|intent.putExtra(key,value)
|@EventParam|getIntent().getStringExtra(key)
|TargetActivity$$EventBean.postBack(object)|setResult(100,intent)
