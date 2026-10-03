package com.example.agendacontacto

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agendacontacto.data.Contacto
import com.example.agendacontacto.ui.theme.AgendaContactoTheme
import com.example.agendacontacto.ui.theme.TealAcento
import com.example.agendacontacto.ui.theme.VioletaPrimario
import com.example.agendacontacto.ui.theme.VioletaSecundario

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgendaContactoTheme(dynamicColor = false) {
                AgendaApp()
            }
        }
    }
}

// ===== Navegacion: bienvenida (solo primera vez) -> sistema =====
@Composable
fun AgendaApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("agenda_prefs", Context.MODE_PRIVATE) }
    var bienvenidaVista by remember { mutableStateOf(prefs.getBoolean("bienvenida_vista", false)) }

    if (!bienvenidaVista) {
        PantallaBienvenida(
            onEntrar = {
                prefs.edit().putBoolean("bienvenida_vista", true).apply()
                bienvenidaVista = true
            }
        )
    } else {
        PantallaContactos()
    }
}

@Composable
fun PantallaContactos(viewModel: ContactoViewModel = viewModel(factory = ContactoViewModel.Factory)) {

    val contactos by viewModel.contactos.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()

    var mostrarFormulario by remember { mutableStateOf(false) }
    var contactoEditando by remember { mutableStateOf<Contacto?>(null) }
    var contactoAEliminar by remember { mutableStateOf<Contacto?>(null) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    contactoEditando = null
                    mostrarFormulario = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar contacto")
                Spacer(modifier = Modifier.size(6.dp))
                Text("Agregar", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ===== Encabezado con degradado =====
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(VioletaPrimario, VioletaSecundario, TealAcento)
                            )
                        )
                        .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 22.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Mis Contactos",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (contactos.isEmpty())
                                    "Guarda y administra tus contactos"
                                else
                                    "${contactos.size} contacto${if (contactos.size == 1) "" else "s"} guardado${if (contactos.size == 1) "" else "s"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Contacts,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // ===== Buscador =====
            OutlinedTextField(
                value = busqueda,
                onValueChange = { viewModel.onBusquedaCambio(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                placeholder = { Text("Buscar contacto...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            )

            if (contactos.isEmpty()) {
                // ===== Estado vacio =====
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Contacts,
                                contentDescription = null,
                                modifier = Modifier.size(42.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (busqueda.isBlank()) "No hay contactos aun" else "Sin resultados para: $busqueda",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (busqueda.isBlank()) {
                            Text(
                                text = "Toca el boton + para agregar el primero",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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

    // ===== Formulario agregar / editar =====
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

    // ===== Confirmacion de eliminacion =====
    contactoAEliminar?.let { contacto ->
        AlertDialog(
            onDismissRequest = { contactoAEliminar = null },
            shape = RoundedCornerShape(22.dp),
            title = { Text("Eliminar contacto") },
            text = { Text("Deseas eliminar a ${contacto.nombre}? Esta accion no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarContacto(contacto)
                        contactoAEliminar = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { contactoAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

// ===== Colores de avatar segun el contacto =====
private data class ParColor(val inicio: Color, val fin: Color)

private val coloresAvatar = listOf(
    ParColor(Color(0xFF6C5CE7), Color(0xFF8E7CF3)),
    ParColor(Color(0xFF0984E3), Color(0xFF74B9FF)),
    ParColor(Color(0xFF00B8A9), Color(0xFF55EFC4)),
    ParColor(Color(0xFFE17055), Color(0xFFFDCB6E)),
    ParColor(Color(0xFFD63031), Color(0xFFFF7675)),
    ParColor(Color(0xFF8E44AD), Color(0xFFC39BD3))
)

private fun colorAvatar(id: Int): ParColor =
    coloresAvatar[id % coloresAvatar.size]

@Composable
fun TarjetaContacto(
    contacto: Contacto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar con degradado segun el contacto
            val avatar = colorAvatar(contacto.id)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(avatar.inicio, avatar.fin))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contacto.nombre.trim().take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contacto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
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

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }
    var errorEmail by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCerrar,
        shape = RoundedCornerShape(22.dp),
        title = {
            Text(
                if (contactoExistente == null) "Nuevo contacto" else "Editar contacto",
                fontWeight = FontWeight.Bold
            )
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
                    label = { Text("Telefono") },
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
                val nombreOk = nombre.trim().length >= 2
                val telefonoOk = telefono.trim().length >= 8 && telefono.trim().all { it.isDigit() || it == '+' || it == ' ' }
                val emailOk = email.isBlank() || android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

                errorNombre = if (!nombreOk) "Ingresa un nombre valido (min. 2 letras)" else null
                errorTelefono = if (!telefonoOk) "Ingresa un telefono valido (min. 8 digitos)" else null
                errorEmail = if (!emailOk) "Ingresa un correo valido" else null

                if (nombreOk && telefonoOk && emailOk) {
                    onGuardar(nombre.trim(), telefono.trim(), email.trim())
                }
            }) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) { Text("Cancelar") }
        }
    )
}
