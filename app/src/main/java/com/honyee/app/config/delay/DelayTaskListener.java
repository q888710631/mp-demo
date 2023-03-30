package com.honyee.app.config.delay;

public interface DelayTaskListener<T> {

    void run(T t);

}