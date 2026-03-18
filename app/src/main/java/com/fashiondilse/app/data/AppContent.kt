package com.fashiondilse.app.data

import java.util.Locale

data class ProductTemplate(
    val id: String,
    val title: String,
    val subtitle: String,
    val collection: String,
    val categoryPath: String,
    val price: Int,
    val originalPrice: Int,
    val rating: Double,
    val deliveryLabel: String,
    val description: String,
    val colors: List<String>,
    val sizes: List<String>,
    val details: List<Pair<String, String>>,
    val images: List<String> = emptyList(),
    val resellerPrice: Int = 0,
    val enableReselling: Boolean = false,
    val meeshoSupplierLink: String = "",
) {
    val priceLabel: String
        get() = "\u20B9$price"

    val originalPriceLabel: String
        get() = "\u20B9$originalPrice"

    val discountPercent: Int
        get() = ((originalPrice - price) * 100 / originalPrice.coerceAtLeast(1))

    val discountLabel: String
        get() = "$discountPercent% off"

    val ratingLabel: String
        get() = "\u2605 ${String.format(Locale.US, "%.1f", rating)}"

    val firstImageUrl: String
        get() = images.firstOrNull().orEmpty()

    val resellerMargin: Int
        get() = if (resellerPrice > 0 && enableReselling) (price - resellerPrice) else 0

    val resellerMarginLabel: String
        get() = "\u20B9$resellerMargin"

    fun shareText(sellingPrice: Int): String {
        val discount = if (originalPrice > sellingPrice) {
            val pct = ((originalPrice - sellingPrice) * 100 / originalPrice.coerceAtLeast(1))
            " ($pct% off)"
        } else ""
        return buildString {
            appendLine("*$title*")
            appendLine("~\u20B9$originalPrice~ \u20B9$sellingPrice$discount")
            appendLine()
            appendLine(description.take(120))
            appendLine()
            appendLine("Sizes: ${sizes.joinToString(", ")}")
            appendLine("Colors: ${colors.joinToString(", ")}")
            appendLine()
            appendLine("$deliveryLabel | Cash on Delivery Available")
            appendLine()
            appendLine("Order now on *Fashion Dil Se*")
        }
    }
}

data class CategoryGroup(
    val title: String,
    val items: List<String>,
)

data class HomeCampaign(
    val title: String,
    val eyebrow: String,
    val subtitle: String,
    val cta: String,
    val imageUrl: String = "",
)

data class DiscoverItem(
    val id: String,
    val sectionKey: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String = "",
)

data class OfferItem(
    val code: String,
    val description: String,
    val minimumOrder: Int,
    val status: String,
)

data class NotificationItem(
    val title: String,
    val message: String,
    val type: String,
    val status: String,
)

object DiscoverSections {
    const val TrendingCollections = "trending_collections"
    const val BudgetPicks = "budget_picks"
    const val ShopByOccasion = "shop_by_occasion"
}

val homeCategories = listOf(
    "Women",
    "Men",
    "Kids",
    "Beauty",
    "Jewellery",
    "Footwear",
    "Bags",
    "Home Decor",
)

val heroCampaigns = listOf(
    HomeCampaign(
        title = "New Arrivals",
        eyebrow = "Fresh Drop",
        subtitle = "Just-landed styles with a cleaner fashion-first edit.",
        cta = "Explore Now",
    ),
    HomeCampaign(
        title = "Festive Collection",
        eyebrow = "Celebration Edit",
        subtitle = "Elevated looks for wedding, festive, and family occasions.",
        cta = "Shop Festive",
    ),
    HomeCampaign(
        title = "Budget Fashion",
        eyebrow = "Value Picks",
        subtitle = "Strong everyday style under budget without losing the premium feel.",
        cta = "View Deals",
    ),
)

val trendingCollections = listOf(
    "Ethnic Wear",
    "Western Looks",
    "Office Style",
    "Party Wear",
    "Casual Daily Wear",
)

val budgetPicks = listOf(
    "Under \u20B9299",
    "Under \u20B9499",
    "Under \u20B9799",
    "Best Value Deals",
)

val shopByOccasion = listOf(
    "Wedding",
    "College",
    "Office",
    "Daily Wear",
    "Festive",
    "Evening Party",
)

val trustCues = listOf(
    "Cash on Delivery Available",
    "Easy Returns",
    "Secure Payments",
    "Verified Seller",
    "Fast Delivery",
    "Genuine Reviews",
)

val productTemplates: List<ProductTemplate> = emptyList()

val categoryGroups = listOf(
    CategoryGroup(
        title = "Women",
        items = listOf(
            "Sarees",
            "Kurtis",
            "Salwar Sets",
            "Dresses",
            "Tops",
            "Jeans",
            "Skirts",
            "Nightwear",
            "Hijab / Modest Wear",
        ),
    ),
    CategoryGroup(
        title = "Men",
        items = listOf(
            "T-Shirts",
            "Shirts",
            "Jeans",
            "Trousers",
            "Ethnic Wear",
            "Jackets",
            "Footwear",
        ),
    ),
    CategoryGroup(
        title = "Kids",
        items = listOf(
            "Boys Wear",
            "Girls Wear",
            "Baby Clothing",
            "Footwear",
            "Accessories",
        ),
    ),
    CategoryGroup(
        title = "Beauty",
        items = listOf(
            "Makeup",
            "Skincare",
            "Haircare",
            "Perfume",
            "Personal Care",
        ),
    ),
    CategoryGroup(
        title = "Jewellery",
        items = listOf(
            "Earrings",
            "Necklaces",
            "Bangles",
            "Rings",
            "Bridal Sets",
        ),
    ),
    CategoryGroup(
        title = "Bags & Footwear",
        items = listOf(
            "Handbags",
            "Sling Bags",
            "Heels",
            "Flats",
            "Sandals",
            "Sneakers",
        ),
    ),
    CategoryGroup(
        title = "Home Decor",
        items = listOf(
            "Bedsheets",
            "Curtains",
            "Cushion Covers",
            "Wall Decor",
            "Storage",
        ),
    ),
)

val listingFilters = listOf(
    "Sort",
    "Filter",
    "Size",
    "Price",
    "Color",
    "Brand",
)

val sortOptions = listOf(
    "Popularity",
    "Newest First",
    "Price Low to High",
    "Price High to Low",
    "Highest Rated",
    "Discount",
)

val filterOptions = listOf(
    "Price Range",
    "Size",
    "Color",
    "Fabric",
    "Style",
    "Pattern",
    "Sleeve Type",
    "Fit",
    "Occasion",
    "Rating",
    "Availability",
)

val searchSuggestions = listOf(
    "Kurti",
    "Saree",
    "Heels",
    "Bags",
    "Jewellery",
    "Beauty",
)

val profileMenuItems = listOf(
    "My Orders",
    "Wishlist",
    "Saved Addresses",
    "Payment Methods",
    "Coupons",
    "Notifications",
    "Help & Support",
    "Return & Refunds",
    "Terms & Policies",
    "Settings",
    "Logout",
)

val settingsItems = listOf(
    "Edit Profile",
    "Language",
    "Notification Preferences",
    "Password Change",
    "Privacy Policy",
    "Terms and Conditions",
    "Delete Account",
    "Logout",
)

val helpTopics = listOf(
    "FAQs",
    "Chat Support",
    "Email Support",
    "Call Support",
    "Return Help",
    "Payment Issues",
    "Delivery Issues",
)

val orderTabs = listOf(
    "Active",
    "Delivered",
    "Cancelled",
    "Returned",
)
