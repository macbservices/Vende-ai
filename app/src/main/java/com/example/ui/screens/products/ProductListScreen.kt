package com.example.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Product
import com.example.data.repository.ProductRepository
import com.example.ui.theme.VendePrimary
import com.example.ui.theme.VendeSecondary
import com.example.ui.theme.VendeSuccess
import kotlinx.coroutines.launch

@Composable
fun ProductListScreen(
    currentUserId: String,
    productRepository: ProductRepository,
    onNavigateToCreate: () -> Unit,
    onSelectProductForEditor: (productId: String) -> Unit,
    onSelectProductForPage: (productId: String) -> Unit,
    onSelectProductForCheckout: (productId: String) -> Unit,
    onSelectProductForMarketing: (productId: String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val products by productRepository.observeProducts(currentUserId).collectAsStateWithLifecycle(initialValue = emptyList())
    var selectedFilter by remember { mutableStateOf("Todos") }

    val filteredProducts = when (selectedFilter) {
        "Prontos" -> products.filter { it.status == "ready" || it.status == "published" }
        "Rascunhos" -> products.filter { it.status == "draft" || it.status == "generating" }
        else -> products
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
            .padding(16.dp)
            .testTag("product_list_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Meus Infoprodutos",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "${products.size} infoprodutos cadastrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }

            Button(
                onClick = onNavigateToCreate,
                colors = ButtonDefaults.buttonColors(containerColor = VendePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color(0xFF0B1120))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Novo", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filters
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todos", "Prontos", "Rascunhos").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VendePrimary,
                        selectedLabelColor = Color(0xFF0B1120),
                        containerColor = Color(0xFF131D31),
                        labelColor = Color(0xFF94A3B8)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Nenhum produto encontrado neste filtro.",
                        color = Color(0xFF94A3B8),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredProducts) { product ->
                    ProductItemCard(
                        product = product,
                        onOpenEditor = { onSelectProductForEditor(product.id) },
                        onOpenPage = { onSelectProductForPage(product.id) },
                        onOpenCheckout = { onSelectProductForCheckout(product.id) },
                        onOpenMarketing = { onSelectProductForMarketing(product.id) },
                        onDelete = {
                            scope.launch {
                                productRepository.deleteProduct(product.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductItemCard(
    product: Product,
    onOpenEditor: () -> Unit,
    onOpenPage: () -> Unit,
    onOpenCheckout: () -> Unit,
    onOpenMarketing: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    if (product.subtitle.isNotBlank()) {
                        Text(
                            text = product.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Surface(
                    color = when (product.status) {
                        "ready", "published" -> VendeSuccess.copy(alpha = 0.2f)
                        "generating" -> VendeSecondary.copy(alpha = 0.2f)
                        else -> Color(0xFF334155)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (product.status) {
                            "published" -> "Publicado"
                            "ready" -> "Pronto"
                            "generating" -> "Gerando..."
                            else -> "Rascunho"
                        },
                        color = when (product.status) {
                            "ready", "published" -> VendeSuccess
                            "generating" -> VendeSecondary
                            else -> Color(0xFFCBD5E1)
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${product.niche} • ${product.chapterCount} Capítulos • R$ ${"%.2f".format(product.price)}",
                style = MaterialTheme.typography.labelMedium,
                color = VendePrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenEditor,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = VendePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editor", color = Color.White, style = MaterialTheme.typography.labelMedium)
                }

                Button(
                    onClick = onOpenPage,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = VendeSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Página", color = Color.White, style = MaterialTheme.typography.labelMedium)
                }

                Button(
                    onClick = onOpenCheckout,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = VendeSuccess, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pix", color = Color.White, style = MaterialTheme.typography.labelMedium)
                }

                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Deletar", tint = Color(0xFFEF4444))
                }
            }
        }
    }
}
