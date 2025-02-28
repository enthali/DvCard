package de.drachenfels.dvcard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.ui.theme.DigtalBusinessCardTheme

/**
 * Composable für die Bearbeitung einer Visitenkarte
 *
 * @param card Die zu bearbeitende Visitenkarte
 * @param onSaveClick Callback wenn die Karte gespeichert wird (null = keine Speicherung)
 * @param onDeleteClick Callback wenn die Karte gelöscht wird (null = keine Löschfunktion)
 * @param onCancel Callback wenn die Bearbeitung abgebrochen wird (null = keine Abbruch-Funktion)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditView(
    card: BusinessCard,
    onDeleteClick: () -> Unit,
    onChange: (BusinessCard) -> Unit // Generischer Callback
) {
    // Hier müssen wir card als Key für remember verwenden, damit die Werte
    // aktualisiert werden, wenn eine neue Karte angezeigt wird
    val title by remember(card) { mutableStateOf(card.title) }
    val name by remember(card) { mutableStateOf(card.name) }
    val position by remember(card) { mutableStateOf(card.position) }
    val company by remember(card) { mutableStateOf(card.company) }
    val phone by remember(card) { mutableStateOf(card.phone) }
    val email by remember(card) { mutableStateOf(card.email) }
    val website by remember(card) { mutableStateOf(card.website) }
    val street by remember(card) { mutableStateOf(card.street) }
    val postalCode by remember(card) { mutableStateOf(card.postalCode) }
    val city by remember(card) { mutableStateOf(card.city) }
    val country by remember(card) { mutableStateOf(card.country) }
    var isPrivate by remember(card) { mutableStateOf(card.isPrivate) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        // Kartentyp-Auswahl mit Segmented Control
        Text(
            text = "Kartentyp",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Segmented Control für Kartentyp
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Geschäftlich Button
            OutlinedButton(
                onClick = { isPrivate = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (!isPrivate) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    contentColor = if (!isPrivate) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                ),
                border = if (!isPrivate) null else ButtonDefaults.outlinedButtonBorder(enabled = true)
            ) {
                Text("Geschäftlich")
                onChange(card.copy(isPrivate = isPrivate))
            }

            // Privat Button
            OutlinedButton(
                onClick = { isPrivate = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isPrivate) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    contentColor = if (isPrivate) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                ),
                border = if (isPrivate) null else ButtonDefaults.outlinedButtonBorder(enabled = true)
            ) {
                Text("Privat")
                onChange(card.copy(isPrivate = isPrivate))
            }
        }

        // Eingabefelder
        OutlinedTextField(
            value = title,
            onValueChange = {onChange(card.copy(title = it))},
            label = { Text("Titel der Karte") },
            placeholder = { Text("Optional - wird statt Name angezeigt, wenn gesetzt") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { onChange(card.copy(name = it)) },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = position,
            onValueChange = { onChange(card.copy(position = it)) },
            label = { Text("Position") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = company,
            onValueChange = { onChange(card.copy(company = it))  },
            label = { Text("Firma") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { onChange(card.copy(phone = it)) },
            label = { Text("Telefon") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { onChange(card.copy(email = it))   },
            label = { Text("E-Mail") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = website,
            onValueChange = { onChange(card.copy(website = it))   },
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
            onValueChange = { onChange(card.copy(street = it))  },
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
                onValueChange = { onChange(card.copy(postalCode = it)) },
                label = { Text("PLZ") },
                modifier = Modifier.weight(0.4f),
                singleLine = true
            )

            OutlinedTextField(
                value = city,
                onValueChange = { onChange(card.copy(city = it)) },
                label = { Text("Ort") },
                modifier = Modifier.weight(0.6f),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = country,
            onValueChange = { onChange(card.copy(country = it)) },
            label = { Text("Land") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Speichern-Button mit aktualisierter Funktion
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
        ) {
            if (onDeleteClick != null) {
                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )) {
                    Text("Löschen")
                }
            }
        }
    }
}

// Beispieldaten für Preview
private val sampleCard = BusinessCard(
    id = 1,
    title = "Geschäftskarte",
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

@Preview(showBackground = true, heightDp = 1000)
@Composable
fun CardEditViewPreview() {
    DigtalBusinessCardTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            CardEditView(
                card = sampleCard,
                onDeleteClick = {},
                onChange = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Neue Karte", heightDp = 1000)
@Composable
fun NewCardEditViewPreview() {
    DigtalBusinessCardTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            CardEditView(
                card = BusinessCard(), // Leere Karte
                onDeleteClick = {},
                onChange = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Private Karte", heightDp = 1000)
@Composable
fun PrivateCardEditViewPreview() {
    DigtalBusinessCardTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            CardEditView(
                card = sampleCard.copy(isPrivate = true),
                onDeleteClick = {},
                onChange = {}
            )
        }
    }
}