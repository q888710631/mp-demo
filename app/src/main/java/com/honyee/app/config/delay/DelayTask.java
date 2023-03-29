package com.honyee.app.config.delay;

import java.io.Serializable;

public class DelayTask implements Serializable {
    private long id;
    private String title;
    private Runnable task;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Runnable getTask() {
        return task;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }
}
