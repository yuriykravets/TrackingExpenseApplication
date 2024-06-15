package com.partitionsoft.trackingexpenseapplication.presentation.balance

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.partitionsoft.trackingexpenseapplication.R
import com.partitionsoft.trackingexpenseapplication.common.viewBinding
import com.partitionsoft.trackingexpenseapplication.data.local.PreferenceHelper
import com.partitionsoft.trackingexpenseapplication.databinding.FragmentBalanceBinding
import com.partitionsoft.trackingexpenseapplication.domain.model.Transaction
import com.partitionsoft.trackingexpenseapplication.domain.model.TransactionType
import com.partitionsoft.trackingexpenseapplication.presentation.adapter.TransactionAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class BalanceFragment : Fragment() {

    private val binding by viewBinding(FragmentBalanceBinding::inflate)
    private val viewModel: BalanceViewModel by viewModel()
    private lateinit var transactionAdapter: TransactionAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeTransactions()
        setupListeners()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }


    private fun setupListeners() {
        binding.btnTopUp.setOnClickListener {
            showTopUpDialog()
        }
        binding.btnAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_balanceFragment_to_transactionsCategoryFragment)
        }

    }

    private fun observeTransactions() {
        viewModel.balance.observe(viewLifecycleOwner) { balance ->
            Log.d("BalanceFragment", "Balance: $balance")
            binding.bitcoinBalance.text = getString(R.string.balance_text, balance)
        }

        viewModel.exchangeRate.observe(viewLifecycleOwner) { rate ->
            Log.d("BalanceFragment", "Rate: $rate")
            binding.bitcoinExchangeRate.text = getString(R.string.exchange_rate_text, rate)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.transactions.collectLatest { pagingData ->
                transactionAdapter.submitData(pagingData)
            }
        }
        viewModel.observeTransactions()
        viewModel.loadExchangeRate()
    }

    private fun showTopUpDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.top_up_dialog, null)
        val amountEditText: EditText = dialogView.findViewById(R.id.amountEditText)
        val btnTopUp: Button = dialogView.findViewById(R.id.btnTopUpDialog)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnTopUp.setOnClickListener {
            val amount = amountEditText.text.toString().toDoubleOrNull()
            if (amount != null) {
                viewModel.addTopUpTransaction(amount)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), R.string.enter_valid_amount, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        dialog.show()
    }
}