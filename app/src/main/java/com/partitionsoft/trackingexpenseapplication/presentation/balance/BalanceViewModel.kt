package com.partitionsoft.trackingexpenseapplication.presentation.balance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.partitionsoft.trackingexpenseapplication.domain.model.Transaction
import com.partitionsoft.trackingexpenseapplication.domain.model.TransactionType
import com.partitionsoft.trackingexpenseapplication.domain.usecase.AddTransactionUseCase
import com.partitionsoft.trackingexpenseapplication.domain.usecase.GetBitcoinExchangeRateUseCase
import com.partitionsoft.trackingexpenseapplication.domain.usecase.GetTransactionsUseCase
import kotlinx.coroutines.launch

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

    private var currentBalance: Double = 0.0

    init {
        loadBalance()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            val transactions = getTransactionsUseCase.execute()
            _transactions.value = transactions
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
            addTransactionUseCase.execute(transaction)
            if (transaction.type == TransactionType.TOP_UP) {
                currentBalance += transaction.amount
            } else {
                currentBalance -= transaction.amount
            }
            _balance.value = currentBalance
        }
    }

    private fun loadBalance() {
        viewModelScope.launch {
            val transactions = getTransactionsUseCase.execute()
            currentBalance = transactions.sumOf {
                if (it.type == TransactionType.TOP_UP) it.amount else -it.amount
            }
            _balance.value = currentBalance
        }
    }
}