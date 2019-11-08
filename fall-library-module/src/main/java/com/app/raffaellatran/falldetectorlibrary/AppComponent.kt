package com.app.raffaellatran.falldetectorlibrary

import android.content.Context
import androidx.room.Room
import com.app.raffaellatran.falldetectorlibrary.data.database.FallDetectorDao
import com.app.raffaellatran.falldetectorlibrary.data.database.FallDetectorDatabase
import com.app.raffaellatran.falldetectorlibrary.data.service.FallDetectorService
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Singleton
@Component(modules = [FallModule::class])
interface AppComponent {
    fun inject(app: FallDetectorService)
    fun inject(fallDetectorLibrary: FallDetectorLibrary)
}

@Module
class FallModule(private val context: Context) {
    @Provides
    @Singleton
    fun provideFallDao(): FallDetectorDao = Room.databaseBuilder(
        context,
        FallDetectorDatabase::class.java, "fallDetectorDatabase"
    ).fallbackToDestructiveMigration()
        .build()
        .fallDao()
}