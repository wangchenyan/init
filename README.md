# CInit

[![](https://jitpack.io/v/wangchenyan/init.svg)](https://jitpack.io/#wangchenyan/init)

Android 依赖任务启动框架

## Feature

| | JetPack StartUp | CInit |
| :-: | :-: | :-: |
| 任务依赖 | ✅ | ✅ |
| 异步任务 | ❌ | ✅ |
| 任务优先级 | ❌ | ✅ |
| 多进程 | ❌ | ✅<br>支持所有进程、主进程、非主进程、:xxx、特定进程名 |
| 模块化 | ❌<br>使用 Class 定义依赖 | ✅<br>使用 String 定义依赖 |
| 初始化 | ContentProvider 和接口 | 仅接口 |
| 配置方式 | 在 Manifest 中配置任务 | 使用注解配置任务 |
| 回调 | ❌ | ✅<br>支持单个任务/全部任务执行完成回调 |

## Usage

### 1. 添加 JitPack 仓库

```kotlin
// Top-level build file
buildscript {
    repositories {
        maven("https://jitpack.io")
    }
}

allprojects {
    repositories {
        maven("https://jitpack.io")
    }
}
```

### 2. 添加 auto-register 插件，用于字节码注入

```kotlin
// Top-level build file
buildscript {
    dependencies {
        classpath("com.github.wangchenyan:AutoRegister:1.4.3-beta02")
    }
}
```

```kotlin
// app build file
plugins {
    id("auto-register")
}

autoregister {
    registerInfo = listOf(
        mapOf(
            "scanInterface" to "me.wcy.init.annotation.ModuleTaskRegister",
            "codeInsertToClassName" to "me.wcy.init.api.FinalTaskRegister",
            "codeInsertToMethodName" to "init",
            "registerMethodName" to "register",
            "include" to listOf("me/wcy/init/apt/taskregister/.*")
        )
    )
}
```

### 3. 添加 init 依赖和注解处理器

kapt 和 ksp 二选一

#### 3.1 使用 kapt

```kotlin
// app build file
plugins {
    id("kotlin-kapt")
}

kapt {
    arguments {
        arg("moduleName", project.name)
    }
}

dependencies {
    kapt("com.github.wangchenyan.init:init-compiler:${latestVersion}")
    implementation("com.github.wangchenyan.init:init-api:${latestVersion}")
}
```

#### 3.2 使用 ksp

```kotlin
// Top-level build file
plugins {
    // 注意 ksp 版本和 kotlin 版本需要对应
    id("com.google.devtools.ksp") version "1.8.20-1.0.11" apply false
}
```

```kotlin
// app build file
plugins {
    // 注意 ksp 版本和 kotlin 版本需要对应
    id("com.google.devtools.ksp") version "1.8.20-1.0.11"
}

ksp {
    arg("moduleName", project.name)
}

dependencies {
    ksp("com.github.wangchenyan.init:init-compiler-ksp:${latestVersion}")
    implementation("com.github.wangchenyan.init:init-api:${latestVersion}")
}
```

### 4. 在代码中使用

#### 4.1 定义任务

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

#### 4.2 在应用 Application 中启动任务

```kotlin
class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CTaskManager.start(this, onTaskComplete = {
            Log.i("WCY", "task complete: $it")
        }) {
            Log.i("WCY", "all task complete")
        }
    }
}
```

## About Me

掘金：https://juejin.cn/user/2313028193754168<br>
微博：https://weibo.com/wangchenyan1993

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
