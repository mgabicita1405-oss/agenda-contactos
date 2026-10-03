package com.example.agendacontacto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agendacontacto.data.Contacto
import com.example.agendacontacto.ui.theme.AgendaContactoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgendaContactoTheme(dynamicColor = false) {
                PantallaContactos()
            }
        }
    }
}

@Composable
fun PantallaContactos(viewModel: ContactoViewModel = viewModel(factory = ContactoViewModel.Factory)) {

    val contactos by viewModel.contactos.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()

    // Estado del formulario (agregar / editar)
    var mostrarFormulario by remember { mutableStateOf(false) }
    var contactoEditando by remember { mutableStateOf<Contacto?>(null) }

    // Estado del dialogo de confirmacion de eliminacion
    var contactoAEliminar by remember { mutableStateOf<Contacto?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    contactoEditando = null
                    mostrarFormulario = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar contacto")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Encabezado
            Text(
                text = "Mis Contactos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)
            )
            Text(
                text = "Guarda y administra tus contactos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            )

            // Buscador
            OutlinedTextField(
                value = busqueda,
                onValueChange = { viewModel.onBusquedaCambio(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = { Text("Buscar contacto...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (contactos.isEmpty()) {
                // Estado vacio
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (busqueda.isBlank()) "No hay contactos aún" else "Sin resultados",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (busqueda.isBlank()) {
                            Text(
                                text = "Toca + para agregar el primero",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 20.dp, end = 20.dp, bottom = 90.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(contactos, key = { it.id }) { contacto ->
                        TarjetaContacto(
                            contacto = contacto,
                            onEditar = {
                                contactoEditando = contacto
                                mostrarFormulario = true
                            },
                            onEliminar = { contactoAEliminar = contacto }
                        )
                    }
                }
            }
        }
    }

    // Formulario agregar / editar
    if (mostrarFormulario) {
        FormularioContacto(
            contactoExistente = contactoEditando,
            onCerrar = {
                mostrarFormulario = false
                contactoEditando = null
            },
            onGuardar = { nombre, telefono, email ->
                if (contactoEditando == null) {
                    viewModel.agregarContacto(Contacto(nombre = nombre, telefono = telefono, email = email))
                } else {
                    viewModel.editarContacto(
                        contactoEditando!!.copy(nombre = nombre, telefono = telefono, email = email)
                    )
                }
                mostrarFormulario = false
                contactoEditando = null
            }
        )
    }

    // Confirmacion de eliminacion
    contactoAEliminar?.let { contacto ->
        AlertDialog(
            onDismissRequest = { contactoAEliminar = null },
            title = { Text("Eliminar contacto") },
            text = { Text("¿Deseas eliminar a ${contacto.nombre}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarContacto(contacto)
                        contactoAEliminar = null
                    }
                ) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { contactoAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun TarjetaContacto(
    contacto: Contacto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar con la inicial
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contacto.nombre.take(1).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contacto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = contacto.telefono,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (contacto.email.isNotBlank()) {
                    Text(
                        text = contacto.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onEditar) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun FormularioContacto(
    contactoExistente: Contacto?,
    onCerrar: () -> Unit,
    onGuardar: (nombre: String, telefono: String, email: String) -> Unit
) {
    var nombre by remember { mutableStateOf(contactoExistente?.nombre ?: "") }
    var telefono by remember { mutableStateOf(contactoExistente?.telefono ?: "") }
    var email by remember { mutableStateOf(contactoExistente?.email ?: "") }

    // Mensajes de error (validacion)
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }
    var errorEmail by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text(if (contactoExistente == null) "Nuevo contacto" else "Editar contacto")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        errorNombre = null
                    },
                    label = { Text("Nombre") },
                    singleLine = true,
                    isError = errorNombre != null,
                    supportingText = { errorNombre?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = telefono,
                    onValueChange = {
                        telefono = it
                        errorTelefono = null
                    },
                    label = { Text("Teléfono") },
                    singleLine = true,
                    isError = errorTelefono != null,
                    supportingText = { errorTelefono?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorEmail = null
                    },
                    label = { Text("Correo (opcional)") },
                    singleLine = true,
                    isError = errorEmail != null,
                    supportingText = { errorEmail?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // Validaciones
                val nombreOk = nombre.trim().length >= 2
                val telefonoOk = telefono.trim().length >= 8 && telefono.trim().all { it.isDigit() || it == '+' || it == ' ' }
                val emailOk = email.isBlank() || android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

                errorNombre = if (!nombreOk) "Ingresa un nombre válido (mín. 2 letras)" else null
                errorTelefono = if (!telefonoOk) "Ingresa un teléfono válido (mín. 8 dígitos)" else null
                errorEmail = if (!emailOk) "Ingresa un correo válido" else null

                if (nombreOk && telefonoOk && emailOk) {
                    onGuardar(nombre.trim(), telefono.trim(), email.trim())
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) { Text("Cancelar") }
        }
    )
}
