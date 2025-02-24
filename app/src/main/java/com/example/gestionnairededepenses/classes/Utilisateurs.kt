package com.example.gestionnairededepenses.classes

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel

data class Utilisateur(
    val nomEtPrenom: String,
    val nomUtilisateur: String,
    val motDePasse: String,
    var estVerifie: Boolean
)
