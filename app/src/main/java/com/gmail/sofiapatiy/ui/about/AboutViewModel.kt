package com.gmail.sofiapatiy.ui.about

import androidx.lifecycle.ViewModel
import com.gmail.sofiapatiy.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AboutViewModel : ViewModel() {

    private val initialAppDesc = """
        This app is developed as part of a coursework project.
        A simple Android app designed to help you effectively plan and track your time. 
        It provides a straightforward way to easily add, organize, and manage all your tasks, appointments, and to-do lists conveniently in one place on your mobile device. """.trimIndent()

    val appName = MutableStateFlow("Planner").asStateFlow()
    val appDesc = MutableStateFlow(initialAppDesc).asStateFlow()
    val appAuthor = MutableStateFlow("Sofiia Patii").asStateFlow()
    val authorEmail = MutableStateFlow("sofia.patiy@gmail.com").asStateFlow()
    val copyright = MutableStateFlow("©2025")

    val appVersion = MutableStateFlow(BuildConfig.VERSION_NAME)

}