package com.example.techshop.Activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techshop.Model.OrderModel
import com.example.techshop.R
import com.example.techshop.ViewModel.OrdersViewModel
import com.example.techshop.utils.toVND
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrdersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrdersScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen() {
    val viewModel = OrdersViewModel()
    val orders = remember { mutableStateListOf<OrderModel>() }
    var showLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.loadOrders()
        viewModel.orders.observeForever {
            orders.clear()
            orders.addAll(it)
            showLoading = false
        }
    }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đơn hàng của bạn", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? ComponentActivity)?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.purple)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            if (showLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (orders.isEmpty()) {
                Text(
                    text = "Bạn chưa có đơn hàng nào.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(orders) { order ->
                        OrderItem(order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItem(order: OrderModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDEDFE3))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đơn hàng #${order.orderId.takeLast(8)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A548D)
                )
                Text(
                    text = order.status,
                    fontSize = 14.sp,
                    color = when (order.status) {
                        "Pending" -> Color.Yellow
                        "Confirmed" -> Color.Blue
                        "Shipped" -> Color(0xFFFFA500) // Orange
                        "Delivered" -> Color.Green
                        "Cancelled" -> Color.Red
                        else -> Color.Gray
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Thời gian đặt: ${
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(Date(order.timestamp))
                }",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Người nhận: ${order.name}",
                fontSize = 14.sp,
                color = Color.Black
            )

            Text(
                text = "Địa chỉ: ${order.address}",
                fontSize = 14.sp,
                color = Color.Black
            )

            Text(
                text = "Số điện thoại: ${order.phone}",
                fontSize = 14.sp,
                color = Color.Black
            )

            Text(
                text = "Phương thức thanh toán: ${order.paymentMethod}",
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sản phẩm:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A548D)
            )

            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "- ${item.title} (x${item.numberInCart})",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${item.price * item.numberInCart} VNĐ",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tổng tiền: ${order.total.toVND()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC72216)
            )
        }
    }
}