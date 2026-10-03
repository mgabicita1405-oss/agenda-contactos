package com.example.agendacontacto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.agendacontacto.data.Contacto
import com.example.agendacontacto.data.ContactoDao
import com.example.agendacontacto.data.ContactoDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactoViewModel(private val dao: ContactoDao) : ViewModel() {

    // Texto del buscador
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    // Lista de contactos reactiva: se actualiza sola al insertar/editar/borrar
    val contactos: StateFlow<List<Contacto>> = combine(dao.obtenerTodos(), _busqueda) { lista, texto ->
        if (texto.isBlank()) lista
        else lista.filter {
            it.nombre.contains(texto, ignoreCase = true) ||
                    it.telefono.contains(texto, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onBusquedaCambio(texto: String) {
        _busqueda.value = texto
    }

    fun agregarContacto(contacto: Contacto) {
        viewModelScope.launch { dao.insertar(contacto) }
    }

    fun editarContacto(contacto: Contacto) {
        viewModelScope.launch { dao.actualizar(contacto) }
    }

    fun eliminarContacto(contacto: Contacto) {
        viewModelScope.launch { dao.eliminar(contacto) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                ContactoViewModel(
                    ContactoDatabase.getInstance(app.applicationContext).contactoDao()
                )
            }
        }
    }
}
