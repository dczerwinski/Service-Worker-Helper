package com.wat.serviceworkerhelper.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wat.serviceworkerhelper.model.daos.GuideEntityDao
import com.wat.serviceworkerhelper.model.daos.ReportEntityDao
import com.wat.serviceworkerhelper.model.daos.UserEntityDao
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.entities.Report
import com.wat.serviceworkerhelper.model.entities.User

@Database(entities = [Guide::class, User::class, Report::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun guideDao(): GuideEntityDao
    abstract fun userDao(): UserEntityDao
    abstract fun reportsDao(): ReportEntityDao

    companion object {

        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getDatabase(
            context: Context
        ): AppRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}