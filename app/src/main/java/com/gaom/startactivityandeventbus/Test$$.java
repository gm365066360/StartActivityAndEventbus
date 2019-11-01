package com.gaom.startactivityandeventbus;

import android.app.Activity;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;

import com.example.base.BaseEvent;

import org.greenrobot.eventbus.Subscribe;


public class Test$$ implements BaseEvent<DetailActivity> {
    private Test$$.Builder builder;

    private Test$$(final Test$$.Builder builder) {
        this.builder = builder;
    }

    public void post(Context from) {
        Intent intent = new Intent(from, DetailActivity.class);
        from.startActivity(intent);
        org.greenrobot.eventbus.EventBus.getDefault().postSticky(this);
    }

    public void postForResult(Activity from, Test$$.Callback callback) {
        Intent intent = new Intent(from, DetailActivity.class);
        from.startActivity(intent);
        org.greenrobot.eventbus.EventBus.getDefault().postSticky(this);
        callback.onCreate();
    }

    public static Test$$.Builder builder() {
        return new Test$$.Builder();
    }

    public static void injectParam(final DetailActivity target) {
        target.getLifecycle().addObserver(new Test$$.MyObserver(target));
    }

    public static void postBack(Object obj) {
        org.greenrobot.eventbus.EventBus.getDefault().post(new Test$$.Callback<>(obj));
    }

    public static class Builder {
        private String name;

        public Test$$.Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Test$$ create() {
            return   new Test$$(this);
        }
    }

    public static class MyObserver implements LifecycleObserver {
        private DetailActivity target;

        private MyObserver(DetailActivity target) {
            this.target = target;
            org.greenrobot.eventbus.EventBus.getDefault().register(this);
        }

        @Subscribe(
                threadMode = org.greenrobot.eventbus.ThreadMode.MAIN,
                sticky = true
        )
        public void onReceive(Test$$ eventBean) {
            target.name =  eventBean.builder.name;
        }

        @OnLifecycleEvent(android.arch.lifecycle.Lifecycle.Event.ON_STOP)
        public void onStop() {
            org.greenrobot.eventbus.EventBus.getDefault().unregister(this);
            target=null;
        }
    }

    public static class Callback<T> {
        public T it;

        public Callback(T t) {
            this.it = t;
        }

        public Callback() {
        }

        public final void onCreate() {
            org.greenrobot.eventbus.EventBus.getDefault().register(this);
        }

        public final void onStop() {
            org.greenrobot.eventbus.EventBus.getDefault().unregister(this);
        }

        /**
         * By the subclass overwrite this method and the field "it" has result of <T> ; 
         * @see #postBack(Object)
         */
        public void onResult() {
        }

        @Subscribe(
                threadMode = org.greenrobot.eventbus.ThreadMode.MAIN,
                sticky = true
        )
        public final void onReceive(Test$$.Callback<T> eventBean) {
            if (this.getClass().getGenericSuperclass() instanceof java.lang.reflect.ParameterizedType &&
                    ((java.lang.reflect.ParameterizedType) (this.getClass().getGenericSuperclass())).getActualTypeArguments().length > 0) {

                Class mPresenterClass = (Class) ((java.lang.reflect.ParameterizedType) (this.getClass()
                        .getGenericSuperclass())).getActualTypeArguments()[0];

                if (eventBean.it.getClass().getSimpleName().equals(mPresenterClass.getSimpleName())) {
                    it= (T) eventBean.it;
                    onResult();
                    onStop();
                }
            }else {
                try {
                    throw new Exception("the <T> of CodeBlock #postForResult(this,Test$$.Callback<T>(){}) not found!");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
