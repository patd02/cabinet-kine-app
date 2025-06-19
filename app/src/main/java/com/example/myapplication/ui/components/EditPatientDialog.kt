package com.example.myapplication.ui.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Patient
import com.example.myapplication.data.Sexe
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EditPatientDialog(
    patient: Patient,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, String, Sexe, LocalDate, String, String, String) -> Unit
) {
    var nom by remember { mutableStateOf(patient.nom) }
    var prenom by remember { mutableStateOf(patient.prenom) }
    var sexe by remember { mutableStateOf(patient.sexe) }
    var dateNaissance by remember { mutableStateOf(patient.dateNaissance) }
    var profession by remember { mutableStateOf(patient.profession) }
    var email by remember { mutableStateOf(patient.email) }
    var phoneNumber by remember { mutableStateOf(patient.phoneNumber) }
    
    var nomError by remember { mutableStateOf(false) }
    var prenomError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var professionError by remember { mutableStateOf(false) }
    var phoneNumberError by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modifier le patient") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                OutlinedTextField(
                    value = nom,
                    onValueChange = { 
                        nom = it
                        nomError = it.isBlank()
                    },
                    label = { Text("Nom") },
                    isError = nomError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (nomError) {
                    Text(
                        text = "Le nom est requis",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = prenom,
                    onValueChange = { 
                        prenom = it
                        prenomError = it.isBlank()
                    },
                    label = { Text("Prénom") },
                    isError = prenomError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (prenomError) {
                    Text(
                        text = "Le prénom est requis",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Column {
                    Text("Sexe:")
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sexe == Sexe.HOMME,
                            onClick = { sexe = Sexe.HOMME }
                        )
                        Text("Homme")
                        RadioButton(
                            selected = sexe == Sexe.FEMME,
                            onClick = { sexe = Sexe.FEMME }
                        )
                        Text("Femme")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                dateNaissance = LocalDate.of(year, month + 1, dayOfMonth)
                            },
                            dateNaissance.year,
                            dateNaissance.monthValue - 1,
                            dateNaissance.dayOfMonth
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Date de naissance: ${dateNaissance.format(dateFormatter)}")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = profession,
                    onValueChange = { 
                        profession = it
                        professionError = it.isBlank()
                    },
                    label = { Text("Profession") },
                    isError = professionError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (professionError) {
                    Text(
                        text = "La profession est requise",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        emailError = !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    },
                    label = { Text("Email") },
                    isError = emailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                if (emailError) {
                    Text(
                        text = "Email invalide",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                PhoneNumberInput(
                    phoneNumber = phoneNumber,
                    onPhoneNumberChange = { 
                        phoneNumber = it
                        phoneNumberError = it.substringAfter(" ").length < 9
                    },
                    isError = phoneNumberError,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    nomError = nom.isBlank()
                    prenomError = prenom.isBlank()
                    emailError = !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    professionError = profession.isBlank()
                    phoneNumberError = phoneNumber.substringAfter(" ").length < 9
                    
                    if (!nomError && !prenomError && !emailError && !professionError && !phoneNumberError) {
                        onConfirm(patient.id, nom, prenom, sexe, dateNaissance, profession, email, phoneNumber)
                        onDismiss()
                    }
                }
            ) {
                Text("Modifier")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
} 