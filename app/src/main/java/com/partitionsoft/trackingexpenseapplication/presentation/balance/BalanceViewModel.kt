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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private var isLastPage = false

    private val allTransactions = mutableListOf<Transaction>()

    init {
        loadInitialTransactions()
        loadExchangeRate()
    }

    private fun calculateBalance() {
        var totalBalance = 0.0
        for (transaction in allTransactions) {
            totalBalance += if (transaction.type == TransactionType.TOP_UP) {
                transaction.amount
            } else {
                -transaction.amount
            }
        }
        _balance.value = totalBalance
    }

    private fun updateTransactions(transactions: List<Transaction>) {
        allTransactions.addAll(transactions)
        _transactions.value = allTransactions.toList()
        calculateBalance()
    }

    fun loadInitialTransactions() {
        viewModelScope.launch {
            try {
                val transactions = getTransactionsUseCase.execute(1, pageSize)
                currentPage = 1
                isLastPage = transactions.size < pageSize
                allTransactions.clear()
                allTransactions.addAll(transactions)
                _transactions.value = allTransactions.toList()
                calculateBalance()

                if (!isLastPage) {
                    prefetchNextPage()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun prefetchNextPage() {
        viewModelScope.launch {
            try {
                val nextPageTransactions = withContext(Dispatchers.IO) {
                    getTransactionsUseCase.execute(currentPage + 1, pageSize)
                }
                currentPage++
                isLastPage = nextPageTransactions.size < pageSize
                updateTransactions(nextPageTransactions)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadNextPage() {
        if (isLastPage) return

        viewModelScope.launch {
            try {
                val newTransactions = withContext(Dispatchers.IO) {
                    getTransactionsUseCase.execute(currentPage + 1, pageSize)
                }
                currentPage++
                isLastPage = newTransactions.size < pageSize
                updateTransactions(newTransactions)
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

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    id = System.currentTimeMillis(),
                    amount = transaction.amount,
                    type = transaction.type,
                    category = transaction.category,
                    timestamp = System.currentTimeMillis()
                )
                addTransactionUseCase.execute(transaction)
                allTransactions.add(0, transaction)
                _transactions.value = allTransactions.toList()
                calculateBalance()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    fun addTopUpTransaction(amount: Double) {
        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    id = System.currentTimeMillis(),
                    amount = amount,
                    type = TransactionType.TOP_UP,
                    category = "Top Up",
                    timestamp = System.currentTimeMillis()
                )
                addTransactionUseCase.execute(transaction)
                allTransactions.add(0, transaction)
                _transactions.value = allTransactions.toList()
                calculateBalance()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
