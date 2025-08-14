package app.own.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import app.own.internal.OwnUserInternal

internal fun View.detachFromParent() {
    val viewParent = (parent as? ViewGroup)
    viewParent?.removeView(this)
}

internal fun ProgressBar.setBrandColor(){
    OwnUserInternal
        .brandColor?.let { brandColor ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.indeterminateTintList = ColorStateList.valueOf(Color.parseColor(brandColor))
            }
        }
}