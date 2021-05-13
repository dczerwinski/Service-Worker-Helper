package com.wat.serviceworkerhelper.utils

enum class HashMapKeys(
    var value: String
) {
    OPINION_STATS_5("5_key"),
    OPINION_STATS_4("4_key"),
    OPINION_STATS_3("3_key"),
    OPINION_STATS_2("2_key"),
    OPINION_STATS_1("1_key");

    companion object {
        fun getKey(value: Int): String {
            when (value) {
                5 -> {
                    return OPINION_STATS_5.value
                }
                4 -> {
                    return OPINION_STATS_4.value
                }
                3 -> {
                    return OPINION_STATS_3.value
                }
                2 -> {
                    return OPINION_STATS_2.value
                }
                1 -> {
                    return OPINION_STATS_1.value
                }
                else -> {
                    return ""
                }
            }
        }
    }
}