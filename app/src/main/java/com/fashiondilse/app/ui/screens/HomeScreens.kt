package com.fashiondilse.app.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.rememberDrawerState
import com.fashiondilse.app.data.CategoryGroup
import com.fashiondilse.app.data.DiscoverItem
import com.fashiondilse.app.data.DiscoverSections
import com.fashiondilse.app.data.HomeCampaign
import com.fashiondilse.app.data.NotificationItem
import com.fashiondilse.app.data.OfferItem
import com.fashiondilse.app.data.ProductTemplate
import com.fashiondilse.app.data.budgetPicks
import com.fashiondilse.app.data.categoryGroups
import com.fashiondilse.app.data.filterOptions
import com.fashiondilse.app.data.heroCampaigns
import com.fashiondilse.app.data.homeCategories
import com.fashiondilse.app.data.listingFilters
import com.fashiondilse.app.data.searchSuggestions
import com.fashiondilse.app.data.shopByOccasion
import com.fashiondilse.app.data.sortOptions
import com.fashiondilse.app.data.trendingCollections
import com.fashiondilse.app.data.trustCues
import com.fashiondilse.app.ui.components.BudgetCard
import com.fashiondilse.app.ui.components.CampaignBanner
import com.fashiondilse.app.ui.components.CategoryOrb
import com.fashiondilse.app.ui.components.ChipWrap
import com.fashiondilse.app.ui.components.DetailRow
import com.fashiondilse.app.ui.components.EmptyStateCard
import com.fashiondilse.app.ui.components.FashionField
import com.fashiondilse.app.ui.components.HeaderBar
import com.fashiondilse.app.ui.components.HomeTopBar
import com.fashiondilse.app.ui.components.ProductTemplateCard
import com.fashiondilse.app.ui.components.ScreenPadding
import com.fashiondilse.app.ui.components.SectionCard
import com.fashiondilse.app.ui.components.SectionHeader
import com.fashiondilse.app.ui.components.TrustCueStrip
import com.fashiondilse.app.ui.components.CollectionCard
import com.fashiondilse.app.ui.theme.OutlineBeige
import com.fashiondilse.app.ui.theme.PitchBlack
import com.fashiondilse.app.ui.theme.SoftGold
import com.fashiondilse.app.ui.theme.WarmWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class HomeDrawerSection(
    val title: String,
    val subtitle: String,
) {
    Discover(
        title = "Home Menu",
        subtitle = "Open separate admin-driven discovery pages.",
    ),
    Shopping(
        title = "Shopping",
        subtitle = "Jump into items, carts, offers, and orders.",
    ),
    Account(
        title = "Account",
        subtitle = "Manage profile, support, and preferences.",
    ),
}

enum class HomeDrawerDestination(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val section: HomeDrawerSection,
) {
    TrendingCollections(
        title = "Trending Collections",
        subtitle = "Open the dedicated trends page.",
        icon = Icons.Outlined.Tune,
        section = HomeDrawerSection.Discover,
    ),
    BudgetPicks(
        title = "Budget Picks",
        subtitle = "Open the dedicated value picks page.",
        icon = Icons.Outlined.LocalOffer,
        section = HomeDrawerSection.Discover,
    ),
    NewArrivals(
        title = "New Arrivals",
        subtitle = "Open the dedicated arrivals page.",
        icon = Icons.Outlined.Inventory2,
        section = HomeDrawerSection.Discover,
    ),
    Bestsellers(
        title = "Bestsellers",
        subtitle = "Open the dedicated bestseller page.",
        icon = Icons.Outlined.ShoppingBag,
        section = HomeDrawerSection.Discover,
    ),
    RecommendedForYou(
        title = "Recommended for You",
        subtitle = "Open the dedicated recommendation page.",
        icon = Icons.Outlined.FavoriteBorder,
        section = HomeDrawerSection.Discover,
    ),
    ShopByOccasion(
        title = "Shop by Mood / Occasion",
        subtitle = "Open the dedicated occasion page.",
        icon = Icons.Outlined.Category,
        section = HomeDrawerSection.Discover,
    ),
    Orders(
        title = "My Orders",
        subtitle = "Track active and delivered orders",
        icon = Icons.Outlined.Inventory2,
        section = HomeDrawerSection.Shopping,
    ),
    Wishlist(
        title = "Wishlist",
        subtitle = "Saved styles and favorites",
        icon = Icons.Outlined.FavoriteBorder,
        section = HomeDrawerSection.Shopping,
    ),
    Cart(
        title = "Cart",
        subtitle = "Review items before checkout",
        icon = Icons.Outlined.ShoppingBag,
        section = HomeDrawerSection.Shopping,
    ),
    Offers(
        title = "Offers",
        subtitle = "Coupons, deals, and bank offers",
        icon = Icons.Outlined.LocalOffer,
        section = HomeDrawerSection.Shopping,
    ),
    Notifications(
        title = "Notifications",
        subtitle = "Order updates and offer alerts",
        icon = Icons.Outlined.NotificationsNone,
        section = HomeDrawerSection.Shopping,
    ),
    SavedAddresses(
        title = "Saved Addresses",
        subtitle = "Manage delivery locations",
        icon = Icons.Outlined.LocationOn,
        section = HomeDrawerSection.Account,
    ),
    HelpSupport(
        title = "Help & Support",
        subtitle = "Returns, delivery, and payment help",
        icon = Icons.AutoMirrored.Outlined.HelpOutline,
        section = HomeDrawerSection.Account,
    ),
    Settings(
        title = "Settings",
        subtitle = "Language, privacy, and preferences",
        icon = Icons.Outlined.Settings,
        section = HomeDrawerSection.Account,
    ),
    Profile(
        title = "Profile",
        subtitle = "Account and personal details",
        icon = Icons.Outlined.PersonOutline,
        section = HomeDrawerSection.Account,
    ),
    Logout(
        title = "Logout",
        subtitle = "Exit the current session",
        icon = Icons.AutoMirrored.Outlined.Logout,
        section = HomeDrawerSection.Account,
    ),
}

private val homeDrawerSections = listOf(
    HomeDrawerSection.Discover,
    HomeDrawerSection.Shopping,
    HomeDrawerSection.Account,
)

private val homeDrawerDestinations = listOf(
    HomeDrawerDestination.TrendingCollections,
    HomeDrawerDestination.BudgetPicks,
    HomeDrawerDestination.NewArrivals,
    HomeDrawerDestination.Bestsellers,
    HomeDrawerDestination.RecommendedForYou,
    HomeDrawerDestination.ShopByOccasion,
    HomeDrawerDestination.Orders,
    HomeDrawerDestination.Offers,
    HomeDrawerDestination.Notifications,
    HomeDrawerDestination.SavedAddresses,
    HomeDrawerDestination.HelpSupport,
    HomeDrawerDestination.Settings,
    HomeDrawerDestination.Logout,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    campaigns: List<HomeCampaign>,
    wishlistIds: Set<String>,
    searchQuery: String,
    searchResults: List<ProductTemplate>,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onImageSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onMenuDestinationClick: (HomeDrawerDestination) -> Unit,
    onCategoryClick: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onToggleWishlist: (String) -> Unit,
) {
    val isSearching = searchQuery.isNotBlank()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.background,
                drawerTonalElevation = 0.dp,
                modifier = Modifier.width(356.dp),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        DrawerBrandPanel()
                    }
                    homeDrawerSections.forEach { section ->
                        item {
                            DrawerSectionHeader(
                                title = section.title,
                                subtitle = section.subtitle,
                            )
                        }
                        items(homeDrawerDestinations.filter { it.section == section && it != HomeDrawerDestination.Logout }) { destination ->
                            DrawerDestinationCard(
                                destination = destination,
                                selected = false,
                                onClick = {
                                    scope.launch {
                                        drawerState.close()
                                        onMenuDestinationClick(destination)
                                    }
                                },
                            )
                        }
                    }
                    item {
                        HorizontalDivider(color = OutlineBeige.copy(alpha = 0.56f))
                    }
                    item {
                        DrawerDestinationCard(
                            destination = HomeDrawerDestination.Logout,
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onMenuDestinationClick(HomeDrawerDestination.Logout)
                                }
                            },
                        )
                    }
                }
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.padding(ScreenPadding),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    HomeTopBar(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        searchQuery = searchQuery,
                        onSearchQueryChange = onSearchQueryChange,
                        onSearchSubmit = onSearchSubmit,
                        onImageSearchClick = onImageSearchClick,
                        onNotificationsClick = onNotificationsClick,
                    )
                    if (isSearching) {
                        SectionHeader(
                            title = "Search Results",
                            subtitle = "Showing matches for \"$searchQuery\" on the same screen.",
                        )
                        if (searchResults.isEmpty()) {
                            EmptyStateCard(
                                title = "No matching products",
                                message = "Try another term or use the camera button for image search.",
                            )
                        } else {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                searchResults.forEach { product ->
                                    ProductTemplateCard(
                                        product = product,
                                        modifier = Modifier.fillMaxWidth(0.47f),
                                        isWishlisted = product.id in wishlistIds,
                                        onToggleWishlist = { onToggleWishlist(product.id) },
                                        onClick = { onProductClick(product.id) },
                                    )
                                }
                            }
                        }
                    } else {
                        CampaignAutoSlider(campaigns = campaigns)

                        TrustCueStrip(items = trustCues)

                        SectionHeader(
                            title = "Categories",
                            subtitle = "Quick access to the main shopping worlds.",
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(homeCategories) { category ->
                                CategoryOrb(title = category, onClick = { onCategoryClick(category) })
                            }
                        }
                        SectionCard(title = "Discover Pages") {
                            Text(
                                text = "Trending collections, budget picks, new arrivals, bestsellers, recommendations, and shop by occasion now open as separate pages from the sidebar.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DrawerBrandPanel() {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            PitchBlack,
                            SoftGold,
                        ),
                    ),
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Fashion Dil Se",
                style = MaterialTheme.typography.headlineSmall,
                color = WarmWhite,
            )
            Text(
                text = "Orders, offers, delivery details, and support ko ek clean sidebar se manage karo.",
                style = MaterialTheme.typography.bodyMedium,
                color = WarmWhite.copy(alpha = 0.84f),
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DrawerTrustPill(label = "Order Tracking")
                DrawerTrustPill(label = "Easy Returns")
                DrawerTrustPill(label = "Secure Payments")
            }
        }
    }
}

@Composable
private fun DrawerTrustPill(label: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = WarmWhite.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, WarmWhite.copy(alpha = 0.16f)),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium,
            color = WarmWhite,
        )
    }
}

@Composable
private fun DrawerSectionHeader(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DrawerDestinationCard(
    destination: HomeDrawerDestination,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val isLogout = destination == HomeDrawerDestination.Logout
    val containerColor = when {
        isLogout -> MaterialTheme.colorScheme.error.copy(alpha = 0.07f)
        selected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        isLogout -> MaterialTheme.colorScheme.error.copy(alpha = 0.28f)
        selected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
        else -> OutlineBeige.copy(alpha = 0.56f)
    }
    val iconTint = when {
        isLogout -> MaterialTheme.colorScheme.error
        selected -> MaterialTheme.colorScheme.primary
        else -> PitchBlack
    }
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        tonalElevation = if (selected) 4.dp else 0.dp,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = if (selected || isLogout) 0.12f else 0.08f)),
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = destination.title,
                    tint = iconTint,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = destination.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    if (selected) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.primary,
                        ) {
                            Text(
                                text = "Now",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
                Text(
                    text = destination.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = if (isLogout) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun CategoriesScreen(onSubcategoryClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            HeaderBar(title = "Categories")
        }
        item {
            Text(
                text = "Browse by category and tap any subcategory to see products",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(categoryGroups) { group ->
            CategoryGroupCard(group = group, onSubcategoryClick = onSubcategoryClick)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrendingCollectionsScreen(
    items: List<DiscoverItem>,
    onBack: () -> Unit,
) {
    DiscoveryPageFrame(
        title = "Trending Collections",
        subtitle = "Managed from the admin panel discover pages section.",
        onBack = onBack,
    ) {
        if (items.isEmpty()) {
            EmptyStateCard(
                title = "No trending collections yet",
                message = "Add trending collection entries from the admin panel to populate this page.",
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items.forEach { item ->
                    DiscoverEntryCard(
                        title = item.title,
                        subtitle = item.subtitle,
                        eyebrow = "Trending",
                        modifier = Modifier.fillMaxWidth(0.47f),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BudgetPicksScreen(
    items: List<DiscoverItem>,
    onBack: () -> Unit,
) {
    DiscoveryPageFrame(
        title = "Budget Picks",
        subtitle = "Admin-managed value picks for the home discovery system.",
        onBack = onBack,
    ) {
        if (items.isEmpty()) {
            EmptyStateCard(
                title = "No budget picks yet",
                message = "Add budget pick entries from the admin panel to populate this page.",
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items.forEach { item ->
                    DiscoverEntryCard(
                        title = item.title,
                        subtitle = item.subtitle,
                        eyebrow = "Budget Pick",
                        modifier = Modifier.fillMaxWidth(0.47f),
                    )
                }
            }
        }
    }
}

@Composable
fun ShopByOccasionScreen(
    items: List<DiscoverItem>,
    onBack: () -> Unit,
) {
    DiscoveryPageFrame(
        title = "Shop by Mood / Occasion",
        subtitle = "Occasion shortcuts are controlled from the admin panel.",
        onBack = onBack,
    ) {
        if (items.isEmpty()) {
            EmptyStateCard(
                title = "No occasions yet",
                message = "Add occasion entries from the admin panel to populate this page.",
            )
        } else {
            items.forEach { item ->
                SectionCard(title = item.title) {
                    Text(
                        text = item.subtitle.ifBlank { "Admin-managed occasion shortcut." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiscoveryProductFeedScreen(
    title: String,
    subtitle: String,
    emptyMessage: String,
    products: List<ProductTemplate>,
    wishlistIds: Set<String>,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onToggleWishlist: (String) -> Unit,
) {
    DiscoveryPageFrame(
        title = title,
        subtitle = subtitle,
        onBack = onBack,
    ) {
        if (products.isEmpty()) {
            EmptyStateCard(
                title = "No products mapped yet",
                message = emptyMessage,
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                products.forEach { product ->
                    ProductTemplateCard(
                        product = product,
                        modifier = Modifier.fillMaxWidth(0.47f),
                        isWishlisted = product.id in wishlistIds,
                        onToggleWishlist = { onToggleWishlist(product.id) },
                        onClick = { onProductClick(product.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscoveryPageFrame(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                HeaderBar(title = title, showBack = true, onBack = onBack)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                content()
            }
        }
    }
}

@Composable
private fun DiscoverEntryCard(
    title: String,
    subtitle: String,
    eyebrow: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, OutlineBeige.copy(alpha = 0.55f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            SoftGold.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.surface,
                        ),
                    ),
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = subtitle.ifBlank { "Managed from the admin panel." },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductListingScreen(
    selectedCategory: String,
    products: List<ProductTemplate>,
    wishlistIds: Set<String>,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onToggleWishlist: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                HeaderBar(
                    title = selectedCategory,
                    showBack = true,
                    onBack = onBack,
                    trailing = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Outlined.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Outlined.Tune, contentDescription = "Filter")
                        }
                    },
                )
                ChipWrap(items = listingFilters)
                SectionCard(title = "Sort & Filter Preview") {
                    ChipWrap(items = sortOptions.take(3))
                    ChipWrap(items = filterOptions.take(6))
                }
                if (products.isEmpty()) {
                    EmptyStateCard(
                        title = "No products in this category yet",
                        message = "This listing layout is ready and will populate as more catalog items are added.",
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        products.forEach { product ->
                            ProductTemplateCard(
                                product = product,
                                modifier = Modifier.fillMaxWidth(0.47f),
                                isWishlisted = product.id in wishlistIds,
                                onToggleWishlist = { onToggleWishlist(product.id) },
                                onClick = { onProductClick(product.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    query: String,
    recentSearches: List<String>,
    results: List<ProductTemplate>,
    wishlistIds: Set<String>,
    onBack: () -> Unit,
    onImageSearchClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onProductClick: (String) -> Unit,
    onToggleWishlist: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                HeaderBar(title = "Search", showBack = true, onBack = onBack)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FashionField(
                        value = query,
                        onValueChange = onQueryChange,
                        label = "Search kurti, saree, heels, bags...",
                        modifier = Modifier.weight(1f),
                    )
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clickable(onClick = onImageSearchClick),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(18.dp)),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = "Search by image",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.Center),
                            )
                        }
                    }
                }
                androidx.compose.material3.TextButton(onClick = { onSearchSubmit(query) }) {
                    Text("Search")
                }

                SectionCard(title = "Recent Searches") {
                    if (recentSearches.isEmpty()) {
                        Text(
                            text = "Your recent searches will appear here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(recentSearches) { term ->
                                Text(
                                    text = term,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = MaterialTheme.shapes.medium,
                                        )
                                        .clickable { onSuggestionClick(term) }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                )
                            }
                        }
                    }
                }
                SectionCard(title = "Trending Searches") {
                    ChipWrap(items = searchSuggestions)
                }
                SectionCard(title = "Popular Categories") {
                    ChipWrap(items = homeCategories)
                }
                if (query.isNotBlank()) {
                    Text(
                        text = "Showing results for \"$query\"",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                if (query.isBlank() && results.isEmpty()) {
                    EmptyStateCard(
                        title = "No live catalog items yet",
                        message = "Search infrastructure is ready. Products will appear here after catalog publishing.",
                    )
                } else if (results.isEmpty()) {
                    EmptyStateCard(
                        title = "No matching products",
                        message = "Try another search term or browse the main categories.",
                    )
                } else {
                    SectionHeader(title = "Suggested Products")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        results.forEach { product ->
                            ProductTemplateCard(
                                product = product,
                                modifier = Modifier.fillMaxWidth(0.47f),
                                isWishlisted = product.id in wishlistIds,
                                onToggleWishlist = { onToggleWishlist(product.id) },
                                onClick = { onProductClick(product.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsScreen(
    notifications: List<NotificationItem>,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        HeaderBar(title = "Notifications", showBack = true, onBack = onBack)
        if (notifications.isEmpty()) {
            ChipWrap(
                items = listOf(
                    "Order Updates",
                    "Delivery Updates",
                    "Offers",
                    "Wishlist Price Drop",
                    "Back in Stock",
                ),
            )
            EmptyStateCard(
                title = "No notifications yet",
                message = "Order updates, delivery alerts, and offer messages will appear here.",
            )
        } else {
            notifications.forEach { notification ->
                SectionCard(title = notification.title) {
                    DetailRow("Type", notification.type)
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
fun OffersScreen(
    offers: List<OfferItem>,
    onBack: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            HeaderBar(title = "Offers", showBack = true, onBack = onBack)
        }
        if (offers.isEmpty()) {
            item {
                SectionCard(title = "Available Coupons") {
                    Text(
                        text = "Coupon cards appear here once live catalog offers are connected.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            item {
                SectionCard(title = "Bank Offers") {
                    Text(
                        text = "Bank-linked discounts and payment-based savings live here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            items(offers) { offer ->
                SectionCard(title = offer.code) {
                    DetailRow("Description", offer.description)
                    DetailRow("Minimum Order", "\u20B9${offer.minimumOrder}")
                    DetailRow("Status", offer.status)
                }
            }
        }
    }
}

@Composable
private fun CampaignAutoSlider(campaigns: List<HomeCampaign>) {
    val featuredCampaigns = remember(campaigns) {
        (campaigns + heroCampaigns.filter { fallback ->
            campaigns.none { it.title.equals(fallback.title, ignoreCase = true) }
        }).take(3)
    }

    if (featuredCampaigns.isEmpty()) {
        return
    }

    val startPage = remember(featuredCampaigns.size) {
        if (featuredCampaigns.size <= 1) {
            0
        } else {
            val midpoint = Int.MAX_VALUE / 2
            midpoint - midpoint % featuredCampaigns.size
        }
    }
    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = {
            if (featuredCampaigns.size <= 1) 1 else Int.MAX_VALUE
        },
    )

    LaunchedEffect(pagerState, featuredCampaigns.size) {
        if (featuredCampaigns.size <= 1) return@LaunchedEffect

        while (isActive) {
            delay(3200)
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(pagerState.settledPage + 1)
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp,
            beyondViewportPageCount = 1,
        ) { page ->
            val campaignIndex = page % featuredCampaigns.size
            CampaignBanner(
                campaign = featuredCampaigns[campaignIndex],
                campaignIndex = campaignIndex,
            )
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(featuredCampaigns.indices.toList()) { page ->
                val active = pagerState.currentPage % featuredCampaigns.size == page
                val indicatorWidth = animateDpAsState(targetValue = if (active) 28.dp else 10.dp, label = "campaignIndicatorWidth")
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                        )
                        .width(indicatorWidth.value)
                        .height(8.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProductSectionGrid(
    title: String,
    subtitle: String,
    products: List<ProductTemplate>,
    wishlistIds: Set<String>,
    onProductClick: (String) -> Unit,
    onToggleWishlist: (String) -> Unit,
) {
    SectionHeader(title = title, subtitle = subtitle)
    if (products.isEmpty()) {
        EmptyStateCard(
            title = "Catalog coming soon",
            message = "This block is ready and will populate as soon as live products are added.",
        )
    } else {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            products.forEach { product ->
                ProductTemplateCard(
                    product = product,
                    modifier = Modifier.fillMaxWidth(0.47f),
                    isWishlisted = product.id in wishlistIds,
                    onToggleWishlist = { onToggleWishlist(product.id) },
                    onClick = { onProductClick(product.id) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryGroupCard(
    group: CategoryGroup,
    onSubcategoryClick: (String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = group.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                group.items.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shadowElevation = 1.dp,
                        modifier = Modifier.clickable { onSubcategoryClick("${group.title} > $item") },
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        )
                    }
                }
            }
        }
    }
}
