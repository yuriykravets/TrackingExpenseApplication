package com.partitionsoft.trackingexpenseapplication.presentation.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partitionsoft.trackingexpenseapplication.data.local.PreferenceHelper
import com.partitionsoft.trackingexpenseapplication.domain.model.Transaction
import com.partitionsoft.trackingexpenseapplication.domain.model.TransactionType
import com.partitionsoft.trackingexpenseapplication.domain.model.TransactionUiModel
import com.partitionsoft.trackingexpenseapplication.domain.usecase.AddTransactionUseCase
import com.partitionsoft.trackingexpenseapplication.domain.usecase.GetBitcoinExchangeRateUseCase
import com.partitionsoft.trackingexpenseapplication.domain.usecase.GetTransactionsUseCase
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class BalanceViewModel(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getBitcoinExchangeRateUseCase: GetBitcoinExchangeRateUseCase
) : ViewModel() {

    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double> get() = _balance

    private val _exchangeRate = MutableLiveData<Double>()
    val exchangeRate: LiveData<Double> get() = _exchangeRate

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    private var currentPage = 1
    private var pageSize = 20

    private var currentBalance: Double = 0.0
    private var hasMoreItems = true

    init {
        loadTransactions()
        loadExchangeRate()
    }

    private fun calculateBalance(transactions: List<Transaction>) {
        currentBalance = 0.0
        for (transaction in transactions) {
            currentBalance += if (transaction.type == TransactionType.TOP_UP) {
                transaction.amount
            } else {
                -transaction.amount
            }
        }
        _balance.value = currentBalance
    }

    fun loadTransactions() {
        if (!hasMoreItems) return

        viewModelScope.launch {
            try {
                val transactions = getTransactionsUseCase.execute(currentPage, pageSize)
                if (transactions.size < pageSize) {
                    hasMoreItems = false
                }
                val currentList = _transactions.value.orEmpty().toMutableList()
                currentList.addAll(transactions)
                _transactions.value = currentList
                calculateBalance(currentList)
                currentPage++
            } catch (e: Exception) {
                e.printStackTrace()
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
            refreshTransactions()
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
            refreshTransactions()
        }
    }

    private fun refreshTransactions() {
        currentPage = 1
        hasMoreItems = true
        _transactions.value = emptyList()
        loadTransactions()
    }
}
