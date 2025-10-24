package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * ═══════════════════════════════════════════════════════════
 * MAIN ACTIVITY - Punto de entrada de la aplicación
 * ═══════════════════════════════════════════════════════════
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ListSample4()
                }
            }
        }
    }
}

/**
 * DATA CLASS: Representa un curso
 */
data class Curso(
    val id: Int,
    val nombre: String,
    val descripcion: String
)

/**
 * COMPOSABLE PRINCIPAL: Lista modificable de cursos
 */
@Composable
fun ListSample4(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Estados para los campos de texto
    var idCurso by remember { mutableStateOf("") }
    var nombreCurso by remember { mutableStateOf("") }

    // ✅ SOLUCIÓN: Lista observable con mutableStateListOf
    val cursos = remember {
        mutableStateListOf<Curso>().apply {
            for (i in 1..100) {
                add(Curso(
                    id = i,
                    nombre = "Nombre $i",
                    descripcion = "Descripcion $i"
                ))
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Modificar Cursos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Card con formulario
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Campo: ID del curso
                OutlinedTextField(
                    value = idCurso,
                    onValueChange = { idCurso = it },
                    label = { Text("ID del curso (1-100)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                // Campo: Nuevo nombre
                OutlinedTextField(
                    value = nombreCurso,
                    onValueChange = { nombreCurso = it },
                    label = { Text("Nuevo nombre del curso") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón: Modificar
                    Button(
                        onClick = {
                            // Validar ID
                            val id = idCurso.toIntOrNull()
                            if (id == null || id < 1 || id > cursos.size) {
                                Toast.makeText(
                                    context,
                                    "ID inválido (debe ser 1-${cursos.size})",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            // Validar nombre
                            if (nombreCurso.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "El nombre no puede estar vacío",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            // ✅ MODIFICAR: Actualiza el curso
                            val index = id - 1
                            val cursoActual = cursos[index]
                            cursos[index] = cursoActual.copy(nombre = nombreCurso)

                            // Mensaje de éxito
                            Toast.makeText(
                                context,
                                "✅ Curso $id modificado exitosamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Limpiar campos
                            nombreCurso = ""
                            idCurso = ""
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Modificar")
                    }

                    // Botón: Ver en Logcat
                    OutlinedButton(
                        onClick = {
                            android.util.Log.d("ComposeTest", "═══════ LISTA DE CURSOS ═══════")
                            cursos.forEach { curso ->
                                android.util.Log.d(
                                    "ComposeTest",
                                    "Item: ${curso.id} - ${curso.nombre}"
                                )
                            }
                            android.util.Log.d("ComposeTest", "══════════════════════════════")

                            Toast.makeText(
                                context,
                                "Ver lista en Logcat",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ver Log")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Información
        Text(
            text = "Total de cursos: ${cursos.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(Modifier.height(8.dp))

        // ✅ LazyColumn se actualiza automáticamente
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = cursos,
                key = { it.id }  // ✅ Key importante para rendimiento
            ) { curso ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE57373)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ID: ${curso.id}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = curso.nombre,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = curso.descripcion,
                            color = Color(0xFFFFCDD2),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}