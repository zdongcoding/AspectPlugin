package com.zdg.aspect.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
/**
 * Created by zoudong on 2017/8/8.
 */
class CleanTask  extends  DefaultTask{

    @TaskAction // 加上这个action的作用是当执行这个task的时候会自动执行这个方法
    void cleanAspectJ(){
        println("开始清除AspectJ")
//        delete rootProject
    }

}
