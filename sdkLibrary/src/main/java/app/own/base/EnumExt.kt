package app.own.base

internal inline fun <reified T : Enum<T>> String?.mapToEnum(defaultValue: T): T {
    return mapToEnum<T>() ?: defaultValue
}

internal inline fun <reified T : Enum<T>> String?.mapToEnum(ignoreCase: Boolean = false): T? {
    this ?: return null
    return enumValues<T>().find { value -> value.name.equals(this, ignoreCase) }
}