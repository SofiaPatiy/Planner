package com.gmail.sofiapatiy.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SettingsViewModel : ViewModel() {

    val isAppUseDarkTheme = MutableStateFlow<Boolean?>(null)

    init {
        isAppUseDarkTheme.filterNotNull().onEach { isUse ->
            when (isUse) {
                true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }.launchIn(viewModelScope)
    }

}