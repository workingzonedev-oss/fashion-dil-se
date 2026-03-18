package com.fashiondilse.app.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fashiondilse.app.data.ProductTemplate
import com.fashiondilse.app.data.trustCues
import com.fashiondilse.app.ui.CartEntry
import com.fashiondilse.app.ui.OrderRecord
import com.fashiondilse.app.ui.PriceSummary
import com.fashiondilse.app.ui.components.DetailRow
import com.fashiondilse.app.ui.components.EmptyStateCard
import com.fashiondilse.app.ui.components.FashionField
import com.fashiondilse.app.ui.components.HeaderBar
import com.fashiondilse.app.ui.components.MenuRow
import com.fashiondilse.app.ui.components.PriceSummaryRow
import com.fashiondilse.app.ui.components.PrimaryCtaButton
import com.fashiondilse.app.ui.components.ProductImage
import com.fashiondilse.app.ui.components.ProductTemplateCard
import com.fashiondilse.app.ui.components.ScreenPadding
import com.fashiondilse.app.ui.components.SectionCard
import com.fashiondilse.app.ui.components.SecondaryCtaButton
import com.fashiondilse.app.ui.components.SelectableChipWrap
import com.fashiondilse.app.ui.components.TrustCueStrip
import com.fashiondilse.app.ui.theme.SoftGold
import com.fashiondilse.app.ui.theme.WarmWhite

@Composable
fun WishlistScreen(
    products: List<ProductTemplate>,
    onProductClick: (String) -> Unit,
    onMoveToCart: (String) -> Unit,
    onToggleWishlist: (String) -> Unit,
    onBrowse: () -> Unit,
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
            HeaderBar(title = "Wishlist")
        }
        if (products.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "Your wishlist is empty",
                    message = "Save styles here for quick access and one-tap move to cart.",
                    actionLabel = "Browse Products",
                    onAction = onBrowse,
                )
            }
        } else {
            items(products, key = { it.id }) { product ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProductTemplateCard(
                        product = product,
                        isWishlisted = true,
                        onToggleWishlist = { onToggleWishlist(product.id) },
                        onClick = { onProductClick(product.id) },
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { onToggleWishlist(product.id) },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text("Remove")
                        }
                        Button(
                            onClick = { onMoveToCart(product.id) },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = WarmWhite,
                            ),
                        ) {
                            Text("Move to Cart")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartScreen(
    cartLines: List<Pair<CartEntry, ProductTemplate>>,
    summary: PriceSummary,
    onCheckout: () -> Unit,
    onContinueShopping: () -> Unit,
    onIncreaseQuantity: (CartEntry) -> Unit,
    onDecreaseQuantity: (CartEntry) -> Unit,
    onRemove: (CartEntry) -> Unit,
    onMoveToWishlist: (CartEntry) -> Unit,
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
            HeaderBar(title = "Cart")
        }
        if (cartLines.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "Your cart is empty",
                    message = "Keep discovery easy and jump back in when you want to shop more.",
                    actionLabel = "Continue Shopping",
                    onAction = onContinueShopping,
                )
            }
        } else {
            items(cartLines, key = { "${it.first.productId}-${it.first.size}-${it.first.color}" }) { (entry, product) ->
                SectionCard(title = product.title) {
                    if (product.firstImageUrl.isNotBlank()) {
                        ProductImage(
                            imageUrl = product.firstImageUrl,
                            contentDescription = product.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    DetailRow("Selected Size", entry.size)
                    DetailRow("Selected Color", entry.color)
                    PriceSummaryRow(label = "Price", value = product.priceLabel)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Quantity", style = MaterialTheme.typography.titleMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(onClick = { onDecreaseQuantity(entry) }) { Text("-") }
                            Text(text = entry.quantity.toString(), style = MaterialTheme.typography.titleMedium)
                            OutlinedButton(onClick = { onIncreaseQuantity(entry) }) { Text("+") }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { onRemove(entry) },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text("Remove")
                        }
                        Button(
                            onClick = { onMoveToWishlist(entry) },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            Text("Move to Wishlist")
                        }
                    }
                }
            }
            item {
                SectionCard(title = "Coupon & Delivery") {
                    MenuRow(title = "Apply Coupon", subtitle = "Offers can be applied here when connected.")
                    MenuRow(title = "Delivery Info", subtitle = "Address preview and delivery promise stay visible.")
                }
            }
            item {
                SectionCard(title = "Price Details") {
                    PriceSummaryRow(label = "Total MRP", value = "\u20B9${summary.totalMrp}")
                    PriceSummaryRow(label = "Discount", value = "-\u20B9${summary.discount}")
                    PriceSummaryRow(
                        label = "Delivery Charge",
                        value = if (summary.deliveryCharge == 0) "Free" else "\u20B9${summary.deliveryCharge}",
                    )
                    PriceSummaryRow(label = "Platform Fee", value = "\u20B9${summary.platformFee}")
                    PriceSummaryRow(label = "Final Payable", value = "\u20B9${summary.finalPayable}", emphasized = true)
                }
            }
            item {
                PrimaryCtaButton(text = "Proceed to Checkout", onClick = onCheckout)
                SecondaryCtaButton(
                    text = "Continue Shopping",
                    onClick = onContinueShopping,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }
    }
}

@Composable
fun ProductDetailsScreen(
    product: ProductTemplate?,
    isWishlisted: Boolean,
    onBack: () -> Unit,
    onToggleWishlist: () -> Unit,
    onAddToCart: (String, String) -> Unit,
    onBuyNow: (String, String) -> Unit,
) {
    if (product == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .padding(ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            HeaderBar(title = "Product Details", showBack = true, onBack = onBack)
            EmptyStateCard(
                title = "No product selected",
                message = "Open any live catalog item and its full product details will appear here.",
            )
        }
        return
    }

    val context = LocalContext.current
    var pincode by rememberSaveable { mutableStateOf("") }
    var descriptionExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedColor by rememberSaveable(product.id) { mutableStateOf(product.colors.firstOrNull().orEmpty()) }
    var selectedSize by rememberSaveable(product.id) { mutableStateOf(product.sizes.firstOrNull().orEmpty()) }
    var showResellerPanel by rememberSaveable { mutableStateOf(false) }
    var customSellingPrice by rememberSaveable(product.id) { mutableIntStateOf(product.price) }
    val shortDescription =
        if (product.description.length > 90) {
            "${product.description.take(90)}..."
        } else {
            product.description
        }
    val hasImages = product.images.isNotEmpty()

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = { onAddToCart(selectedSize, selectedColor) },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text("Add to Cart")
                    }
                    Button(
                        onClick = { onBuyNow(selectedSize, selectedColor) },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = WarmWhite,
                        ),
                    ) {
                        Text("Buy Now")
                    }
                }
                if (product.enableReselling) {
                    Button(
                        onClick = {
                            val shareText = product.shareText(customSellingPrice)
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share product"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B5E20),
                            contentColor = WarmWhite,
                        ),
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share & Earn \u20B9${customSellingPrice - product.resellerPrice}")
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                if (hasImages) {
                    val pagerState = rememberPagerState(pageCount = { product.images.size })
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp)
                            .clip(MaterialTheme.shapes.large),
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                        ) { page ->
                            ProductImage(
                                imageUrl = product.images[page],
                                contentDescription = "${product.title} image ${page + 1}",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(WarmWhite.copy(alpha = 0.85f)),
                            ) {
                                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                            }
                            Row {
                                IconButton(
                                    onClick = {
                                        val shareText = product.shareText(product.price)
                                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share product"))
                                    },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(WarmWhite.copy(alpha = 0.85f)),
                                ) {
                                    Icon(Icons.Outlined.Share, contentDescription = "Share")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = onToggleWishlist,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(WarmWhite.copy(alpha = 0.85f)),
                                ) {
                                    Icon(
                                        imageVector = if (isWishlisted) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Wishlist",
                                        tint = if (isWishlisted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                            }
                        }
                        if (product.images.size > 1) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                repeat(product.images.size) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(if (pagerState.currentPage == index) 10.dp else 7.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (pagerState.currentPage == index) WarmWhite
                                                else WarmWhite.copy(alpha = 0.5f),
                                            ),
                                    )
                                }
                            }
                        }
                    }
                    if (product.images.size > 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            itemsIndexed(product.images) { index, imageUrl ->
                                ProductImage(
                                    imageUrl = imageUrl,
                                    contentDescription = "Thumbnail ${index + 1}",
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .then(
                                            if (pagerState.currentPage == index) {
                                                Modifier.background(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                                    RoundedCornerShape(12.dp),
                                                )
                                            } else Modifier,
                                        ),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(420.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                                        SoftGold.copy(alpha = 0.16f),
                                        MaterialTheme.colorScheme.surface,
                                    ),
                                ),
                                shape = MaterialTheme.shapes.large,
                            )
                            .padding(18.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                            }
                            Row {
                                IconButton(onClick = {
                                    val shareText = product.shareText(product.price)
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share product"))
                                }) {
                                    Icon(Icons.Outlined.Share, contentDescription = "Share")
                                }
                                IconButton(onClick = onToggleWishlist) {
                                    Icon(
                                        imageVector = if (isWishlisted) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Wishlist",
                                        tint = if (isWishlisted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                            }
                        }
                        Text(
                            text = product.subtitle,
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.BottomStart),
                        )
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = product.title, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = product.categoryPath,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = product.ratingLabel, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = product.deliveryLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.priceLabel,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = product.originalPriceLabel,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = product.discountLabel,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    TrustCueStrip(items = trustCues)
                }
            }
            if (product.enableReselling) {
                item {
                    SectionCard(title = "Share & Earn (Reselling)") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text("Your cost price", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("\u20B9${product.resellerPrice}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Your margin", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "\u20B9${customSellingPrice - product.resellerPrice}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20),
                                )
                            }
                        }
                        DetailRow("Set your selling price", "Adjust the price you share with customers")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (customSellingPrice > product.resellerPrice + 10) {
                                        customSellingPrice -= 10
                                    }
                                },
                            ) { Text("-\u20B910") }
                            Text(
                                text = "\u20B9$customSellingPrice",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                            )
                            OutlinedButton(
                                onClick = { customSellingPrice += 10 },
                            ) { Text("+\u20B910") }
                        }
                        if (product.meeshoSupplierLink.isNotBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFFFF3E0))
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Outlined.Storefront,
                                    contentDescription = null,
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(24.dp),
                                )
                                Column {
                                    Text(
                                        "Meesho Supplier Product",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100),
                                    )
                                    Text(
                                        "This product is sourced via Meesho supplier",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFBF360C),
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                SectionCard(title = "Variants") {
                    DetailRow("Color", if (selectedColor.isBlank()) "Not available yet" else selectedColor)
                    SelectableChipWrap(
                        items = product.colors,
                        selectedItem = selectedColor,
                        onSelected = { selectedColor = it },
                    )
                    DetailRow("Size", if (selectedSize.isBlank()) "Not available yet" else selectedSize)
                    SelectableChipWrap(
                        items = product.sizes,
                        selectedItem = selectedSize,
                        onSelected = { selectedSize = it },
                    )
                }
            }
            item {
                SectionCard(title = "Delivery") {
                    FashionField(value = pincode, onValueChange = { pincode = it }, label = "Enter pincode")
                    DetailRow("Delivery Estimate", "Fast delivery is highlighted wherever available.")
                    DetailRow("Cash on Delivery", "Available on eligible addresses")
                }
            }
            item {
                SectionCard(title = "Offers") {
                    DetailRow("Bank Offers", "Payment-based offers can appear here.")
                    DetailRow("Coupons", "Clean coupon messaging without clutter.")
                    DetailRow("Free Delivery", if (product.price >= 999) "Eligible" else "Shown at checkout")
                }
            }
            item {
                SectionCard(title = "Product Details") {
                    product.details.forEach { (label, value) ->
                        DetailRow(label, value)
                    }
                }
            }
            item {
                SectionCard(title = "Description") {
                    Text(
                        text = if (descriptionExpanded) product.description else shortDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    SecondaryCtaButton(
                        text = if (descriptionExpanded) "Show Less" else "Read More",
                        onClick = { descriptionExpanded = !descriptionExpanded },
                    )
                }
            }
            item {
                SectionCard(title = "Seller") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Verified seller")
                    }
                    DetailRow("Seller Details", "Verified seller information appears once catalog publishing is connected.")
                    DetailRow("Store Rating", "Seller scores are shown only after live merchant data is available.")
                }
            }
            item {
                SectionCard(title = "Reviews") {
                    DetailRow("Rating Breakdown", "Customer ratings appear here after verified review sync.")
                    DetailRow("Customer Photos", "Customer uploads are shown once approved review media is available.")
                    DetailRow("Latest Reviews", "Only genuine customer reviews will be displayed here.")
                }
            }
        }
    }
}

@Composable
fun CheckoutScreen(
    cartLines: List<Pair<CartEntry, ProductTemplate>>,
    summary: PriceSummary,
    selectedAddressLabel: String,
    selectedDeliveryMethod: String,
    selectedPaymentMethod: String,
    onSelectDeliveryMethod: (String) -> Unit,
    onSelectPaymentMethod: (String) -> Unit,
    onBack: () -> Unit,
    onPlaceOrder: () -> Unit,
    onPaymentFailed: () -> Unit,
) {
    val hasCartItems = cartLines.isNotEmpty()

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (hasCartItems) {
                    PrimaryCtaButton(text = "Place Order", onClick = onPlaceOrder)
                    SecondaryCtaButton(text = "Preview Payment Failed", onClick = onPaymentFailed)
                } else {
                    SecondaryCtaButton(text = "Back", onClick = onBack)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                HeaderBar(title = "Checkout", showBack = true, onBack = onBack)
            }
            item {
                SectionCard(title = "Step 1: Address") {
                    DetailRow("Saved Address", selectedAddressLabel)
                    DetailRow("Address Management", "Saved addresses screen is connected from profile.")
                }
            }
            item {
                SectionCard(title = "Step 2: Delivery Method") {
                    SelectableChipWrap(
                        items = listOf("Standard Delivery", "Express Delivery"),
                        selectedItem = selectedDeliveryMethod,
                        onSelected = onSelectDeliveryMethod,
                    )
                }
            }
            item {
                SectionCard(title = "Step 3: Payment Method") {
                    SelectableChipWrap(
                        items = listOf("UPI", "Card", "Net Banking", "Wallet", "Cash on Delivery"),
                        selectedItem = selectedPaymentMethod,
                        onSelected = onSelectPaymentMethod,
                    )
                }
            }
            item {
                SectionCard(title = "Step 4: Order Summary") {
                    if (cartLines.isEmpty()) {
                        EmptyStateCard(
                            title = "No items ready for checkout",
                            message = "Add a live catalog item to cart and the checkout summary will appear here.",
                        )
                    } else {
                        cartLines.forEach { (entry, product) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (product.firstImageUrl.isNotBlank()) {
                                    ProductImage(
                                        imageUrl = product.firstImageUrl,
                                        contentDescription = product.title,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.title, style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "${entry.quantity} x ${product.priceLabel} \u2022 ${entry.size}, ${entry.color}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                    PriceSummaryRow(label = "Total Payable", value = "\u20B9${summary.finalPayable}", emphasized = true)
                }
            }
        }
    }
}

@Composable
fun OrderSuccessScreen(
    order: OrderRecord?,
    onContinueShopping: () -> Unit,
    onViewOrder: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(92.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), shape = MaterialTheme.shapes.large),
        ) {
            Icon(
                imageVector = Icons.Outlined.Verified,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(38.dp)
                    .align(Alignment.Center),
            )
        }
        Text(text = "Order placed", style = MaterialTheme.typography.headlineMedium)
        order?.let {
            DetailRow("Order ID", it.id)
            DetailRow("Estimated Delivery", it.deliveryMethod)
        }
        PrimaryCtaButton(text = "Continue Shopping", onClick = onContinueShopping)
        SecondaryCtaButton(text = "View Order", onClick = onViewOrder)
    }
}

@Composable
fun PaymentFailedScreen(
    onRetry: () -> Unit,
    onChangeMethod: () -> Unit,
    onReturnToCart: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(92.dp)
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f), shape = MaterialTheme.shapes.large),
        ) {
            Icon(
                imageVector = Icons.Outlined.RemoveShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(38.dp)
                    .align(Alignment.Center),
            )
        }
        Text(text = "Payment failed", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Retry, switch payment mode, or head back to cart without getting stuck.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        PrimaryCtaButton(text = "Retry Payment", onClick = onRetry)
        SecondaryCtaButton(text = "Change Payment Method", onClick = onChangeMethod)
        SecondaryCtaButton(text = "Return to Cart", onClick = onReturnToCart)
    }
}

@Composable
fun OrdersScreen(
    orders: List<OrderRecord>,
    onBack: () -> Unit,
    onOrderClick: (String) -> Unit,
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
            HeaderBar(title = "My Orders", showBack = true, onBack = onBack)
        }
        if (orders.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "No orders yet",
                    message = "Orders placed from checkout will appear here once the shopping flow is used.",
                )
            }
        } else {
            items(orders, key = { it.id }) { order ->
                SectionCard(title = order.id) {
                    DetailRow("Status", order.status)
                    DetailRow("Placed On", order.placedOn)
                    DetailRow("Amount", "\u20B9${order.total}")
                    PrimaryCtaButton(text = "Track / View", onClick = { onOrderClick(order.id) })
                }
            }
        }
    }
}

@Composable
fun OrderDetailsScreen(
    order: OrderRecord?,
    orderLines: List<Pair<CartEntry, ProductTemplate>>,
    onBack: () -> Unit,
    onReturnRequest: () -> Unit,
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
            HeaderBar(title = "Order Details", showBack = true, onBack = onBack)
        }
        if (order == null) {
            item {
                EmptyStateCard(
                    title = "No order selected",
                    message = "Open an order from the orders screen to view its full details.",
                )
            }
        } else {
            item {
                SectionCard(title = "Item Details") {
                    orderLines.forEach { (entry, product) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (product.firstImageUrl.isNotBlank()) {
                                ProductImage(
                                    imageUrl = product.firstImageUrl,
                                    contentDescription = product.title,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.title, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "${entry.quantity} x ${product.priceLabel}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    DetailRow("Order ID", order.id)
                    DetailRow("Status", order.status)
                }
            }
            item {
                SectionCard(title = "Payment & Shipping") {
                    DetailRow("Payment Method", order.paymentMethod)
                    DetailRow("Shipping Address", order.addressLabel)
                    DetailRow("Delivery Mode", order.deliveryMethod)
                }
            }
            item {
                SectionCard(title = "Invoice & Actions") {
                    MenuRow(title = "Download Invoice", subtitle = "Ready for invoice integration")
                    MenuRow(title = "Return / Cancel", subtitle = "Open the return request flow")
                }
            }
            item {
                PrimaryCtaButton(text = "Open Return Request", onClick = onReturnRequest)
            }
        }
    }
}

@Composable
fun ReturnRequestScreen(onBack: () -> Unit) {
    var product by rememberSaveable { mutableStateOf("") }
    var reason by rememberSaveable { mutableStateOf("") }
    var comment by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        HeaderBar(title = "Return Request", showBack = true, onBack = onBack)
        FashionField(value = product, onValueChange = { product = it }, label = "Select product")
        FashionField(value = reason, onValueChange = { reason = it }, label = "Choose reason")
        FashionField(value = comment, onValueChange = { comment = it }, label = "Write comment")
        SecondaryCtaButton(text = "Upload Images", onClick = {})
        PrimaryCtaButton(text = "Submit Request", onClick = {})
    }
}
