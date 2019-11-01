# 代替Intent、简化传值方案

### 思路
* APT生成参数容器Bean、Builder模式赋值
* Bean中对EventBus传递、接收
* Javassist注入目标activity到Bean


### 简单、优雅：
**Step 1. 给目标Activity需要传递的属性加注解**
```
class TargetActivity extends AppCompatActivity {
    @EventParam
    public  Data data;
```
**Step 2. `Build Project` 生成 EventBean**
<img src=http://chuantu.xyz/t6/702/1572617764x2073530527.png  width="30%" />

APT generated EventBean.java

<img src=http://chuantu.xyz/t6/702/1572620778x2073530529.png  width="30%" />

**Step 3. 跳转传值代码**
```
TargetActivity$$EventBean
                .builder()
                .setData(new Data("sss"))
                .create()
                .post(this);
```
**others**
* `postForResult` 代替 `startActivityForResult`

```
TargetActivity$$EventBean
                .builder()
                .setData(new Data("sss"))
                .create()
                .postForResult(this, new TargetActivity$$EventBean.Callback<Data>() {
                    @Override
                    public void onResult() {
                        Log.e(" onResult= ", it.getString1());
                    }
                });
```
* `postBack` 代替 `setResult`

```
BActivity$$EventBean.postBack( new Data("data") );
```

### 依赖：
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
