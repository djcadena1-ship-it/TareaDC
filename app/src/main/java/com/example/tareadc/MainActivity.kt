package com.example.tareadc

//primer avance
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

// ==========================================
// CONFIGURACIÓN DE NAVEGACIÓN
// ==========================================
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "input_screen") {
        composable("input_screen") {
            InputScreen(navController)
        }

        // 2 multiples parametros: ruta con nombre e imc
        composable(
            route = "resultado/{nombre}/{imc}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("imc") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val imc = backStackEntry.arguments?.getFloat("imc") ?: 0f
            ResultScreen(navController, nombre, imc)
        }
    }
}
//segundo avance
// ==========================================
// PANTALLA 1: INGRESO DE DATOS
// ==========================================
@Composable
fun InputScreen(navController: NavHostController) {
    var nombre by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Calculadora de IMC", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = peso,
            onValueChange = { peso = it },
            label = { Text("Peso (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = altura,
            onValueChange = { altura = it },
            label = { Text("Altura (m)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        // 1 validacion y error en color rojo
        if (showError) {
            Text(
                text = "Por favor, ingresa valores válidos (mayores a 0)",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        Button(onClick = {
            val pesoNum = peso.toFloatOrNull()
            val alturaNum = altura.toFloatOrNull()

            // se valida que no sean nulos y sean mayores a 0
            if (pesoNum != null && alturaNum != null && pesoNum > 0 && alturaNum > 0 && nombre.isNotBlank()) {
                showError = false
                val imc = pesoNum / (alturaNum * alturaNum)
                // navegamos enviando los parametros
                navController.navigate("resultado/$nombre/$imc")
            } else {
                showError = true // mostramos el error si los datos son incorrectos
            }
        }) {
            Text("Calcular")
        }
    }
}

// ==========================================
// PANTALLA 2: RESULTADO
// ==========================================
@Composable
fun ResultScreen(navController: NavHostController, nombre: String, imc: Float) {

    // clasificacion del IMC
    val (categoria, colorCategoria) = when {
        imc < 18.5f -> "Bajo peso" to Color.Red
        imc in 18.5f..24.9f -> "Peso normal" to Color.Green
        imc in 25.0f..29.9f -> "Sobrepeso" to Color(0xFFFFA500) // Naranja
        else -> "Obesidad" to Color.Red
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Hola $nombre, tu resultado es:", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))

        // mostrar IMC con 1 decimal
        Text(
            text = String.format(java.util.Locale.US, "%.1f", imc),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        // 3 interfaz dinamica (color cambia segun el resultado)
        Text(
            text = categoria,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colorCategoria
        )

        Spacer(modifier = Modifier.height(30.dp))

        // boton Volver con popBackStack()
        Button(onClick = {
            navController.popBackStack()
        }) {
            Text("Volver")
        }
    }
}




// vista previa en local


@Preview(showBackground = true, name = "Pantalla de Ingreso")
@Composable
fun PreviewInputScreen() {
    MaterialTheme {
        // Usamos un controlador de navegación falso solo para que la vista previa funcione
        InputScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, name = "Pantalla de Resultado (Normal)")
@Composable
fun PreviewResultScreenNormal() {
    MaterialTheme {
        ResultScreen(navController = rememberNavController(), nombre = "Juan", imc = 22.5f)
    }
}

@Preview(showBackground = true, name = "Pantalla de Resultado (Obesidad)")
@Composable
fun PreviewResultScreenObeso() {
    MaterialTheme {
        ResultScreen(navController = rememberNavController(), nombre = "María", imc = 31.0f)
    }
}