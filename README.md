# CInit
[![](https://jitpack.io/v/wangchenyan/init.svg)](https://jitpack.io/#wangchenyan/init)

Android 依赖任务启动框架

## Feature
|  | JetPack StartUp | CInit |
| :-: | :-: | :-: |
| 任务依赖 | ✅ | ✅ |
| 异步任务 | ❌ | ✅ |
| 任务优先级 | ❌ | ✅ |
| 多进程 | ❌ | ✅<br>支持所有进程、主进程、非主进程、:xxx、特定进程名 |
| 模块化 | ❌<br>使用 Class 定义依赖 | ✅<br>使用 String 定义依赖 |
| 初始化 | ContentProvider 和接口 | 仅接口 |
| 配置方式 | 在 Manifest 中配置任务 | 使用注解配置任务 |

## Usage
**root project's build.gradle**

```groovy
buildscript {
  // ...
  dependencies {
    // 用于字节码注入
    classpath 'com.billy.android:autoregister:1.4.2'
  }
}
```

**app's build.gradle**

```groovy
apply plugin: 'kotlin-kapt'
apply plugin: 'auto-register'

// 配置 AutoRegister 注入信息
autoregister {
  registerInfo = [
    [
      'scanInterface'         : 'me.wcy.init.annotation.ModuleTaskRegister',
      'codeInsertToClassName' : 'me.wcy.init.api.FinalTaskRegister',
      'codeInsertToMethodName': 'init',
      'registerMethodName'    : 'register',
      'include'               : ['me/wcy/init/apt/taskregister/.*']
    ]
  ]
}

kapt {
  arguments {
    arg("moduleName", project.name)
  }
}

dependencies {
  kapt "com.github.wangchenyan.init:init-compiler:${latestVersion}"
  implementation "com.github.wangchenyan.init:init-api:${latestVersion}"
}
```

定义任务
```kotlin
@InitTask(
    name = "main",
    background = false,
    process = [InitTask.PROCESS_MAIN],
    priority = InitTask.PRIORITY_NORM,
    depends = ["lib"]
)
class MainTask : IInitTask {
    override fun execute(application: Application) {
        // Do init
    }
}
```

在应用 Application 中启动任务
```kotlin
class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CTaskManager.start(this)
    }
}
```

## About Me
掘金：https://juejin.im/user/58abd9f1da2f607e924e945a<br>
微博：http://weibo.com/wangchenyan1993

## License

    Copyright 2021 wangchenyan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
