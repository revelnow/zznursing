package com.zzyl.nursing.task;

import org.springframework.stereotype.Component;

@Component("helloTask")
public class HelloTask {
    public void myTask() {
        System.out.println("定时任务执行了...");
    }
}