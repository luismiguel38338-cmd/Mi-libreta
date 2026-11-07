package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ClienteDao
import com.example.data.dao.ConfigDao
import com.example.data.dao.MovimientoDao
import com.example.data.model.Cliente
import com.example.data.model.Movimiento
import com.example.data.model.NegocioConfig
import com.example.data.model.TipoMovimiento

class Converters {
    @TypeConverter
    fun fromTipoMovimiento(value: TipoMovimiento): String = value.name

    @TypeConverter
    fun toTipoMovimiento(value: String): TipoMovimiento = try {
        TipoMovimiento.valueOf(value)
    } catch (e: Exception) {
        TipoMovimiento.DEUDA
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE clientes ADD COLUMN limiteCredito REAL NOT NULL DEFAULT 0.0")
        db.execSQL("ALTER TABLE clientes ADD COLUMN fechaPromesaPago INTEGER DEFAULT NULL")
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS configuracion_negocio (
                id INTEGER PRIMARY KEY NOT NULL,
                nombreNegocio TEXT NOT NULL,
                propietario TEXT NOT NULL,
                telefonoNegocio TEXT NOT NULL,
                direccion TEXT NOT NULL,
                mensajeWhatsAppPlantilla TEXT NOT NULL,
                monedaSimbolo TEXT NOT NULL
            )
        """.trimIndent())
    }
}

@Database(
    entities = [Cliente::class, Movimiento::class, NegocioConfig::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun movimientoDao(): MovimientoDao
    abstract fun configDao(): ConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mi_libreta_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
