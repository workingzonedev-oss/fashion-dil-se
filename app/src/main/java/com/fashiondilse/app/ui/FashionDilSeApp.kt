package com.fashiondilse.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fashiondilse.app.data.DiscoverItem
import com.fashiondilse.app.data.DiscoverSections
import com.fashiondilse.app.data.LinkedAdminStore
import com.fashiondilse.app.data.ProductTemplate
import com.fashiondilse.app.data.fetchLinkedAdminStore
import com.fashiondilse.app.data.pushOrderToAdmin
import com.fashiondilse.app.ui.navigation.FashionRoute
import com.fashiondilse.app.ui.navigation.bottomBarRoutes
import com.fashiondilse.app.ui.navigation.bottomDestinations
import com.fashiondilse.app.ui.screens.BudgetPicksScreen
import com.fashiondilse.app.ui.screens.CartScreen
import com.fashiondilse.app.ui.screens.CategoriesScreen
import com.fashiondilse.app.ui.screens.CheckoutScreen
import com.fashiondilse.app.ui.screens.DiscoveryProductFeedScreen
import com.fashiondilse.app.ui.screens.HelpSupportScreen
import com.fashiondilse.app.ui.screens.HomeScreen
import com.fashiondilse.app.ui.screens.HomeDrawerDestination
import com.fashiondilse.app.ui.screens.ImageSearchScreen
import com.fashiondilse.app.ui.screens.LoginScreen
import com.fashiondilse.app.ui.screens.NotificationsScreen
import com.fashiondilse.app.ui.screens.OffersScreen
import com.fashiondilse.app.ui.screens.OrderDetailsScreen
import com.fashiondilse.app.ui.screens.OrderSuccessScreen
import com.fashiondilse.app.ui.screens.OrdersScreen
import com.fashiondilse.app.ui.screens.PaymentFailedScreen
import com.fashiondilse.app.ui.screens.ProductDetailsScreen
import com.fashiondilse.app.ui.screens.ProductListingScreen
import com.fashiondilse.app.ui.screens.ProfileScreen
import com.fashiondilse.app.ui.screens.ReturnRequestScreen
import com.fashiondilse.app.ui.screens.SavedAddressesScreen
import com.fashiondilse.app.ui.screens.SearchScreen
import com.fashiondilse.app.ui.screens.SettingsScreen
import com.fashiondilse.app.ui.screens.ShopByOccasionScreen
import com.fashiondilse.app.ui.screens.SignUpScreen
import com.fashiondilse.app.ui.screens.SplashScreen
import com.fashiondilse.app.ui.screens.TrendingCollectionsScreen
import com.fashiondilse.app.ui.screens.WelcomeScreen
import com.fashiondilse.app.ui.screens.WishlistScreen
import com.fashiondilse.app.ui.screens.PrivacyPolicyScreen
import com.fashiondilse.app.ui.screens.TermsConditionsScreen
import com.fashiondilse.app.ui.screens.PaymentMethodsScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FashionDilSeApp() {
    val navController = rememberNavController()
    val appState = rememberFashionAppState()
    val scope = rememberCoroutineScope()
    var linkedStore by remember { mutableStateOf(LinkedAdminStore()) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val showBottomBar = currentRoute in bottomBarRoutes

    // Optimized polling: fetch immediately, then every 10s instead of 5s
    LaunchedEffect(Unit) {
        while (true) {
            fetchLinkedAdminStore()?.let { linkedStore = it }
            delay(10_000)
        }
    }

    LaunchedEffect(linkedStore.products) {
        appState.syncCatalog(linkedStore.products)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    windowInsets = WindowInsets.navigationBars,
                ) {
                    bottomDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                androidx.compose.material3.Icon(
                                    imageVector = if (selected) destination.selectedIcon else destination.icon,
                                    contentDescription = destination.label,
                                )
                            },
                            label = { androidx.compose.material3.Text(destination.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = FashionRoute.Splash.route,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            composable(FashionRoute.Splash.route) {
                SplashScreen(
                    onFinished = {
                        val nextRoute = if (appState.hasSession()) FashionRoute.Home.route else FashionRoute.Welcome.route
                        navController.navigate(nextRoute) {
                            popUpTo(FashionRoute.Splash.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(FashionRoute.Welcome.route) {
                WelcomeScreen(
                    onLogin = { navController.navigate(FashionRoute.Login.route) },
                    onCreateAccount = { navController.navigate(FashionRoute.SignUp.route) },
                    onGuest = {
                        appState.continueAsGuest()
                        navController.navigate(FashionRoute.Home.route) {
                            popUpTo(FashionRoute.Welcome.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(FashionRoute.Login.route) {
                LoginScreen(
                    onBack = { navController.popBackStack() },
                    onLoginSuccess = {
                        appState.login()
                        navController.navigate(FashionRoute.Home.route) {
                            popUpTo(FashionRoute.Welcome.route) { inclusive = true }
                        }
                    },
                    onCreateAccount = { navController.navigate(FashionRoute.SignUp.route) },
                )
            }
            composable(FashionRoute.SignUp.route) {
                SignUpScreen(
                    onBack = { navController.popBackStack() },
                    onCreateAccount = {
                        appState.login()
                        navController.navigate(FashionRoute.Home.route) {
                            popUpTo(FashionRoute.Welcome.route) { inclusive = true }
                        }
                    },
                    onLogin = { navController.navigate(FashionRoute.Login.route) },
                )
            }
            composable(FashionRoute.Home.route) {
                HomeScreen(
                    campaigns = linkedStore.campaigns,
                    wishlistIds = appState.wishlistIds,
                    searchQuery = appState.searchQuery,
                    searchResults = appState.searchResults,
                    onSearchQueryChange = appState::updateSearchQuery,
                    onSearchSubmit = appState::submitSearch,
                    onImageSearchClick = { navController.navigate(FashionRoute.ImageSearch.route) },
                    onNotificationsClick = { navController.navigate(FashionRoute.Notifications.route) },
                    onMenuDestinationClick = { destination ->
                        when (destination) {
                            HomeDrawerDestination.TrendingCollections -> navController.navigate(FashionRoute.TrendingCollections.route)
                            HomeDrawerDestination.BudgetPicks -> navController.navigate(FashionRoute.BudgetPicks.route)
                            HomeDrawerDestination.NewArrivals -> navController.navigate(FashionRoute.NewArrivals.route)
                            HomeDrawerDestination.Bestsellers -> navController.navigate(FashionRoute.Bestsellers.route)
                            HomeDrawerDestination.RecommendedForYou -> navController.navigate(FashionRoute.RecommendedForYou.route)
                            HomeDrawerDestination.ShopByOccasion -> navController.navigate(FashionRoute.ShopByOccasion.route)
                            HomeDrawerDestination.Orders -> navController.navigate(FashionRoute.Orders.route)
                            HomeDrawerDestination.Offers -> navController.navigate(FashionRoute.Offers.route)
                            HomeDrawerDestination.Notifications -> navController.navigate(FashionRoute.Notifications.route)
                            HomeDrawerDestination.SavedAddresses -> navController.navigate(FashionRoute.SavedAddresses.route)
                            HomeDrawerDestination.HelpSupport -> navController.navigate(FashionRoute.HelpSupport.route)
                            HomeDrawerDestination.Settings -> navController.navigate(FashionRoute.Settings.route)
                            HomeDrawerDestination.Logout -> {
                                appState.logout()
                                navController.navigate(FashionRoute.Welcome.route) {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            }
                            else -> Unit
                        }
                    },
                    onCategoryClick = { category ->
                        appState.openCategory(defaultPathForCategory(category))
                        navController.navigate(FashionRoute.ProductListing.route)
                    },
                    onProductClick = { productId ->
                        appState.openProduct(productId)
                        navController.navigate(FashionRoute.ProductDetails.route)
                    },
                    onToggleWishlist = appState::toggleWishlist,
                )
            }
            composable(FashionRoute.Categories.route) {
                CategoriesScreen(
                    onSubcategoryClick = { categoryPath ->
                        appState.openCategory(categoryPath)
                        navController.navigate(FashionRoute.ProductListing.route)
                    },
                )
            }
            composable(FashionRoute.Wishlist.route) {
                WishlistScreen(
                    products = appState.wishlistProducts,
                    onProductClick = { productId ->
                        appState.openProduct(productId)
                        navController.navigate(FashionRoute.ProductDetails.route)
                    },
                    onMoveToCart = { productId ->
                        appState.moveWishlistToCart(productId)
                        navController.navigate(FashionRoute.Cart.route)
                    },
                    onToggleWishlist = appState::toggleWishlist,
                    onBrowse = { navController.navigate(FashionRoute.Home.route) },
                )
            }
            composable(FashionRoute.Cart.route) {
                CartScreen(
                    cartLines = appState.cartLines,
                    summary = appState.priceSummary(),
                    onCheckout = {
                        if (appState.cartLines.isNotEmpty()) {
                            navController.navigate(FashionRoute.Checkout.route)
                        }
                    },
                    onContinueShopping = { navController.navigate(FashionRoute.Home.route) },
                    onIncreaseQuantity = { entry ->
                        appState.updateQuantity(entry.productId, entry.size, entry.color, 1)
                    },
                    onDecreaseQuantity = { entry ->
                        appState.updateQuantity(entry.productId, entry.size, entry.color, -1)
                    },
                    onRemove = { entry ->
                        appState.removeFromCart(entry.productId, entry.size, entry.color)
                    },
                    onMoveToWishlist = { entry ->
                        appState.moveCartItemToWishlist(entry.productId, entry.size, entry.color)
                    },
                )
            }
            composable(FashionRoute.Profile.route) {
                ProfileScreen(
                    onOrders = { navController.navigate(FashionRoute.Orders.route) },
                    onAddresses = { navController.navigate(FashionRoute.SavedAddresses.route) },
                    onOffers = { navController.navigate(FashionRoute.Offers.route) },
                    onNotifications = { navController.navigate(FashionRoute.Notifications.route) },
                    onHelp = { navController.navigate(FashionRoute.HelpSupport.route) },
                    onSettings = { navController.navigate(FashionRoute.Settings.route) },
                    onWishlist = {
                        navController.navigate(FashionRoute.Wishlist.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPaymentMethods = { navController.navigate(FashionRoute.PaymentMethods.route) },
                )
            }
            composable(FashionRoute.Search.route) {
                SearchScreen(
                    query = appState.searchQuery,
                    recentSearches = appState.recentSearches,
                    results = appState.searchResults,
                    wishlistIds = appState.wishlistIds,
                    onBack = { navController.popBackStack() },
                    onImageSearchClick = { navController.navigate(FashionRoute.ImageSearch.route) },
                    onQueryChange = appState::updateSearchQuery,
                    onSearchSubmit = appState::submitSearch,
                    onSuggestionClick = {
                        appState.submitSearch(it)
                    },
                    onProductClick = { productId ->
                        appState.openProduct(productId)
                        navController.navigate(FashionRoute.ProductDetails.route)
                    },
                    onToggleWishlist = appState::toggleWishlist,
                )
            }
            composable(FashionRoute.ImageSearch.route) {
                ImageSearchScreen(
                    onBack = { navController.popBackStack() },
                    onOpenTextSearch = { navController.navigate(FashionRoute.Search.route) },
                )
            }
            composable(FashionRoute.Notifications.route) {
                NotificationsScreen(
                    notifications = linkedStore.notifications,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(FashionRoute.TrendingCollections.route) {
                TrendingCollectionsScreen(
                    items = linkedStore.discoverItems.forSection(DiscoverSections.TrendingCollections),
                    onBack = { navController.popBackStack() },
                )
            }
            composable(FashionRoute.BudgetPicks.route) {
                BudgetPicksScreen(
                    items = linkedStore.discoverItems.forSection(DiscoverSections.BudgetPicks),
                    onBack = { navController.popBackStack() },
                )
            }
            composable(FashionRoute.ShopByOccasion.route) {
                ShopByOccasionScreen(
                    items = linkedStore.discoverItems.forSection(DiscoverSections.ShopByOccasion),
                    onBack = { navController.popBackStack() },
                )
            }
            composable(FashionRoute.NewArrivals.route) {
                DiscoveryProductFeedScreen(
                    title = "New Arrivals",
                    subtitle = "Products on this page are controlled from the admin product catalog.",
                    emptyMessage = "Publish products with collection name containing \"New Arrivals\" from the admin panel.",
                    products = appState.allProducts.forCollectionTag("new arrivals", "new arrival"),
                    wishlistIds = appState.wishlistIds,
                    onBack = { navController.popBackStack() },
                    onProductClick = { productId ->
                        appState.openProduct(productId)
                        navController.navigate(FashionRoute.ProductDetails.route)
                    },
                    onToggleWishlist = appState::toggleWishlist,
                )
            }
            composable(FashionRoute.Bestsellers.route) {
                DiscoveryProductFeedScreen(
                    title = "Bestsellers",
                    subtitle = "This page reads bestseller products from the admin product catalog.",
                    emptyMessage = "Publish products with collection name containing \"Bestsellers\" or \"Bestseller\" from the admin panel.",
                    products = appState.allProducts.forCollectionTag("bestsellers", "bestseller", "best seller"),
                    wishlistIds = appState.wishlistIds,
                    onBack = { navController.popBackStack() },
                    onProductClick = { productId ->
                        appState.openProduct(productId)
                        navController.navigate(FashionRoute.ProductDetails.route)
                    },
                    onToggleWishlist = appState::toggleWishlist,
                )
            }
            composable(FashionRoute.RecommendedForYou.route) {
                DiscoveryProductFeedScreen(
                    title = "Recommended for You",
                    subtitle = "This page reads recommended products from the admin product catalog.",
                    emptyMessage = "Publish products with collection name containing \"Recommended\" from the admin panel.",
                    products = appState.allProducts.forCollectionTag("recommended for you", "recommended"),
                    wishlistIds = appState.wishlistIds,
                    onBack = { navController.popBackStack() },
                    onProductClick = { productId ->
                        appState.openProduct(productId)
                        navController.navigate(FashionRoute.ProductDetails.route)
                    },
                    onToggleWishlist = appState::toggleWishlist,
                )
            }
            composable(FashionRoute.ProductListing.route) {
                ProductListingScreen(
                    selectedCategory = appState.selectedCategoryPath,
                    products = appState.listingProducts,
                    wishlistIds = appState.wishlistIds,
                    onBack = { navController.popBackStack() },
                    onProductClick = { productId ->
                        appState.openProduct(productId)
                        navController.navigate(FashionRoute.ProductDetails.route)
                    },
                    onToggleWishlist = appState::toggleWishlist,
                )
            }
            composable(FashionRoute.ProductDetails.route) {
                val selectedProduct = appState.selectedProduct
                ProductDetailsScreen(
                    product = selectedProduct,
                    isWishlisted = selectedProduct?.id in appState.wishlistIds,
                    onBack = { navController.popBackStack() },
                    onToggleWishlist = { selectedProduct?.id?.let(appState::toggleWishlist) },
                    onAddToCart = { size, color ->
                        selectedProduct?.id?.let { productId ->
                            appState.addToCart(productId, size, color)
                            navController.navigate(FashionRoute.Cart.route)
                        }
                    },
                    onBuyNow = { size, color ->
                        selectedProduct?.id?.let { productId ->
                            appState.addToCart(productId, size, color)
                            navController.navigate(FashionRoute.Checkout.route)
                        }
                    },
                )
            }
            composable(FashionRoute.Checkout.route) {
                CheckoutScreen(
                    cartLines = appState.cartLines,
                    summary = appState.priceSummary(),
                    selectedAddressLabel = appState.selectedAddressLabel,
                    selectedDeliveryMethod = appState.selectedDeliveryMethod,
                    selectedPaymentMethod = appState.selectedPaymentMethod,
                    onSelectDeliveryMethod = appState::selectDeliveryMethod,
                    onSelectPaymentMethod = appState::selectPaymentMethod,
                    onBack = { navController.popBackStack() },
                    onPlaceOrder = {
                        val placedOrder = appState.placeOrder()
                        if (placedOrder != null) {
                            scope.launch {
                                pushOrderToAdmin(
                                    order = placedOrder,
                                    catalog = appState.allProducts,
                                )
                            }
                            navController.navigate(FashionRoute.OrderSuccess.route)
                        }
                    },
                    onPaymentFailed = { navController.navigate(FashionRoute.PaymentFailed.route) },
                )
            }
            composable(FashionRoute.OrderSuccess.route) {
                OrderSuccessScreen(
                    order = appState.selectedOrder,
                    onContinueShopping = {
                        navController.navigate(FashionRoute.Home.route) {
                            popUpTo(FashionRoute.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onViewOrder = { navController.navigate(FashionRoute.OrderDetails.route) },
                )
            }
            composable(FashionRoute.PaymentFailed.route) {
                PaymentFailedScreen(
                    onRetry = { navController.navigate(FashionRoute.Checkout.route) },
                    onChangeMethod = { navController.popBackStack() },
                    onReturnToCart = { navController.navigate(FashionRoute.Cart.route) },
                )
            }
            composable(FashionRoute.Orders.route) {
                OrdersScreen(
                    orders = appState.orders,
                    onBack = { navController.popBackStack() },
                    onOrderClick = { orderId ->
                        appState.openOrder(orderId)
                        navController.navigate(FashionRoute.OrderDetails.route)
                    },
                )
            }
            composable(FashionRoute.OrderDetails.route) {
                val selectedOrder = appState.selectedOrder
                val orderLines = selectedOrder?.items?.mapNotNull { entry ->
                    appState.allProducts.firstOrNull { it.id == entry.productId }?.let { entry to it }
                } ?: emptyList()
                OrderDetailsScreen(
                    order = selectedOrder,
                    orderLines = orderLines,
                    onBack = { navController.popBackStack() },
                    onReturnRequest = { navController.navigate(FashionRoute.ReturnRequest.route) },
                )
            }
            composable(FashionRoute.Offers.route) {
                OffersScreen(
                    offers = linkedStore.offers,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(FashionRoute.HelpSupport.route) {
                HelpSupportScreen(onBack = { navController.popBackStack() })
            }
            composable(FashionRoute.SavedAddresses.route) {
                SavedAddressesScreen(onBack = { navController.popBackStack() })
            }
            composable(FashionRoute.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        appState.logout()
                        navController.navigate(FashionRoute.Welcome.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onPrivacyPolicy = { navController.navigate(FashionRoute.PrivacyPolicy.route) },
                    onTermsConditions = { navController.navigate(FashionRoute.TermsConditions.route) },
                )
            }
            composable(FashionRoute.PrivacyPolicy.route) {
                PrivacyPolicyScreen(onBack = { navController.popBackStack() })
            }
            composable(FashionRoute.TermsConditions.route) {
                TermsConditionsScreen(onBack = { navController.popBackStack() })
            }
            composable(FashionRoute.PaymentMethods.route) {
                PaymentMethodsScreen(onBack = { navController.popBackStack() })
            }
            composable(FashionRoute.ReturnRequest.route) {
                ReturnRequestScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

private fun defaultPathForCategory(category: String): String {
    return when (category) {
        "Women" -> "Women > Kurtis"
        "Men" -> "Men > Shirts"
        "Kids" -> "Kids > Girls Wear"
        "Beauty" -> "Beauty > Makeup"
        "Jewellery" -> "Jewellery > Earrings"
        "Footwear" -> "Bags & Footwear > Heels"
        "Bags" -> "Bags & Footwear > Handbags"
        "Home Decor" -> "Home Decor > Bedsheets"
        else -> "Women > Kurtis"
    }
}

private fun List<DiscoverItem>.forSection(sectionKey: String): List<DiscoverItem> {
    return filter { it.sectionKey == sectionKey }
}

private fun List<ProductTemplate>.forCollectionTag(vararg tags: String): List<ProductTemplate> {
    return filter { product ->
        tags.any { tag -> product.collection.contains(tag, ignoreCase = true) }
    }
}
