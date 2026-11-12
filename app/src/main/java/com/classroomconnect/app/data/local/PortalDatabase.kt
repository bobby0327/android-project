package com.classroomconnect.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.classroomconnect.app.data.model.AssignmentEntity
import com.classroomconnect.app.data.model.ClassEntity
import com.classroomconnect.app.data.model.GradeEntity
import com.classroomconnect.app.data.model.MessageEntity
import com.classroomconnect.app.data.model.SubjectEntity
import com.classroomconnect.app.data.model.SubmissionEntity
import com.classroomconnect.app.data.model.UserEntity
import com.classroomconnect.app.data.model.UserRole

class PortalConverters {
    @TypeConverter fun fromRole(role: UserRole): String = role.name
    @TypeConverter fun toRole(value: String): UserRole = UserRole.valueOf(value)
}

@Database(
    entities = [ClassEntity::class, SubjectEntity::class, AssignmentEntity::class, SubmissionEntity::class, GradeEntity::class, MessageEntity::class, UserEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(PortalConverters::class)
abstract class PortalDatabase : RoomDatabase() {
    abstract fun portalDao(): PortalDao
}
