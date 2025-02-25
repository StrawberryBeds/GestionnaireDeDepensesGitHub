package com.example.gestionnairededepenses.classes

data class Utilisateur(
//    val context: Context, // Permettre le utilisation des fichiers privées pour chaque utilisateur
    val nomUtilisateur: String, // Utilisé comme ID Utilisateur
    val motDePasse: String,
    var estVerifie: Boolean,
    val nomEtPrenom: String,
//    val transactions: MutableList<Transaction> = mutableListOf<Transaction>()
)
//{
//    // Chaque utilisateur a son propre fichier dans SharedPreferences.
//    private val userPreferences: SharedPreferences = context.getSharedPreferences(
//        "UserPrefs $nomUtilisateur",
//        Context.MODE_PRIVATE)
//}
