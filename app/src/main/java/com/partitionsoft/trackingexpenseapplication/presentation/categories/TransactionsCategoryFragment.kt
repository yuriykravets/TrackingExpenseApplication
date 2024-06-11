package com.partitionsoft.trackingexpenseapplication.presentation.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.partitionsoft.trackingexpenseapplication.R
import com.partitionsoft.trackingexpenseapplication.common.viewBinding
import com.partitionsoft.trackingexpenseapplication.databinding.FragmentTransactionsCategoryBinding
import com.partitionsoft.trackingexpenseapplication.domain.model.Transaction
import com.partitionsoft.trackingexpenseapplication.domain.model.TransactionType
import com.partitionsoft.trackingexpenseapplication.domain.model.TransactionUiModel
import com.partitionsoft.trackingexpenseapplication.presentation.balance.BalanceViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class TransactionsCategoryFragment : Fragment() {

    private val viewModel: BalanceViewModel by viewModel()
    private val binding by viewBinding(FragmentTransactionsCategoryBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categories = resources.getStringArray(R.array.transaction_categories)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter

        binding.btnAddTransaction.setOnClickListener {
            val amount = binding.amountEditText.text.toString().toDoubleOrNull()
            val category = binding.categorySpinner.selectedItem as String

            if (amount != null) {
                val transactionUI = TransactionUiModel(
                    amount = amount,
                    category = category
                )
                viewModel.addTransaction(transactionUI)
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }
    }
}