package com.wat.serviceworkerhelper.utils

import com.wat.serviceworkerhelper.model.entities.User

enum class DatabaseUtils(
    val value: String
) {
    USERS("users"),
    REPORTS("reports"),
    GUIDES("guides");

    companion object {

        @JvmStatic
        fun getDefaultCategories(): List<User.Category> {
            val result = ArrayList<User.Category>()
            result.add(
                User.Category(
                    "createdByMe",
                    deletable = false
                )
            )
            result.add(
                User.Category(
                    "fav",
                    deletable = false
                )
            )
            return result
        }
    }
}