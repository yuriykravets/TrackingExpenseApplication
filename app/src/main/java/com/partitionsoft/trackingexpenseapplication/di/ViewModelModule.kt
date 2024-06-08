package com.partitionsoft.trackingexpenseapplication.di

import com.partitionsoft.trackingexpenseapplication.presentation.balance.BalanceViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { BalanceViewModel(get(), get(), get()) }
}