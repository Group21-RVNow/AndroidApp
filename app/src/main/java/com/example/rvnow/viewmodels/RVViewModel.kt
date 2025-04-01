package com.example.rvnow.viewmodels

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rvnow.api.RVInformation
import com.example.rvnow.model.RV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RVViewModel : ViewModel() {

    private val rvApiService = RVInformation()
    private val _rvs = MutableStateFlow<List<RV>>(emptyList())
    val rvs: StateFlow<List<RV>> = _rvs

    private val _loading = MutableStateFlow(false) // Loading state
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null) // Error state
    val error: StateFlow<String?> = _error

    init {
        fetchRVs()
    }

    fun fetchRVs() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val fetchedRVs = rvApiService.fetchAllRVs()
                println("DEBUG: Fetched RVs - $fetchedRVs")
                _rvs.value = fetchedRVs
            } catch (e: Exception) {
                _error.value = "Failed to fetch RVs: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }



    fun fetchLastRVId() {
        viewModelScope.launch {
            try {
                val lastId = rvApiService.fetchLastRVId()
                println("DEBUG: Last RV ID = $lastId")
            } catch (e: Exception) {
                println("DEBUG: Error fetching last RV ID - ${e.message}")
            }
        }
    }


    fun addRV(rv: RV) {
        viewModelScope.launch {
            _loading.value = true
            try {
                rvApiService.addAllRV(rv)
                fetchRVs()  // Refresh the list after adding
//                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                _error.value = "Failed to add RV: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}
