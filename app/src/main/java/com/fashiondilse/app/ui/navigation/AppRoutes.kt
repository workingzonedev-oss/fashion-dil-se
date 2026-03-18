package com.fashiondilse.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

sealed class FashionRoute(val route: String) {
    data object Splash : FashionRoute("splash")
    data object Welcome : FashionRoute("welcome")
    data object Login : FashionRoute("login")
    data object SignUp : FashionRoute("signup")
    data object Home : FashionRoute("home")
    data object Categories : FashionRoute("categories")
    data object Wishlist : FashionRoute("wishlist")
    data object Cart : FashionRoute("cart")
    data object Profile : FashionRoute("profile")
    data object Search : FashionRoute("search")
    data object ImageSearch : FashionRoute("image_search")
    data object Notifications : FashionRoute("notifications")
    data object ProductListing : FashionRoute("product_listing")
    data object ProductDetails : FashionRoute("product_details")
    data object Checkout : FashionRoute("checkout")
    data object OrderSuccess : FashionRoute("order_success")
    data object PaymentFailed : FashionRoute("payment_failed")
    data object Orders : FashionRoute("orders")
    data object OrderDetails : FashionRoute("order_details")
    data object Offers : FashionRoute("offers")
    data object HelpSupport : FashionRoute("help_support")
    data object SavedAddresses : FashionRoute("saved_addresses")
    data object Settings : FashionRoute("settings")
    data object ReturnRequest : FashionRoute("return_request")
    data object TrendingCollections : FashionRoute("trending_collections")
    data object BudgetPicks : FashionRoute("budget_picks")
    data object NewArrivals : FashionRoute("new_arrivals")
    data object Bestsellers : FashionRoute("bestsellers")
    data object RecommendedForYou : FashionRoute("recommended_for_you")
    data object ShopByOccasion : FashionRoute("shop_by_occasion")
    data object PrivacyPolicy : FashionRoute("privacy_policy")
    data object TermsConditions : FashionRoute("terms_conditions")
    data object PaymentMethods : FashionRoute("payment_methods")
}

data class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
)

val bottomDestinations = listOf(
    BottomDestination(
        route = FashionRoute.Home.route,
        label = "Home",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Rounded.Home,
    ),
    BottomDestination(
        route = FashionRoute.Categories.route,
        label = "Categories",
        icon = Icons.Outlined.Category,
        selectedIcon = Icons.Rounded.Category,
    ),
    BottomDestination(
        route = FashionRoute.Wishlist.route,
        label = "Wishlist",
        icon = Icons.Outlined.FavoriteBorder,
        selectedIcon = Icons.Rounded.Favorite,
    ),
    BottomDestination(
        route = FashionRoute.Cart.route,
        label = "Cart",
        icon = Icons.Outlined.ShoppingBag,
        selectedIcon = Icons.Rounded.ShoppingBag,
    ),
    BottomDestination(
        route = FashionRoute.Profile.route,
        label = "Profile",
        icon = Icons.Outlined.PersonOutline,
        selectedIcon = Icons.Rounded.Person,
    ),
)

val bottomBarRoutes = bottomDestinations.map { it.route }.toSet()
