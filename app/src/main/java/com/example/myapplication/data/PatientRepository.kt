package com.example.myapplication.data

import com.example.myapplication.ui.model.PatientFilters
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class PatientRepository(private val patientDao: PatientDao) {
    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()

    fun searchPatients(filters: PatientFilters): Flow<List<Patient>> =
        patientDao.searchPatients(
            nom = filters.nom,
            prenom = filters.prenom,
            sexe = filters.sexe,
            dateNaissance = filters.dateNaissance
        )

    suspend fun insertPatient(patient: Patient) {
        patientDao.insertPatient(patient)
    }

    suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient)
    }

    suspend fun deletePatient(patient: Patient) {
        patientDao.deletePatient(patient)
    }

    suspend fun getPatientById(id: Long): Patient? {
        return patientDao.getPatientById(id)
    }

    suspend fun patientExists(nom: String, prenom: String, dateNaissance: LocalDate): Boolean {
        return patientDao.patientExists(nom, prenom, dateNaissance)
    }
} 