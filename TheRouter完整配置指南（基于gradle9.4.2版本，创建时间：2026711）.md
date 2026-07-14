# TheRouter 完整配置指南（WeatherForecast2 项目）

> 本指南基于 **AGP 9.2.1 + Gradle 9.x + Kotlin 2.1.x + TheRouter 1.4.0-rc1** 环境编写，
> 内容贴合项目实际代码，涵盖配置、使用、调试、踩坑一体化。

---

## 一、环境要求

| 组件 | 本项目版本 |
|------|-----------|
| Android Gradle Plugin | 9.2.1 |
| Gradle | 9.x |
| Kotlin | 2.1.0 |
| TheRouter | 1.4.0-rc1 |
| KSP | 2.1.0-1.0.29 |
| JDK | 17+ |

> ⚠️ TheRouter 1.3.x 依赖 AGP 7.4.2，与 AGP 9.x 不兼容，本项目最低版本为 **1.4.0-rc1**。

---

## 二、项目配置（完整步骤）

### 2.1 Version Catalog — `gradle/libs.versions.toml`

```toml
[versions]
theRouter = "1.4.0-rc1"
ksp = "2.1.0-1.0.29"

[libraries]
therouter-apt = { group = "cn.therouter", name = "apt", version.ref = "theRouter" }
therouter-router = { group = "cn.therouter", name = "router", version.ref = "theRouter" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

> ⚠️ TheRouter 的 Gradle 插件不需要在 `[plugins]` 中声明，原因见下文"踩坑记录"。

---

### 2.2 根目录 `build.gradle.kts`

```kotlin
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("cn.therouter:plugin:${libs.versions.theRouter.get()}")
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
}
```

**关键点：**
- 必须用 `buildscript { classpath }` 引入 TheRouter Gradle 插件，不能通过 `plugins {}` 方式
- `ksp` 插件放在 `plugins {}` 中统一管理版本，各模块使用 `alias` 引入

---

### 2.3 各模块 `build.gradle.kts`

**核心原则：凡是使用了 `@Route` 注解的模块，都必须同时配置 KSP 插件 + TheRouter APT 依赖。**

#### app 模块（壳工程）

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}
apply(plugin = "therouter")

android {
    defaultConfig {
        // 确保 KSP 产物目录正确
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }
}

dependencies {
    implementation(libs.therouter.router)       // 运行时
    ksp(libs.therouter.apt)                     // 注解处理器
}
```

#### feature/* 模块（如 auth、weather）

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}
// ⚠️ 注意：library 模块不需要 apply(plugin = "therouter")
//    只需要 KSP 来处理 @Route 注解的编译时索引

dependencies {
    implementation(libs.therouter.router)
    ksp(libs.therouter.apt)
}
```

**项目实际配置情况：**

| 模块 | KSP 插件 | therouter 依赖 | 说明 |
|------|---------|---------------|------|
| **app** | ✅ | ✅ | 壳工程，应用了 `therouter` 插件 |
| **feature:auth** | ✅ | ✅ | 有 `@Route(path = "/auth/home")` |
| **feature:weather** | ✅ | ✅ | 有 `@Route(path = "/weather/home")` |
| core/router | ❌ | ❌ | 仅定义路由常量，无需 TheRouter |
| core/model | ❌ | ❌ | 纯数据模型 |
| core/network | ❌ | ❌ | 网络层 |
| core/database | ✅（Room） | ❌ | KSP 仅用于 Room，与 TheRouter 无关 |
| data/user | ❌ | ❌ | 数据层 |
| data/weather | ❌ | ❌ | 数据层 |

---

### 2.4 Application 初始化

```kotlin
// app/src/main/java/com/example/weatherapp/WeatherApp.kt
class WeatherApp : Application() {

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        TheRouter.init(this)    // ✅ TheRouter 必须在 Application.onCreate 中初始化
    }
}
```

**必须确认 `AndroidManifest.xml` 中 `android:name` 指向了正确的 Application 类：**

```xml
<application
    android:name=".WeatherApp"
    ...>
```

---

### 2.5 AndroidManifest.xml — 版本声明

```xml
<application ...>
    <!-- ⚠️ 必须添加，否则 TheRouter 无法正常工作 -->
    <meta-data
        android:name="therouter_version"
        android:value="1.4.0-rc1" />
</application>
```

> 🔴 **容易遗漏**：缺少该 meta-data 会导致 TheRouter 初始化时找不到版本信息，部分功能异常。

---

### 2.6 混淆规则 — `app/proguard-rules.pro`

```proguard
# ========== TheRouter ==========
-keep class com.therouter.** { *; }
-keep class * implements com.therouter.inject.InjectConstructor { *; }
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.therouter.router.Route <methods>;    # ← 注意是 router.Route，不是 annotation.Route
}
```

> ⚠️ **常见笔误**：`@com.therouter.router.Route` 的正确包名是 `router`，不是 `annotation`。
> 项目中实际 `proguard-rules.pro` 已写对。

---

## 三、路由定义规范

### 3.1 路由路径规范

本项目采用 **小写层级路径 + 常量管理** 的方式：

```kotlin
// core/router/src/main/java/com/example/weatherapp/core/router/AppRoutes.kt
object AppRoutes {
    /** 认证模块首页（登录注册） */
    const val AUTH_HOME = "/auth/home"
    /** 天气模块首页（天气查询） */
    const val WEATHER_HOME = "/weather/home"
}

// 可选：统一管理传参 key
object RouteParams {
    const val DATE = "date"
    const val WEATHER = "weather"
    const val DAY_TEMPERATURE = "dayTemperature"
    const val NIGHT_TEMPERATURE = "nightTemperature"
    const val WIND = "wind"
    const val WIND_SPEED = "windSpeed"
}
```

**路径命名约定：**
```
/{模块名}/{页面功能}
```
- 模块名：小写英文，如 `auth`、`weather`
- 页面功能：小写英文，如 `home`、`detail`、`settings`
- 全部小写，单词间用连字符（`-`）或斜杠（`/`）分隔，**不要使用驼峰类名风格**

---

### 3.2 注解页面路由（`@Route`）

```kotlin
// feature/auth/src/main/java/.../AuthActivity.kt
@Route(path = "/auth/home")    // ← 常量应与 AppRoutes.AUTH_HOME 保持一致
class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}
```

```kotlin
// feature/weather/src/main/java/.../WeatherActivity.kt
@Route(path = "/weather/home")  // ← 常量应与 AppRoutes.WEATHER_HOME 保持一致
class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
    }
}
```

**最佳实践：**
- `@Route(path = ...)` 中的字符串字面量与 `AppRoutes` 常量保持完全一致
- 推荐在 `AppRoutes` 常量上方加 `/** 注释 */` 说明页面用途
- 路径变更时只需修改 `AppRoutes` 一处，所有调用处自动生效

---

## 四、路由调用方式

### 4.1 基本跳转

```kotlin
// 使用常量跳转（推荐）
TheRouter.build(AppRoutes.WEATHER_HOME).navigation()
TheRouter.build(AppRoutes.AUTH_HOME).navigation()
```

### 4.2 携带参数跳转

```kotlin
TheRouter.build(AppRoutes.WEATHER_HOME)
    .withString(RouteParams.DATE, "2024-01-15")
    .withString(RouteParams.WEATHER, "晴")
    .withInt("type", 1)
    .navigation()
```

### 4.3 带结果回调跳转

```kotlin
TheRouter.build(AppRoutes.WEATHER_HOME)
    .navigation(this) { routeResult ->
        // 路由结果回调
    }
```

### 4.4 项目中的实际使用

本项目共 4 处 TheRouter 调用，均为 `TheRouter.build(path).navigation()` 模式：

| 文件 | 调用代码 | 触发时机 |
|------|---------|---------|
| `MainActivity.kt` | `TheRouter.build(AppRoutes.WEATHER_HOME).navigation()` | 已登录 → 进主页 |
| `MainActivity.kt` | `TheRouter.build(AppRoutes.AUTH_HOME).navigation()` | 未登录 → 进登录 |
| `LoginFragment.kt` | `TheRouter.build(AppRoutes.WEATHER_HOME).navigation()` | 登录成功 → 跳天气 |
| `WeatherFragment.kt` | `TheRouter.build(AppRoutes.AUTH_HOME).navigation()` | 退出登录 → 回登录 |

---

## 五、进阶功能（本项目暂未使用）

以下功能 TheRouter 完全支持，项目后续可扩展：

### 5.1 服务发现（`@Action`）

```kotlin
// 公共模块定义接口
interface IUserService {
    fun getUserName(): String
    fun isLogin(): Boolean
}

// 具体模块实现
@Action(description = "获取用户服务")
fun provideUserService(): IUserService {
    return UserServiceImpl()
}

// 任意模块调用
val userService = TheRouter.get(IUserService::class.java)
```

### 5.2 依赖注入（`@Inject`）

```kotlin
@Inject
class PayServiceImpl : IPayService {
    override fun pay(orderId: String, amount: Double) { ... }
}

// 调用
val payService = TheRouter.get(IPayService::class.java)
```

---

## 六、踩坑记录

### 坑 1：插件 ID 不匹配（必须用 buildscript）

**现象：**
```
Could not apply requested plugin [id: 'cn.therouter', version: '1.3.2']
as it does not provide a plugin with id 'cn.therouter'.
```

**原因：** TheRouter 的 marker artifact（`cn.therouter.gradle.plugin`）声明的插件 ID 是 `cn.therouter`，但实际 JAR 中 `META-INF/gradle-plugins/` 注册的 ID 是 `therouter`。这是 TheRouter 的发布 Bug，1.3.2 和 1.4.0-rc1 均存在。

**解决：** 用 `buildscript { classpath(...) }` + `apply(plugin = "therouter")` 引入，绕过 marker artifact 的 ID 校验。

### 坑 2：Kotlin 插件冲突

**现象：**
```
Cannot add extension with name 'kotlin',
as there is an extension already registered with that name.
```

**原因：** AGP 9.x 已内置 Kotlin 支持，TheRouter 插件内部也可能注册了 kotlin 扩展，再显式声明 `org.jetbrains.kotlin.android` 会冲突。

**解决：** 不要手动声明 `kotlin-android` 插件，只声明 KSP 插件即可。

### 坑 3：子模块遗漏 KSP 配置

**现象：** 跳转路由时页面无法找到，运行时抛出 `RouteNotFoundException` 或 `Path not found`。

**原因：** 子模块（如 `feature:auth`）使用了 `@Route` 注解，但 `build.gradle.kts` 中没有配置 `ksp(libs.therouter.apt)`，导致该模块的路由信息未被编译期收集。

**解决：** 每个使用 `@Route` 的模块必须同时配置：
```kotlin
plugins {
    alias(libs.plugins.ksp)
}
dependencies {
    ksp(libs.therouter.apt)
}
```

### 坑 4：AndroidManifest 遗漏 `therouter_version`

**现象：** 路由跳转无反应或初始化日志报错。

**解决：** 确保 `app/src/main/AndroidManifest.xml` 中添加：
```xml
<meta-data android:name="therouter_version" android:value="1.4.0-rc1" />
```

### 坑 5：混淆后路由失效

**现象：** Release 包路由跳转失败，Debug 正常。

**解决：** 检查 `proguard-rules.pro` 中是否包含了 TheRouter 的 keep 规则，特别注意 `@com.therouter.router.Route` 的包名是 `router` 不是 `annotation`。

### 坑 6：KSP 增量编译缓存问题

**现象：** 修改路由路径后重新运行，跳转仍指向旧路径。

**解决：** 执行 `Build > Clean Project` 或手动删除 `build/` 目录，触发 KSP 重新处理注解。

---

## 七、调试技巧

### 7.1 开启 TheRouter 调试日志

```kotlin
// 在 Application.onCreate 中加入
TheRouter.init(this)
TheRouter.setDebuggable(BuildConfig.DEBUG)  // Debug 模式开启日志
```

设置后可在 Logcat 中搜索 `TheRouter` 标签，查看路由注册详情：

```
TheRouter: Navigator init end, path count = 2    // 确认路由数量正确
TheRouter: Navigate to path: /auth/home           // 确认跳转路径
```

### 7.2 检查已注册的路由列表

```kotlin
// 在任意位置获取所有已注册的路径
val allPaths = TheRouter.getAllPath()
allPaths.forEach { path ->
    Log.d("TheRouter", "Registered path: $path")
}
```

### 7.3 确认 KSP 产物是否生成

编译后在以下位置检查 TheRouter KSP 生成的索引文件：

```
app/build/generated/ksp/  ← KSP 代码生成产物
```

如果目录为空或不存在，说明 KSP 配置有误。

### 7.4 常见排查步骤

| 现象 | 排查方向 |
|------|---------|
| 跳转无反应 | ① 检查 `AndroidManifest.xml` 是否有 `therouter_version` ② 检查 `TheRouter.init(this)` 是否调用 |
| "Path not found" 错误 | ① 确认目标模块配置了 `ksp(libs.therouter.apt)` ② 执行 Clean Project ③ 检查 `@Route(path = ...)` 路径拼写 |
| Release 包失效 | 检查 `proguard-rules.pro` 中 TheRouter 的 keep 规则 |
| 路径跳转到错误页面 | 搜索项目中是否有重复的路由路径 |

---

## 八、多模块项目架构

```
WeatherForecast2/
├── app/                    # 壳工程（初始化 + 路由入口）
├── feature/
│   ├── auth/               # 认证模块（@Route = /auth/home）
│   └── weather/            # 天气模块（@Route = /weather/home）
├── core/
│   ├── router/             # 路由常量定义（AppRoutes.kt）
│   ├── model/              # 公共数据模型
│   ├── network/            # 网络层
│   └── database/           # 本地数据库
└── data/
    ├── user/               # 用户数据层
    └── weather/            # 天气数据层
```

**依赖关系：**

```
app ──→ feature/auth, feature/weather
     ──→ core/router, core/model, core/network, core/database

feature/auth ──→ core/router, data/user
feature/weather ──→ core/router, data/weather
```

**关键原则：**
- 业务模块（feature/*）之间**不直接依赖**，完全通过 TheRouter 路径解耦
- 所有路由路径在 `core/router` 模块中集中管理（`AppRoutes.kt`）
- 跳转时使用 `AppRoutes.AUTH_HOME` / `AppRoutes.WEATHER_HOME` 常量，不写硬编码字符串
- `core/router` 模块本身无需 KSP 和 TheRouter 依赖，它只提供常量

---

## 九、配置检查清单

初次配置或迁移后逐项确认：

- [ ] `libs.versions.toml` 中定义了 `therouter-router` 和 `therouter-apt`
- [ ] 根 `build.gradle.kts` 中 `buildscript` 添加了 `classpath("cn.therouter:plugin:...")`
- [ ] 每个使用 `@Route` 的模块都配置了 `ksp(libs.therouter.apt)`
- [ ] `app/build.gradle.kts` 中 `apply(plugin = "therouter")`（仅 app 模块需要）
- [ ] `AndroidManifest.xml` 中有 `<meta-data android:name="therouter_version">`
- [ ] `proguard-rules.pro` 中有 TheRouter keep 规则（`@com.therouter.router.Route`）
- [ ] `Application.onCreate()` 中调用了 `TheRouter.init(this)`

---

## 十、参考链接

- GitHub：https://github.com/HuolalaTech/hll-wp-therouter-android
- 官网：https://therouter.cn
- API 文档：https://therouter.cn/docs/
