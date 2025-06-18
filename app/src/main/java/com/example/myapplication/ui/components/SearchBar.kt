package com.example.myapplication.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.Sexe

@Composable
fun SearchBar(
    searchQuery: String,
    selectedSexe: Sexe?,
    onSearchQueryChange: (String) -> Unit,
    onSexeFilterChange: (Sexe?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                placeholder = { Text("Rechercher un patient...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Rechercher") },
                trailingIcon = {
                    Row {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, "Effacer la recherche")
                            }
                        }
                        IconButton(onClick = { showFilters = !showFilters }) {
                            Icon(
                                Icons.Outlined.FilterList,
                                contentDescription = "Filtres",
                                tint = if (selectedSexe != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }

        AnimatedVisibility(visible = showFilters) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "Filtrer par sexe:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    RadioButton(
                        selected = selectedSexe == null,
                        onClick = { onSexeFilterChange(null) }
                    )
                    Text("Tous", modifier = Modifier.padding(start = 8.dp))
                    
                    RadioButton(
                        selected = selectedSexe == Sexe.HOMME,
                        onClick = { onSexeFilterChange(Sexe.HOMME) }
                    )
                    Text("Homme", modifier = Modifier.padding(start = 8.dp))
                    
                    RadioButton(
                        selected = selectedSexe == Sexe.FEMME,
                        onClick = { onSexeFilterChange(Sexe.FEMME) }
                    )
                    Text("Femme", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
} 