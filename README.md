# NeverForget — AI 消息整理助手

一个 Android 应用，通过悬浮窗 + 无障碍服务扫描微信/企业微信/QQ 的聊天消息，用 AI（DeepSeek / 豆包）自动生成摘要并存入本地。

## 核心功能

- **悬浮窗扫描** — 可拖拽悬浮球，在微信/企微/QQ 聊天界面点击「扫描屏幕」自动抓取当前可见消息
- **AI 智能摘要** — 接入 DeepSeek / 豆包 API，对聊天记录进行智能总结：关键话题、待办事项、重要决策
- **多会话管理** — 自动识别 App 来源和会话名，支持多选会话合并摘要
- **系统通知** — 摘要生成后推送通知，点击跳转详情
- **消息去重** — SHA256 哈希去重，避免重复入库

## 技术栈

Kotlin · Jetpack Compose · Room · Hilt · OkHttp · Coroutines + Flow · DataStore · AccessibilityService · WindowManager

## 项目结构

```
app/src/main/java/com/neverforget/
├── accessibility/    # 无障碍服务（屏幕扫描）
├── floating/         # 悬浮窗服务
├── data/
│   ├── local/        # Room 数据库（Entity / DAO / Database）
│   ├── remote/       # AI API 接口
│   └── preferences/  # DataStore 偏好设置
├── domain/           # 业务逻辑（消息抓取 / AI 摘要引擎）
├── repository/       # 数据仓库层
├── ui/               # Jetpack Compose UI
│   ├── main/         # 消息列表
│   ├── summary/      # 摘要列表 + 详情
│   └── settings/     # 设置页
└── util/             # 工具类
```

## 构建运行

1. Android Studio 打开项目根目录
2. 等待 Gradle 同步（已配置阿里云镜像加速）
3. 连接手机（USB 调试模式）或使用模拟器
4. Run `app`

### 环境要求

| 项目 | 版本 |
|------|------|
| Android SDK | ≥ 28（Android 9.0） |
| Gradle | 8.5 |
| AGP | 8.2.2 |
| Kotlin | 1.9.22 |
| JDK | 17 |

## 使用说明

1. **开启无障碍服务** — 设置 → 无障碍服务 → 开启 NeverForget
2. **开启悬浮窗权限** — 设置页 → 悬浮窗 → 授权并启动
3. **配置 AI API** — 设置页 → AI 配置 → 填入 API 地址、Key 和模型名
4. **扫描消息** — 打开微信/企微/QQ 聊天界面 → 点悬浮球 → 点「扫描屏幕」
5. **生成摘要** — 点悬浮球 → 点「生成 AI 摘要」，或进入 App 摘要页点 ✨ 选会话
6. **查看结果** — 摘要页查看详情 + 原始消息

### 推荐的 API 配置

| 服务 | API 地址 | 模型 |
|------|---------|------|
| DeepSeek | `https://api.deepseek.com/v1` | `deepseek-chat` |
| 豆包 | `https://ark.cn-beijing.volces.com/api/v3` | `doubao-pro-32k` |

## APK 下载

[app/release/app-release.apk](app/release/app-release.apk)

## 权限说明

| 权限 | 用途 |
|------|------|
| 无障碍服务 | 扫描微信/企微/QQ 聊天界面文字 |
| 悬浮窗 | 在其他应用上方显示悬浮球 |
| 通知 | 摘要生成后推送通知 |
| 网络 | 调用 AI API |

## License

MIT
