package com.partitionsoft.trackingexpenseapplication.presentation.balance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.partitionsoft.trackingexpenseapplication.data.local.PreferenceHelper
import com.partitionsoft.trackingexpenseapplication.domain.model.Transaction
import com.partitionsoft.trackingexpenseapplication.domain.model.TransactionType
import com.partitionsoft.trackingexpenseapplication.domain.model.TransactionUiModel
import com.partitionsoft.trackingexpenseapplication.domain.usecase.AddTransactionUseCase
import com.partitionsoft.trackingexpenseapplication.domain.usecase.GetBitcoinExchangeRateUseCase
import com.partitionsoft.trackingexpenseapplication.domain.usecase.GetTransactionsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class BalanceViewModel(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getBitcoinExchangeRateUseCase: GetBitcoinExchangeRateUseCase
) : ViewModel() {

    val transactions: Flow<PagingData<Transaction>> = getTransactionsUseCase.execute()
        .cachedIn(viewModelScope)

    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double> get() = _balance

    private val _exchangeRate = MutableLiveData<Double>()
    val exchangeRate: LiveData<Double> get() = _exchangeRate

    private var currentBalance: Double = 0.0

    init {
        loadExchangeRate()
        observeTransactions()
    }

    fun observeTransactions() {
        viewModelScope.launch {
            transactions.collectLatest { pagingData ->
                var totalBalance = 0.0
                pagingData.map { transaction ->
                    if (transaction.type == TransactionType.TOP_UP) {
                        transaction.amount
                    } else {
                        -transaction.amount
                    }
                }.map { amountToAdd ->
                    totalBalance += amountToAdd
                    _balance.postValue(totalBalance)
                }
            }
        }
    }



    fun loadExchangeRate() {
        viewModelScope.launch {
            val rate = getBitcoinExchangeRateUseCase.execute()
            _exchangeRate.value = rate
        }
    }

    fun addTransaction(transactionUiModel: TransactionUiModel) {
        viewModelScope.launch {
            val transaction = Transaction(
                id = System.currentTimeMillis(),
                amount = transactionUiModel.amount,
                type = TransactionType.EXPENSE,
                category = transactionUiModel.category,
                timestamp = System.currentTimeMillis()
            )
            addTransactionUseCase.execute(transaction)
        }
    }


    fun addTopUpTransaction(amount: Double) {
        viewModelScope.launch {
            val transaction = Transaction(
                id = System.currentTimeMillis(),
                amount = amount,
                type = TransactionType.TOP_UP,
                category = "Top Up",
                timestamp = System.currentTimeMillis()
            )
            addTransactionUseCase.execute(transaction)
        }
    }
}

