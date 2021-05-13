package com.wat.serviceworkerhelper.utils

enum class Determiners(
    val determiner: String
) {
    STRING_ARRAY_LIST(":"),
    KEYS(";"),
    VALUES("|"),
    VALUES_CAT("~"),
    HASH_MAP("+")
}