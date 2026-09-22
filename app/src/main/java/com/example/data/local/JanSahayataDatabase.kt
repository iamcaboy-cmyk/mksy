package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AdminDao
import com.example.data.local.dao.ApplicationDao
import com.example.data.local.dao.FeedbackDao
import com.example.data.local.dao.NotificationDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.AdminEntity
import com.example.data.local.entity.ApplicationEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BankDetailsEntity
import com.example.data.local.entity.BeneficiaryEntity
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.FeedbackEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.NotificationPreferencesEntity
import com.example.data.local.entity.StatusHistoryEntity
import com.example.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ApplicationEntity::class,
        BeneficiaryEntity::class,
        BankDetailsEntity::class,
        DocumentEntity::class,
        StatusHistoryEntity::class,
        AdminEntity::class,
        AuditLogEntity::class,
        NotificationEntity::class,
        NotificationPreferencesEntity::class,
        FeedbackEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class JanSahayataDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun adminDao(): AdminDao
    abstract fun notificationDao(): NotificationDao
    abstract fun feedbackDao(): FeedbackDao

    companion object {
        @Volatile
        private var INSTANCE: JanSahayataDatabase? = null

        fun getInstance(context: Context): JanSahayataDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JanSahayataDatabase::class.java,
                    "jansahayata_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
