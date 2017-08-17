package com.zdg.aspect.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class AspectJPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        final def log = project.logger
//        log.debugEnabled=true
//        log.infoEnabled=true
        def hasApp = project.plugins.withType(AppPlugin)  //判断是否是主module
        def hasLib = project.plugins.withType(LibraryPlugin)//判断是否是library

        if (!hasApp && !hasLib) {
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        }
        log.warn('-------------------------------------')
        log.warn(project.name)
        log.warn('-------------------------------------')

        final def variants
        if (hasApp) {
            variants = project.android.applicationVariants
        } else {
            variants = project.android.libraryVariants
        }

        project.dependencies {
            compile 'org.aspectj:aspectjrt:1.8.10'
        }

//        project.extensions.create('aspect')


        variants.all { variant ->
            JavaCompile javaCompile = variant.javaCompile
            javaCompile.doLast {
                String[] args = ["-showWeaveInfo",
                                 "-1.8",
//                                 "-verbose",
//                                 "-log",'./aspectj.log',
                                 "-inpath", javaCompile.destinationDir.toString(),
                                 "-aspectpath", javaCompile.classpath.asPath,
                                 "-d", javaCompile.destinationDir.toString(),
                                 "-classpath", javaCompile.classpath.asPath,
                                 "-bootclasspath", project.android.bootClasspath.join(
                        File.pathSeparator)]
                MessageHandler handler = new MessageHandler(true)
                handler.ignore(IMessage.INFO)
                new Main().run(args, handler)
                log.warn('-------------------------------------')
                for (IMessage message : handler.getMessages(null, true)) {
                    if(null!=message){
                        log.warn(Main.MessagePrinter.render(message))
                    }

                    switch (message.getKind()) {
                        case IMessage.ABORT:
                        case IMessage.ERROR:
                        case IMessage.FAIL:
                            log.error message.message, message.thrown
                            break
                        case IMessage.WARNING:
                        case IMessage.INFO:
                            log.info message.message, message.thrown
                            break
                        case IMessage.DEBUG:
                            log.debug message.message, message.thrown
                            break
                    }
                }
                log.warn('-------------------------------------')
            }
        }
    }
}