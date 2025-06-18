package com.example.myapplication.ui.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Sexe
import com.example.myapplication.ui.model.PatientFilters
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun FiltersDialog(
    currentFilters: PatientFilters,
    onDismiss: () -> Unit,
    onApplyFilters: (PatientFilters) -> Unit,
    onClearFilters: () -> Unit
) {
    var nom by remember { mutableStateOf(currentFilters.nom) }
    var prenom by remember { mutableStateOf(currentFilters.prenom) }
    var sexe by remember { mutableStateOf(currentFilters.sexe) }
    var dateNaissance by remember { mutableStateOf(currentFilters.dateNaissance) }

    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtres") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Nom
                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = if (nom.isNotEmpty()) {
                        { 
                            IconButton(onClick = { nom = "" }) {
                                Icon(Icons.Default.Clear, "Effacer")
                            }
                        }
                    } else null
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Prénom
                OutlinedTextField(
                    value = prenom,
                    onValueChange = { prenom = it },
                    label = { Text("Prénom") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = if (prenom.isNotEmpty()) {
                        { 
                            IconButton(onClick = { prenom = "" }) {
                                Icon(Icons.Default.Clear, "Effacer")
                            }
                        }
                    } else null
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date de naissance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Date de naissance:",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            DatePickerDialog(
                                context,
                                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                    dateNaissance = LocalDate.of(year, month + 1, dayOfMonth)
                                },
                                dateNaissance?.year ?: LocalDate.now().year,
                                (dateNaissance?.monthValue ?: 1) - 1,
                                dateNaissance?.dayOfMonth ?: 1
                            ).show()
                        }
                    ) {
                        Text(dateNaissance?.format(dateFormatter) ?: "Sélectionner")
                    }
                    if (dateNaissance != null) {
                        IconButton(onClick = { dateNaissance = null }) {
                            Icon(Icons.Default.Clear, "Effacer la date")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sexe
                Text(
                    text = "Sexe:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sexe == null,
                            onClick = { sexe = null }
                        )
                        Text("Tous", modifier = Modifier.padding(start = 8.dp))
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sexe == Sexe.HOMME,
                            onClick = { sexe = Sexe.HOMME }
                        )
                        Text("Homme", modifier = Modifier.padding(start = 8.dp))
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sexe == Sexe.FEMME,
                            onClick = { sexe = Sexe.FEMME }
                        )
                        Text("Femme", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onApplyFilters(
                        PatientFilters(
                            nom = nom,
                            prenom = prenom,
                            sexe = sexe,
                            dateNaissance = dateNaissance
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("Appliquer")
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = {
                        onClearFilters()
                        onDismiss()
                    }
                ) {
                    Text("Réinitialiser")
                }
                TextButton(onClick = onDismiss) {
                    Text("Annuler")
                }
            }
        }
    )
} 