package com.example.gestionnairededepenses.classes

data class Transaction(
//    val utilisateur: ViewModelUtilisateur,
    val idTransaction: String,
    val selectedOption: String,
    val montant: Double = 0.00,
    val categorieTransaction: String
)

