package app.own.utils

import android.content.Context
import android.util.TypedValue

internal fun Context.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)
}

internal fun Context.pxToDp(px: Int): Float {
    val density = resources.displayMetrics.density
    return px / density
}