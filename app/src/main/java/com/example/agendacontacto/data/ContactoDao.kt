package com.example.agendacontacto.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {

    @Query("SELECT * FROM contactos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Contacto>>

    @Query("SELECT * FROM contactos WHERE nombre LIKE '%' || :busqueda || '%' OR telefono LIKE '%' || :busqueda || '%' ORDER BY nombre ASC")
    fun buscar(busqueda: String): Flow<List<Contacto>>

    @Insert
    suspend fun insertar(contacto: Contacto)

    @Update
    suspend fun actualizar(contacto: Contacto)

    @Delete
    suspend fun eliminar(contacto: Contacto)
}
