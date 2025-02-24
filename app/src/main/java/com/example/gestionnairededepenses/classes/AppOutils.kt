package com.example.gestionnairededepenses.classes

object AppOutils {

    fun apporteNomFichierUtilisateur(nomUtilisateur: String): String {
        var nomFichier = "Transactions_${nomUtilisateur}"
        return nomFichier
    }
}