package com.example.myapplication.ui.model

import com.example.myapplication.data.Sexe
import java.time.LocalDate

data class PatientFilters(
    val nom: String = "",
    val prenom: String = "",
    val sexe: Sexe? = null,
    val dateNaissance: LocalDate? = null
) {
    fun hasActiveFilters(): Boolean {
        return nom.isNotEmpty() || 
               prenom.isNotEmpty() || 
               sexe != null || 
               dateNaissance != null
    }
} 