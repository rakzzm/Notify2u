package com.notify2u.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.notify2u.app.data.local.PaymentReminderDao
import com.notify2u.app.data.repository.FirestoreRepository

class HomeViewModelFactory(
    private val dao: PaymentReminderDao,
    private val firestoreRepository: FirestoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(dao, firestoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
