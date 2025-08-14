package app.own.config

import app.own.view.ChatBotButtonType

interface BotConfigListener {
    fun listen(chatBotButtonType: ChatBotButtonType)
}