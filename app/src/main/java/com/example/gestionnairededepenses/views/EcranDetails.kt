package com.example.gestionnairededepenses.views

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gestionnairededepenses.classes.Categories
import com.example.gestionnairededepenses.viewModels.ViewModelTransactions

@Composable
fun EcranDetails(
//    viewModelUtilisateur: ViewModelUtilisateur,
    viewModelTransactions: ViewModelTransactions,
    idTransaction: String,
    onBackToTransactions: () -> Unit ) {

    var nomUtilisateur = viewModelTransactions.nomUtilisateur
//    var idTransaction = viewModelTransactions.idTransaction()

    val transactions by viewModelTransactions.chargerTXNUtilisateur(nomUtilisateur.toString())
        .collectAsState()
    val transaction = transactions.find { it.idTransaction == idTransaction }

    transaction?.let {
        val radioOptions = listOf("Revenu", "Dépense")
        var selectedOption by remember { mutableStateOf(it.selectedOption) }

        var modifieMontant by remember { mutableStateOf(it.montant.toString()) }
        var text by remember { mutableStateOf(modifieMontant) }
        var doubleValue by remember { mutableStateOf<Double?>(null) }
        var error by remember { mutableStateOf(false) }

        var modifieCategorieTransaction by remember { mutableStateOf(Categories.valueOf(it.categorieTransaction)) }
        var expanded by remember { mutableStateOf(false) }
        val menuItems = Categories.values().toList()

        var modifieDetailsSupplementaires by remember { mutableStateOf(it.detailsSupplementaires) }

        Scaffold() { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Détails de la transaction", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.padding(8.dp))

                Row(Modifier.selectableGroup()) {
                    radioOptions.forEach { option ->
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (option == selectedOption),
                                onClick = { selectedOption = option },
                                role = Role.RadioButton
                            )
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = { selectedOption = option }
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextField(
                        value = text,
                        onValueChange = { modifieMontant ->
                            text = modifieMontant
                            doubleValue = modifieMontant.toDoubleOrNull()
                            error = doubleValue == null && modifieMontant.isNotEmpty()
                        },
                        label = { Text("Montant") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = error,
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Gray)
                            .padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))

                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = modifieCategorieTransaction.name)
                }

                // Menu déroulant
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    menuItems.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(text = category.name) },
                            onClick = {
                                modifieCategorieTransaction = category
                                expanded = false
                            }
                        )
                    }
                }

                TextField(
                    value = modifieDetailsSupplementaires,
                    onValueChange = { modifieDetailsSupplementaires = it },
                    label = { Text("Détails Supplémentaires") },
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Gray)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.padding(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            if (text.isNotEmpty()) {
                                val montantDouble = text.toDoubleOrNull() // change
                                if (montantDouble != null) {
                                    viewModelTransactions.modifieTransaction(
                                        nomUtilisateur.toString(),
                                        idTransaction,
                                        selectedOption,
                                        montantDouble,
                                        modifieCategorieTransaction.name,
                                        modifieDetailsSupplementaires
                                    )
                                    onBackToTransactions()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        Text("Sauvegarder les modifications")
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            viewModelTransactions.supprimeTXNUtilisateur(
                                nomUtilisateur.toString(),
                                transaction.idTransaction
                            )
                            onBackToTransactions()
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        colors = ButtonColors(
                            containerColor = Red,
                            contentColor = White,
                            disabledContainerColor = Red,
                            disabledContentColor = White
                        )
                    ) {
                        Text("Supprimmer")
                    }
                }
                Log.i("System.out", "ED: TXN delete request ${transaction.idTransaction}")


                Spacer(modifier = Modifier.padding(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { onBackToTransactions() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Liste des Transactions")
                    }
                }
            }
        }
    }
}


