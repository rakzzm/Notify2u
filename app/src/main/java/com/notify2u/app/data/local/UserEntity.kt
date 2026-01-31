package com.notify2u.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)
