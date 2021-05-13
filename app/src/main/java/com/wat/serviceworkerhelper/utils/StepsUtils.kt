package com.wat.serviceworkerhelper.utils

import com.wat.serviceworkerhelper.model.entities.Guide
import kotlin.math.min

class StepsUtils {

    companion object {
        fun toString(value: ArrayList<Guide.Step>): String {
            val result = StringBuilder()
            for (i in 0 until min(4, value.size)) {
                result.append("${i + 1}. ")
                result.append(value[i].content)
                result.append("\n")
            }
            return result.toString()
        }
    }
}