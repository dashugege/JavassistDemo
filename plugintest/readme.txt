rootProject/build.gradle 配置        classpath 'com.xiaomu.test:plugintest:0.0.1'

app/build.gradle 配置
plugins {
    id 'com.xiaomu.plugin'
}


configExtension{
    packageName  = 'com.xiaomu'
}
