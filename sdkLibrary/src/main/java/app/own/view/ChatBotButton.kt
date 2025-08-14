package app.own.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import app.own.activity.WebViewActivity
import app.own.base.BitmapHelper
import app.own.base.SdkCreator
import app.own.config.BotConfigListener
import app.own.event.ChatbotEventType
import app.own.internal.OwnInternal
import app.own.internal.OwnUserInternal
import app.own.utils.dpToPx
import app.own.utils.pxToDp
import app.own.utils.setBrandColor
import app.own.view.ChatBotButtonPosition.*
import com.own.databinding.ChatBotButtonBinding
import kotlin.math.min

class ChatBotButton : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding = ChatBotButtonBinding.inflate(LayoutInflater.from(context), this, true)
    private val botConfigListener: BotConfigListener
    private val openChatsClickListener = OnClickListener {
        OwnInternal.userChatbotEventListener?.onEvent(ChatbotEventType.CHATBOT_BUTTON_CLICKED)
        openChats()
    }

    init {

        setClickListeners()

        botConfigListener = object : BotConfigListener {
            override fun listen(chatBotButtonType: ChatBotButtonType) {
                post { setType(chatBotButtonType) }
            }
        }
        OwnUserInternal
            .addBotConfigListener(botConfigListener = botConfigListener)
        OwnInternal.userChatbotEventListener?.onEvent(ChatbotEventType.CHATBOT_BUTTON_LOADED)
    }

    private fun setClickListeners() {
        if (OwnInternal.chatIframeUrl.isNullOrBlank())
            return
        arrayOf(binding.textTypeParent, binding.imageTypeParent, binding.textImageTypeParent).forEach {
            it.setOnClickListener(openChatsClickListener)
        }
    }

    @SuppressLint("UseKtx")
    fun setType(type: ChatBotButtonType) {
        setClickListeners()
        when (type) {
            is ChatBotButtonType.Text -> {
                setPosition(
                    position = type.position,
                    viewParams = binding.textTypeParent.layoutParams,
                )
                hideOtherViews(text = true, image = false, textImage = false)
                setTextTypeTheme(type, binding.textTitleTv)
                binding.textTitleTv.setPadding(
                    /* left = */ context.dpToPx(10).toInt(),
                    /* top = */ context.dpToPx(10).toInt(),
                    /* right = */ context.dpToPx(10).toInt(),
                    /* bottom = */ context.dpToPx(10).toInt(),
                )
                binding.textTitleTv.setTextColor(Color.parseColor(type.textColor))
                setSideSpacingDp(binding.textTitleTv, type.sideSpacingDp, type.bottomSpacingDp, type.position)
            }

            is ChatBotButtonType.Image -> {
                setPosition(
                    position = type.position,
                    viewParams = binding.imageTypeParent.layoutParams,
                )
                hideOtherViews(text = false, image = true, textImage = false)
                setImageTypeTheme(type, binding.imageTypeIv, binding.imagePb)
                binding.imagePb.setBrandColor()
                setSideSpacingDp(binding.imageTypeIv, type.sideSpacingDp, type.bottomSpacingDp, type.position)
            }

            is ChatBotButtonType.TextImage -> {
                setPosition(
                    position = type.position,
                    viewParams = binding.textImageTypeParent.layoutParams
                )
                hideOtherViews(text = false, image = false, textImage = true)
                setTextTypeTheme(type.text, binding.textImageTitleTv)
                binding.textImagePb.setBrandColor()
                setImageTypeTheme(type.image, binding.textImageTypeIv, binding.textImagePb)
                setSideSpacingDp(binding.textImageTitleTv, type.sideSpacingDp, type.bottomSpacingDp, type.position)
            }
        }
        if (OwnInternal.chatIframeUrl.isNullOrBlank()) {
            binding.rootFL.visibility = View.GONE
        } else {
            binding.rootFL.visibility = View.VISIBLE
        }
    }

    fun destroy() {
        OwnUserInternal.removeBotConfigListener(
            botConfigListener = botConfigListener
        )
    }

    fun setWidth(width: Int) {
        binding.rootFL.layoutParams.width = width
    }

    fun setHeight(width: Int) {
        binding.rootFL.layoutParams.height = height
    }

    private fun setTextTypeTheme(type: ChatBotButtonType.Text, textView: TextView) {

        textView.text = type.text

        val drawable = textView.background as? GradientDrawable
        drawable?.setColor(Color.parseColor(type.backgroundColor))
        drawable?.cornerRadius = context.dpToPx(30)
        textView.background = drawable

        textView.setTextColor(Color.parseColor(type.textColor))


    }

    private fun setImageTypeTheme(type: ChatBotButtonType.Image, imageView: ImageView, progressBar: ProgressBar) {
        if (type.url.isBlank()) {
            return
        }
        SdkCreator
            .executorService.execute {
                val bitmap = BitmapHelper.getBitmapFromUrl(type.url)
                if (bitmap == null) {
                    progressBar.visibility = View.GONE
                    return@execute
                }
                imageView.post {
                    imageView.setImageBitmap(BitmapHelper.getCircularBitmap(bitmap))
                    progressBar.visibility = View.GONE
                }
            }
    }

    private fun hideOtherViews(
        text: Boolean,
        image: Boolean,
        textImage: Boolean
    ) {
        binding.textTypeParent.visibility = if (text) View.VISIBLE else View.GONE
        binding.imageTypeParent.visibility = if (image) View.VISIBLE else View.GONE
        binding.textImageTypeParent.visibility = if (textImage) View.VISIBLE else View.GONE
    }

    private fun setPosition(
        position: ChatBotButtonPosition,
        viewParams: ViewGroup.LayoutParams
    ) {
        val gravity = when (position) {
            LEFT -> Gravity.START or Gravity.BOTTOM
            RIGHT -> Gravity.END or Gravity.BOTTOM
        }
        (viewParams as? FrameLayout.LayoutParams)?.gravity = gravity
    }

    private fun setSideSpacingDp(view: View, sideSpacingDp: Int, bottomSpacingDp: Int, position: ChatBotButtonPosition) {
        val screenHalfSizeInDp = context.pxToDp(resources.displayMetrics.widthPixels).toInt()
        val safeSideSpacingDp = min(sideSpacingDp, screenHalfSizeInDp)
        val layoutParams = view.layoutParams as MarginLayoutParams

        when(position){
            LEFT -> {
                layoutParams.setMargins(
                    /* left = */ context.dpToPx(safeSideSpacingDp).toInt(),
                    /* top = */ 0,
                    /* right = */ 0,
                    /* bottom = */ context.dpToPx(bottomSpacingDp).toInt()
                )
            }
            RIGHT -> {
                layoutParams.setMargins(
                    /* left = */ 0,
                    /* top = */ 0,
                    /* right = */ context.dpToPx(safeSideSpacingDp).toInt(),
                    /* bottom = */ context.dpToPx(bottomSpacingDp).toInt()
                )
            }
        }
        view.layoutParams = layoutParams
    }

    private fun openChats() {
        OwnInternal.userChatbotEventListener?.onEvent(ChatbotEventType.CHATBOT_OPENED)
        WebViewActivity.openChats(context = context)
    }

}