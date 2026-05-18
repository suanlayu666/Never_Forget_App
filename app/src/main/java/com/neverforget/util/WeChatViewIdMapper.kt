package com.neverforget.util

/**
 * 微信/企微 View ID 映射表
 * 用于无障碍服务定位聊天消息节点
 * 微信频繁更新会改变内部 View ID，需持续维护
 */
object WeChatViewIdMapper {

    // 微信 (com.tencent.mm)
    data class WeChatIds(
        val packageName: String,
        val chatListViewId: String,
        val messageItemId: String,
        val messageContentId: String,
        val senderNameId: String,
        val conversationTitleId: String
    )

    val WECHAT_IDS = WeChatIds(
        packageName = "com.tencent.mm",
        chatListViewId = "com.tencent.mm:id/ks5",
        messageItemId = "com.tencent.mm:id/ks6",
        messageContentId = "com.tencent.mm:id/ks7",
        senderNameId = "com.tencent.mm:id/ks8",
        conversationTitleId = "com.tencent.mm:id/ks9"
    )

    val WEWORK_IDS = WeChatIds(
        packageName = "com.tencent.wework",
        chatListViewId = "com.tencent.wework:id/list_view",
        messageItemId = "com.tencent.wework:id/message_item",
        messageContentId = "com.tencent.wework:id/message_content",
        senderNameId = "com.tencent.wework:id/sender_name",
        conversationTitleId = "com.tencent.wework:id/conversation_title"
    )
}
