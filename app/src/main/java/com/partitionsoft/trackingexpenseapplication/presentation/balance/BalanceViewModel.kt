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
        viewModelScope.launch {
            try {
                val transactions = getTransactionsUseCase.execute(currentPage, pageSize)
                _transactions.value = transactions
                calculateBalance(transactions)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            try {
                val newTransactions = getTransactionsUseCase.execute(currentPage + 1, pageSize)
                val currentList = _transactions.value ?: emptyList()
                val updatedList = currentList.toMutableList()
                updatedList.addAll(newTransactions)
                _transactions.value = updatedList
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
            try {
                val transaction = Transaction(
                    id = System.currentTimeMillis(),
                    amount = transactionUiModel.amount,
                    type = TransactionType.EXPENSE,
                    category = transactionUiModel.category,
                    timestamp = System.currentTimeMillis() // Ensure the timestamp is set correctly
                )
                addTransactionUseCase.execute(transaction)

                // Get the current list of transactions
                val currentTransactions = _transactions.value?.toMutableList() ?: mutableListOf()

                // Add the new transaction to the list
                currentTransactions.add(transaction)

                // Update the LiveData with the new list
                _transactions.value = currentTransactions

                // Recalculate balance with the updated list
                calculateBalance(currentTransactions)
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
            val transactions = getTransactionsUseCase.execute(currentPage, pageSize)
            _transactions.value = transactions
            calculateBalance(transactions)
        }
    }
}
