package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.NegocioConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {
    @Query("SELECT * FROM configuracion_negocio WHERE id = 1")
    fun getConfig(): Flow<NegocioConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: NegocioConfig)
}
