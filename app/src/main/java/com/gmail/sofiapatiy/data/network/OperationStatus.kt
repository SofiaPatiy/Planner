package com.gmail.sofiapatiy.data.network

sealed class OperationStatus(id: String) {
    data object Success : OperationStatus("success")
    data class Failure(val t: Throwable) : OperationStatus("failure")

}