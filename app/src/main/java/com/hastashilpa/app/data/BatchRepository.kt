package com.hastashilpa.app.data
import kotlinx.coroutines.flow.Flow

class BatchRepository(private val dao: BatchDao) {

    val allBatches: Flow<List<BatchEntity>> = dao.getAllBatches()

    suspend fun insert(batch: BatchEntity) {
        dao.insertBatch(batch)
    }

    suspend fun delete(batch: BatchEntity) {
        dao.deleteBatch(batch)
    }
}