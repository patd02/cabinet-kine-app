package com.example.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.time.LocalDate

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE patients ADD COLUMN phoneNumber TEXT NOT NULL DEFAULT ''")
    }
}

@Database(entities = [Patient::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PatientDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao

    companion object {
        @Volatile
        private var Instance: PatientDatabase? = null

        fun getDatabase(context: Context): PatientDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    PatientDatabase::class.java,
                    "patient_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { Instance = it }
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromSexe(value: String): Sexe {
        return enumValueOf(value)
    }

    @TypeConverter
    fun sexeToString(sexe: Sexe): String {
        return sexe.name
    }
} 