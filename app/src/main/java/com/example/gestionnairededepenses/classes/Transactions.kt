package com.example.gestionnairededepenses.classes

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class Transaction(
    val idTransaction: String,
    val selectedOption: String,
    val montant: Double = 0.00,
    val categorieTransaction: String
)

