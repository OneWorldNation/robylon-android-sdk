package app.own.view

import app.own.internal.OwnInternal

sealed class ChatBotButtonType {
    data class Text(
        val text: String = "Chat Bot",
        val backgroundColor: String = "#6200EE",
        val textColor: String = "#FFFFFF",
        val cornerRadiusDp: Int = 10,
        val sideSpacingDp: Int = 10,
        val bottomSpacingDp: Int = 10,
        val position: ChatBotButtonPosition = ChatBotButtonPosition.RIGHT
    ) : ChatBotButtonType()

    data class Image(
        val url: String="",
        val sideSpacingDp: Int = 10,
        val bottomSpacingDp: Int = 10,
        val position: ChatBotButtonPosition = ChatBotButtonPosition.RIGHT
    ) : ChatBotButtonType()

    data class TextImage(
        val text: Text,
        val image: Image,
        val sideSpacingDp: Int = 10,
        val bottomSpacingDp: Int = 10,
        val position: ChatBotButtonPosition = ChatBotButtonPosition.RIGHT
    ) : ChatBotButtonType()
}