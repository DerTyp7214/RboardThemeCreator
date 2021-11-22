package de.dertyp7214.rboardthemecreator.core

fun Map<String, Boolean>.toSet(): Set<String> {
    return map { (key, value) -> "${value.toString().lowercase()}:$key" }.toSet()
}

fun <K, V> Map<K, V>.joinToString(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((Map.Entry<K, V>) -> CharSequence)? = null
): String {
    return map { it }.joinToString(separator, prefix, postfix, limit, truncated, transform)
}