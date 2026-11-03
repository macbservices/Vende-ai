package com.example.ui.screens.reports

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.OrderRepository
import com.example.data.repository.ProductRepository
import com.example.ui.theme.VendePrimary
import com.example.ui.theme.VendeSecondary
import com.example.ui.theme.VendeSuccess

@Composable
fun ReportsScreen(
    currentUserId: String,
    productRepository: ProductRepository,
    orderRepository: OrderRepository
) {
    val products by productRepository.observeProducts(currentUserId).collectAsStateWithLifecycle(initialValue = emptyList())
    val orders by orderRepository.observeOrders(currentUserId).collectAsStateWithLifecycle(initialValue = emptyList())

    val totalRevenue = orders.filter { it.status == "paid" }.sumOf { it.amount }
    val averageTicket = if (orders.any { it.status == "paid" }) {
        totalRevenue / orders.count { it.status == "paid" }
    } else 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("reports_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(color = Color(0xFF131D31), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Relatórios & Métricas de Conversão",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Acompanhamento em tempo real de faturamento, ticket médio e IA",
                    style = MaterialTheme.typography.bodySmall,
                    color = VendePrimary
                )
            }
        }

        // Metrics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportMetricCard(
                modifier = Modifier.weight(1f),
                title = "Ticket Médio",
                value = "R$ ${"%.2f".format(averageTicket)}",
                subtitle = "Por venda aprovada",
                accentColor = VendeSuccess
            )
            ReportMetricCard(
                modifier = Modifier.weight(1f),
                title = "Conversão Pix",
                value = if (orders.isNotEmpty()) "${(orders.count { it.status == "paid" } * 100) / orders.size}%" else "0%",
                subtitle = "Pedidos pagos",
                accentColor = VendePrimary
            )
        }

        // AI Consumption Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = VendeSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Consumo de Inteligência Artificial Gemini", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Capítulos Gerados neste ciclo:", color = Color(0xFF94A3B8), style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { 0.35f },
                    modifier = Modifier.fillMaxWidth(),
                    color = VendePrimary,
                    trackColor = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "35 / 100 gerações mensais (Plano Pro)", color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall)

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Tokens de Linguagem Utilizados:", color = Color(0xFF94A3B8), style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { 0.18f },
                    modifier = Modifier.fillMaxWidth(),
                    color = VendeSecondary,
                    trackColor = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "182.400 / 1.000.000 tokens (Latência média: 1.2s)", color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun ReportMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = accentColor, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = Color(0xFF64748B), style = MaterialTheme.typography.labelSmall)
        }
    }
}
