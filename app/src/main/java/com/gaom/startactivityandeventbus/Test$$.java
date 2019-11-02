package com.gaom.startactivityandeventbus;

import android.app.Activity;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;

import com.example.base.BaseEvent;

import org.greenrobot.eventbus.Subscribe;



public class Test$$ implements BaseEvent<CActivity> {
    private Test$$.Builder builder;

    private Test$$(final Test$$.Builder builder) {
        this.builder = builder;
    }

    public void post(Context from) {
        Intent intent = new Intent(from, CActivity.class);
        from.startActivity(intent);
        org.greenrobot.eventbus.EventBus.getDefault().postSticky(this);
    }

    public static void postBack(Object... obj) {
        org.greenrobot.eventbus.EventBus.getDefault().post(new Test$$.Callback2(obj));
    }

    public void postForResult(Activity from, Test$$.Callback2 callback) {
        Intent intent = new Intent(from, CActivity.class);
        from.startActivity(intent);
        org.greenrobot.eventbus.EventBus.getDefault().postSticky(this);
        callback.onCreate();
    }

    public static Test$$.Builder builder() {
        return new Test$$.Builder();
    }

    public static void injectParam(final CActivity target) {
        target.getLifecycle().addObserver(new Test$$.MyObserver(target));
    }

    public static class Builder {
        private String name;

        public Test$$.Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Test$$ create() {
            return new Test$$(this);
        }
    }

    public static class MyObserver implements LifecycleObserver {
        private CActivity target;

        private MyObserver(CActivity target) {
            this.target = target;
            org.greenrobot.eventbus.EventBus.getDefault().register(this);
        }

        @Subscribe(
                threadMode = org.greenrobot.eventbus.ThreadMode.MAIN,
                sticky = true
        )
        public void onReceive(Test$$ eventBean) {
            target.name = eventBean.builder.name;
        }

        @OnLifecycleEvent(android.arch.lifecycle.Lifecycle.Event.ON_STOP)
        public void onStop() {
            org.greenrobot.eventbus.EventBus.getDefault().unregister(this);
            target = null;
        }
    }

    public static class Callback2 {
        public Object [] params;

        public Callback2(Object... objects) {
          this.  params =  objects ;
        }
        public Callback2() {
        }

        public final void onCreate() {
            org.greenrobot.eventbus.EventBus.getDefault().register(this);
        }

        public final void onStop() {
            org.greenrobot.eventbus.EventBus.getDefault().unregister(this);
        }

        /**
         * By the subclass overwrite this method and the field "params" has result callback ;
         *
         * @see #postBack(Object...)
         */
        public void onResult() {
        }

        @Subscribe(
                threadMode = org.greenrobot.eventbus.ThreadMode.MAIN,
                sticky = true
        )
        public final void onReceive(Test$$.Callback2 eventBean) {
                params = eventBean.params;
                onResult();
                onStop();
        }
    }
}
