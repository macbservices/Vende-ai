package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeminiAiEngine
import com.example.data.repository.LandingPageRepository
import com.example.data.repository.LeadRepository
import com.example.data.repository.OrderRepository
import com.example.data.repository.ProductRepository
import com.example.ui.screens.admin.SaaSAdminScreen
import com.example.ui.screens.assistant.GeminiAssistantScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.checkout.CheckoutPaymentsScreen
import com.example.ui.screens.creator.CreateProductWizardScreen
import com.example.ui.screens.creatives.CreativesVideosScreen
import com.example.ui.screens.crm.CrmLeadsScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.editor.EbookEditorScreen
import com.example.ui.screens.landingpages.LandingPageBuilderScreen
import com.example.ui.screens.marketing.MarketingAiScreen
import com.example.ui.screens.orders.OrdersSalesScreen
import com.example.ui.screens.products.ProductListScreen
import com.example.ui.screens.reports.ReportsScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.whatsapp.WhatsAppAutomationScreen
import com.example.ui.theme.VendePrimary
import com.example.ui.theme.VendeSecondary
import com.example.ui.theme.VendeSuccess
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

enum class ScreenRoute(val label: String, val icon: ImageVector) {
    DASHBOARD("Visão Geral", Icons.Default.Dashboard),
    CREATE_PRODUCT("Criar Infoproduto", Icons.Default.Add),
    PRODUCTS("Meus Produtos", Icons.Default.MenuBook),
    EBOOK_EDITOR("Editor de E-books", Icons.Default.Edit),
    LANDING_PAGES("Páginas de Vendas", Icons.Default.Public),
    CHECKOUT("Checkout & Pix", Icons.Default.Payment),
    MARKETING("Marketing com IA", Icons.Default.Campaign),
    CREATIVES("Criativos & Vídeos", Icons.Default.Videocam),
    CRM_LEADS("CRM & Leads", Icons.Default.People),
    WHATSAPP("WhatsApp", Icons.Default.Chat),
    ORDERS("Pedidos & Vendas", Icons.Default.Receipt),
    REPORTS("Relatórios", Icons.Default.TrendingUp),
    ASSISTANT("Assistente Gemini", Icons.Default.AutoAwesome),
    SETTINGS("Configurações", Icons.Default.Settings),
    SAAS_ADMIN("Administração SaaS", Icons.Default.AdminPanelSettings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = { currentUser = Firebase.auth.currentUser }
        )
    } else {
        val currentUserId = currentUser!!.uid
        val productRepository = remember { ProductRepository(context) }
        val landingPageRepository = remember { LandingPageRepository(context) }
        val orderRepository = remember { OrderRepository(context) }
        val leadRepository = remember { LeadRepository(context) }
        val geminiEngine = remember { GeminiAiEngine() }

        var currentScreen by remember { mutableStateOf(ScreenRoute.DASHBOARD) }
        var activeProductId by remember { mutableStateOf<String?>(null) }
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        BackHandler(enabled = currentScreen != ScreenRoute.DASHBOARD) {
            currentScreen = ScreenRoute.DASHBOARD
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = Color(0xFF0F172A),
                    modifier = Modifier
                        .width(300.dp)
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(VendePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF0B1120))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("VendeIA SaaS", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("Infoprodutos & Gemini", color = VendePrimary, style = MaterialTheme.typography.labelSmall)
                            }
                            IconButton(onClick = { scope.launch { drawerState.close() } }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar Menu", tint = Color.White)
                            }
                        }

                        Divider(color = Color(0xFF1E293B), modifier = Modifier.padding(vertical = 12.dp))

                        // Modules
                        ScreenRoute.values().forEach { route ->
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        imageVector = route.icon,
                                        contentDescription = null,
                                        tint = if (currentScreen == route) VendePrimary else Color(0xFF94A3B8)
                                    )
                                },
                                label = {
                                    Text(
                                        text = route.label,
                                        color = if (currentScreen == route) Color.White else Color(0xFFCBD5E1),
                                        fontWeight = if (currentScreen == route) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                selected = currentScreen == route,
                                onClick = {
                                    currentScreen = route
                                    scope.launch { drawerState.close() }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = Color(0xFF1E293B),
                                    unselectedContainerColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        ) {
            Scaffold(
                containerColor = Color(0xFF0B1120),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = currentScreen.label,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu Principal", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF131D31)),
                        actions = {
                            IconButton(onClick = { currentScreen = ScreenRoute.ASSISTANT }) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Assistente", tint = VendeSecondary)
                            }
                        }
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = Color(0xFF131D31),
                        contentColor = Color.White
                    ) {
                        val bottomTabs = listOf(
                            ScreenRoute.DASHBOARD,
                            ScreenRoute.CREATE_PRODUCT,
                            ScreenRoute.PRODUCTS,
                            ScreenRoute.ORDERS,
                            ScreenRoute.ASSISTANT
                        )
                        bottomTabs.forEach { tab ->
                            NavigationBarItem(
                                selected = currentScreen == tab,
                                onClick = { currentScreen = tab },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = when(tab) {
                                            ScreenRoute.DASHBOARD -> "Início"
                                            ScreenRoute.CREATE_PRODUCT -> "Criar"
                                            ScreenRoute.PRODUCTS -> "Produtos"
                                            ScreenRoute.ORDERS -> "Vendas"
                                            ScreenRoute.ASSISTANT -> "Gemini"
                                            else -> tab.label
                                        },
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF0B1120),
                                    selectedTextColor = VendePrimary,
                                    indicatorColor = VendePrimary,
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        ScreenRoute.DASHBOARD -> DashboardScreen(
                            currentUserId = currentUserId,
                            productRepository = productRepository,
                            orderRepository = orderRepository,
                            onNavigateToCreate = { currentScreen = ScreenRoute.CREATE_PRODUCT },
                            onNavigateToProducts = { currentScreen = ScreenRoute.PRODUCTS },
                            onNavigateToPages = { currentScreen = ScreenRoute.LANDING_PAGES },
                            onNavigateToOrders = { currentScreen = ScreenRoute.ORDERS },
                            onNavigateToLeads = { currentScreen = ScreenRoute.CRM_LEADS },
                            onNavigateToAssistant = { currentScreen = ScreenRoute.ASSISTANT }
                        )

                        ScreenRoute.CREATE_PRODUCT -> CreateProductWizardScreen(
                            currentUserId = currentUserId,
                            productRepository = productRepository,
                            geminiEngine = geminiEngine,
                            onFinish = { productId ->
                                activeProductId = productId
                                currentScreen = ScreenRoute.EBOOK_EDITOR
                            },
                            onBack = { currentScreen = ScreenRoute.DASHBOARD }
                        )

                        ScreenRoute.PRODUCTS -> ProductListScreen(
                            currentUserId = currentUserId,
                            productRepository = productRepository,
                            onNavigateToCreate = { currentScreen = ScreenRoute.CREATE_PRODUCT },
                            onSelectProductForEditor = { productId ->
                                activeProductId = productId
                                currentScreen = ScreenRoute.EBOOK_EDITOR
                            },
                            onSelectProductForPage = { productId ->
                                activeProductId = productId
                                currentScreen = ScreenRoute.LANDING_PAGES
                            },
                            onSelectProductForCheckout = { productId ->
                                activeProductId = productId
                                currentScreen = ScreenRoute.CHECKOUT
                            },
                            onSelectProductForMarketing = { productId ->
                                activeProductId = productId
                                currentScreen = ScreenRoute.MARKETING
                            }
                        )

                        ScreenRoute.EBOOK_EDITOR -> EbookEditorScreen(
                            currentUserId = currentUserId,
                            productId = activeProductId ?: "",
                            productRepository = productRepository,
                            geminiEngine = geminiEngine,
                            onBack = { currentScreen = ScreenRoute.PRODUCTS }
                        )

                        ScreenRoute.LANDING_PAGES -> LandingPageBuilderScreen(
                            currentUserId = currentUserId,
                            initialProductId = activeProductId,
                            landingPageRepository = landingPageRepository,
                            productRepository = productRepository,
                            geminiEngine = geminiEngine
                        )

                        ScreenRoute.CHECKOUT -> CheckoutPaymentsScreen(
                            currentUserId = currentUserId,
                            initialProductId = activeProductId,
                            productRepository = productRepository,
                            orderRepository = orderRepository
                        )

                        ScreenRoute.MARKETING -> MarketingAiScreen(
                            currentUserId = currentUserId,
                            initialProductId = activeProductId,
                            productRepository = productRepository,
                            geminiEngine = geminiEngine
                        )

                        ScreenRoute.CREATIVES -> CreativesVideosScreen(
                            currentUserId = currentUserId,
                            productRepository = productRepository,
                            geminiEngine = geminiEngine
                        )

                        ScreenRoute.CRM_LEADS -> CrmLeadsScreen(
                            currentUserId = currentUserId,
                            leadRepository = leadRepository
                        )

                        ScreenRoute.WHATSAPP -> WhatsAppAutomationScreen()

                        ScreenRoute.ORDERS -> OrdersSalesScreen(
                            currentUserId = currentUserId,
                            orderRepository = orderRepository
                        )

                        ScreenRoute.REPORTS -> ReportsScreen(
                            currentUserId = currentUserId,
                            productRepository = productRepository,
                            orderRepository = orderRepository
                        )

                        ScreenRoute.ASSISTANT -> GeminiAssistantScreen(
                            geminiEngine = geminiEngine
                        )

                        ScreenRoute.SETTINGS -> SettingsScreen(
                            currentUserId = currentUserId,
                            onSignOut = {
                                currentUser = null
                            }
                        )

                        ScreenRoute.SAAS_ADMIN -> SaaSAdminScreen()
                    }
                }
            }
        }
    }
}
