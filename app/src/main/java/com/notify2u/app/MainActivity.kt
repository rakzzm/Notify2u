package com.notify2u.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.notify2u.app.data.local.Notify2uDatabase
import com.notify2u.app.navigation.AppNavigation
import com.notify2u.app.ui.theme.Notify2uTheme
import com.notify2u.app.ui.viewmodel.HomeViewModel
import com.notify2u.app.ui.viewmodel.HomeViewModelFactory
import com.notify2u.app.ui.viewmodel.AuthViewModel
import com.notify2u.app.ui.viewmodel.AuthViewModelFactory
import com.notify2u.app.data.repository.FirestoreRepository
import com.notify2u.app.utils.createNotificationChannel
import com.notify2u.app.utils.showPersistentQuickAddNotification

class MainActivity : ComponentActivity() {

    // 🔔 Modern permission launcher for notifications
    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            println("✅ Notification permission granted")
        } else {
            println("❌ Notification permission denied")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Ask for POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!granted) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // ✅ Create notification channel early
        createNotificationChannel(this)
        showPersistentQuickAddNotification(this)

        // ⚙️ Initialize Room + ViewModel
        val database = Notify2uDatabase.getInstance(applicationContext)
        val firestoreRepository = FirestoreRepository()
        val dao = database.paymentReminderDao()
        val factory = HomeViewModelFactory(dao, firestoreRepository)
        val viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        val authDao = database.userDao()
        val authFactory = AuthViewModelFactory(authDao)
        val authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]

        // 🔄 Start Real-time Cloud Sync when logged in
        val todoDao = database.todoDao()
        lifecycleScope.launch {
            authViewModel.isLoggedIn.collect { loggedIn ->
                if (loggedIn) {
                    firestoreRepository.startRealtimeSync(dao, todoDao, lifecycleScope)
                }
            }
        }

        var initialTaskTitle = ""
        var openAddSheet = false
        
        if (intent?.action == android.content.Intent.ACTION_SEND && "text/plain" == intent.type) {
            intent.getStringExtra(android.content.Intent.EXTRA_TEXT)?.let {
                initialTaskTitle = it
                openAddSheet = true
            }
        }
        
        if (intent?.getBooleanExtra("ACTION_QUICK_ADD", false) == true) {
            openAddSheet = true
        }

        // 🎨 Compose UI
        setContent {
            Notify2uTheme {
                val navController = rememberNavController()
                AppNavigation(
                    viewModel = viewModel, 
                    authViewModel = authViewModel,
                    firestoreRepository = firestoreRepository,
                    navController = navController,
                    initialTaskTitle = initialTaskTitle,
                    openAddSheet = openAddSheet
                )
            }
        }
    }
}