package com.example.tabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ═══════════════════════════════════════════════════════════════════
 * COMPORTAMIENTO DE LISTAS EN TABS NO VISIBLES
 * ═══════════════════════════════════════════════════════════════════
 *
 * Pregunta: ¿Qué pasa con una LazyColumn que está en un Tab no visible?
 *
 * Respuesta: Depende de cómo implementes los tabs.
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
                    // Puedes cambiar entre diferentes ejemplos
                    EjemploTabsConExplicacion()
                }
            }
        }
    }
}

data class Fruta(
    val id: Int,
    val nombre: String,
    val color: String
)

/**
 * ═══════════════════════════════════════════════════════════════════
 * EJEMPLO 1: COMPORTAMIENTO ESTÁNDAR (Solo compone lo visible)
 * ═══════════════════════════════════════════════════════════════════
 */
@Composable
fun EjemploTabsEstandar() {
    var tabSeleccionado by remember { mutableStateOf(0) }
    val tabs = listOf("Tab 1", "Tab 2")

    Column(modifier = Modifier.fillMaxSize()) {
        // TabRow
        TabRow(selectedTabIndex = tabSeleccionado) {
            tabs.forEachIndexed { index, titulo ->
                Tab(
                    selected = tabSeleccionado == index,
                    onClick = { tabSeleccionado = index },
                    text = { Text(titulo) }
                )
            }
        }

        // Contenido de los tabs
        when (tabSeleccionado) {
            0 -> ContenidoTab1Estandar()
            1 -> ContenidoTab2ConLista()
        }
    }
}

@Composable
fun ContenidoTab1Estandar() {
    // Este Composable se ejecuta cuando Tab 1 es visible
    LaunchedEffect(Unit) {
        println("🔵 Tab 1 SE COMPUSO")
    }

    DisposableEffect(Unit) {
        onDispose {
            println("🔴 Tab 1 SE DESTRUYÓ")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBDEFB)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Contenido del Tab 1",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ContenidoTab2ConLista() {
    // ✅ Este Composable SOLO se ejecuta cuando Tab 2 es visible
    LaunchedEffect(Unit) {
        println("🔵 Tab 2 SE COMPUSO (Lista cargada)")
    }

    DisposableEffect(Unit) {
        onDispose {
            println("🔴 Tab 2 SE DESTRUYÓ (Lista descargada)")
        }
    }

    // La lista se crea SOLO cuando el tab es visible
    val frutas = remember {
        println("🍎 CREANDO LISTA DE FRUTAS")
        listOf(
            Fruta(1, "Manzana", "Roja"),
            Fruta(2, "Plátano", "Amarillo"),
            Fruta(3, "Naranja", "Naranja"),
            Fruta(4, "Uva", "Morada"),
            Fruta(5, "Fresa", "Roja")
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Lista de Frutas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(frutas) { fruta ->
                // Cada item se renderiza solo cuando es visible
                println("🎨 Renderizando fruta: ${fruta.nombre}")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFE0B2)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = fruta.nombre,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = fruta.color,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}


/**
 * ═══════════════════════════════════════════════════════════════════
 * EJEMPLO 2: PROBLEMA - Tabs siempre compuestos (INEFICIENTE)
 * ═══════════════════════════════════════════════════════════════════
 */
@Composable
fun EjemploTabsProblematico() {
    var tabSeleccionado by remember { mutableStateOf(0) }
    val tabs = listOf("Tab 1", "Tab 2")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabSeleccionado) {
            tabs.forEachIndexed { index, titulo ->
                Tab(
                    selected = tabSeleccionado == index,
                    onClick = { tabSeleccionado = index },
                    text = { Text(titulo) }
                )
            }
        }

        // ❌ PROBLEMA: Ambos tabs siempre compuestos
        Box(modifier = Modifier.fillMaxSize()) {
            // Tab 1 siempre compuesto (solo oculto con alpha)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (tabSeleccionado == 0) Modifier else Modifier.alpha(0f)
                    )
            ) {
                ContenidoTab1ConLog("Tab 1 (Siempre compuesto)")
            }

            // Tab 2 siempre compuesto (solo oculto con alpha)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (tabSeleccionado == 1) Modifier else Modifier.alpha(0f)
                    )
            ) {
                ContenidoTab2ConListaLog("Tab 2 (Siempre compuesto)")
            }
        }
    }
}

@Composable
fun ContenidoTab1ConLog(mensaje: String) {
    LaunchedEffect(Unit) {
        println("⚠️ $mensaje SE COMPUSO (aunque no sea visible)")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBDEFB)),
        contentAlignment = Alignment.Center
    ) {
        Text(mensaje, fontSize = 20.sp)
    }
}

@Composable
fun ContenidoTab2ConListaLog(mensaje: String) {
    LaunchedEffect(Unit) {
        println("⚠️ $mensaje SE COMPUSO (aunque no sea visible)")
        println("⚠️ Lista cargada innecesariamente")
    }

    val frutas = remember {
        println("⚠️ DESPERDICIO: Creando lista aunque Tab no sea visible")
        listOf(
            Fruta(1, "Manzana", "Roja"),
            Fruta(2, "Plátano", "Amarillo"),
            Fruta(3, "Naranja", "Naranja")
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(frutas) { fruta ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = fruta.nombre,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}


/**
 * ═══════════════════════════════════════════════════════════════════
 * EJEMPLO 3: CARGA DIFERIDA (Lazy Loading)
 * ═══════════════════════════════════════════════════════════════════
 */
@Composable
fun EjemploTabsConCargaDiferida() {
    var tabSeleccionado by remember { mutableStateOf(0) }
    val tabs = listOf("Tab 1", "Tab 2")

    // Estado para rastrear qué tabs se han visitado
    val tabsVisitados = remember { mutableStateSetOf(0) }

    // Marcar tab como visitado cuando se selecciona
    LaunchedEffect(tabSeleccionado) {
        tabsVisitados.add(tabSeleccionado)
        println("✅ Tab $tabSeleccionado marcado como visitado")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabSeleccionado) {
            tabs.forEachIndexed { index, titulo ->
                Tab(
                    selected = tabSeleccionado == index,
                    onClick = { tabSeleccionado = index },
                    text = { Text(titulo) }
                )
            }
        }

        // Solo cargar contenido cuando el tab ha sido visitado
        when (tabSeleccionado) {
            0 -> {
                if (tabsVisitados.contains(0)) {
                    ContenidoTab1ConIndicador()
                }
            }
            1 -> {
                if (tabsVisitados.contains(1)) {
                    ContenidoTab2ConCargaDiferida()
                } else {
                    // Mostrar indicador de carga
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun ContenidoTab1ConIndicador() {
    LaunchedEffect(Unit) {
        println("📱 Tab 1 cargado")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBDEFB)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Tab 1", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Cambia a Tab 2 para cargar la lista", color = Color.Gray)
        }
    }
}

@Composable
fun ContenidoTab2ConCargaDiferida() {
    // Simular carga de datos
    var frutasCargadas by remember { mutableStateOf<List<Fruta>?>(null) }

    LaunchedEffect(Unit) {
        println("⏳ Iniciando carga de frutas...")
        // Simular delay de red
        kotlinx.coroutines.delay(1000)

        frutasCargadas = listOf(
            Fruta(1, "Manzana", "Roja"),
            Fruta(2, "Plátano", "Amarillo"),
            Fruta(3, "Naranja", "Naranja"),
            Fruta(4, "Uva", "Morada"),
            Fruta(5, "Fresa", "Roja"),
            Fruta(6, "Sandía", "Verde"),
            Fruta(7, "Melón", "Naranja"),
            Fruta(8, "Kiwi", "Verde")
        )

        println("✅ Frutas cargadas exitosamente")
    }

    if (frutasCargadas == null) {
        // Mostrar loading
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Cargando frutas...", color = Color.Gray)
            }
        }
    } else {
        // Mostrar lista
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Lista de Frutas (Carga Diferida)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(frutasCargadas!!) { fruta ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFE0B2)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = fruta.nombre,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = fruta.color,
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * ═══════════════════════════════════════════════════════════════════
 * EJEMPLO PRINCIPAL: CON EXPLICACIÓN VISUAL
 * ═══════════════════════════════════════════════════════════════════
 */
@Composable
fun EjemploTabsConExplicacion() {
    var tabSeleccionado by remember { mutableStateOf(0) }
    var contadorComposicionesTab1 by remember { mutableStateOf(0) }
    var contadorComposicionesTab2 by remember { mutableStateOf(0) }

    val tabs = listOf("Tab 1", "Tab 2")

    Column(modifier = Modifier.fillMaxSize()) {
        // Información en la parte superior
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📊 Comportamiento de Tabs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Tab 1 composiciones: $contadorComposicionesTab1",
                    fontSize = 14.sp
                )
                Text(
                    text = "Tab 2 composiciones: $contadorComposicionesTab2",
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "✅ CORRECTO: Solo el tab visible se compone",
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50)
                )
            }
        }

        // TabRow
        TabRow(selectedTabIndex = tabSeleccionado) {
            tabs.forEachIndexed { index, titulo ->
                Tab(
                    selected = tabSeleccionado == index,
                    onClick = { tabSeleccionado = index },
                    text = { Text(titulo) }
                )
            }
        }

        // Contenido según tab seleccionado
        when (tabSeleccionado) {
            0 -> {
                LaunchedEffect(Unit) {
                    contadorComposicionesTab1++
                }

                ContenidoTab1Explicado(contadorComposicionesTab1)
            }
            1 -> {
                LaunchedEffect(Unit) {
                    contadorComposicionesTab2++
                }

                ContenidoTab2Explicado(contadorComposicionesTab2)
            }
        }
    }
}

@Composable
fun ContenidoTab1Explicado(contador: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBDEFB))
            .padding(16.dp)
    ) {
        Text(
            text = "Tab 1 - Contenido Simple",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🔵 Este tab se compuso $contador vez/veces", fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Cuando cambias a Tab 2, este composable SE DESTRUYE y se libera memoria.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFE0B2)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("💡 Cambiar al Tab 2", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Haz clic en Tab 2 para ver cómo se carga la lista SOLO cuando es visible.",
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ContenidoTab2Explicado(contador: Int) {
    val frutas = remember {
        println("🍎 Lista de frutas creada (contador: $contador)")
        listOf(
            Fruta(1, "🍎 Manzana", "Roja"),
            Fruta(2, "🍌 Plátano", "Amarillo"),
            Fruta(3, "🍊 Naranja", "Naranja"),
            Fruta(4, "🍇 Uva", "Morada"),
            Fruta(5, "🍓 Fresa", "Roja"),
            Fruta(6, "🍉 Sandía", "Verde"),
            Fruta(7, "🍈 Melón", "Naranja"),
            Fruta(8, "🥝 Kiwi", "Verde"),
            Fruta(9, "🍑 Durazno", "Naranja"),
            Fruta(10, "🍐 Pera", "Verde")
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFC8E6C9)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "✅ Lista cargada SOLO cuando tab es visible",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Composiciones: $contador",
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "La lista se creó cuando hiciste clic en este tab, NO antes.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(frutas, key = { it.id }) { fruta ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFE0B2)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fruta.nombre,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = fruta.color,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}




