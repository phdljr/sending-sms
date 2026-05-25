package com.example.android_sms

data class Contact(
    val name: String,
    val phone: String,
    var isSelected: Boolean = false
)