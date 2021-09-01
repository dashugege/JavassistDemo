package com.xiaomu.test

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class InitPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        println '***************** Plugin apply*********************'
        AppExtension appExtension = project.getExtensions().findByType(AppExtension.class);
        appExtension.registerTransform(new InjectTransform(project))

    }
}