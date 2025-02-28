package com.example.gestionnairededepenses.viewModels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.gestionnairededepenses.classes.Transaction
import com.example.gestionnairededepenses.classes.Utilisateur
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class ViewModelTransactions(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("TXN_Prefs", MODE_PRIVATE)

    // Permettre la gestion de la liste des utilisateurs - privée, invisible aux Views
    private val gson = Gson()
    private val lock = Any()
    private val _transactionsMap = mutableMapOf<String, MutableStateFlow<List<Transaction>>>()

    //    // Details d'un utilisateur  - visible aux Views au besoin
//    val transactions: StateFlow<List<Transaction>> = _transactionsMap.getOrPut(nomUtilisateur)
    internal var nomUtilisateur: Utilisateur? = null

    // Initialise les utilisateurs par le fonction chargerUtilisateurs
    init {
        nomUtilisateur = Utilisateur("user@example.com")
        chargerTXNUtilisateur(nomUtilisateur!!.toString())
    }

    // Charger la liste des utilisateurs du fichier et le convertir de json à List
    // - privée, inaccessible aux Views
    fun chargerTXNUtilisateur(nomUtilisateur: String): StateFlow<List<Transaction>> {
        return synchronized(lock) {
            _transactionsMap.getOrPut(nomUtilisateur.toString()) {
                MutableStateFlow(loadTXNUtilisateaur(nomUtilisateur.toString()))
            }
        }
    }

    private fun loadTXNUtilisateaur(nomUtilisateur: String): List<Transaction> {
        val json = sharedPreferences.getString(nomUtilisateur, null)
        return if (json != null) {
            try {
                gson.fromJson(json, object : TypeToken<List<Transaction>>() {}.type)
            } catch (e: JsonSyntaxException) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    private fun sauvegarderTXNUtilisateur(nomUtilisateur: String, transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        sharedPreferences.edit().putString(nomUtilisateur, json).apply()
    }

    fun ajouteTXNUtilisateur(
        nomUtilisateur: String,
        selectedOption: String,
        montant: Double,
        categorieTransaction: String,
        detailsSupplenentaires: String
    ) {
        synchronized(lock) {
            // Create a new transaction with a UUID
            val nouvelleTransaction = Transaction(
                idTransaction = UUID.randomUUID().toString(),
                selectedOption = selectedOption,
                montant = montant,
                categorieTransaction = categorieTransaction,
                detailsSupplementaires = detailsSupplenentaires
            )

            // Get the current list of transactions for the user
            val currentTransactions =
                _transactionsMap[nomUtilisateur]?.value.orEmpty().toMutableList()

            // Add the new transaction to the list
            currentTransactions.add(nouvelleTransaction)

            // Update the StateFlow with the modified list
            _transactionsMap[nomUtilisateur]?.value = currentTransactions

            // Save the updated list to SharedPreferences
            sauvegarderTXNUtilisateur(nomUtilisateur, currentTransactions)

            // Log the new transaction
            Log.i("System.out", "VMU: ajouterTransaction $nouvelleTransaction")
        }
    }

    fun supprimeTXNUtilisateur(nomUtilisateur: String, idTransaction: String) {
        synchronized(lock) {
            // Log the transaction ID to ensure it is not empty
            Log.i("System.out", "VMT: delete started for transaction ID: $idTransaction")

            // Get the current list of transactions for the user
            val transactionActuel =
                _transactionsMap[nomUtilisateur]?.value.orEmpty().toMutableList()

            // Log the list of transactions before removal
            Log.i("System.out", "VMT: List before removal: $transactionActuel")

            // Remove the specified transaction from the list using its ID
            val isRemoved = transactionActuel.removeAll { it.idTransaction == idTransaction }
            Log.i("System.out", "VMT: TXN removed: $idTransaction, Removal result: $isRemoved")

            // Update the StateFlow with the modified list
            _transactionsMap[nomUtilisateur]?.value = transactionActuel
            Log.i("System.out", "VMT: List after modification: $transactionActuel")

            // Save the updated list to SharedPreferences
            sauvegarderTXNUtilisateur(nomUtilisateur, transactionActuel)
            Log.i("System.out", "VMT: List saved: $transactionActuel")
        }
    }

    fun modifieTransaction(
        nomUtilisateur: String,
        idTransaction: String,
        selectedOption: String,
        montant: Double,
        categorieTransaction: String,
        detailsSupplenentaires: String
    ) {
        synchronized(lock) {
            // Get the current list of transactions for the user
            Log.i("System.out", "VMT: Modification called ${idTransaction}",)
            val currentTransactions = _transactionsMap[nomUtilisateur]?.value.orEmpty().toMutableList()

            // Find the index of the old transaction using its ID
            val index = currentTransactions.indexOfFirst { it.idTransaction == idTransaction }
            if (index != -1) {
                // Create a new transaction with the updated details
                val newTransaction = Transaction(
                    idTransaction = idTransaction,
                    selectedOption = selectedOption,
                    montant = montant,
                    categorieTransaction = categorieTransaction,
                    detailsSupplementaires = detailsSupplenentaires
                )
                // Replace the old transaction with the new one
                currentTransactions[index] = newTransaction
                Log.i("System.out", "ET: VMT replaced ${idTransaction}")
            }

            // Update the StateFlow with the modified list
            _transactionsMap[nomUtilisateur]?.value = currentTransactions

            // Save the updated list to SharedPreferences
            sauvegarderTXNUtilisateur(nomUtilisateur, currentTransactions)
        }
    }
}

