package com.example.myapplication.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

data class CountryCode(
    val name: String,
    val code: String,
    val flag: String
)

val countryCodes = listOf(
    CountryCode("Cameroun", "+237", "🇨🇲"),
    CountryCode("France", "+33", "🇫🇷"),
    CountryCode("États-Unis", "+1", "🇺🇸"),
    CountryCode("Canada", "+1", "🇨🇦"),
    CountryCode("Royaume-Uni", "+44", "🇬🇧"),
    CountryCode("Allemagne", "+49", "🇩🇪"),
    CountryCode("Belgique", "+32", "🇧🇪"),
    CountryCode("Suisse", "+41", "🇨🇭"),
    CountryCode("Maroc", "+212", "🇲🇦"),
    CountryCode("Sénégal", "+221", "🇸🇳"),
    CountryCode("Côte d'Ivoire", "+225", "🇨🇮"),
    CountryCode("Mali", "+223", "🇲🇱"),
    CountryCode("Congo", "+242", "🇨🇬"),
    CountryCode("Gabon", "+241", "🇬🇦")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberInput(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showCountryDialog by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf(countryCodes[0]) }
    var localPhoneNumber by remember { mutableStateOf(phoneNumber.substringAfter(" ", "")) }
    var isFocused by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(
                    BorderStroke(
                        1.dp,
                        if (isError) MaterialTheme.colorScheme.error
                        else if (isFocused) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outline
                    ),
                    RoundedCornerShape(4.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indicatif pays
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .fillMaxHeight()
                        .clickable { showCountryDialog = true }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedCountry.flag} ${selectedCountry.code}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Séparateur vertical
                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .padding(vertical = 8.dp)
                )

                // Champ de numéro
                BasicTextField(
                    value = localPhoneNumber,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() || it == ' ' }) {
                            localPhoneNumber = newValue
                            onPhoneNumberChange("${selectedCountry.code} $newValue")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .onFocusChanged { isFocused = it.isFocused },
                    textStyle = MaterialTheme.typography.bodyLarge.merge(
                        TextStyle(color = MaterialTheme.colorScheme.onSurface)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                )
            }

            // Label flottant
            if (localPhoneNumber.isEmpty() && !isFocused) {
                Text(
                    text = "Numéro de téléphone",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .padding(start = 90.dp) // Pour aligner avec le champ de numéro
                )
            }
        }

        if (isError) {
            Text(
                text = "Veuillez entrer un numéro de téléphone valide",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        // Dialog de sélection du pays
        if (showCountryDialog) {
            Dialog(onDismissRequest = { showCountryDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        items(countryCodes) { country ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCountry = country
                                        onPhoneNumberChange("${country.code} $localPhoneNumber")
                                        showCountryDialog = false
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${country.flag}  ${country.name}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = country.code,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 