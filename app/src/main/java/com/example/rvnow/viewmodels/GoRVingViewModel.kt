package com.example.rvnow.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rvnow.model.RVDestination
import com.example.rvnow.model.RVTravelGuide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GoRVingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "GoRVingViewModel"

    // 状态流
    private val _destinations = MutableStateFlow<List<RVDestination>>(emptyList())
    val destinations: StateFlow<List<RVDestination>> = _destinations

    private val _featuredDestinations = MutableStateFlow<List<RVDestination>>(emptyList())
    val featuredDestinations: StateFlow<List<RVDestination>> = _featuredDestinations

    private val _travelGuides = MutableStateFlow<List<RVTravelGuide>>(emptyList())
    val travelGuides: StateFlow<List<RVTravelGuide>> = _travelGuides

    private val _selectedDestination = MutableStateFlow<RVDestination?>(null)
    val selectedDestination: StateFlow<RVDestination?> = _selectedDestination

    private val _selectedTravelGuide = MutableStateFlow<RVTravelGuide?>(null)
    val selectedTravelGuide: StateFlow<RVTravelGuide?> = _selectedTravelGuide

    private val _searchResults = MutableStateFlow<List<Any>>(emptyList())
    val searchResults: StateFlow<List<Any>> = _searchResults

    private val _countryDestinations = MutableStateFlow<List<RVDestination>>(emptyList())
    val countryDestinations: StateFlow<List<RVDestination>> = _countryDestinations

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // 加载所有目的地
    fun loadDestinations() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val snapshot = db.collection("rv_destinations").get().await()
                val destinations = snapshot.documents.mapNotNull { doc ->
                    val destination = doc.toObject(RVDestination::class.java)
                    destination?.copy(id = doc.id)
                }
                _destinations.value = destinations
                Log.d(TAG, "Loaded ${destinations.size} destinations")
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load destinations: ${e.message}"
                Log.e(TAG, "Failed to load destinations", e)
                _destinations.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 加载特色目的地
    fun loadFeaturedDestinations() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val snapshot = db.collection("rv_destinations")
                    .whereEqualTo("isFeatured", true)
                    .get()
                    .await()

                val featuredDestinations = snapshot.documents.mapNotNull { doc ->
                    val destination = doc.toObject(RVDestination::class.java)
                    destination?.copy(id = doc.id)
                }
                _featuredDestinations.value = featuredDestinations
                Log.d(TAG, "Loaded ${featuredDestinations.size} featured destinations")
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load featured destinations: ${e.message}"
                Log.e(TAG, "Failed to load featured destinations", e)
                _featuredDestinations.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 加载旅游攻略
    fun loadTravelGuides() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val snapshot = db.collection("rv_travel_guides").get().await()
                val guides = snapshot.documents.mapNotNull { doc ->
                    val guide = doc.toObject(RVTravelGuide::class.java)
                    guide?.copy(id = doc.id)
                }
                _travelGuides.value = guides
                Log.d(TAG, "Loaded ${guides.size} travel guides")
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load travel guides: ${e.message}"
                Log.e(TAG, "Failed to load travel guides", e)
                _travelGuides.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 获取目的地详情
    fun getDestinationById(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val doc = db.collection("rv_destinations").document(id).get().await()
                val destination = doc.toObject(RVDestination::class.java)
                _selectedDestination.value = destination?.copy(id = doc.id)
                Log.d(TAG, "Loaded destination details for ID: $id")
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load destination details: ${e.message}"
                Log.e(TAG, "Failed to load destination details for ID: $id", e)
                _selectedDestination.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 获取旅游攻略详情
    fun getTravelGuideById(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val doc = db.collection("rv_travel_guides").document(id).get().await()
                val guide = doc.toObject(RVTravelGuide::class.java)
                _selectedTravelGuide.value = guide?.copy(id = doc.id)
                Log.d(TAG, "Loaded travel guide details for ID: $id")
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load travel guide details: ${e.message}"
                Log.e(TAG, "Failed to load travel guide details for ID: $id", e)
                _selectedTravelGuide.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 按国家获取目的地
    fun getDestinationsByCountry(country: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val snapshot = db.collection("rv_destinations")
                    .whereEqualTo("country", country)
                    .get()
                    .await()

                val destinations = snapshot.documents.mapNotNull { doc ->
                    val destination = doc.toObject(RVDestination::class.java)
                    destination?.copy(id = doc.id)
                }
                _countryDestinations.value = destinations
                Log.d(TAG, "Loaded ${destinations.size} destinations for country: $country")
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load destinations for $country: ${e.message}"
                Log.e(TAG, "Failed to load destinations for country: $country", e)
                _countryDestinations.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 改进的搜索功能
    fun search(query: String): Flow<List<Any>> = flow {
        try {
            _isLoading.value = true
            _error.value = null
            _searchResults.value = emptyList()

            val queryLowerCase = query.lowercase().trim()
            Log.d(TAG, "Starting search for: '$queryLowerCase'")

            // 1. 尝试直接Firestore查询
            val firestoreResults = mutableListOf<RVDestination>()

            // 搜索名称
            val nameSnapshot = db.collection("rv_destinations")
                .orderBy("name")
                .startAt(queryLowerCase)
                .endAt(queryLowerCase + "\uf8ff")
                .get()
                .await()
            nameSnapshot.documents.mapNotNullTo(firestoreResults) { doc ->
                doc.toObject(RVDestination::class.java)?.copy(id = doc.id)
            }

            // 搜索国家
            val countrySnapshot = db.collection("rv_destinations")
                .orderBy("country")
                .startAt(queryLowerCase)
                .endAt(queryLowerCase + "\uf8ff")
                .get()
                .await()
            countrySnapshot.documents.mapNotNullTo(firestoreResults) { doc ->
                val dest = doc.toObject(RVDestination::class.java)?.copy(id = doc.id)
                if (dest != null && !firestoreResults.any { it.id == doc.id }) dest else null
            }

            if (firestoreResults.isNotEmpty()) {
                Log.d(TAG, "Firestore search found ${firestoreResults.size} results")
                _searchResults.value = firestoreResults
                emit(firestoreResults)
                return@flow
            }

            // 2. 回退到客户端过滤
            Log.d(TAG, "Falling back to client-side filtering")

            // 获取所有数据
            val allDestinations = db.collection("rv_destinations").get().await()
                .documents.mapNotNull { doc ->
                    doc.toObject(RVDestination::class.java)?.copy(id = doc.id)
                }

            val allGuides = db.collection("rv_travel_guides").get().await()
                .documents.mapNotNull { doc ->
                    doc.toObject(RVTravelGuide::class.java)?.copy(id = doc.id)
                }

            // 过滤结果
            val filteredResults = mutableListOf<Any>()

            // 过滤目的地
            filteredResults.addAll(allDestinations.filter { dest ->
                dest.name.lowercase().contains(queryLowerCase) ||
                        dest.country.lowercase().contains(queryLowerCase) ||
                        dest.location.lowercase().contains(queryLowerCase) ||
                        dest.description.lowercase().contains(queryLowerCase)
            })

            // 过滤旅游指南
            filteredResults.addAll(allGuides.filter { guide ->
                guide.title.lowercase().contains(queryLowerCase) ||
                        guide.summary.lowercase().contains(queryLowerCase) ||
                        guide.content.lowercase().contains(queryLowerCase) ||
                        guide.tags.any { it.lowercase().contains(queryLowerCase) }
            })

            Log.d(TAG, "Client-side filtering found ${filteredResults.size} results")
            _searchResults.value = filteredResults
            emit(filteredResults)
        } catch (e: Exception) {
            _error.value = "Search failed: ${e.message}"
            Log.e(TAG, "Search error", e)
            emit(emptyList())
        } finally {
            _isLoading.value = false
        }
    }

    // 执行搜索并直接更新状态
    suspend fun performSearch(query: String) {
        try {
            _isLoading.value = true
            _error.value = null
            _searchResults.value = emptyList()

            val queryLowerCase = query.lowercase().trim()
            Log.d(TAG, "Starting search for: '$queryLowerCase'")

            // 1. 尝试直接Firestore查询
            val firestoreResults = mutableListOf<RVDestination>()

            // 搜索名称
            val nameSnapshot = db.collection("rv_destinations")
                .orderBy("name")
                .startAt(queryLowerCase)
                .endAt(queryLowerCase + "\uf8ff")
                .get()
                .await()
            nameSnapshot.documents.mapNotNullTo(firestoreResults) { doc ->
                doc.toObject(RVDestination::class.java)?.copy(id = doc.id)
            }

            // 搜索国家
            val countrySnapshot = db.collection("rv_destinations")
                .orderBy("country")
                .startAt(queryLowerCase)
                .endAt(queryLowerCase + "\uf8ff")
                .get()
                .await()
            countrySnapshot.documents.mapNotNullTo(firestoreResults) { doc ->
                val dest = doc.toObject(RVDestination::class.java)?.copy(id = doc.id)
                if (dest != null && !firestoreResults.any { it.id == doc.id }) dest else null
            }

            if (firestoreResults.isNotEmpty()) {
                Log.d(TAG, "Firestore search found ${firestoreResults.size} results")
                _searchResults.value = firestoreResults
                return
            }

            // 2. 回退到客户端过滤
            Log.d(TAG, "Falling back to client-side filtering")

            // 获取所有数据
            val allDestinations = db.collection("rv_destinations").get().await()
                .documents.mapNotNull { doc ->
                    doc.toObject(RVDestination::class.java)?.copy(id = doc.id)
                }

            val allGuides = db.collection("rv_travel_guides").get().await()
                .documents.mapNotNull { doc ->
                    doc.toObject(RVTravelGuide::class.java)?.copy(id = doc.id)
                }

            // 过滤结果
            val filteredResults = mutableListOf<Any>()

            // 过滤目的地
            filteredResults.addAll(allDestinations.filter { dest ->
                dest.name.lowercase().contains(queryLowerCase) ||
                        dest.country.lowercase().contains(queryLowerCase) ||
                        dest.location.lowercase().contains(queryLowerCase) ||
                        dest.description.lowercase().contains(queryLowerCase)
            })

            // 过滤旅游指南
            filteredResults.addAll(allGuides.filter { guide ->
                guide.title.lowercase().contains(queryLowerCase) ||
                        guide.summary.lowercase().contains(queryLowerCase) ||
                        guide.content.lowercase().contains(queryLowerCase) ||
                        guide.tags.any { it.lowercase().contains(queryLowerCase) }
            })

            Log.d(TAG, "Client-side filtering found ${filteredResults.size} results")
            _searchResults.value = filteredResults
        } catch (e: Exception) {
            _error.value = "Search failed: ${e.message}"
            Log.e(TAG, "Search error", e)
            _searchResults.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }


    // 清除搜索结果
    fun clearSearch() {
        _searchResults.value = emptyList()
        _error.value = null
        Log.d(TAG, "Search results cleared")
    }
}
