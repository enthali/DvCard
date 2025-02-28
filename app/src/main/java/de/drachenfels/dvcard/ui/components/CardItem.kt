package de.drachenfels.dvcard.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.drachenfels.dvcard.R
import de.drachenfels.dvcard.data.model.BusinessCard
import de.drachenfels.dvcard.ui.theme.DigtalBusinessCardTheme

/**
 * Composable for a single business card item in the list
 *
 * @param card The business card to display
 * @param onQrCodeClick Callback when the QR code button is clicked
 * @param onDeleteClick Callback when the card is deleted
 * @param onChange Callback when the card is edited
 */
@Composable
fun CardItem(
    card: BusinessCard,
    onQrCodeClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onChange: (BusinessCard) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Card Header
            CardHeader(
                card = card,
                onQrCodeClick = onQrCodeClick
            )

            // Card Content
            CardContent(
                card = card,
                modifier = Modifier.clickable(onClick = onQrCodeClick)
            )

            // Card Footer (Expand/Collapse control)
            CardFooter(
                isExpanded = card.isExpanded,
                onClick = {
                    val newExpandedState = !card.isExpanded
                    onChange(card.copy(isExpanded = newExpandedState))
                }
            )

            // Expandable Edit Section
            CardEditSection(
                isExpanded = card.isExpanded,
                card = card,
                onDeleteClick = onDeleteClick,
                onChange = onChange
            )
        }
    }
}

@Composable
private fun CardHeader(
    card: BusinessCard,
    onQrCodeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Card type (Private/Business)
        Text(
            text = if (card.isPrivate) stringResource(R.string.card_type_private) else stringResource(R.string.card_type_business),
            style = MaterialTheme.typography.labelMedium,
            color = if (card.isPrivate)
                MaterialTheme.colorScheme.secondary
            else
                MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        // QR-Code Icon
        IconButton(onClick = onQrCodeClick) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = stringResource(R.string.qr_code_show),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun CardContent(
    card: BusinessCard,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        // Title/Name section
        CardTitleSection(card)

        // Job section (if available)
        CardJobSection(card)

        // Contact data
        if (card.phone.isNotEmpty() || card.email.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            CardContactSection(card)
        }

        // Address data
        if (card.street.isNotEmpty() || card.city.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            CardAddressSection(card)
        }
    }
}

@Composable
private fun CardTitleSection(card: BusinessCard) {
    // Title or name, depending on what is set
    val displayTitle = if (card.title.isNotEmpty()) card.title else card.name
    Text(
        text = displayTitle,
        style = MaterialTheme.typography.titleLarge
    )

    // If a title is set, we also show the name
    if (card.title.isNotEmpty()) {
        Text(
            text = card.name,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun CardJobSection(card: BusinessCard) {
    if (card.position.isNotEmpty()) {
        Text(
            text = card.position,
            style = MaterialTheme.typography.bodyMedium
        )
    }
    if (card.company.isNotEmpty()) {
        Text(
            text = card.company,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun CardContactSection(card: BusinessCard) {
    if (card.phone.isNotEmpty()) {
        Text(
            text = stringResource(R.string.phone_prefix, card.phone),
            style = MaterialTheme.typography.bodySmall
        )
    }

    if (card.email.isNotEmpty()) {
        Text(
            text = stringResource(R.string.email_prefix, card.email),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun CardAddressSection(card: BusinessCard) {
    if (card.street.isNotEmpty()) {
        Text(
            text = card.street,
            style = MaterialTheme.typography.bodySmall
        )
    }

    val locationText = buildString {
        if (card.postalCode.isNotEmpty()) {
            append(card.postalCode)
            append(" ")
        }
        if (card.city.isNotEmpty()) {
            append(card.city)
        }
    }

    if (locationText.isNotEmpty()) {
        Text(
            text = locationText,
            style = MaterialTheme.typography.bodySmall
        )
    }

    if (card.country.isNotEmpty()) {
        Text(
            text = card.country,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun CardFooter(
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left divider
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        )

        // Arrow up/down
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) stringResource(R.string.card_collapse) else stringResource(R.string.card_edit),
            tint = MaterialTheme.colorScheme.primary
        )

        // Right divider
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun CardEditSection(
    isExpanded: Boolean,
    card: BusinessCard,
    onDeleteClick: () -> Unit,
    onChange: (BusinessCard) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier.fillMaxWidth()
        ) {
            CardEditView(
                card = card,
                onDeleteClick = onDeleteClick,
                onChange = onChange
            )
        }
    }
}

// Preview functions 
@Preview(showBackground = true)
@Composable
fun CardItemPreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CardItem(
                card = sampleCard,
                onQrCodeClick = {},
                onDeleteClick = {},
                onChange = {}
            )
        }
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

private val privateCard = BusinessCard(
    id = 2,
    title = "Meine Privatkarte",
    name = "Erika Mustermann",
    position = "",
    company = "",
    phone = "+49 987 654321",
    email = "erika@example.com",
    website = "",
    street = "Privatweg 42",
    postalCode = "54321",
    city = "Beispielstadt",
    country = "Deutschland",
    isPrivate = true
)

private val minimalCard = BusinessCard(
    id = 3,
    title = "",
    name = "John Doe",
    phone = "+49 555 123456",
    email = "john@example.com"
)

@Preview(showBackground = true, name = "Card without title")
@Composable
fun CardItemNoTitlePreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CardItem(
                card = minimalCard,
                onQrCodeClick = {},
                onDeleteClick = {},
                onChange = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Expanded Card", heightDp = 1300 )
@Composable
fun ExpandedCardItemPreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CardItem(
                card = sampleCard.copy(isExpanded = true),
                onQrCodeClick = {},
                onDeleteClick = {},
                onChange = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Private Card")
@Composable
fun PrivateCardItemPreview() {
    DigtalBusinessCardTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CardItem(
                card = privateCard,
                onQrCodeClick = {},
                onDeleteClick = {},
                onChange = {}
            )
        }
    }
}