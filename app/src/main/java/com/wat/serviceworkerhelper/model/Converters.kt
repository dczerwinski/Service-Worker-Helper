package com.wat.serviceworkerhelper.model

import androidx.room.TypeConverter
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.entities.User
import com.wat.serviceworkerhelper.utils.Determiners
import com.google.gson.Gson
import java.util.stream.Collectors

class Converters {

    @TypeConverter
    fun fromStringArrayList(value: ArrayList<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringArrayList(value: String): ArrayList<String> {
        if (value.isEmpty()) return ArrayList()
        return ArrayList(Gson().fromJson(value, Array<String>::class.java).toList())
    }

    @TypeConverter
    fun fromIntHashMap(value: HashMap<String, Int>): String {
        if (value.isEmpty()) return ""
        val keys = value.keys.stream().collect(Collectors.joining(Determiners.KEYS.determiner))
        var values = ""
        value.values.forEach {
            values += "$it${Determiners.KEYS.determiner}"
        }
        return "$keys${Determiners.HASH_MAP.determiner}$values"
    }

    @TypeConverter
    fun toIntHashMap(value: String): HashMap<String, Int> {
        if (value.isEmpty()) return HashMap()

        val keys = value.substring(0, value.indexOf(Determiners.HASH_MAP.determiner)) +
                Determiners.KEYS.determiner

        val stringValues = value.substring(
            value.indexOf(Determiners.HASH_MAP.determiner) + 1,
            value.length
        )

        if (keys.isNotEmpty() || stringValues.isNotEmpty()) {
            keys.split(Determiners.KEYS.determiner).toList()
            val values = ArrayList<Int>()
            stringValues.split(Determiners.KEYS.determiner).toList().forEach {
                if (it.isNotEmpty()) {
                    values.add(it.toInt())
                }
            }

            return hashMapOf(
                *keys.split(Determiners.KEYS.determiner)
                    .toList()
                    .zip(values)
                    .toTypedArray()
            )
        }
        return HashMap()
    }

    @TypeConverter
    fun fromOpinionHashMap(value: HashMap<String, Guide.Opinion>): String {
        if (value.isEmpty()) return ""
        val keys = value.keys.stream().collect(Collectors.joining(Determiners.KEYS.determiner))
        var values = ""
        value.values.forEach {
            values += "$it${Determiners.KEYS.determiner}"
        }
        return "$keys${Determiners.HASH_MAP.determiner}$values"
    }

    @TypeConverter
    fun toOpinionHashMap(value: String): HashMap<String, Guide.Opinion> {
        if (value.isEmpty()) return HashMap()

        val keys = value.substring(0, value.indexOf(Determiners.HASH_MAP.determiner)) +
                Determiners.KEYS.determiner

        val stringValues = value.substring(
            value.indexOf(Determiners.HASH_MAP.determiner) + 1,
            value.length
        )

        if (keys.isNotEmpty() || stringValues.isNotEmpty()) {
            keys.split(Determiners.KEYS.determiner).toList()
            val values = ArrayList<Guide.Opinion>()
            stringValues.split(Determiners.KEYS.determiner).toList().forEach {
                if (it.isNotEmpty()) {
                    values.add(Guide.Opinion.toOpinion(it))
                }
            }

            return hashMapOf(
                *keys.split(Determiners.KEYS.determiner)
                    .toList()
                    .zip(values)
                    .toTypedArray()
            )
        }
        return HashMap()
    }

    @TypeConverter
    fun fromUserType(value: User.Type): Int {
        return value.ordinal
    }

    @TypeConverter
    fun toUserType(value: Int): User.Type {
        return when {
            User.Type.ADMIN.ordinal == value -> User.Type.ADMIN
            User.Type.NORMAL.ordinal == value -> User.Type.NORMAL
            else -> throw IllegalStateException("Wrong type!")
        }
    }

    @TypeConverter
    fun fromGuideStatus(value: Guide.Status): Int {
        return value.ordinal
    }

    @TypeConverter
    fun toGuideStatus(value: Int): Guide.Status {
        return when {
            Guide.Status.PENDING.ordinal == value -> Guide.Status.PENDING
            Guide.Status.REPORTED.ordinal == value -> Guide.Status.REPORTED
            Guide.Status.ADDED.ordinal == value -> Guide.Status.ADDED
            else -> throw IllegalStateException("Wrong status!")
        }
    }

    @TypeConverter
    fun fromCategories(value: ArrayList<User.Category>): String {
        val determiner = Determiners.VALUES_CAT.determiner
        val result = StringBuilder()
        value.forEach {
            result.append("$determiner$it")
        }
        return result.toString()
    }

    @TypeConverter
    fun toCategories(value: String): ArrayList<User.Category> {
        val result = ArrayList<User.Category>()
        val determiner = Determiners.VALUES_CAT.determiner
        val temp = value
            .substring(1)
            .split(determiner)
            .toTypedArray()
        temp.forEach {
            result.add(User.Category.fromString(it))
        }
        return result
    }

    @TypeConverter
    fun fromStepsArrayList(value: ArrayList<Guide.Step>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStepsArrayList(value: String): ArrayList<Guide.Step> {
        return ArrayList(Gson().fromJson(value, Array<Guide.Step>::class.java).toList())
    }
}