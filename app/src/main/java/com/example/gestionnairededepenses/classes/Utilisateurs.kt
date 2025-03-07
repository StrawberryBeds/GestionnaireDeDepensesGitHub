package com.example.gestionnairededepenses.classes

data class Utilisateur(
//    val context: Context, // Permettre le utilisation des fichiers privées pour chaque utilisateur
    val nomUtilisateur: String = "user@example.com", // Utilisé comme ID Utilisateur
    val motDePasse: String = "password123",
    var estVerifie: Boolean = false,
    val nomEtPrenom: String = "Penny Counter",
//    val transactions: MutableList<Transaction> = mutableListOf<Transaction>()
)
//{
//    // Chaque utilisateur a son propre fichier dans SharedPreferences.
//    private val userPreferences: SharedPreferences = context.getSharedPreferences(
//        "UserPrefs $nomUtilisateur",
//        Context.MODE_PRIVATE)
//}
