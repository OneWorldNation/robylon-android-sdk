package app.own.event

enum class ChatbotEventType {
    CHATBOT_BUTTON_LOADED,
    CHATBOT_BUTTON_CLICKED,
    CHATBOT_OPENED,
    CHATBOT_CLOSED,
    CHATBOT_APP_READY,
    CHATBOT_LOADED,
    CHAT_INITIALIZED,
    SESSION_REFRESHED,
    CHAT_INITIALIZATION_FAILED;

    fun isAppEvent() {
        this == CHATBOT_BUTTON_LOADED || this == CHATBOT_BUTTON_CLICKED || this == CHATBOT_OPENED
    }

    fun isSystemEvent(): Boolean {
        return this == CHATBOT_OPENED || this == CHATBOT_APP_READY || this == CHATBOT_LOADED || this == CHAT_INITIALIZED || this == SESSION_REFRESHED || this == CHAT_INITIALIZATION_FAILED
    }
}
