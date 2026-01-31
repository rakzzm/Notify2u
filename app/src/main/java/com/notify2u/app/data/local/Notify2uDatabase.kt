package com.notify2u.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PaymentReminderEntity::class,
        TodoTaskEntity::class,
        CategoryEntity::class,
        UserEntity::class
    ],
    version = 10,
    exportSchema = false
)
abstract class Notify2uDatabase : RoomDatabase() {
    abstract fun paymentReminderDao(): PaymentReminderDao
    abstract fun todoDao(): TodoDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: Notify2uDatabase? = null

        fun getInstance(context: Context): Notify2uDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Notify2uDatabase::class.java,
                    "notify2u_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}