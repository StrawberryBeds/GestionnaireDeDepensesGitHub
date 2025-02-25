package com.example.gestionnairededepenses.classes

import android.content.Context
import android.content.SharedPreferences

data class Utilisateur(
//    val context: Context, // Permettre le utilisation des fichiers privées pour chaque utilisateur
    val nomUtilisateur: String, // Utilisé comme ID Utilisateur
    val motDePasse: String,
    var estVerifie: Boolean,
    val nomEtPrenom: String
)
//{
//    // Chaque utilisateur a son propre fichier dans SharedPreferences.
//    private val userPreferences: SharedPreferences = context.getSharedPreferences(
//        "UserPrefs $nomUtilisateur",
//        Context.MODE_PRIVATE)
//}
