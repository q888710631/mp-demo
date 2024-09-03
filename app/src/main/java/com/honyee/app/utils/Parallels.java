package com.honyee.app.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * JDK11以上版本存在Springboot和ForkJoinPool整合问题。
 * 主要表现，开启并行流处理时，假设当前有io操作一出现，会出现ClassNotFoundException问题
 */
public final class Parallels {

    public static final ForkJoinPool SPRING_FORK_POOL = new ForkJoinPool(8, new SpringBootForkJoinWorkerThreadFactory(), null, false);

    public static <V> Callable<V> buildCallable(Callable<V> callable) {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return () -> {
            ClassLoader targetContextClassloader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
                return callable.call();
            } finally {
                Thread.currentThread().setContextClassLoader(targetContextClassloader);
            }
        };
    }


    public static Runnable buildRunnable(Runnable runnable) {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return () -> {
            ClassLoader targetContextClassloader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
                runnable.run();
            } finally {
                Thread.currentThread().setContextClassLoader(targetContextClassloader);
            }
        };
    }


    public static <T> Consumer<T> buildConsumer(Consumer<T> consumer) {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return t -> {
            ClassLoader targetContextClassloader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
                consumer.accept(t);
            } finally {
                Thread.currentThread().setContextClassLoader(targetContextClassloader);
            }
        };
    }


    public static <T, R> Function<T, R> buildFunction(Function<T, R> function) {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return t -> {
            ClassLoader targetContextClassloader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
                return function.apply(t);
            } finally {
                Thread.currentThread().setContextClassLoader(targetContextClassloader);
            }
        };
    }


    public static final class SpringBootForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            return new SpringForkJoinWorkerThread(pool, SpringBootForkJoinWorkerThreadFactory.class.getClassLoader());
        }

        public static class SpringForkJoinWorkerThread extends ForkJoinWorkerThread {

            public SpringForkJoinWorkerThread(ForkJoinPool pool, ClassLoader classLoader) {
                super(pool);
                super.setContextClassLoader(classLoader);
            }
        }

    }
}
