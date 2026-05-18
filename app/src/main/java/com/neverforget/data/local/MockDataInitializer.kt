package com.neverforget.data.local

import com.neverforget.data.local.entity.MessageEntity
import com.neverforget.data.local.entity.SummaryEntity
import com.neverforget.data.preferences.AppPreferences
import com.neverforget.repository.MessageRepository
import com.neverforget.repository.SummaryRepository
import com.neverforget.util.HashUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockDataInitializer @Inject constructor(
    private val messageRepository: MessageRepository,
    private val summaryRepository: SummaryRepository,
    private val appPreferences: AppPreferences
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val sixDaysAgo = System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000L
    private val fiveDaysAgo = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000L
    private val fourDaysAgo = System.currentTimeMillis() - 4 * 24 * 60 * 60 * 1000L
    private val threeDaysAgo = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L

    fun seedIfEmpty() {
        scope.launch {
            val alreadySeeded = appPreferences.isMockSeeded.first()
            if (alreadySeeded) return@launch

            seedMessages()
            seedSummaries()
            appPreferences.setMockSeeded(true)
        }
    }

    private suspend fun seedMessages() {
        val messages = listOf(
            // ========== 产品需求讨论群（微信群，6条）==========
            makeMsg("wechat", "产品需求讨论群", "group", "张三", "大家看一下新版的登录页设计稿，我发到群里了", fiveDaysAgo + 100),
            makeMsg("wechat", "产品需求讨论群", "group", "李四", "收到，设计稿整体风格不错，但是按钮颜色需要调整一下", fiveDaysAgo + 200),
            makeMsg("wechat", "产品需求讨论群", "group", "张三", "好的，我让设计师改一下，预计明天出第二版", fiveDaysAgo + 300),
            makeMsg("wechat", "产品需求讨论群", "group", "王五", "支付接口这边联调遇到了点问题，支付宝的沙箱环境挂了", fiveDaysAgo + 400),
            makeMsg("wechat", "产品需求讨论群", "group", "赵六", "@王五 沙箱环境昨天就恢复了，你再试试", fourDaysAgo + 100),
            makeMsg("wechat", "产品需求讨论群", "group", "王五", "好的我试一下，还不行的话我直接找支付宝技术", fourDaysAgo + 200),

            // ========== 技术交流群（微信群，8条）==========
            makeMsg("wechat", "技术交流群", "group", "老刘", "有人用过 Jetpack Compose 做复杂列表吗？性能怎么样？", sixDaysAgo + 100),
            makeMsg("wechat", "技术交流群", "group", "小明", "我们在用，LazyColumn 优化好的话完全没问题，关键是 key 要设置对", sixDaysAgo + 200),
            makeMsg("wechat", "技术交流群", "group", "老刘", "我们项目想从 XML 迁移到 Compose，有什么建议？", sixDaysAgo + 300),
            makeMsg("wechat", "技术交流群", "group", "小明", "建议逐步迁移，不要一次性全改。先拿简单的页面练手", fiveDaysAgo + 100),
            makeMsg("wechat", "技术交流群", "group", "大卫", "同意，我们之前一次性迁移踩了很多坑，特别是自定义 View 那块", fiveDaysAgo + 200),
            makeMsg("wechat", "技术交流群", "group", "老刘", "多谢各位建议，我们先从设置页开始试试", fourDaysAgo + 100),
            makeMsg("wechat", "技术交流群", "group", "小红", "Compose 的动画 API 特别香，建议早点用上", threeDaysAgo + 100),
            makeMsg("wechat", "技术交流群", "group", "小明", "对，AnimatedVisibility 和 animateContentSize 用起来很顺手", threeDaysAgo + 200),

            // ========== 前端项目组（企微群，5条）==========
            makeMsg("wework", "前端项目组", "group", "陈前端", "这个迭代的 UI 组件库已经更新到 v2.3 了，大家升级一下", fiveDaysAgo + 500),
            makeMsg("wework", "前端项目组", "group", "林前端", "v2.3 的 Table 组件有 breaking change，需要注意", fiveDaysAgo + 600),
            makeMsg("wework", "前端项目组", "group", "陈前端", "对，主要是 columns 的配置方式变了，文档我更新了", fourDaysAgo + 300),
            makeMsg("wework", "前端项目组", "group", "周前端", "在升级了，有个小问题——日期选择器的国际化配置要额外引入", fourDaysAgo + 400),
            makeMsg("wework", "前端项目组", "group", "林前端", "提了个 MR，把日期选择器的中文配置加进去了", threeDaysAgo + 300),

            // ========== 张经理私聊（微信，4条）==========
            makeMsg("wechat", "张经理", "private", "张经理", "小刘，甲方那边确认了需求的优先级，登录和支付先做", threeDaysAgo + 400),
            makeMsg("wechat", "张经理", "private", "我", "好的张经理，我这周把排期表整理出来", threeDaysAgo + 500),
            makeMsg("wechat", "张经理", "private", "张经理", "可以，另外周五下午三点有个需求评审会，记得参加", threeDaysAgo + 600),
            makeMsg("wechat", "张经理", "private", "我", "收到，已经加日历了", threeDaysAgo + 700),

            // ========== 李设计师私聊（企微，3条）==========
            makeMsg("wework", "李设计师", "private", "李设计师", "新版的 icon 素材已经上传到蓝湖了，路径在 /icons/v2.0", fourDaysAgo + 500),
            makeMsg("wework", "李设计师", "private", "我", "好的，我下载看下尺寸适配情况", fourDaysAgo + 600),
            makeMsg("wework", "李设计师", "private", "李设计师", "有问题随时找我，我这两天都在", fourDaysAgo + 700),

            // ========== 运维通知群（微信，4条）==========
            makeMsg("wechat", "运维通知群", "group", "运维助手", "【通知】今晚 22:00-24:00 进行数据库升级维护，期间服务会有短暂中断", fiveDaysAgo + 700),
            makeMsg("wechat", "运维通知群", "group", "运维助手", "【通知】升级已完成，所有服务恢复正常", fiveDaysAgo + 800),
            makeMsg("wechat", "运维通知群", "group", "运维助手", "【通知】本周六进行全链路压测，请各项目组配合关闭非必要定时任务", threeDaysAgo + 800),
            makeMsg("wechat", "运维通知群", "group", "老刘", "收到，我们这边已关闭", threeDaysAgo + 900),
        )

        messageRepository.insertMessages(messages)
    }

    private suspend fun seedSummaries() {
        val allMessages = messageRepository.getAllMessages().first()
        val productGroupMsgs = allMessages.filter { it.conversationName == "产品需求讨论群" }
        val techGroupMsgs = allMessages.filter { it.conversationName == "技术交流群" }
        val managerMsgs = allMessages.filter { it.conversationName == "张经理" }

        val summaries = listOf(
            SummaryEntity(
                title = "产品需求讨论周报",
                summaryContent = """## 关键话题
1. 登录页改版方案讨论——设计稿已出，按钮颜色待优化，预计明天出第二版
2. 支付接口联调——支付宝沙箱环境异常导致联调受阻，已确认环境恢复

## 待办事项
- 张三：协调设计师明天出第二版登录页设计稿
- 王五：重新测试支付宝支付接口联调
- 赵六：协助确认沙箱环境状态

## 结论与决策
- 登录页整体设计方向确认，仅需微调细节
- 支付接口联调整体进度可控""",
                originalMessageIds = productGroupMsgs.joinToString(",") { it.id.toString() },
                sourceApp = "wechat",
                conversationName = "产品需求讨论群",
                dateRange = "2026-05-12 ~ 2026-05-15",
                isRead = false,
                createdAt = System.currentTimeMillis() - 12 * 60 * 60 * 1000
            ),
            SummaryEntity(
                title = "Compose 迁移讨论总结",
                summaryContent = """## 关键话题
1. Jetpack Compose 迁移经验分享——从 XML 到 Compose 的渐进式迁移策略
2. 复杂列表性能优化——LazyColumn 使用技巧与 key 设置
3. Compose 动画 API 的应用场景

## 待办事项
- 老刘：团队从设置页开始试点 Compose 迁移
- 小明：整理 Compose 迁移最佳实践文档

## 结论与决策
- 建议采用渐进式迁移策略，避免一次性全改
- 优先从简单页面（设置页）开始试点
- LazyColumn 配合正确的 key 策略可满足复杂列表场景""",
                originalMessageIds = techGroupMsgs.joinToString(",") { it.id.toString() },
                sourceApp = "wechat",
                conversationName = "技术交流群",
                dateRange = "2026-05-11 ~ 2026-05-16",
                isRead = false,
                createdAt = System.currentTimeMillis() - 24 * 60 * 60 * 1000
            ),
            SummaryEntity(
                title = "项目跟进——张经理沟通纪要",
                summaryContent = """## 关键话题
1. 需求优先级确认——甲方明确登录和支付为最高优先级
2. 本周五下午三点需求评审会

## 待办事项
- 我：本周内整理项目排期表
- 我：参加周五需求评审会

## 结论与决策
- 开发优先级：登录功能 > 支付功能 > 其他功能
- 排期表需在周五评审会前完成""",
                originalMessageIds = managerMsgs.joinToString(",") { it.id.toString() },
                sourceApp = "wechat",
                conversationName = "张经理",
                dateRange = "2026-05-14 ~ 2026-05-14",
                isRead = false,
                createdAt = System.currentTimeMillis() - 6 * 60 * 60 * 1000
            ),
        )

        summaries.forEach { summaryRepository.insertSummary(it) }
    }

    private fun makeMsg(
        sourceApp: String,
        conversationName: String,
        conversationType: String,
        senderName: String,
        content: String,
        timestamp: Long
    ): MessageEntity {
        return MessageEntity(
            sourceApp = sourceApp,
            conversationName = conversationName,
            conversationType = conversationType,
            senderName = senderName,
            content = content,
            timestamp = timestamp,
            messageHash = HashUtils.messageHash(sourceApp, conversationName, senderName, content, timestamp)
        )
    }
}
