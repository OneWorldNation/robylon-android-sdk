# Robylon Android SDK - Developer Guide

## 1. Overview
The Robylon Android SDK allows you to integrate the Robylon chatbot into your Android application.

---

## 2. Integration Steps

### 2.1 Add `robylon-release.aar`
1. Place `robylon-release.aar` in `app/libs/`
2. Add in `app/build.gradle`:
```gradle
dependencies {
    implementation files('libs/robylon-release.aar')
}
repositories {
    flatDir { dirs 'libs' }
}
```

---

## 3. Initialization
Initialize in your `Application` class:

```kotlin
Robylon.initialize(applicationContext, API_KEY)
```

---

## 4. API Reference

### `Robylon.initialize(context, apiKey)`
Initialize SDK.

### `Robylon.setUserId(userId)`
Set unique user ID.

### `Robylon.setUserProfile(userProfileJo)`
Set user profile JSON.

### `Robylon.setUserToken(userToken)`
Set authentication token.

### `Robylon.setChatbotEventListener(listener)`
Register chatbot event listener.

**Event Types**:
- CHATBOT_BUTTON_LOADED
- CHATBOT_BUTTON_CLICKED
- CHATBOT_OPENED
- CHATBOT_CLOSED
- CHATBOT_APP_READY
- CHATBOT_LOADED
- CHAT_INITIALIZED
- SESSION_REFRESHED
- CHAT_INITIALIZATION_FAILED

### `Robylon.forceRefresh()`
Refresh chatbot session.

### `Robylon.destroy()`
Clean up resources.

---

## 5. UI Integration

```xml
<app.own.view.ChatBotButton
    android:id="@+id/chatBotButton"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

---

## 6. Best Practices
- Initialize once in `Application.onCreate()`
- Set user details after login
- Handle events for analytics
- Call `destroy()` when no longer needed
- Use `forceRefresh()` after user updates
