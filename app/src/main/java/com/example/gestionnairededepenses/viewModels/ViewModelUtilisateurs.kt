package com.example.gestionnairededepenses.viewModels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.gestionnairededepenses.classes.Utilisateur
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ViewModelUtilisateur (application: Application) : AndroidViewModel(application) {

    // Ficher des utilisateurs - privée, invisible aux Views
    private val sharedPreferences = application.getSharedPreferences("AppPrefs", MODE_PRIVATE)

    // Les données des utilisateurs dans la ficher - privée, invisible aux Views
    private val _utilisateur = Utilisateur(
        nomEtPrenom = sharedPreferences.getString("NOM_ET_PRENOM", "Penny Counter")
            ?: "Penny Counter",
        nomUtilisateur = sharedPreferences.getString("NOM_UTILISATEUR", "user@example.com")
            ?: "user@example.com",
        motDePasse = sharedPreferences.getString("MOT_DE_PASSE", "password123") ?: "password123",
        estVerifie = sharedPreferences.getBoolean("UTILISATEUR_VERIFIE", false),
//        context = sharedPreferences.getString("CONTEXT", "$Context")?: ""
    )

    // Permettre la gestion de la iste des utilisateurs - privée, invisible aux Views
    private val _utilisateurs = mutableStateListOf<Utilisateur>()

    // Details d'un utilisateur  - visible aux Views au besoin
    val utilisateur: Utilisateur get() = _utilisateur


    // Truc Gson qui transform les données en JSON
    private val gson = Gson()

    // Initialise les utilisateurs par le fonction chargerUtilisateurs
    init {
        chargerUtilisateurs()
        Log.i("System.out", "Utilisateurs chargé")
    }

    // Charger la liste des utilisateurs du fichier et le convertir de json à List
    // - privée, inaccessible aux Views
    private fun chargerUtilisateurs() {
        val jsonString = sharedPreferences.getString(
            "PREF_KEY_UTILISATEURS",
            null) ?: return
        val listType = object : TypeToken<List<Utilisateur>>() {}.type
        try {
            val utilisateursSauves: List<Utilisateur> = gson.fromJson(jsonString, listType)
            Log.i("System.out", "VMU: Utilisateurs chargés avec succès: $utilisateursSauves")
            // Vider le List des utilisateurs
            _utilisateurs.clear()
            // Ajoute les utilisateurs stockés et convertis
            _utilisateurs.addAll(utilisateursSauves)
        } catch (e: Exception) {
            Log.e("System.err", "VMU: Erreur lors du chargement des utilisateurs", e)
        }
    }

    // Verifier l'utilisateur ...
    fun verifierUtilisateur(nomUtilisateur: String, motDePasse: String): Boolean {
        // ... par comparer les données aux entrées
        val estVerifie =
            (utilisateur.nomUtilisateur == nomUtilisateur) && (utilisateur.motDePasse == motDePasse)
        // Changer le statut d'utilisateur verifié dans les données
        sharedPreferences.edit().putBoolean("UTILISATEUR_VERIFIE", estVerifie).apply()
        // Retour pour amener l'utiliasteur à l'Accueil
        Log.i("System.out", "VMU: verifierUtilisateur: $estVerifie")
        return estVerifie
    }

    // Deconnecter le utilisateur ...
    fun deconnecterUtilisateur(nomUtilisateur: String) {
        // Changer le statut d'utilisateur parti dans les données
        sharedPreferences.edit().clear().apply()
        sharedPreferences.edit().putBoolean("UTILISATEUR_VERIFIE", false).apply()
        // Retour pour amener l'utilisateur au SeConnecter
        utilisateur.estVerifie = false
        Log.i("System.out", "VMU: deconnecterUtilisateur: ${utilisateur.estVerifie}")
    }

}

