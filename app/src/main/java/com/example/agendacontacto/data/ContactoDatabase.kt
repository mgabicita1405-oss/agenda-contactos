package com.example.agendacontacto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Contacto::class], version = 1, exportSchema = false)
abstract class ContactoDatabase : RoomDatabase() {

    abstract fun contactoDao(): ContactoDao

    companion object {
        @Volatile
        private var INSTANCIA: ContactoDatabase? = null

        fun getInstance(context: Context): ContactoDatabase {
            return INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    ContactoDatabase::class.java,
                    "agenda_contactos.db"
                ).build().also { INSTANCIA = it }
            }
        }
    }
}
