package com.example.gestionnairededepenses

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestionnairededepenses.ui.theme.GestionnaireDeDepensesTheme
import com.example.gestionnairededepenses.viewModels.ViewModelTransactions
import com.example.gestionnairededepenses.viewModels.ViewModelUtilisateur
import com.example.gestionnairededepenses.views.Accueil
import com.example.gestionnairededepenses.views.EcranDetails
import com.example.gestionnairededepenses.views.EcranTransactions
import com.example.gestionnairededepenses.views.SeConnecter

class MainActivity : ComponentActivity() {

    private val viewModelTransactions: ViewModelTransactions by viewModels()
    private val viewModelUtilisateur: ViewModelUtilisateur by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                GestionnaireDeDepensesTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        GestionnaireDeDepenses (
                            viewModelTransactions,
                            viewModelUtilisateur,
                            navController = rememberNavController())
                    }
                }
            }
        }
    }
}

@Composable
fun GestionnaireDeDepenses(
    viewModelTransactions: ViewModelTransactions,
    viewModelUtilisateur: ViewModelUtilisateur,
    navController: NavHostController,
) {
    Log.i("System.out", "Lance de Composable MainActivity")
    // Avec l'aide de ChatGPT pour le fonction LaunchedEffect
//    LaunchedEffect(Unit) {
//        if (viewModelUtilisateur.) {
//            navController.navigate("se_connecter")
//        }
//    }

    NavHost(
        navController = navController,
        startDestination = "accueil"
    ) {
        composable("se_connecter") { SeConnecter(viewModelUtilisateur, navController) }
        composable("accueil") { Accueil(viewModelUtilisateur, navController) }
        composable("ecran_transactions") { EcranTransactions(viewModelTransactions, navController) }
        composable("ecran_details/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            taskId?.let { EcranDetails(viewModelTransactions, navController, it.toString()) }
        }
    }
}