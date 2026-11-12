package com.classroomconnect.app.di

import android.content.Context
import androidx.room.Room
import com.classroomconnect.app.data.local.PortalDao
import com.classroomconnect.app.data.local.PortalDatabase
import com.classroomconnect.app.data.remote.DriveGateway
import com.classroomconnect.app.data.remote.GoogleDriveGateway
import com.classroomconnect.app.data.remote.GoogleSheetsGateway
import com.classroomconnect.app.data.remote.SheetsApiGateway
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PortalDatabase = Room.databaseBuilder(context, PortalDatabase::class.java, "classroom-connect.db").build()

    @Provides fun provideDao(database: PortalDatabase): PortalDao = database.portalDao()
    @Provides @Singleton fun provideSheets(): GoogleSheetsGateway = SheetsApiGateway()
    @Provides @Singleton fun provideDrive(): DriveGateway = GoogleDriveGateway()
    @Provides @Singleton fun provideMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
    @Provides @Singleton fun provideAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    @Provides @Singleton fun provideRemoteConfig(): FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    @Provides @Singleton fun provideAnalytics(@ApplicationContext context: Context): FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    @Provides @Singleton fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    @Provides @Singleton fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}
