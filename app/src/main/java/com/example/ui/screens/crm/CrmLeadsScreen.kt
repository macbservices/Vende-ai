package com.example.ui.screens.crm

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Lead
import com.example.data.repository.LeadRepository
import com.example.ui.theme.VendePrimary
import com.example.ui.theme.VendeSecondary
import com.example.ui.theme.VendeSuccess
import kotlinx.coroutines.launch

@Composable
fun CrmLeadsScreen(
    currentUserId: String,
    leadRepository: LeadRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val leads by leadRepository.observeLeads(currentUserId).collectAsStateWithLifecycle(initialValue = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var newLeadName by remember { mutableStateOf("") }
    var newLeadEmail by remember { mutableStateOf("") }
    var newLeadPhone by remember { mutableStateOf("") }
    var newLeadOrigin by remember { mutableStateOf("Página de Captura") }

    val stages = listOf(
        "new" to "Novos Leads",
        "contacted" to "Contatados",
        "negotiating" to "Em Negociação",
        "interested" to "Interessados",
        "converted" to "Convertidos (Venda)",
        "lost" to "Perdidos"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
            .testTag("crm_leads_screen")
    ) {
        Surface(color = Color(0xFF131D31), modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CRM & Funil de Vendas",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "${leads.size} leads no pipeline ativo",
                        style = MaterialTheme.typography.bodySmall,
                        color = VendePrimary
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = VendePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color(0xFF0B1120))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar Lead", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold)
                }
            }
        }

        // Horizontal Kanban Funnel Board
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            stages.forEach { (stageKey, stageLabel) ->
                val stageLeads = leads.filter { it.stage == stageKey }
                KanbanColumn(
                    title = stageLabel,
                    leads = stageLeads,
                    onAdvanceLead = { lead ->
                        val nextStage = when (lead.stage) {
                            "new" -> "contacted"
                            "contacted" -> "negotiating"
                            "negotiating" -> "interested"
                            "interested" -> "converted"
                            else -> "converted"
                        }
                        scope.launch {
                            leadRepository.updateLeadStage(lead.id, nextStage)
                            Toast.makeText(context, "Lead avançado no funil!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Cadastrar Novo Lead", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newLeadName,
                        onValueChange = { newLeadName = it },
                        label = { Text("Nome Completo", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VendePrimary,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF0B1120),
                            unfocusedContainerColor = Color(0xFF0B1120)
                        )
                    )
                    OutlinedTextField(
                        value = newLeadEmail,
                        onValueChange = { newLeadEmail = it },
                        label = { Text("E-mail", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = VendePrimary,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF0B1120),
                            unfocusedContainerColor = Color(0xFF0B1120)
                        )
                    )
                    OutlinedTextField(
                        value = newLeadPhone,
                        onValueChange = { newLeadPhone = it },
                        label = { Text("WhatsApp", color = Color(0xFF94A3B8)) },
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
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newLeadName.isNotBlank() && newLeadEmail.isNotBlank()) {
                            scope.launch {
                                leadRepository.saveLead(
                                    Lead(
                                        userId = currentUserId,
                                        name = newLeadName,
                                        email = newLeadEmail,
                                        phone = newLeadPhone,
                                        stage = "new",
                                        origin = newLeadOrigin
                                    )
                                )
                                showAddDialog = false
                                newLeadName = ""
                                newLeadEmail = ""
                                newLeadPhone = ""
                                Toast.makeText(context, "Lead registrado no funil!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VendePrimary)
                ) {
                    Text("Salvar", color = Color(0xFF0B1120), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }
}

@Composable
private fun KanbanColumn(
    title: String,
    leads: List<Lead>,
    onAdvanceLead: (Lead) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Surface(
                    color = Color(0xFF1E293B),
                    shape = CircleShape
                ) {
                    Text(
                        text = "${leads.size}",
                        color = VendePrimary,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (leads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text("Sem contatos", color = Color(0xFF64748B), style = MaterialTheme.typography.labelMedium)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(leads) { lead ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = lead.name, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = lead.email, color = Color(0xFF94A3B8), style = MaterialTheme.typography.labelSmall)
                                if (lead.phone.isNotBlank()) {
                                    Text(text = "WhatsApp: ${lead.phone}", color = VendeSuccess, style = MaterialTheme.typography.labelSmall)
                                }

                                if (lead.stage != "converted" && lead.stage != "lost") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { onAdvanceLead(lead) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Avançar Etapa", color = VendePrimary, style = MaterialTheme.typography.labelSmall)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = VendePrimary, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
