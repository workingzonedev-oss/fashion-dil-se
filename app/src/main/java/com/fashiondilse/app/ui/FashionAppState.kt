package com.fashiondilse.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.fashiondilse.app.data.ProductTemplate
import com.fashiondilse.app.data.productTemplates
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class SessionState {
    LoggedOut,
    Guest,
    LoggedIn,
}

data class CartEntry(
    val productId: String,
    val size: String,
    val color: String,
    val quantity: Int = 1,
)

data class PriceSummary(
    val totalMrp: Int,
    val discount: Int,
    val deliveryCharge: Int,
    val platformFee: Int,
    val finalPayable: Int,
)

data class OrderRecord(
    val id: String,
    val items: List<CartEntry>,
    val total: Int,
    val status: String,
    val placedOn: String,
    val paymentMethod: String,
    val deliveryMethod: String,
    val addressLabel: String,
)

class FashionAppState {
    private var catalog by mutableStateOf(productTemplates)
    private var orderSequence by mutableStateOf(1001)

    var sessionState by mutableStateOf(SessionState.LoggedOut)
        private set

    var selectedProductId by mutableStateOf<String?>(catalog.firstOrNull()?.id)
        private set

    var selectedCategoryPath by mutableStateOf("Women > Kurtis")
        private set

    var selectedOrderId by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var recentSearches by mutableStateOf(emptyList<String>())
        private set

    var wishlistIds by mutableStateOf(emptySet<String>())
        private set

    var cartEntries by mutableStateOf(emptyList<CartEntry>())
        private set

    var orders by mutableStateOf(emptyList<OrderRecord>())
        private set

    var selectedAddressLabel by mutableStateOf("Primary delivery address")
        private set

    var selectedDeliveryMethod by mutableStateOf("Standard Delivery")
        private set

    var selectedPaymentMethod by mutableStateOf("UPI")
        private set

    val allProducts: List<ProductTemplate>
        get() = catalog

    val selectedProduct: ProductTemplate?
        get() = selectedProductId?.let(::productById)

    val wishlistProducts: List<ProductTemplate>
        get() = catalog.filter { it.id in wishlistIds }

    val listingProducts: List<ProductTemplate>
        get() = catalog.filter {
            it.categoryPath.startsWith(selectedCategoryPath.substringBefore(" >")) ||
                it.categoryPath == selectedCategoryPath
        }

    val searchResults: List<ProductTemplate>
        get() {
            val query = searchQuery.trim()
            if (query.isBlank()) return emptyList()
            return catalog.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.subtitle.contains(query, ignoreCase = true) ||
                    it.collection.contains(query, ignoreCase = true) ||
                    it.categoryPath.contains(query, ignoreCase = true)
            }
        }

    val cartLines: List<Pair<CartEntry, ProductTemplate>>
        get() = cartEntries.mapNotNull { entry -> productById(entry.productId)?.let { entry to it } }

    val selectedOrder: OrderRecord?
        get() = orders.firstOrNull { it.id == selectedOrderId } ?: orders.firstOrNull()

    fun hasSession(): Boolean = sessionState != SessionState.LoggedOut

    fun login() {
        sessionState = SessionState.LoggedIn
    }

    fun continueAsGuest() {
        sessionState = SessionState.Guest
    }

    fun logout() {
        sessionState = SessionState.LoggedOut
    }

    fun syncCatalog(products: List<ProductTemplate>) {
        catalog = products
        val validIds = products.map { it.id }.toSet()
        wishlistIds = wishlistIds.intersect(validIds)
        cartEntries = cartEntries.filter { it.productId in validIds }
        selectedProductId = when {
            selectedProductId in validIds -> selectedProductId
            products.isNotEmpty() -> products.first().id
            else -> null
        }
    }

    fun openProduct(productId: String) {
        if (productById(productId) != null) {
            selectedProductId = productId
        }
    }

    fun openCategory(categoryPath: String) {
        selectedCategoryPath = categoryPath
    }

    fun openOrder(orderId: String?) {
        selectedOrderId = orderId
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun submitSearch(query: String) {
        val trimmed = query.trim()
        searchQuery = trimmed
        if (trimmed.isNotBlank()) {
            recentSearches =
                listOf(trimmed) + recentSearches.filterNot { it.equals(trimmed, ignoreCase = true) }.take(4)
        }
    }

    fun toggleWishlist(productId: String) {
        if (productById(productId) == null) return
        wishlistIds = if (productId in wishlistIds) wishlistIds - productId else wishlistIds + productId
    }

    fun addToCart(productId: String, size: String? = null, color: String? = null) {
        val product = productById(productId) ?: return
        val resolvedSize = size ?: product.sizes.firstOrNull() ?: return
        val resolvedColor = color ?: product.colors.firstOrNull() ?: return
        val existing = cartEntries.firstOrNull {
            it.productId == productId && it.size == resolvedSize && it.color == resolvedColor
        }
        cartEntries =
            if (existing != null) {
                cartEntries.map {
                    if (it == existing) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                cartEntries + CartEntry(
                    productId = productId,
                    size = resolvedSize,
                    color = resolvedColor,
                    quantity = 1,
                )
            }
    }

    fun moveWishlistToCart(productId: String) {
        addToCart(productId)
        wishlistIds = wishlistIds - productId
    }

    fun removeFromCart(productId: String, size: String, color: String) {
        cartEntries = cartEntries.filterNot {
            it.productId == productId && it.size == size && it.color == color
        }
    }

    fun moveCartItemToWishlist(productId: String, size: String, color: String) {
        toggleWishlist(productId)
        removeFromCart(productId, size, color)
    }

    fun updateQuantity(productId: String, size: String, color: String, delta: Int) {
        cartEntries = cartEntries.mapNotNull { entry ->
            if (entry.productId == productId && entry.size == size && entry.color == color) {
                val newQuantity = entry.quantity + delta
                if (newQuantity <= 0) null else entry.copy(quantity = newQuantity)
            } else {
                entry
            }
        }
    }

    fun selectDeliveryMethod(method: String) {
        selectedDeliveryMethod = method
    }

    fun selectPaymentMethod(method: String) {
        selectedPaymentMethod = method
    }

    fun placeOrder(): OrderRecord? {
        val summary = priceSummary()
        if (cartEntries.isEmpty()) return null

        val record = OrderRecord(
            id = "FDS-$orderSequence",
            items = cartEntries,
            total = summary.finalPayable,
            status = "Active",
            placedOn = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US)),
            paymentMethod = selectedPaymentMethod,
            deliveryMethod = selectedDeliveryMethod,
            addressLabel = selectedAddressLabel,
        )
        orderSequence += 1
        orders = listOf(record) + orders
        selectedOrderId = record.id
        cartEntries = emptyList()
        return record
    }

    fun priceSummary(): PriceSummary {
        val totalMrp = cartLines.sumOf { (entry, product) -> product.originalPrice * entry.quantity }
        val subtotal = cartLines.sumOf { (entry, product) -> product.price * entry.quantity }
        val discount = totalMrp - subtotal
        val deliveryCharge = if (subtotal >= 999 || subtotal == 0) 0 else 49
        val platformFee = if (subtotal == 0) 0 else 9
        val finalPayable = subtotal + deliveryCharge + platformFee
        return PriceSummary(
            totalMrp = totalMrp,
            discount = discount,
            deliveryCharge = deliveryCharge,
            platformFee = platformFee,
            finalPayable = finalPayable,
        )
    }

    private fun productById(productId: String): ProductTemplate? {
        return catalog.firstOrNull { it.id == productId }
    }
}

@Composable
fun rememberFashionAppState(): FashionAppState {
    return remember { FashionAppState() }
}
