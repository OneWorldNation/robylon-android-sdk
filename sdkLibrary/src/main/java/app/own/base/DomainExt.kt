package app.own.base

internal fun getBestFontColor(
    backgroundColor: String,
    contrastThreshold: Double = 0.5
): String {
    val whiteShade = "#FFFFFF" // Light white shade
    val blackShade = "#0E0E0F" // Dark black shade

    // Function to convert hex to RGB
    fun hexToRgb(hex: String): Triple<Int, Int, Int> {
        val cleanedHex = hex.removePrefix("#")
        val fullHex = if (cleanedHex.length == 3) {
            cleanedHex.map { "$it$it" }.joinToString("")
        } else {
            cleanedHex
        }

        val r = fullHex.substring(0, 2).toInt(16)
        val g = fullHex.substring(2, 4).toInt(16)
        val b = fullHex.substring(4, 6).toInt(16)

        return Triple(r, g, b)
    }

    // Function to calculate luminance
    fun luminance(r: Int, g: Int, b: Int): Double {
        fun channel(c: Int): Double {
            val v = c / 255.0
            return if (v <= 0.03928) v / 12.92 else Math.pow((v + 0.055) / 1.055, 2.4)
        }

        return channel(r) * 0.2126 + channel(g) * 0.7152 + channel(b) * 0.0722
    }

    val (r, g, b) = hexToRgb(backgroundColor)
    val bgLuminance = luminance(r, g, b)

    return if (bgLuminance > contrastThreshold) blackShade else whiteShade
}