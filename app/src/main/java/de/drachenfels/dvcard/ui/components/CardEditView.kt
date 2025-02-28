package de.drachenfels.dvcard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.R
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.ui.theme.DigtalBusinessCardTheme

/**
 * Composable for editing a business card
 *
 * @param card The business card to edit
 * @param onDeleteClick Callback when the card is deleted (null = no delete function)
 * @param onChange Callback when the card is modified
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditView(
    card: BusinessCard,
    onDeleteClick: (() -> Unit)?,
    onChange: (BusinessCard) -> Unit
) {
    var isPrivate by remember(card) { mutableStateOf(card.isPrivate) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        // Card type selection
        CardTypeSelector(
            isPrivate = isPrivate,
            onCardTypeChange = { 
                isPrivate = it
                onChange(card.copy(isPrivate = it))
            }
        )

        // Personal information section
        PersonalInfoSection(card, onChange)
        
        // Contact information section
        ContactInfoSection(card, onChange)
        
        // Address information section
        AddressInfoSection(card, onChange)

        // Delete button
        if (onDeleteClick != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.card_delete))
                }
            }
        }
    }
}

@Composable
private fun CardTypeSelector(
    isPrivate: Boolean,
    onCardTypeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.card_type_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Segmented control for card type
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Business button
            OutlinedButton(
                onClick = { onCardTypeChange(false) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (!isPrivate) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    contentColor = if (!isPrivate) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                ),
                border = if (!isPrivate) null else ButtonDefaults.outlinedButtonBorder(enabled = true)
            ) {
                Text(stringResource(R.string.card_type_business))
            }

            // Private button
            OutlinedButton(
                onClick = { onCardTypeChange(true) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isPrivate) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    contentColor = if (isPrivate) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                ),
                border = if (isPrivate) null else ButtonDefaults.outlinedButtonBorder(enabled = true)
            ) {
                Text(stringResource(R.string.card_type_private))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonalInfoSection(
    card: BusinessCard,
    onChange: (BusinessCard) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Card title
        var title by remember(card) { mutableStateOf(card.title) }
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title_label)) },
            placeholder = { Text(stringResource(R.string.title_placeholder)) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent{ if (!it.isFocused) onChange(card.copy(title = title)) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Name
        var name by remember(card) { mutableStateOf(card.name) }
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.name_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent{ if (!it.isFocused) onChange(card.copy(name = name)) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Position
        var position by remember(card) { mutableStateOf(card.position) }
        OutlinedTextField(
            value = position,
            onValueChange = { position = it },
            label = { Text(stringResource(R.string.position_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent{ if (!it.isFocused) onChange(card.copy(position = position)) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Company
        var company by remember(card) { mutableStateOf(card.company) }
        OutlinedTextField(
            value = company,
            onValueChange = { company = it },
            label = { Text(stringResource(R.string.company_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent{ if (!it.isFocused) onChange(card.copy(company = company)) },
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactInfoSection(
    card: BusinessCard,
    onChange: (BusinessCard) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Phone
        var phone by remember(card) { mutableStateOf(card.phone) }
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(stringResource(R.string.phone_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent{ if (!it.isFocused) onChange(card.copy(phone = phone)) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        var email by remember(card) { mutableStateOf(card.email) }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent{ if (!it.isFocused) onChange(card.copy(email = email)) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Website
        var website by remember(card) { mutableStateOf(card.website) }
        OutlinedTextField(
            value = website,
            onValueChange = { website = it },
            label = { Text(stringResource(R.string.website_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent{ if (!it.isFocused) onChange(card.copy(website = website)) },
            singleLine = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressInfoSection(
    card: BusinessCard,
    onChange: (BusinessCard) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Address header
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.address_section_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Street
        var street by remember(card) { mutableStateOf(card.street) }
        OutlinedTextField(
            value = street,
            onValueChange = { street = it },
            label = { Text(stringResource(R.string.street_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent{ if (!it.isFocused) onChange(card.copy(street = street)) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Postal code and city in one row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var postalCode by remember(card) { mutableStateOf(card.postalCode) }
            OutlinedTextField(
                value = postalCode,
                onValueChange = { postalCode = it },
                label = { Text(stringResource(R.string.postal_code_label)) },
                modifier = Modifier
                    .weight(0.4f)
                    .onFocusEvent{ if (!it.isFocused) onChange(card.copy(postalCode = postalCode)) },
                singleLine = true
            )

            var city by remember(card) { mutableStateOf(card.city) }
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text(stringResource(R.string.city_label)) },
                modifier = Modifier
                    .weight(0.6f)
                    .onFocusEvent{ if (!it.isFocused) onChange(card.copy(city = city)) },
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Country
        var country by remember(card) { mutableStateOf(card.country) }
        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            label = { Text(stringResource(R.string.country_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusEvent{ if (!it.isFocused) onChange(card.copy(country = country)) },
            singleLine = true
        )
    }
}

// Sample data for previews
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

@Preview(showBackground = true, name = "New Card", heightDp = 1000)
@Composable
fun NewCardEditViewPreview() {
    DigtalBusinessCardTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            CardEditView(
                card = BusinessCard(), // Empty card
                onDeleteClick = {},
                onChange = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Private Card", heightDp = 1000)
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