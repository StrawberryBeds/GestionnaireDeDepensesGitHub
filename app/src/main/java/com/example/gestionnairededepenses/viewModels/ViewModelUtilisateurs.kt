package com.example.gestionnairededepenses.viewModels

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.gestionnairededepenses.classes.AppOutils
import com.example.gestionnairededepenses.classes.Utilisateur
import com.example.gestionnairededepenses.classes.utilisateur01
import com.example.gestionnairededepenses.classes.utilisateur02
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ViewModelUtilisateur (application: Application): AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("AppPrefs", MODE_PRIVATE)
    private val gson = Gson()

    private val _utilisateurs = mutableStateListOf<Utilisateur>() // ViewModel
    val utilisateurs: List<Utilisateur> get() = _utilisateurs // View

    init {
        _utilisateurs.add(utilisateur01)
        _utilisateurs.add(utilisateur02)
        apporteUtilisateurs()
    }

    private val _utilisateur = Utilisateur(
        nomEtPrenom = sharedPreferences.getString("NOM_ET_PRENOM", "Penny Counter")
            ?: "Penny Counter",
        nomUtilisateur = sharedPreferences.getString("NOM_UTILISATEUR", "user@example.com")
            ?: "user@example.com",
        motDePasse = sharedPreferences.getString("MOT_DE_PASSE", "password123") ?: "password123",
        estVerifie = sharedPreferences.getBoolean("UTILISATEUR_VERIFIE", false)
    )
    val utilisateur: Utilisateur get() = _utilisateur

    fun verifierUtilisateur(nomUtilisateur: String, motDePasse: String): Boolean {
        val estVerifie =
            (utilisateur.nomUtilisateur == nomUtilisateur) && (utilisateur.motDePasse == motDePasse)

        sharedPreferences.edit().putBoolean("UTILISATEUR_VERIFIE", estVerifie).apply()
        return estVerifie
    }

//    fun apporteNomFichierUtilisateur(nomUtilisateur: String): String {
//        var nomFichier = "Transactions_${nomUtilisateur}"
//        return nomFichier
//    }

    fun deconecterUtilisateur(nomUtilisateur: String) {
        val nomFichier = AppOutils.apporteNomFichierUtilisateur(nomUtilisateur)
        val sharedPreferencesUtilisateur =
            getApplication<Application>().getSharedPreferences(nomFichier, Context.MODE_PRIVATE)
        utilisateur.estVerifie = false

        sharedPreferences.edit().putBoolean("UTILISATEUR_VERIFIE", false).apply()
    }

    fun estUtilisateurVerifie(): Boolean {
        return utilisateur.estVerifie
    }

    fun ajouteUtilisateur(nomUtilisateur: String, motDePasse: String) {

        val nouvelleUtilisateur = Utilisateur(
            nomEtPrenom = nomUtilisateur,
            nomUtilisateur = nomUtilisateur,
            motDePasse = motDePasse,
            estVerifie = false
        )
        _utilisateurs.add(nouvelleUtilisateur)
        sauvegarderUtilisateur()
    }

    fun sauvegarderUtilisateur() {
        val jsonString = gson.toJson(_utilisateurs)
        sharedPreferences.edit().putString("PREF_KEY_UTILISATEURS", jsonString).apply()
    }

    private fun apporteUtilisateurs() {
        val jsonString = sharedPreferences.getString("PREF_KEY_UTILISATEURS", null) ?: return
        val listType = object : TypeToken<List<Utilisateur>>() {}.type
        val utilisateursSauves: List<Utilisateur> = gson.fromJson(jsonString, listType)
        _utilisateurs.clear()
        _utilisateurs.addAll(utilisateursSauves)
    }
}