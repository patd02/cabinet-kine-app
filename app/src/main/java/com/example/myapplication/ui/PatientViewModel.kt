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
        email: String,
        phoneNumber: String
    ) {
        viewModelScope.launch {
            try {
                repository.insertPatient(
                    Patient(
                        nom = nom,
                        prenom = prenom,
                        sexe = sexe,
                        dateNaissance = dateNaissance,
                        profession = profession,
                        email = email,
                        phoneNumber = phoneNumber
                    )
                )
                _showAddPatientDialog.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Erreur lors de l'ajout du patient: ${e.message}"
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
        email: String,
        phoneNumber: String
    ) {
        viewModelScope.launch {
            try {
                repository.updatePatient(
                    Patient(
                        id = id,
                        nom = nom,
                        prenom = prenom,
                        sexe = sexe,
                        dateNaissance = dateNaissance,
                        profession = profession,
                        email = email,
                        phoneNumber = phoneNumber
                    )
                )
                _showEditDialog.value = false
                _patientToEdit.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Erreur lors de la mise à jour du patient: ${e.message}"
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