package com.app.raffaellatran.falldetectorlibrary

import android.content.Context
import androidx.room.Room
import com.app.raffaellatran.falldetectorlibrary.data.database.FallDao
import com.app.raffaellatran.falldetectorlibrary.data.database.FallDatabase
import com.app.raffaellatran.falldetectorlibrary.data.service.FallService
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Singleton
@Component(modules = [FallModule::class])
interface AppComponent {
    fun inject(app: FallService)
    fun inject(fallLibrary: FallLibrary)
}

@Module
class FallModule(private val context: Context) {
    @Provides
    @Singleton
    fun provideFallDao(): FallDao = Room.databaseBuilder(
        context,
        FallDatabase::class.java, "fallDatabase"
    ).fallbackToDestructiveMigration()
        .build()
        .fallDao()
}