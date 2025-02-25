package de.drachenfels.dvcard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.ui.theme.DigtalBusinessCardTheme
import de.drachenfels.dvcard.util.logger.Log
import de.drachenfels.dvcard.util.logger.LogConfig

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
    
    var street by remember { mutableStateOf(card.street) }
    var postalCode by remember { mutableStateOf(card.postalCode) }
    var city by remember { mutableStateOf(card.city) }
    var country by remember { mutableStateOf(card.country) }
    
    var isPrivate by remember { mutableStateOf(card.isPrivate) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
        
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
        
        // Neue Adressfelder
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Adresse",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        OutlinedTextField(
            value = street,
            onValueChange = { street = it },
            label = { Text("Straße und Hausnummer") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = postalCode,
                onValueChange = { postalCode = it },
                label = { Text("PLZ") },
                modifier = Modifier.weight(0.4f),
                singleLine = true
            )
            
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Ort") },
                modifier = Modifier.weight(0.6f),
                singleLine = true
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            label = { Text("Land") },
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
                    Log.d(LogConfig.TAG_UI, "Speichere Karte: ${card.id}")
                    onSaveClick(
                        card.copy(
                            name = name,
                            position = position,
                            company = company,
                            phone = phone,
                            email = email,
                            website = website,
                            street = street,
                            postalCode = postalCode,
                            city = city,
                            country = country,
                            isPrivate = isPrivate
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

// Beispieldaten für Preview
private val sampleCard = BusinessCard(
    id = 1,
    name = "Max Mustermann",
    position = "Software Developer",
    company = "Muster GmbH",
    phone = "+49 123 456789",
    email = "max@example.com",
    website = "www.example.com",
    street = "Musterstraße 123",
    postalCode = "12345",
    city = "Musterstadt",
    country = "Deutschland",
    isPrivate = false
)

@Preview(showBackground = true)
@Composable
fun CardEditViewPreview() {
    DigtalBusinessCardTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            CardEditView(
                card = sampleCard,
                onSaveClick = {},
                onDeleteClick = {},
                onCancel = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Neue Karte")
@Composable
fun NewCardEditViewPreview() {
    DigtalBusinessCardTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            CardEditView(
                card = BusinessCard(), // Leere Karte
                onSaveClick = {},
                onDeleteClick = null, // Keine Löschfunktion
                onCancel = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Private Karte")
@Composable
fun PrivateCardEditViewPreview() {
    DigtalBusinessCardTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            CardEditView(
                card = sampleCard.copy(isPrivate = true),
                onSaveClick = {},
                onDeleteClick = {},
                onCancel = {}
            )
        }
    }
}