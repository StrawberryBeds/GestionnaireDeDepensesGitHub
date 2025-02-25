package com.example.gestionnairededepenses.viewModels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import com.example.gestionnairededepenses.classes.Transaction
import com.example.gestionnairededepenses.classes.Utilisateur
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

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
        Log.i("System.out", "VMU: Utilisateur: $estVerifie")
        return estVerifie
    }

    // Deconnecter le utilisateur ...
    fun deconnecterUtilisateur(nomUtilisateur: String) {
//        val nomFichier = AppOutils.apporteNomFichierUtilisateur(nomUtilisateur)
//        val sharedPreferencesUtilisateur =
//            getApplication<Application>().getSharedPreferences(nomFichier, MODE_PRIVATE)
        // Changer le statut d'utilisateur parti dans les données
        sharedPreferences.edit().clear().apply()
        sharedPreferences.edit().putBoolean("UTILISATEUR_VERIFIE", false).apply()
        // Retour pour amener l'utilisateur au SeConnecter
        utilisateur.estVerifie = false
        Log.i("System.out", "VMU: Utilisateur: ${utilisateur.estVerifie}")
    }

// *** GESTION DES TRANSACTIONS

    // Ficher des transactions d'un utilisateur invisible aux Views
    private val userPreferences: SharedPreferences = application.getSharedPreferences(
        "UserPrefs ${utilisateur.nomUtilisateur}",
        MODE_PRIVATE
    )
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList()) // ViewModel
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow() // View

    init {
        chargerTransactions()
    }

    private fun chargerTransactions() {
        val jsonString = userPreferences.getString(
            "PREF_KEY_TRANSACTIONS",
            null
        ) ?: return
        val listType = object : TypeToken<List<Transaction>>() {}.type
        try {
            val transactionsList: List<Transaction> = gson.fromJson(jsonString, listType)
            _transactions.value = transactionsList // Change suggèré par Mistal
            Log.i("System.out", "VMU: Transactions chargées: $transactionsList")
        } catch (e: Exception) {
            Log.i("System.out", "VMU: Erreur lors du chargement des transactions", e)
            Log.e("System.err", "VMU: Erreur lors du chargement des transactions", e)
            // Vous pouvez également réinitialiser _transactions.value à une liste vide en cas d'erreur
            _transactions.value = emptyList()
            Log.i("System.out", "VMU: JSON chargé: $jsonString")

        }
    }

    fun ajouterTransaction(selectedOption: String, montant: Double, categorieTransaction: String) {

        val nouvelleTransaction = Transaction(
            idTransaction = UUID.randomUUID().toString(),
            selectedOption = selectedOption,
            montant = montant,
            categorieTransaction = categorieTransaction)

        _transactions.value = _transactions.value + nouvelleTransaction
        sauvegarderTransaction(nomUtilisateur = utilisateur.nomUtilisateur)
        Log.i("System.out", "VMU: TxnAjouté ${utilisateur.nomUtilisateur}, ${nouvelleTransaction}")
    }

    fun sauvegarderTransaction(nomUtilisateur: String) {
        // val nomFichier = AppOutils.apporteNomFichierUtilisateur(nomUtilisateur)
        val userPreferences: SharedPreferences = application.getSharedPreferences(
            "UserPrefs ${utilisateur.nomUtilisateur}",
            MODE_PRIVATE
        )
        val jsonString = gson.toJson(_transactions.value)
        val editor =
            userPreferences.edit()
        Log.i("System.out", "VMU: JSON à sauvegarder: $jsonString")
        editor.putString("PREF_KEY_TRANSACTIONS", jsonString)
        editor.apply()
        Log.i("System.out", "VMU: JSON sauvegardé: $jsonString")
        Log.i("System.out", "VMU: TxnSauvé ${utilisateur.nomUtilisateur}, ${jsonString}")
    }
}




//    // Ficher des transactions d'un utilisateur invisible aux Views
//    private val userPreferences: SharedPreferences = context.getSharedPreferences(
//        "UserPrefs ${utilisateur.nomUtilisateur}",
//        MODE_PRIVATE
//    )







//    fun apporteNomFichierUtilisateur(nomUtilisateur: String): String {
//        var nomFichier = "Transactions_${nomUtilisateur}"
//        return nomFichier
//    }

//    fun ajouteUtilisateur(nomUtilisateur: String, motDePasse: String) {
//
//        val nouvelleUtilisateur = Utilisateur(
//            nomEtPrenom = nomUtilisateur,
//            nomUtilisateur = nomUtilisateur,
//            motDePasse = motDePasse,
//            estVerifie = false
//        )
//        _utilisateurs.add(nouvelleUtilisateur)
//        sauvegarderUtilisateur()
//    }
//
//    fun sauvegarderUtilisateur() {
//        val jsonString = gson.toJson(_utilisateurs)
//        sharedPreferences.edit().putString("PREF_KEY_UTILISATEURS", jsonString).apply()
//    }
//
//    private fun apporteUtilisateurs() {
//        val jsonString = sharedPreferences.getString("PREF_KEY_UTILISATEURS", null) ?: return
//        val listType = object : TypeToken<List<Utilisateur>>() {}.type
//        val utilisateursSauves: List<Utilisateur> = gson.fromJson(jsonString, listType)
//        _utilisateurs.clear()
//        _utilisateurs.addAll(utilisateursSauves)
//    }
