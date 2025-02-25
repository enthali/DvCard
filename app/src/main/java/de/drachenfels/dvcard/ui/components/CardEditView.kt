package de.drachenfels.dvcard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.data.model.BusinessCard

/**
 * Composable für die Bearbeitung einer Visitenkarte
 * 
 * @param card Die zu bearbeitende Visitenkarte
 * @param onSaveClick Callback wenn die Karte gespeichert wird
 * @param onDeleteClick Callback wenn die Karte gelöscht wird (null = keine Löschfunktion)
 * @param onCancel Callback wenn die Bearbeitung abgebrochen wird
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditView(
    card: BusinessCard,
    onSaveClick: (BusinessCard) -> Unit,
    onDeleteClick: (() -> Unit)?,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(card.name) }
    var position by remember { mutableStateOf(card.position) }
    var company by remember { mutableStateOf(card.company) }
    var phone by remember { mutableStateOf(card.phone) }
    var email by remember { mutableStateOf(card.email) }
    var website by remember { mutableStateOf(card.website) }
    var isPrivate by remember { mutableStateOf(card.isPrivate) }
    var countryCode by remember { mutableStateOf(card.countryCode) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Divider(modifier = Modifier.padding(bottom = 16.dp))
        
        // Eingabefelder
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = position,
            onValueChange = { position = it },
            label = { Text("Position") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = company,
            onValueChange = { company = it },
            label = { Text("Firma") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefon") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-Mail") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = website,
            onValueChange = { website = it },
            label = { Text("Webseite") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = countryCode,
            onValueChange = { countryCode = it },
            label = { Text("Ländercode") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // Checkbox für Private/Geschäftliche Karte
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isPrivate,
                onCheckedChange = { isPrivate = it }
            )
            Text("Private Karte")
        }
        
        // Aktionsbuttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Abbrechen")
            }
            
            if (onDeleteClick != null) {
                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Löschen")
                }
            }
            
            Button(
                onClick = {
                    onSaveClick(
                        card.copy(
                            name = name,
                            position = position,
                            company = company,
                            phone = phone,
                            email = email,
                            website = website,
                            isPrivate = isPrivate,
                            countryCode = countryCode
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Speichern")
            }
        }
    }
}