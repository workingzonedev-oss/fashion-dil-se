package com.fashiondilse.app.data

import com.fashiondilse.app.ui.OrderRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

private const val AdminStoreUrl = "http://10.0.2.2:4173/api/store"
private const val AdminOrdersUrl = "http://10.0.2.2:4173/api/orders"

data class LinkedAdminStore(
    val campaigns: List<HomeCampaign> = emptyList(),
    val discoverItems: List<DiscoverItem> = emptyList(),
    val products: List<ProductTemplate> = emptyList(),
    val offers: List<OfferItem> = emptyList(),
    val notifications: List<NotificationItem> = emptyList(),
    val updatedAt: String = "",
)

suspend fun fetchLinkedAdminStore(): LinkedAdminStore? = withContext(Dispatchers.IO) {
    runCatching {
        val connection = (URL(AdminStoreUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5000
            readTimeout = 5000
            useCaches = true
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Connection", "keep-alive")
        }

        try {
            connection.inputStream.bufferedReader().use(BufferedReader::readText).let(::JSONObject).let { root ->
                LinkedAdminStore(
                    campaigns = root.optJSONArray("campaigns").toCampaigns(),
                    discoverItems = root.optJSONArray("discoverItems").toDiscoverItems(),
                    products = root.optJSONArray("products").toProducts(),
                    offers = root.optJSONArray("offers").toOffers(),
                    notifications = root.optJSONArray("notifications").toNotifications(),
                    updatedAt = root.optString("updatedAt"),
                )
            }
        } finally {
            connection.disconnect()
        }
    }.getOrNull()
}

suspend fun pushOrderToAdmin(
    order: OrderRecord,
    catalog: List<ProductTemplate>,
): Boolean = withContext(Dispatchers.IO) {
    runCatching {
        val payload = JSONObject().apply {
            put("id", order.id)
            put("customerName", "Fashion Dil Se customer")
            put("status", order.status)
            put("total", order.total)
            put("placedOn", order.placedOn)
            put("paymentMethod", order.paymentMethod)
            put("deliveryMethod", order.deliveryMethod)
            put("addressLabel", order.addressLabel)
            put(
                "items",
                JSONArray(
                    order.items.map { entry ->
                        val product = catalog.firstOrNull { it.id == entry.productId }
                        JSONObject().apply {
                            put("productId", entry.productId)
                            put("title", product?.title ?: "Unknown product")
                            put("quantity", entry.quantity)
                            put("size", entry.size)
                            put("color", entry.color)
                        }
                    },
                ),
            )
        }

        val connection = (URL(AdminOrdersUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 5000
            readTimeout = 5000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Connection", "keep-alive")
        }

        try {
            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(payload.toString())
            }
            connection.responseCode in 200..299
        } finally {
            connection.disconnect()
        }
    }.getOrDefault(false)
}

private fun JSONArray?.toCampaigns(): List<HomeCampaign> {
    if (this == null) return emptyList()
    return buildList {
        for (index in 0 until length()) {
            val item = optJSONObject(index) ?: continue
            if (!item.optString("status", "Published").equals("Published", ignoreCase = true)) continue
            add(
                HomeCampaign(
                    title = item.optString("title"),
                    eyebrow = item.optString("eyebrow"),
                    subtitle = item.optString("subtitle"),
                    cta = item.optString("cta"),
                    imageUrl = item.optString("imageUrl"),
                ),
            )
        }
    }
}

private fun JSONArray?.toProducts(): List<ProductTemplate> {
    if (this == null) return emptyList()
    return buildList {
        for (index in 0 until length()) {
            val item = optJSONObject(index) ?: continue
            if (!item.optString("status", "Published").equals("Published", ignoreCase = true)) continue
            add(
                ProductTemplate(
                    id = item.optString("id"),
                    title = item.optString("title"),
                    subtitle = item.optString("subtitle"),
                    collection = item.optString("collection"),
                    categoryPath = item.optString("categoryPath"),
                    price = item.optInt("price"),
                    originalPrice = item.optInt("originalPrice"),
                    rating = item.optDouble("rating", 0.0),
                    deliveryLabel = item.optString("deliveryLabel"),
                    description = item.optString("description"),
                    colors = item.optJSONArray("colors").toStringList(),
                    sizes = item.optJSONArray("sizes").toStringList(),
                    details = item.optJSONArray("details").toDetailsList(),
                    images = item.optJSONArray("images").toStringList(),
                    resellerPrice = item.optInt("resellerPrice", 0),
                    enableReselling = item.optBoolean("enableReselling", false),
                    meeshoSupplierLink = item.optString("meeshoSupplierLink", ""),
                ),
            )
        }
    }
}

private fun JSONArray?.toDiscoverItems(): List<DiscoverItem> {
    if (this == null) return emptyList()
    return buildList {
        for (index in 0 until length()) {
            val item = optJSONObject(index) ?: continue
            if (!item.optString("status", "Published").equals("Published", ignoreCase = true)) continue
            add(
                DiscoverItem(
                    id = item.optString("id"),
                    sectionKey = item.optString("sectionKey"),
                    title = item.optString("title"),
                    subtitle = item.optString("subtitle"),
                    imageUrl = item.optString("imageUrl"),
                ),
            )
        }
    }
}

private fun JSONArray?.toOffers(): List<OfferItem> {
    if (this == null) return emptyList()
    return buildList {
        for (index in 0 until length()) {
            val item = optJSONObject(index) ?: continue
            if (!item.optString("status", "Published").equals("Published", ignoreCase = true)) continue
            add(
                OfferItem(
                    code = item.optString("code"),
                    description = item.optString("description"),
                    minimumOrder = item.optInt("minimumOrder"),
                    status = item.optString("status"),
                ),
            )
        }
    }
}

private fun JSONArray?.toNotifications(): List<NotificationItem> {
    if (this == null) return emptyList()
    return buildList {
        for (index in 0 until length()) {
            val item = optJSONObject(index) ?: continue
            if (!item.optString("status", "Published").equals("Published", ignoreCase = true)) continue
            add(
                NotificationItem(
                    title = item.optString("title"),
                    message = item.optString("message"),
                    type = item.optString("type"),
                    status = item.optString("status"),
                ),
            )
        }
    }
}

private fun JSONArray?.toStringList(): List<String> {
    if (this == null) return emptyList()
    return buildList {
        for (index in 0 until length()) {
            add(optString(index))
        }
    }
}

private fun JSONArray?.toDetailsList(): List<Pair<String, String>> {
    if (this == null) return emptyList()
    return buildList {
        for (index in 0 until length()) {
            val entry = optJSONArray(index) ?: continue
            add(entry.optString(0) to entry.optString(1))
        }
    }
}
