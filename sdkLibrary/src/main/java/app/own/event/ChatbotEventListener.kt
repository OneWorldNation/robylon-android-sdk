package app.own.event

interface ChatbotEventListener {
    fun onEvent(chatbotEventType: ChatbotEventType)
}