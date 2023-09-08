package com.albertjk.chatapp.models

class ChatMessage(val messageId: String, val text: String, val fromUserId: String, val toUserId: String, val timestamp: Long) {
    constructor(): this("", "", "", "", -1)
}