package com.example.gestionnairededepenses

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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestionnairededepenses.ui.theme.GestionnaireDeDepensesTheme
import com.example.gestionnairededepenses.viewModels.ViewModelTransactions
import com.example.gestionnairededepenses.viewModels.ViewModelUtilisateur
import com.example.gestionnairededepenses.views.Accueil
import com.example.gestionnairededepenses.views.EcranDetails
import com.example.gestionnairededepenses.views.EcranTransactions
import com.example.gestionnairededepenses.views.SeConnecter


class MainActivity : ComponentActivity() {

    // Lien au GitHub : https://github.com/StrawberryBeds/GestionnaireDeDepensesGitHub

    // Version remise : 1e5a05d8141be25fb53ab63f11f3e9b64582a269

    // Lien au version remise : https://github.com/StrawberryBeds/GestionnaireDeDepensesGitHub/commit/1e5a05d8141be25fb53ab63f11f3e9b64582a269

    private val viewModelUtilisateur: ViewModelUtilisateur by viewModels()
    private val viewModelTransactions: ViewModelTransactions by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                GestionnaireDeDepensesTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        GestionnaireDeDepenses(
                            viewModelUtilisateur,
                            viewModelTransactions,
                            navController = rememberNavController()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GestionnaireDeDepenses(
    viewModelUtilisateur: ViewModelUtilisateur,
    viewModelTransactions: ViewModelTransactions,
    navController: NavHostController,
) {
    Log.i("System.out", "Lance de Composable MainActivity")

    // Avec l'aide de ChatGPT pour le fonction LaunchedEffect
    LaunchedEffect(Unit) {
        if (viewModelUtilisateur.utilisateur.estVerifie) {
            navController.navigate("accueil")
        }
    }

    NavHost(
        navController = navController,
        startDestination = "se_connecter"
    ) {
        composable("se_connecter") { SeConnecter(viewModelUtilisateur, navController) }
        composable("accueil") {
            Accueil(
                viewModelUtilisateur,
                onNavigateToSeConnecter = {
                    navController.navigate("se_connecter") {
                        popUpTo("se_connecter")
                    }
                },
                onNavigateToTransactions = {
                    navController.navigate("ecran_transactions") {
                        popUpTo("ecran_tranactions") {
                            inclusive = false
                            saveState = false
                        }
                        restoreState = false
                    }
                }
            )
        }

        composable("ecran_transactions") {
            EcranTransactions(
//                viewModelUtilisateur,
                viewModelTransactions,
                onNavigateToDetails = { idTransaction ->
                    navController.navigate("ecran_details/$idTransaction") {
                        popUpTo("accueil") {
                            inclusive = false
                            saveState = false
                        }
                        restoreState = false
                    }
                }
            )
        }

        composable(
            "ecran_details/{idTransaction}",
            arguments = listOf(navArgument("idTransaction") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val idTransaction = backStackEntry.arguments?.getString("idTransaction")
            idTransaction?.let {
                EcranDetails(
//                    viewModelUtilisateur,
                    viewModelTransactions,
                    idTransaction,
                    onBackToTransactions = {
                        navController.navigate("ecran_transactions") {
                            popUpTo("ecran_transactions") {
                                inclusive = true
                                saveState = true
                            }
                            restoreState = true
                        }
                    },
                )
            }
        }
    }
}

