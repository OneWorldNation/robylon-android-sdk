package app.own.utils

import org.json.JSONObject

internal fun JSONObject.merge(other: JSONObject): JSONObject {
    val keys = other.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        this.put(key, other.get(key))
    }
    return this
}