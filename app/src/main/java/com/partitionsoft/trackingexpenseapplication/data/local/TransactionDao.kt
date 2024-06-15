package com.partitionsoft.trackingexpenseapplication.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.partitionsoft.trackingexpenseapplication.domain.model.Transaction

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): PagingSource<Int, Transaction>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getTransactions(): List<Transaction>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :pageSize OFFSET :offset")
    suspend fun getAllTransactionWithPagination(offset: Int, pageSize: Int): List<Transaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)
}