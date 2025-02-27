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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gestionnairededepenses.classes.Categories
import com.example.gestionnairededepenses.viewModels.ViewModelUtilisateur

@Composable
fun EcranTransactions(viewModel: ViewModelUtilisateur, onNavigateToDetails: (String) -> Unit) {

    val transactions by viewModel.transactions.collectAsState()

    val radioOptions = listOf("Revenu", "Dépense")
    var (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    var nouvelleMontant by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var doubleValue by remember { mutableStateOf<Double?>(null) }
    var error by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var nouvelleCategorieTransaction by remember { mutableStateOf(Categories.SALAIRE) }
    val menuItems = Categories.values().toList()

    var detailsSupplementaires by remember { mutableStateOf("") }


    Scaffold() { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                "Nouvelle transaction",
                style = MaterialTheme.typography.headlineMedium
            )
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
                        onClick = { onOptionSelected(text) }
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    value = text,
                    onValueChange = { nouvelleMontant ->
                        text = nouvelleMontant
                        doubleValue = nouvelleMontant.toDoubleOrNull()
                        error = doubleValue == null && nouvelleMontant.isNotEmpty()

                    },
                    label = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = error,
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Gray)
                        .padding(8.dp)
                )
            }
            Spacer(
                modifier = Modifier.padding(8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = nouvelleCategorieTransaction.name)
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
                                nouvelleCategorieTransaction = category
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .padding(8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    value = detailsSupplementaires,
                    onValueChange = { detailsSupplementaires = it },
                    label = { Text("Détails Supplementaires") },
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Gray)
                        .padding(8.dp)
                )
            }
            Spacer(
                modifier = Modifier
                    .padding(8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (text.isNotEmpty()) {
                            val montantDouble = text.toDoubleOrNull()
                            if (montantDouble != null) {
                                viewModel.ajouterTransaction(
                                    selectedOption,
                                    montantDouble,
                                    nouvelleCategorieTransaction.name,
                                    detailsSupplementaires
                                )
                                selectedOption = radioOptions[0]
                                nouvelleMontant = ""
                                nouvelleCategorieTransaction.name
                                detailsSupplementaires = ""
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                ) {
                    Text("Ajouter")
                }
            }
            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(transactions, key = { it.idTransaction }) { transaction ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Calculer la couleur en fonction de la condition
                        val textCouleur = if (transaction.selectedOption == "Revenu") {
                            Color.Black
                        } else {
                            Color.Red
                        }
                        IconButton(onClick = {
                            val idTransction = "${transaction.idTransaction}"
                            onNavigateToDetails(idTransction) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Modifier")
                        }
                        Text(
                            "${transaction.montant}",
                            color = textCouleur,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "${transaction.categorieTransaction}",
                            color = textCouleur,
                            modifier = Modifier.weight(1f)
                        )

//                        IconButton(onClick = {
                        //    viewModel.supprimeTransaction(idTransaction = transaction.idTransaction)
//                        } ) {
//                            Icon(Icons.Default.Delete, contentDescription = "Supprimer")
//                        }
                    }
                }
            }
        }
    }
}


//fun onOptionSelected(text: String) {
//
//}