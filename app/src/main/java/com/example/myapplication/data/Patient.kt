package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.Period

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String,
    val prenom: String,
    val sexe: Sexe,
    val dateNaissance: LocalDate,
    val profession: String,
    val email: String,
    val phoneNumber: String // Format: "+237 612345678"
) {
    val nomComplet: String
        get() = "$prenom $nom"

    val age: Int
        get() = Period.between(dateNaissance, LocalDate.now()).years
}

enum class Sexe {
    HOMME,
    FEMME
} 