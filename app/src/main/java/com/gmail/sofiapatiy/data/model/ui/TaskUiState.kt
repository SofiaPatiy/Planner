package com.gmail.sofiapatiy.data.model.ui

sealed class TaskUiState {
    data object View : TaskUiState()
    data object Edit : TaskUiState()
}