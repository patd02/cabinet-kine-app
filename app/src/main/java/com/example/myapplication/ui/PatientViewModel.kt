package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Patient
import com.example.myapplication.data.PatientRepository
import com.example.myapplication.data.Sexe
import com.example.myapplication.ui.model.PatientFilters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class PatientUiState(
    val patients: List<Patient> = emptyList(),
    val isLoading: Boolean = true,
    val filters: PatientFilters = PatientFilters(),
    val errorMessage: String? = null,
    val showDeleteConfirmation: Boolean = false,
    val patientToDelete: Patient? = null,
    val showEditDialog: Boolean = false,
    val patientToEdit: Patient? = null
)

class PatientViewModel(private val repository: PatientRepository) : ViewModel() {
    private val _filters = MutableStateFlow(PatientFilters())
    private val _showAddPatientDialog = MutableStateFlow(false)
    private val _showFiltersDialog = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _showDeleteConfirmation = MutableStateFlow(false)
    private val _patientToDelete = MutableStateFlow<Patient?>(null)
    private val _showEditDialog = MutableStateFlow(false)
    private val _patientToEdit = MutableStateFlow<Patient?>(null)
    
    val showAddPatientDialog: StateFlow<Boolean> = _showAddPatientDialog
    val showFiltersDialog: StateFlow<Boolean> = _showFiltersDialog
    val currentFilters: StateFlow<PatientFilters> = _filters

    private val filteredPatients = combine(
        repository.getAllPatients(),
        _filters
    ) { patients, filters ->
        if (!filters.hasActiveFilters()) {
            patients
        } else {
            patients.filter { patient ->
                val matchNom = filters.nom.isEmpty() || 
                    patient.nom.lowercase().contains(filters.nom.lowercase())
                
                val matchPrenom = filters.prenom.isEmpty() || 
                    patient.prenom.lowercase().contains(filters.prenom.lowercase())
                
                val matchSexe = filters.sexe == null || 
                    patient.sexe == filters.sexe
                
                val matchDate = filters.dateNaissance == null || 
                    patient.dateNaissance == filters.dateNaissance

                matchNom && matchPrenom && matchSexe && matchDate
            }
        }
    }

    private val dialogState = combine(
        _showDeleteConfirmation,
        _patientToDelete,
        _showEditDialog,
        _patientToEdit
    ) { showDelete, toDelete, showEdit, toEdit ->
        Triple(
            Pair(showDelete, toDelete),
            Pair(showEdit, toEdit),
            _errorMessage.value
        )
    }

    val uiState: StateFlow<PatientUiState> = combine(
        filteredPatients,
        _filters,
        dialogState,
        _errorMessage
    ) { patients, filters, dialogs, error ->
        PatientUiState(
            patients = patients,
            isLoading = false,
            filters = filters,
            errorMessage = error,
            showDeleteConfirmation = dialogs.first.first,
            patientToDelete = dialogs.first.second,
            showEditDialog = dialogs.second.first,
            patientToEdit = dialogs.second.second
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PatientUiState()
    )

    fun updateFilters(newFilters: PatientFilters) {
        _filters.value = newFilters
    }

    fun clearFilters() {
        _filters.value = PatientFilters()
    }

    fun onShowFiltersClick() {
        _showFiltersDialog.value = true
    }

    fun onFiltersDialogDismiss() {
        _showFiltersDialog.value = false
    }

    fun onAddPatientClick() {
        _showAddPatientDialog.value = true
        _errorMessage.value = null
    }

    fun onAddPatientDialogDismiss() {
        _showAddPatientDialog.value = false
        _errorMessage.value = null
    }

    fun addPatient(
        nom: String,
        prenom: String,
        sexe: Sexe,
        dateNaissance: LocalDate,
        profession: String,
        email: String
    ) {
        viewModelScope.launch {
            val patient = Patient(
                nom = nom,
                prenom = prenom,
                sexe = sexe,
                dateNaissance = dateNaissance,
                profession = profession,
                email = email
            )
            
            if (!repository.patientExists(nom, prenom, dateNaissance)) {
                repository.insertPatient(patient)
                _showAddPatientDialog.value = false
                _errorMessage.value = null
            } else {
                _errorMessage.value = "Ce patient existe déjà dans la base de données.\nUn patient avec le même nom (${patient.nom}), prénom (${patient.prenom}) et date de naissance (${patient.dateNaissance}) est déjà enregistré."
            }
        }
    }

    fun onDeletePatientClick(patient: Patient) {
        _patientToDelete.value = patient
        _showDeleteConfirmation.value = true
    }

    fun onDeleteConfirmed() {
        viewModelScope.launch {
            _patientToDelete.value?.let { patient ->
                repository.deletePatient(patient)
            }
            _showDeleteConfirmation.value = false
            _patientToDelete.value = null
        }
    }

    fun onDeleteCancelled() {
        _showDeleteConfirmation.value = false
        _patientToDelete.value = null
    }

    fun onEditPatientClick(patient: Patient) {
        _patientToEdit.value = patient
        _showEditDialog.value = true
        _errorMessage.value = null
    }

    fun onEditDialogDismiss() {
        _showEditDialog.value = false
        _patientToEdit.value = null
        _errorMessage.value = null
    }

    fun onUpdatePatient(
        id: Long,
        nom: String,
        prenom: String,
        sexe: Sexe,
        dateNaissance: LocalDate,
        profession: String,
        email: String
    ) {
        viewModelScope.launch {
            val updatedPatient = Patient(
                id = id,
                nom = nom,
                prenom = prenom,
                sexe = sexe,
                dateNaissance = dateNaissance,
                profession = profession,
                email = email
            )

            // Vérifier si un autre patient avec les mêmes informations existe déjà
            val exists = repository.patientExists(nom, prenom, dateNaissance)
            val currentPatient = repository.getPatientById(id)

            // Permettre la mise à jour si c'est le même patient ou si aucun doublon n'existe
            if (!exists || (currentPatient?.nom?.equals(nom, true) == true && 
                          currentPatient.prenom.equals(prenom, true) && 
                          currentPatient.dateNaissance == dateNaissance)) {
                repository.updatePatient(updatedPatient)
                _showEditDialog.value = false
                _patientToEdit.value = null
                _errorMessage.value = null
            } else {
                _errorMessage.value = "Un autre patient avec le même nom (${updatedPatient.nom}), prénom (${updatedPatient.prenom}) et date de naissance (${updatedPatient.dateNaissance}) existe déjà."
            }
        }
    }
}

class PatientViewModelFactory(private val repository: PatientRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PatientViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 