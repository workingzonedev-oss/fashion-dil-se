package com.fashiondilse.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.GppGood
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fashiondilse.app.ui.components.EmptyStateCard
import com.fashiondilse.app.ui.components.HeaderBar
import com.fashiondilse.app.ui.components.MenuRow
import com.fashiondilse.app.ui.components.PrimaryCtaButton
import com.fashiondilse.app.ui.components.ScreenPadding
import com.fashiondilse.app.ui.components.SectionCard
import com.fashiondilse.app.ui.components.SectionHeader
import com.fashiondilse.app.ui.theme.LocalThemeState

@Composable
fun ProfileScreen(
    onOrders: () -> Unit,
    onAddresses: () -> Unit,
    onOffers: () -> Unit,
    onNotifications: () -> Unit,
    onHelp: () -> Unit,
    onSettings: () -> Unit,
    onWishlist: () -> Unit,
    onPaymentMethods: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            HeaderBar(title = "Profile")
        }
        item {
            SectionCard(title = "Fashion Dil Se Account") {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), shape = MaterialTheme.shapes.large),
                ) {
                    Text(
                        text = "S",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                Text(text = "Style Profile", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Name, email, and phone can live here once authentication is connected.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item { MenuRow(title = "My Orders", subtitle = "Track active and delivered orders", onClick = onOrders) }
        item { MenuRow(title = "Wishlist", subtitle = "Saved styles and favourites", onClick = onWishlist) }
        item { MenuRow(title = "Saved Addresses", subtitle = "Manage delivery locations", onClick = onAddresses) }
        item { MenuRow(title = "Payment Methods", subtitle = "UPI, cards, wallets & more", onClick = onPaymentMethods) }
        item { MenuRow(title = "Coupons", subtitle = "Coupons, deals, and bank offers", onClick = onOffers) }
        item { MenuRow(title = "Notifications", subtitle = "Order updates and offer alerts", onClick = onNotifications) }
        item { MenuRow(title = "Help & Support", subtitle = "Returns, delivery, and payment help", onClick = onHelp) }
        item { MenuRow(title = "Settings", subtitle = "Language, privacy, and preferences", onClick = onSettings) }
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onTermsConditions: () -> Unit,
) {
    val context = LocalContext.current
    val themeState = LocalThemeState.current
    var notificationsEnabled by rememberSaveable { mutableStateOf(true) }
    var selectedLanguage by rememberSaveable { mutableStateOf("English") }
    var showLanguagePicker by rememberSaveable { mutableStateOf(false) }
    var showEditProfile by rememberSaveable { mutableStateOf(false) }
    var showChangePassword by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }
    var showLogoutConfirm by rememberSaveable { mutableStateOf(false) }
    var profileName by rememberSaveable { mutableStateOf("Fashion Dil Se User") }
    var profileEmail by rememberSaveable { mutableStateOf("") }

    if (showLanguagePicker) {
        val languages = listOf("English", "Hindi", "Marathi", "Tamil", "Telugu", "Bengali", "Gujarati")
        AlertDialog(
            onDismissRequest = { showLanguagePicker = false },
            title = { Text("Select Language") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    languages.forEach { lang ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedLanguage = lang
                                    showLanguagePicker = false
                                    Toast.makeText(context, "Language set to $lang", Toast.LENGTH_SHORT).show()
                                },
                            color = if (lang == selectedLanguage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        ) {
                            Text(
                                text = lang,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (lang == selectedLanguage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguagePicker = false }) { Text("Cancel") }
            },
        )
    }

    if (showEditProfile) {
        AlertDialog(
            onDismissRequest = { showEditProfile = false },
            title = { Text("Edit Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    androidx.compose.material3.OutlinedTextField(
                        value = profileName,
                        onValueChange = { profileName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = profileEmail,
                        onValueChange = { profileEmail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showEditProfile = false
                    Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfile = false }) { Text("Cancel") }
            },
        )
    }

    if (showChangePassword) {
        var currentPass by rememberSaveable { mutableStateOf("") }
        var newPass by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showChangePassword = false },
            title = { Text("Change Password") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    androidx.compose.material3.OutlinedTextField(
                        value = currentPass,
                        onValueChange = { currentPass = it },
                        label = { Text("Current Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("New Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showChangePassword = false
                    Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                }) { Text("Update") }
            },
            dismissButton = {
                TextButton(onClick = { showChangePassword = false }) { Text("Cancel") }
            },
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Account?") },
            text = { Text("This will permanently delete your Fashion Dil Se account and all data. This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    Toast.makeText(context, "Account deletion requested", Toast.LENGTH_SHORT).show()
                    onLogout()
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
        )
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Logout?") },
            text = { Text("Are you sure you want to logout from Fashion Dil Se?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutConfirm = false
                    onLogout()
                }) { Text("Logout") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancel") }
            },
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { HeaderBar(title = "Settings", showBack = true, onBack = onBack) }
        item { SectionHeader(title = "Account") }
        item {
            SettingsClickRow(
                title = "Edit Profile",
                subtitle = profileName,
                icon = Icons.Outlined.Edit,
                onClick = { showEditProfile = true },
            )
        }
        item {
            SettingsClickRow(
                title = "Change Password",
                subtitle = "Update your account password",
                icon = Icons.Outlined.Lock,
                onClick = { showChangePassword = true },
            )
        }
        item { SectionHeader(title = "Preferences") }
        item {
            SettingsClickRow(
                title = "Language",
                subtitle = selectedLanguage,
                icon = Icons.Outlined.Language,
                onClick = { showLanguagePicker = true },
            )
        }
        item {
            SettingsToggleRow(
                title = "Push Notifications",
                subtitle = if (notificationsEnabled) "Enabled" else "Disabled",
                icon = Icons.Outlined.Notifications,
                checked = notificationsEnabled,
                onCheckedChange = {
                    notificationsEnabled = it
                    Toast.makeText(
                        context,
                        if (it) "Notifications enabled" else "Notifications disabled",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )
        }
        item {
            SettingsToggleRow(
                title = "Dark Mode",
                subtitle = if (themeState.isDarkMode) "On" else "Off",
                icon = Icons.Outlined.DarkMode,
                checked = themeState.isDarkMode,
                onCheckedChange = { themeState.isDarkMode = it },
            )
        }
        item { SectionHeader(title = "Legal") }
        item {
            SettingsClickRow(
                title = "Privacy Policy",
                subtitle = "How we handle your data",
                icon = Icons.Outlined.GppGood,
                onClick = onPrivacyPolicy,
            )
        }
        item {
            SettingsClickRow(
                title = "Terms and Conditions",
                subtitle = "Usage terms for Fashion Dil Se",
                icon = Icons.Outlined.Description,
                onClick = onTermsConditions,
            )
        }
        item { SectionHeader(title = "Danger Zone") }
        item {
            SettingsClickRow(
                title = "Delete Account",
                subtitle = "Permanently remove your account",
                icon = Icons.Outlined.Delete,
                tint = MaterialTheme.colorScheme.error,
                onClick = { showDeleteConfirm = true },
            )
        }
        item {
            SettingsClickRow(
                title = "Logout",
                subtitle = "Sign out of your account",
                icon = Icons.AutoMirrored.Outlined.Logout,
                onClick = { showLogoutConfirm = true },
            )
        }
    }
}

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(ScreenPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HeaderBar(title = "Privacy Policy", showBack = true, onBack = onBack)
        Text(text = "Last Updated: March 2026", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        LegalSectionItem("1. Information We Collect", "We collect the following information when you use Fashion Dil Se:\n\n\u2022 Personal Information: Name, mobile number, email address, delivery addresses\n\u2022 Order Information: Products ordered, payment method, delivery preferences\n\u2022 Device Information: Device type, OS version, app version for analytics\n\u2022 Usage Data: Pages viewed, search queries, time spent (anonymized)")

        LegalSectionItem("2. How We Use Your Information", "Your information is used to:\n\n\u2022 Process and deliver your orders\n\u2022 Send order updates via SMS and push notifications\n\u2022 Personalize product recommendations\n\u2022 Improve app performance and user experience\n\u2022 Send promotional offers (with your consent)")

        LegalSectionItem("3. Data Sharing", "We do not sell your personal data. We share information only with:\n\n\u2022 Delivery partners to fulfil orders\n\u2022 Payment gateways for secure transactions\n\u2022 Analytics providers (anonymized data only)")

        LegalSectionItem("4. Data Security", "We implement industry-standard encryption (SSL/TLS) for data transmission. Payment information is processed by PCI-DSS compliant gateways and is never stored on our servers.")

        LegalSectionItem("5. Your Rights", "You can:\n\n\u2022 Access and download your personal data\n\u2022 Request correction of inaccurate data\n\u2022 Delete your account and associated data\n\u2022 Opt out of promotional communications\n\nContact us at support@fashiondilse.com for any privacy-related requests.")

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun TermsConditionsScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(ScreenPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HeaderBar(title = "Terms & Conditions", showBack = true, onBack = onBack)
        Text(text = "Last Updated: March 2026", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        LegalSectionItem("1. Acceptance of Terms", "By using Fashion Dil Se, you agree to these Terms and Conditions. If you do not agree, please do not use the app.")

        LegalSectionItem("2. Account Registration", "\u2022 You must provide a valid mobile number to create an account\n\u2022 You are responsible for maintaining the confidentiality of your OTP and account\n\u2022 You must be at least 13 years old to use this app")

        LegalSectionItem("3. Orders & Payments", "\u2022 All prices are in Indian Rupees (\u20B9) and include applicable taxes\n\u2022 We accept UPI, credit/debit cards, net banking, wallets, and Cash on Delivery\n\u2022 Orders are subject to product availability and delivery area coverage\n\u2022 We reserve the right to cancel orders due to pricing errors or stock issues")

        LegalSectionItem("4. Returns & Refunds", "\u2022 Returns are accepted within 7 days of delivery for most items\n\u2022 Items must be unused, unwashed, and in original packaging with tags\n\u2022 Refunds are processed within 5-7 business days to the original payment method\n\u2022 Non-returnable items: innerwear, beauty products, and customized items")

        LegalSectionItem("5. Delivery", "\u2022 Standard delivery: 5-7 business days\n\u2022 Express delivery: 2-3 business days (where available)\n\u2022 Free delivery on orders above \u20B9999\n\u2022 We deliver across India through our logistics partners")

        LegalSectionItem("6. Intellectual Property", "All content on Fashion Dil Se including logos, images, designs, and text is owned by us and protected under Indian copyright laws.")

        LegalSectionItem("7. Limitation of Liability", "Fashion Dil Se is not liable for delays caused by logistics partners, force majeure events, or third-party payment gateway issues.")

        LegalSectionItem("8. Contact", "For any queries regarding these terms:\n\nEmail: support@fashiondilse.com\nPhone: 1800-xxx-xxxx (Mon-Sat, 9AM-7PM)")

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun LegalSectionItem(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(text = body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun PaymentMethodsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var selectedMethod by rememberSaveable { mutableStateOf("UPI") }
    val methods = listOf(
        "UPI" to "Google Pay, PhonePe, Paytm UPI",
        "Credit / Debit Card" to "Visa, Mastercard, RuPay",
        "Net Banking" to "All major Indian banks",
        "Wallets" to "Paytm, Amazon Pay, Mobikwik",
        "Cash on Delivery" to "Pay when you receive your order",
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { HeaderBar(title = "Payment Methods", showBack = true, onBack = onBack) }
        item {
            Text(
                text = "Select your preferred payment method for checkout.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(methods) { (method, description) ->
            val isSelected = method == selectedMethod
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        selectedMethod = method
                        Toast.makeText(context, "$method selected", Toast.LENGTH_SHORT).show()
                    },
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = if (isSelected) 4.dp else 1.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            ),
                    )
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = method,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsClickRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = tint)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onCheckedChange(!checked) },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        }
    }
}

@Composable
fun HelpSupportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val helpItems = listOf(
        "FAQs" to "Find answers to the most common questions about Fashion Dil Se.",
        "Chat Support" to "Chat with our support team for instant help.",
        "Email Support" to "Send us an email at support@fashiondilse.com",
        "Call Support" to "Call us at 1800-xxx-xxxx (Mon-Sat, 9AM-7PM)",
        "Return Help" to "Learn about our 7-day easy return policy and process.",
        "Payment Issues" to "Facing payment failure? Get help resolving it.",
        "Delivery Issues" to "Track, delay, or missing delivery? We'll help.",
    )
    var expandedItem by rememberSaveable { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { HeaderBar(title = "Help & Support", showBack = true, onBack = onBack) }
        item {
            SectionCard(title = "How can we help you?") {
                Text(
                    text = "Tap on any topic below to get help. For urgent issues, use Chat or Call Support.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(helpItems) { (title, description) ->
            val isExpanded = expandedItem == title
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        expandedItem = if (isExpanded) null else title
                        when (title) {
                            "Email Support" -> {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:support@fashiondilse.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "Fashion Dil Se - Help Request")
                                }
                                runCatching { context.startActivity(intent) }
                            }
                            "Call Support" -> {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:1800000000")
                                }
                                runCatching { context.startActivity(intent) }
                            }
                        }
                    },
                color = if (isExpanded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 1.dp,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        )
                        Icon(
                            imageVector = Icons.Outlined.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (isExpanded) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SavedAddressesScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        HeaderBar(title = "Saved Addresses", showBack = true, onBack = onBack)
        EmptyStateCard(
            title = "No saved addresses yet",
            message = "Full address cards, edit controls, delete, and set-default actions live here.",
        )
        PrimaryCtaButton(text = "Add New Address", onClick = {})
    }
}
