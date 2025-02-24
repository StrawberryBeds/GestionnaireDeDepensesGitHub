package com.example.gestionnairededepenses.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gestionnairededepenses.viewModels.ViewModelUtilisateur

@Composable
fun Accueil(
    viewModelUtilisateur: ViewModelUtilisateur, navController: NavHostController
) {

    var utilisateur = viewModelUtilisateur.utilisateur

    Scaffold() { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Bienvenue ${utilisateur.nomEtPrenom}!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.padding(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { navController.navigate("ecran_transactions") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                ) {
                    Text("Mes transactions")
                }
            }
            Spacer(
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        viewModelUtilisateur.deconecterUtilisateur(nomUtilisateur = utilisateur.nomUtilisateur)
                        navController.navigate("se_connecter")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp),
                    colors = ButtonColors(
                        containerColor = Red,
                        contentColor = White,
                        disabledContainerColor = Red,
                        disabledContentColor = White
                    )
                ) {
                    Text("Se déconnecter ")
                }
            }
        }
    }
}