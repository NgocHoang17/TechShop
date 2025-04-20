package com.example.techshop.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.techshop.Model.OrderItemModel
import com.example.techshop.Model.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrdersViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference
    private val _orders = MutableLiveData<List<OrderModel>>()
    val orders: LiveData<List<OrderModel>> get() = _orders

    fun loadOrders() {
        val ordersRef = database.child("orders")
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = mutableListOf<OrderModel>()
                for (orderSnapshot in snapshot.children) {
                    val orderId = orderSnapshot.key ?: ""
                    val name = orderSnapshot.child("name").getValue(String::class.java) ?: ""
                    val address = orderSnapshot.child("address").getValue(String::class.java) ?: ""
                    val phone = orderSnapshot.child("phone").getValue(String::class.java) ?: ""
                    val paymentMethod = orderSnapshot.child("paymentMethod").getValue(String::class.java) ?: ""
                    val total = orderSnapshot.child("total").getValue(Double::class.java) ?: 0.0
                    val timestamp = orderSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                    val status = orderSnapshot.child("status").getValue(String::class.java) ?: "Pending"

                    val itemsSnapshot = orderSnapshot.child("items")
                    val items = mutableListOf<OrderItemModel>()
                    for (itemSnapshot in itemsSnapshot.children) {
                        val item = itemSnapshot.getValue(OrderItemModel::class.java)
                        item?.let { items.add(it) }
                    }

                    val order = OrderModel(
                        orderId = orderId,
                        name = name,
                        address = address,
                        phone = phone,
                        paymentMethod = paymentMethod,
                        total = total,
                        items = items,
                        timestamp = timestamp,
                        status = status
                    )
                    orderList.add(order)
                }
                _orders.value = orderList.sortedByDescending { it.timestamp }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu cần
            }
        })
    }
}