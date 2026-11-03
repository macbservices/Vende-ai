package com.example.ui.screens.checkout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.repository.OrderRepository
import com.example.data.repository.ProductRepository
import com.example.ui.theme.VendePrimary
import com.example.ui.theme.VendeSecondary
import com.example.ui.theme.VendeSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CheckoutPaymentsScreen(
    currentUserId: String,
    initialProductId: String? = null,
    productRepository: ProductRepository,
    orderRepository: OrderRepository
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    val products by productRepository.observeProducts(currentUserId).collectAsStateWithLifecycle(initialValue = emptyList())
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Checkout em Tempo Real, 1: Gateways & Webhooks

    // Checkout buyer fields
    var buyerName by remember { mutableStateOf("Carlos Silva") }
    var buyerEmail by remember { mutableStateOf("carlos.silva@exemplo.com") }
    var buyerPhone by remember { mutableStateOf("(11) 98765-4321") }
    var couponCode by remember { mutableStateOf("") }
    var discountPercent by remember { mutableIntStateOf(0) }
    var selectedPaymentMethod by remember { mutableStateOf("pix") } // pix, credit_card, boleto

    // Generated Order State
    var currentOrder by remember { mutableStateOf<Order?>(null) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var isDeliveredSuccess by remember { mutableStateOf(false) }

    // Gateway configs
    var selectedGateway by remember { mutableStateOf("Efí Pix (Banco Central)") }
    var gatewayApiKey by remember { mutableStateOf("efi_live_client_id_sec_893274") }
    var webhookUrl by remember { mutableStateOf("https://vendeia-api.cloudrun.app/webhooks/efi") }

    LaunchedEffect(products, initialProductId) {
        if (products.isNotEmpty()) {
            selectedProduct = if (!initialProductId.isNullOrBlank()) {
                products.firstOrNull { it.id == initialProductId } ?: products.first()
            } else {
                products.first()
            }
        }
    }

    val basePrice = selectedProduct?.price ?: 97.0
    val finalPrice = basePrice * (1.0 - (discountPercent / 100.0))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
            .testTag("checkout_payments_screen")
    ) {
        // TopBar
        Surface(color = Color(0xFF131D31), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Checkout & Pagamentos",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Recebimento via Pix, Cartão e Entrega Automática de Infoprodutos",
                    style = MaterialTheme.typography.bodySmall,
                    color = VendePrimary
                )
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF0F172A),
            contentColor = VendePrimary
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Checkout Ativo") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Configurar Gateways") })
        }

        if (selectedTab == 0) {
            // CHECKOUT FLOW
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Product Summary Header
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Infoproduto:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                            Text(
                                text = selectedProduct?.title ?: "Selecione um Produto",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "${selectedProduct?.chapterCount ?: 0} Capítulos • Entrega Automática em PDF",
                                style = MaterialTheme.typography.bodySmall,
                                color = VendePrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "R$ ${"%.2f".format(finalPrice)}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                color = VendeSuccess
                            )
                            if (discountPercent > 0) {
                                Text(text = "$discountPercent% OFF aplicado", color = VendeSecondary, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                if (isDeliveredSuccess && currentOrder != null) {
                    // TELA DE ENTREGA AUTOMÁTICA PÓS-PAGAMENTO
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, VendeSuccess, RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = VendeSuccess, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Pagamento Confirmado via Pix!",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "O pedido #${currentOrder?.id?.take(8)} foi liquidado via webhook e o material foi liberado com sucesso.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFCBD5E1),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            Button(
                                onClick = {
                                    Toast.makeText(context, "Download do PDF do e-book iniciado!", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VendeSuccess),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Acessar / Baixar Meu E-book", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    isDeliveredSuccess = false
                                    currentOrder = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Novo Pedido de Teste", color = Color.White)
                            }
                        }
                    }
                } else if (currentOrder != null && currentOrder?.status == "pending") {
                    // PIX QR CODE & EMV
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Pague com Pix para Liberação Imediata",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Escaneie o QR Code ou use o Pix Copia e Cola:",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = "Pix QR Code",
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(140.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Surface(
                                color = Color(0xFF0B1120),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = currentOrder?.pixCode?.take(36) ?: "",
                                        color = Color(0xFF94A3B8),
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(currentOrder?.pixCode ?: ""))
                                            Toast.makeText(context, "Código Pix copiado!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copiar", tint = VendePrimary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Webhook Payment Simulator Button
                            Button(
                                onClick = {
                                    val order = currentOrder ?: return@Button
                                    isProcessingPayment = true
                                    scope.launch {
                                        delay(1500)
                                        orderRepository.updateOrderStatus(order.id, status = "paid", isDelivered = true)
                                        isProcessingPayment = false
                                        isDeliveredSuccess = true
                                        Toast.makeText(context, "Webhook recebido: Pagamento aprovado!", Toast.LENGTH_LONG).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VendeSuccess),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                enabled = !isProcessingPayment
                            ) {
                                if (isProcessingPayment) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                                } else {
                                    Text("Simular Confirmação Webhook Pix", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // BUYER FORM
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = "Dados do Comprador", color = Color.White, fontWeight = FontWeight.Bold)

                            CheckoutTextField(value = buyerName, onValueChange = { buyerName = it }, label = "Nome Completo")
                            CheckoutTextField(value = buyerEmail, onValueChange = { buyerEmail = it }, label = "E-mail para Entrega")
                            CheckoutTextField(value = buyerPhone, onValueChange = { buyerPhone = it }, label = "WhatsApp / Celular")

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CheckoutTextField(
                                    value = couponCode,
                                    onValueChange = { couponCode = it },
                                    label = "Cupom de Desconto",
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        if (couponCode.uppercase() == "PROMO10" || couponCode.uppercase() == "VENDE10") {
                                            discountPercent = 10
                                            Toast.makeText(context, "Cupom aplicado: 10% OFF!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Cupom inválido", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Aplicar", color = VendePrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "Forma de Pagamento:", color = Color.White, fontWeight = FontWeight.Bold)

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("pix" to "Pix Instantâneo", "credit_card" to "Cartão de Crédito").forEach { (method, label) ->
                                    FilterChip(
                                        selected = selectedPaymentMethod == method,
                                        onClick = { selectedPaymentMethod = method },
                                        label = { Text(label) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = VendePrimary,
                                            selectedLabelColor = Color(0xFF0B1120),
                                            containerColor = Color(0xFF1E293B),
                                            labelColor = Color(0xFF94A3B8)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val prod = selectedProduct ?: return@Button
                                    val fakePix = "00020126580014br.gov.bcb.pix0136vendeia-${prod.id}-emv5204000053039865405${"%.2f".format(finalPrice)}5802BR5925VendeIA6009SaoPaulo62070503***6304"
                                    scope.launch {
                                        val newOrder = Order(
                                            productId = prod.id,
                                            userId = currentUserId,
                                            customerName = buyerName,
                                            customerEmail = buyerEmail,
                                            customerPhone = buyerPhone,
                                            amount = finalPrice,
                                            status = "pending",
                                            paymentMethod = selectedPaymentMethod,
                                            pixCode = fakePix
                                        )
                                        val res = orderRepository.createOrder(newOrder)
                                        val orderId = res.getOrNull() ?: ""
                                        currentOrder = newOrder.copy(id = orderId)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VendeSuccess),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text("Gerar Pedido Pix (R$ ${"%.2f".format(finalPrice)})", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // CONFIGURAÇÕES DE GATEWAY
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Gateways & Webhooks Homologados",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                val gateways = listOf(
                    "Efí Pix (Banco Central)",
                    "Mercado Pago",
                    "Stripe Checkout",
                    "Kiwify",
                    "Perfect Pay"
                )

                gateways.forEach { gw ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedGateway == gw) Color(0xFF1E293B) else Color(0xFF131D31)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                if (selectedGateway == gw) VendePrimary else Color(0xFF334155),
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = gw, color = Color.White, fontWeight = FontWeight.Bold)
                                Text(text = "Taxa: 0.99% • Pix Dinâmico e Boleto", color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelSmall)
                            }
                            Button(
                                onClick = { selectedGateway = gw },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedGateway == gw) VendePrimary else Color(0xFF0F172A)
                                )
                            ) {
                                Text(
                                    text = if (selectedGateway == gw) "Ativo ✓" else "Ativar",
                                    color = if (selectedGateway == gw) Color(0xFF0B1120) else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Credenciais do Gateway Ativo ($selectedGateway):", color = Color.White, fontWeight = FontWeight.Bold)
                CheckoutTextField(value = gatewayApiKey, onValueChange = { gatewayApiKey = it }, label = "Client ID / Chave Secreta")
                CheckoutTextField(value = webhookUrl, onValueChange = { webhookUrl = it }, label = "URL de Webhook (Notificação Instantânea)")

                Button(
                    onClick = {
                        Toast.makeText(context, "Configurações de gateway salvas!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VendePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salvar Integração de Pagamento", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CheckoutTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF94A3B8)) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = VendePrimary,
            unfocusedBorderColor = Color(0xFF334155),
            focusedContainerColor = Color(0xFF0B1120),
            unfocusedContainerColor = Color(0xFF0B1120)
        )
    )
}
