package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.data.PatientDatabase
import com.example.myapplication.data.PatientRepository
import com.example.myapplication.ui.PatientViewModel
import com.example.myapplication.ui.PatientViewModelFactory
import com.example.myapplication.ui.components.AddPatientDialog
import com.example.myapplication.ui.components.DeleteConfirmationDialog
import com.example.myapplication.ui.components.EditPatientDialog
import com.example.myapplication.ui.components.ErrorDialog
import com.example.myapplication.ui.components.FiltersDialog
import com.example.myapplication.ui.components.PatientCard
import com.example.myapplication.ui.components.SearchBar
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val database by lazy { PatientDatabase.getDatabase(applicationContext) }
    private val repository by lazy { PatientRepository(database.patientDao()) }
    private val viewModel: PatientViewModel by viewModels {
        PatientViewModelFactory(repository)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Cabinet de Kinésithérapie") },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                actions = {
                                    IconButton(onClick = { viewModel.onShowFiltersClick() }) {
                                        Icon(
                                            Icons.Outlined.FilterList,
                                            contentDescription = "Filtres",
                                            tint = if (viewModel.currentFilters.collectAsState().value.hasActiveFilters())
                                                MaterialTheme.colorScheme.onPrimary
                                            else
                                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            )
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = { viewModel.onAddPatientClick() },
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Ajouter un patient")
                            }
                        }
                    ) { paddingValues ->
                        val uiState by viewModel.uiState.collectAsState()
                        val showAddDialog by viewModel.showAddPatientDialog.collectAsState()
                        val showFiltersDialog by viewModel.showFiltersDialog.collectAsState()

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            } else {
                                LazyColumn {
                                    items(uiState.patients) { patient ->
                                        PatientCard(
                                            patient = patient,
                                            onEdit = { viewModel.onEditPatientClick(it) },
                                            onDelete = { viewModel.onDeletePatientClick(it) }
                                        )
                                    }
                                }
                            }

                            if (showAddDialog) {
                                AddPatientDialog(
                                    onDismiss = { viewModel.onAddPatientDialogDismiss() },
                                    onConfirm = { nom, prenom, sexe, dateNaissance, profession, email ->
                                        viewModel.addPatient(
                                            nom = nom,
                                            prenom = prenom,
                                            sexe = sexe,
                                            dateNaissance = dateNaissance,
                                            profession = profession,
                                            email = email
                                        )
                                    }
                                )
                            }

                            if (showFiltersDialog) {
                                FiltersDialog(
                                    currentFilters = uiState.filters,
                                    onDismiss = { viewModel.onFiltersDialogDismiss() },
                                    onApplyFilters = { viewModel.updateFilters(it) },
                                    onClearFilters = { viewModel.clearFilters() }
                                )
                            }

                            if (uiState.showEditDialog) {
                                uiState.patientToEdit?.let { patient ->
                                    EditPatientDialog(
                                        patient = patient,
                                        onDismiss = { viewModel.onEditDialogDismiss() },
                                        onConfirm = { id, nom, prenom, sexe, dateNaissance, profession, email ->
                                            viewModel.onUpdatePatient(
                                                id = id,
                                                nom = nom,
                                                prenom = prenom,
                                                sexe = sexe,
                                                dateNaissance = dateNaissance,
                                                profession = profession,
                                                email = email
                                            )
                                        }
                                    )
                                }
                            }

                            if (uiState.showDeleteConfirmation) {
                                uiState.patientToDelete?.let { patient ->
                                    DeleteConfirmationDialog(
                                        patient = patient,
                                        onConfirm = { viewModel.onDeleteConfirmed() },
                                        onDismiss = { viewModel.onDeleteCancelled() }
                                    )
                                }
                            }

                            uiState.errorMessage?.let { error ->
                                ErrorDialog(
                                    message = error,
                                    onDismiss = { viewModel.onAddPatientDialogDismiss() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}