package app.own.base

import android.util.Log
import app.own.internal.OwnInternal
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object NetworkClient {

    internal fun httpCall(
        urL: String,
        requestJson: String? = null,
        requestMethod: String = "POST",
        instanceFollowRedirects: Boolean? = null
    ): HttPResponse {
        Log.i(OwnInternal.TAG, "Requesting : $urL requestJson:$requestJson")
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var httpResponse = HttPResponse(400)

        var currentUrl = urL
        var redirectCount = 0
        val maxRedirects = 5

        try {
            while (redirectCount < maxRedirects) {
                val url = URL(currentUrl)
                connection = url.openConnection() as HttpURLConnection

                // Apply redirect behavior only if provided
                instanceFollowRedirects?.let { connection.instanceFollowRedirects = it }

                connection.requestMethod = requestMethod
                connection.setRequestProperty("Content-Type", "application/json")
                connection.useCaches = false
                if (requestMethod.equals("POST", true))
                    connection.doOutput = true
                connection.doInput = true

                // Send request
                if (requestJson != null && requestMethod.equals("POST", true)) {
                    val wr = DataOutputStream(connection.outputStream)
                    wr.writeBytes(requestJson)
                    wr.flush()
                    wr.close()
                }

                val status = connection.responseCode

                if (status in 300..399 && instanceFollowRedirects == true) {
                    val location = connection.getHeaderField("Location")
                    if (location != null) {
                        currentUrl = URL(URL(currentUrl), location).toString() // handle relative URLs
                        redirectCount++
                        connection.disconnect()
                        continue
                    } else {
                        break
                    }
                }

                try {
                    inputStream = connection.inputStream
                } catch (ioe: IOException) {
                    inputStream = connection.errorStream
                }

                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    response.append(line)
                    response.append('\r')
                }
                bufferedReader.close()

                httpResponse = HttPResponse(connection.responseCode, response.toString())
                break // request completed
            }
        } catch (e: Exception) {
            Log.e(OwnInternal.TAG, "", e)
        } finally {
            connection?.disconnect()
        }

        Log.i(OwnInternal.TAG, "Response(${httpResponse.statusCode}): $urL")
        Log.i(OwnInternal.TAG, "Response : ${httpResponse.response}")
        return httpResponse
    }

}


data class HttPResponse(
    val statusCode: Int,
    val response: String? = null
) {
    fun ok(): Boolean {
        return statusCode == 200
    }

}