package com.wat.serviceworkerhelper.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "reports_table")
data class Report(

    @PrimaryKey
    @ColumnInfo(name = "guideUID")
    var guideUID: String = "",

    @ColumnInfo(name = "creatorUID")
    var creatorUID: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "creationDate")
    var creationDate: String = ""
) : Serializable