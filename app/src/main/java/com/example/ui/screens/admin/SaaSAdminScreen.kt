package com.example.ui.screens.admin

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VendePrimary
import com.example.ui.theme.VendeSecondary
import com.example.ui.theme.VendeSuccess

@Composable
fun SaaSAdminScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Planos & Assinaturas, 1: Usuários, 2: Logs & Auditoria

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
            .testTag("saas_admin_screen")
    ) {
        Surface(color = Color(0xFF131D31), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = VendeSecondary, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Painel Superadministrador SaaS",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Gestão de Tenants, Planos, Limites de IA e Auditoria",
                        style = MaterialTheme.typography.bodySmall,
                        color = VendePrimary
                    )
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF0F172A),
            contentColor = VendePrimary
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Planos SaaS") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Usuários") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Auditoria") })
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // PLANOS E ASSINATURAS
                    Text(text = "Tabela de Planos Comerciais", color = Color.White, fontWeight = FontWeight.Bold)

                    val plans = listOf(
                        PlanItem(
                            name = "Plano Gratuito (Trial)",
                            price = "R$ 0,00",
                            features = listOf("1 Infoproduto", "5 Gerações Gemini / mês", "Checkout Pix com taxa de 4.99%", "Suporte comunitário")
                        ),
                        PlanItem(
                            name = "Plano Básico",
                            price = "R$ 47,00 / mês",
                            features = listOf("5 Infoprodutos", "30 Gerações Gemini / mês", "Páginas de Vendas com domínio próprio", "Taxa Pix 2.99%")
                        ),
                        PlanItem(
                            name = "Plano Profissional (Mais Popular)",
                            price = "R$ 97,00 / mês",
                            isPopular = true,
                            features = listOf("Infoprodutos Ilimitados", "150 Gerações Gemini / mês", "CRM & Automação WhatsApp", "Taxa Pix 0.99%", "Exportação PDF Alta Resolução")
                        ),
                        PlanItem(
                            name = "Plano Empresarial (White-Label)",
                            price = "R$ 297,00 / mês",
                            features = listOf("Múltiplos usuários por organização", "Gerações de IA Ilimitadas", "Suporte prioritário 24/7", "Webhooks customizados")
                        )
                    )

                    plans.forEach { plan ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (plan.isPopular) VendePrimary else Color(0xFF334155),
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = plan.name, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text(text = plan.price, color = VendeSuccess, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                plan.features.forEach { feat ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = VendePrimary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = feat, color = Color(0xFFCBD5E1), style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // USUÁRIOS
                    Text(text = "Usuários e Organizações Cadastradas", color = Color.White, fontWeight = FontWeight.Bold)

                    val demoUsers = listOf(
                        UserAdminItem("Cláudio Silva", "claudiorevestres@gmail.com", "Super Administrador", "Ativo", VendeSuccess),
                        UserAdminItem("Mariana Costa", "mariana.infoprod@gmail.com", "Criador Pro", "Ativo", VendeSuccess),
                        UserAdminItem("Lucas Rocha", "lucas.rocha@tech.io", "Criador Básico", "Ativo", VendeSuccess),
                        UserAdminItem("Empresa Alpha Treinamentos", "contato@alphatreinamentos.com", "Empresarial White-Label", "Ativo", VendePrimary)
                    )

                    demoUsers.forEach { u ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = u.name, color = Color.White, fontWeight = FontWeight.Bold)
                                    Text(text = "${u.email} • ${u.role}", color = Color(0xFF94A3B8), style = MaterialTheme.typography.bodySmall)
                                }
                                Surface(
                                    color = u.statusColor.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = u.status,
                                        color = u.statusColor,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // LOGS E AUDITORIA
                    Text(text = "Logs de Segurança & Auditoria do Sistema", color = Color.White, fontWeight = FontWeight.Bold)

                    val auditLogs = listOf(
                        "Hoje 17:41 • [SECURITY] Regras de segurança Firestore implementadas com zero-trust",
                        "Hoje 17:41 • [APP_CHECK] Token reCAPTCHA Enterprise provisionado com sucesso",
                        "Hoje 17:38 • [DATABASE] Cloud Firestore Enterprise instanciado em us-east1",
                        "Hoje 17:35 • [AUTH] Google Sign-In Credential Manager autorizado",
                        "Hoje 17:30 • [GEMINI] Motor Gemini 3.8 Flash conectado via Firebase AI Logic"
                    )

                    auditLogs.forEach { log ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = log,
                                color = Color(0xFF94A3B8),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class PlanItem(
    val name: String,
    val price: String,
    val isPopular: Boolean = false,
    val features: List<String>
)

private data class UserAdminItem(
    val name: String,
    val email: String,
    val role: String,
    val status: String,
    val statusColor: Color
)
