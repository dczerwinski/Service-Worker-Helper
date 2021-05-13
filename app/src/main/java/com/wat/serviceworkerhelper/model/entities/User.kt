package com.wat.serviceworkerhelper.model.entities

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.utils.DatabaseUtils
import com.wat.serviceworkerhelper.utils.Determiners
import java.io.Serializable

@Entity(tableName = "users_table")
data class User(

    @PrimaryKey
    @ColumnInfo(name = "uid")
    var uid: String = "",

    @ColumnInfo(name = "displayName")
    var displayName: String = "",

    @ColumnInfo(name = "email")
    var email: String = "",

    @ColumnInfo(name = "photoURL")
    var photoURL: String = "",

    @ColumnInfo(name = "userType")
    var userType: Type = Type.NORMAL,

    @ColumnInfo(name = "isActivated")
    var isActivated: Boolean = false,

    @ColumnInfo(name = "categories")
    var categories: ArrayList<Category> = ArrayList(DatabaseUtils.getDefaultCategories())

) : Serializable {

    enum class Type {
        NORMAL,
        ADMIN;

        companion object {
            fun toType(arg: String, context: Context): Type {
                return when (arg) {
                    context.getString(R.string.admin) -> {
                        ADMIN
                    }
                    context.getString(R.string.normal) -> {
                        NORMAL
                    }
                    else -> {
                        throw IllegalStateException("Wrong type!")
                    }
                }
            }
        }
    }

    data class Category(
        var name: String = "",
        var guidesUIDs: ArrayList<String> = ArrayList(),
        var deletable: Boolean = true
    ) : Serializable {

        override fun toString(): String {
            return "$determiner$name" +
                    "$determiner${guidesUIDsToString(guidesUIDs)}" +
                    "$determiner$deletable"
        }

        private fun guidesUIDsToString(value: List<String>): String {
            var result = ""
            value.forEach {
                result += "${Determiners.STRING_ARRAY_LIST.determiner}$it"
            }
            return result
        }

        companion object {

            private val determiner = Determiners.VALUES.determiner

            fun fromString(value: String): Category {
                val temp = value
                    .substring(1)
                    .split(determiner)
                    .toTypedArray()

                return Category(temp[0], fromStringToGuidesUIDs(temp[1]), stringToBoolean(temp[2]))
            }

            private fun stringToBoolean(value: String): Boolean {
                return when (value) {
                    "false" -> false
                    "true" -> true
                    else -> throw  IllegalArgumentException("Wrong value!")
                }
            }

            private fun fromStringToGuidesUIDs(value: String): ArrayList<String> {
                if (value.isEmpty()) return ArrayList()
                val temp = value
                    .substring(1)
                    .split(Determiners.STRING_ARRAY_LIST.determiner)
                    .toTypedArray()

                return ArrayList(temp.asList())
            }
        }
    }
}