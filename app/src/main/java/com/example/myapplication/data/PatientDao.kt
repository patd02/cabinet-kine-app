package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients ORDER BY nom, prenom")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("""
        SELECT * FROM patients 
        WHERE (:nom = '' OR nom LIKE '%' || :nom || '%')
        AND (:prenom = '' OR prenom LIKE '%' || :prenom || '%')
        AND (:sexe IS NULL OR sexe = :sexe)
        AND (:dateNaissance IS NULL OR dateNaissance = :dateNaissance)
        ORDER BY nom, prenom
    """)
    fun searchPatients(
        nom: String = "",
        prenom: String = "",
        sexe: Sexe? = null,
        dateNaissance: LocalDate? = null
    ): Flow<List<Patient>>

    @Insert
    suspend fun insertPatient(patient: Patient)

    @Update
    suspend fun updatePatient(patient: Patient)

    @Delete
    suspend fun deletePatient(patient: Patient)

    @Query("SELECT * FROM patients WHERE id = :id")
    suspend fun getPatientById(id: Long): Patient?

    @Query("""
        SELECT EXISTS (
            SELECT 1 FROM patients 
            WHERE TRIM(LOWER(nom)) = TRIM(LOWER(:nom))
            AND TRIM(LOWER(prenom)) = TRIM(LOWER(:prenom))
            AND dateNaissance = :dateNaissance
        )
    """)
    suspend fun patientExists(nom: String, prenom: String, dateNaissance: LocalDate): Boolean
} 