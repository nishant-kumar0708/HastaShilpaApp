package com.hastashilpa.app.viewmodel
import android.app.Application
import androidx.lifecycle.*
import com.hastashilpa.app.data.BatchDatabase
import com.hastashilpa.app.data.BatchEntity
import com.hastashilpa.app.data.BatchRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BatchRepository

    val batches: StateFlow<List<BatchEntity>>

    init {
        val dao = BatchDatabase.getDatabase(application).batchDao()
        repository = BatchRepository(dao)
        batches = repository.allBatches.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
    }

    fun addBatch(product: String, poles: Int, strips: Int, date: String) {
        viewModelScope.launch {
            repository.insert(BatchEntity(
                product = product,
                poles   = poles,
                strips  = strips,
                date    = date
            ))
        }
    }

    fun deleteBatch(batch: BatchEntity) {
        viewModelScope.launch { repository.delete(batch) }
    }
}