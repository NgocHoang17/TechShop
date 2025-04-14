package com.example.techshop.Model

data class OrderModel(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val paymentMethod: String = "",
    val total: Double = 0.0,
    val items: List<OrderItemModel> = emptyList()
)

