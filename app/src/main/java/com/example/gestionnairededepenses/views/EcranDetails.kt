package com.example.gestionnairededepenses.views

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gestionnairededepenses.viewModels.ViewModelTransactions

@Composable
fun EcranDetails(viewModel: ViewModelTransactions, navController: NavHostController, transactionID: String) {

    val transactions by viewModel.transactions.collectAsState()
    val transaction = transactions.firstOrNull { it.idTransaction == transactionID }

    val radioOptions = listOf("Revenu", "Dépense")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    var text by remember { mutableStateOf("") }
    var doubleValue by remember { mutableStateOf<Double?>(null) }
    var error by remember { mutableStateOf(false) }
    var modifieMontant by remember { mutableStateOf("${transaction?.montant}") }
    var modifieCategorieTransaction by remember { mutableStateOf("${transaction?.categorieTransaction}") }

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
                radioOptions.forEach { text ->
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        )
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
            if (transaction != null) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = text,
                        onValueChange = { modifieMontant ->
                            text = modifieMontant
                            doubleValue = modifieMontant.toDoubleOrNull()
                            error = doubleValue == null && modifieMontant.isNotEmpty()
                        },
                        label = { Text("Enter a Double") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = error,
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Gray)
                            .padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    BasicTextField(
                        modifieCategorieTransaction,
                        onValueChange = { modifieCategorieTransaction = it },
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Gray)
                            .padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            if (modifieMontant.isNotEmpty() ) {
                                val montantDouble = modifieMontant.toDoubleOrNull()
                                if (montantDouble != null) {
                                    viewModel.modifieTransaction(
                                        idTransaction = transaction.idTransaction,
                                        selectedOption = selectedOption,
                                        montant = montantDouble,
                                        categorieTransaction = modifieCategorieTransaction
                                    )
                                    modifieMontant = ""
                                    modifieCategorieTransaction = ""
                                }
                                navController.navigate("ecran_transactions")
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
//                    IconButton(onClick = { viewModel.toggleTransaction(transaction.idTransaction, transaction.estRevenu) }) {
//                        Icon(
//                            Icons.Default.Done,
//                            contentDescription = "Revenu ou Dépense",
//                            tint = if (transaction.estRevenu) Color.Green else Color.Red
//                        )
//                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    IconButton(onClick = {
                        viewModel.supprimeTransaction(idTransaction = transaction.idTransaction)
                        navController.navigate("ecran_transactions")
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer")
                    }
                }

                Spacer(modifier = Modifier.padding(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Retour au liste des Transactions")
                    }
                }
            }
        }
    }
}