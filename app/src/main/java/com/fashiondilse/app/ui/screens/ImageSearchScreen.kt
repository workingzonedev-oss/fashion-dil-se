package com.fashiondilse.app.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.fashiondilse.app.ui.components.DetailRow
import com.fashiondilse.app.ui.components.EmptyStateCard
import com.fashiondilse.app.ui.components.HeaderBar
import com.fashiondilse.app.ui.components.PrimaryCtaButton
import com.fashiondilse.app.ui.components.ScreenPadding
import com.fashiondilse.app.ui.components.SectionCard
import com.fashiondilse.app.ui.components.SecondaryCtaButton

@Composable
fun ImageSearchScreen(
    onBack: () -> Unit,
    onOpenTextSearch: () -> Unit,
) {
    val context = LocalContext.current
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var sourceLabel by rememberSaveable { mutableStateOf("No image selected yet") }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            previewBitmap = bitmap
            sourceLabel = "Camera image ready for visual search"
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val bitmap = uri?.let { loadBitmapFromUri(context, it) }
        if (bitmap != null) {
            previewBitmap = bitmap
            sourceLabel = "Gallery image ready for visual search"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        HeaderBar(title = "Search by Image", showBack = true, onBack = onBack)

        if (previewBitmap == null) {
            EmptyStateCard(
                title = "Visual search is ready",
                message = "Capture a product photo or upload a screenshot to look for similar styles.",
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Image(
                    bitmap = previewBitmap!!.asImageBitmap(),
                    contentDescription = "Selected search image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        SectionCard(title = "Choose Image Source") {
            DetailRow("Current Status", sourceLabel)
            PrimaryCtaButton(
                text = "Use Camera",
                onClick = { cameraLauncher.launch(null) },
            )
            SecondaryCtaButton(
                text = "Open Gallery",
                onClick = { galleryLauncher.launch("image/*") },
            )
        }

        SectionCard(title = "Visual Search Flow") {
            DetailRow("Step 1", "Capture or upload a clear product image.")
            DetailRow("Step 2", "Review the preview before searching for similar items.")
            DetailRow("Step 3", "Continue to the main search flow for live catalog matching.")
        }

        SectionCard(title = "Quick Actions") {
            ActionRow(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                title = "Retake Photo",
                subtitle = "Capture a fresh product image from camera",
                onClick = { cameraLauncher.launch(null) },
            )
            ActionRow(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.PhotoLibrary,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                title = "Choose Another Image",
                subtitle = "Open gallery and select a different style image",
                onClick = { galleryLauncher.launch("image/*") },
            )
        }

        PrimaryCtaButton(
            text = "Continue to Search",
            onClick = onOpenTextSearch,
        )
    }
}

@Composable
private fun ActionRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    androidx.compose.material3.Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(12.dp),
            ) {
                icon()
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun loadBitmapFromUri(
    context: Context,
    uri: Uri,
): Bitmap? {
    return runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }.getOrNull()
}
