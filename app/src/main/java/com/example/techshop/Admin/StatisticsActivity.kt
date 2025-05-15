package com.example.techshop.Admin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.techshop.Model.OrderModel
import com.example.techshop.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.DecimalFormat

class StatisticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StatisticsScreen(onBackClick = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(onBackClick: () -> Unit) {
    val database = FirebaseDatabase.getInstance()
    var totalRevenue by remember { mutableStateOf(0.0) }
    var totalOrders by remember { mutableStateOf(0) }
    var totalUsers by remember { mutableStateOf(0) }
    var totalCategories by remember { mutableStateOf(0) }
    var totalItems by remember { mutableStateOf(0) }
    var ordersByStatus by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
    var itemsByCategoryData by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val decimalFormat = DecimalFormat("#,###")

    DisposableEffect(Unit) {
        val ordersRef = database.getReference("orders")
        val ordersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var revenue = 0.0
                val statusCount = mutableMapOf<String, Int>()

                Log.d("Firebase", "Orders snapshot: ${snapshot.childrenCount} children")
                for (childSnapshot in snapshot.children) {
                    val order = childSnapshot.getValue(OrderModel::class.java)
                    Log.d("Firebase", "Order: $order, Status: ${order?.status}")
                    if (order != null) {
                        statusCount[order.status] = (statusCount[order.status] ?: 0) + 1
                        if (order.status == "Delivered") {
                            revenue += order.total
                        }
                    }
                }

                totalRevenue = revenue
                totalOrders = snapshot.childrenCount.toInt() // Hiển thị tổng số đơn hàng
                ordersByStatus = statusCount.map { it.key to it.value }.sortedBy { it.first }

                if (totalOrders == 0) {
                    Toast.makeText(context, "Không có đơn hàng nào trong hệ thống", Toast.LENGTH_LONG).show()
                }
                Log.d("Firebase", "Total Revenue: $totalRevenue, Total Orders: $totalOrders, OrdersByStatus: $ordersByStatus")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Lỗi tải đơn hàng: ${error.message}", Toast.LENGTH_LONG).show()
                isLoading = false
            }
        }

        val usersRef = database.getReference("users")
        val usersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalUsers = snapshot.childrenCount.toInt()
                Log.d("Firebase", "Users snapshot: ${snapshot.childrenCount} children")
                if (totalUsers == 0) {
                    Toast.makeText(context, "Không có người dùng nào trong hệ thống", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Lỗi tải người dùng: ${error.message}", Toast.LENGTH_LONG).show()
                isLoading = false
            }
        }

        val categoriesRef = database.getReference("Category")
        val categoriesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalCategories = snapshot.childrenCount.toInt()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Lỗi tải danh mục: ${error.message}", Toast.LENGTH_LONG).show()
                isLoading = false
            }
        }

        val itemsRef = database.getReference("Items")
        val itemsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalItems = snapshot.childrenCount.toInt()
                val itemsByCategory = mutableMapOf<String, Int>()
                for (childSnapshot in snapshot.children) {
                    val categoryId = childSnapshot.child("categoryId").getValue(String::class.java)
                    if (categoryId != null) {
                        itemsByCategory[categoryId] = (itemsByCategory[categoryId] ?: 0) + 1
                    }
                }
                itemsByCategoryData = itemsByCategory.map { it.key to it.value }.sortedBy { it.first.toIntOrNull() ?: 0 }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Lỗi tải sản phẩm: ${error.message}", Toast.LENGTH_LONG).show()
                isLoading = false
            }
        }

        ordersRef.addValueEventListener(ordersListener)
        usersRef.addValueEventListener(usersListener)
        categoriesRef.addValueEventListener(categoriesListener)
        itemsRef.addValueEventListener(itemsListener)

        onDispose {
            ordersRef.removeEventListener(ordersListener)
            usersRef.removeEventListener(usersListener)
            categoriesRef.removeEventListener(categoriesListener)
            itemsRef.removeEventListener(itemsListener)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Thống kê",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.purple),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.lightGrey)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Tổng quan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        StatisticItem("Tổng doanh thu", "${decimalFormat.format(totalRevenue)} VNĐ")
                        StatisticItem("Tổng số đơn hàng", totalOrders.toString())
                        StatisticItem("Số người dùng", totalUsers.toString())
                        StatisticItem("Số danh mục", totalCategories.toString())
                        StatisticItem("Số sản phẩm", totalItems.toString())
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.lightGrey)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Số lượng đơn hàng theo trạng thái",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        ordersByStatus.forEach { (status, count) ->
                            StatisticItem("Trạng thái: $status", count.toString())
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.lightGrey)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Số lượng sản phẩm theo danh mục",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        AndroidView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            factory = { context ->
                                BarChart(context).apply {
                                    description.isEnabled = false
                                    setTouchEnabled(true)
                                    isDragEnabled = true
                                    setScaleEnabled(true)
                                    setPinchZoom(true)
                                    animateY(1000)

                                    xAxis.apply {
                                        position = XAxis.XAxisPosition.BOTTOM
                                        valueFormatter = IndexAxisValueFormatter(itemsByCategoryData.map { it.first })
                                        granularity = 1f
                                        setDrawGridLines(true)
                                        textSize = 12f
                                        labelRotationAngle = 45f
                                    }

                                    axisLeft.apply {
                                        axisMinimum = 0f
                                        setDrawGridLines(true)
                                    }
                                    axisRight.isEnabled = false
                                    legend.isEnabled = true
                                    legend.textSize = 12f
                                }
                            },
                            update = { chart ->
                                val entries = itemsByCategoryData.mapIndexed { index, data ->
                                    BarEntry(index.toFloat(), data.second.toFloat())
                                }
                                val dataSet = BarDataSet(entries, "Số sản phẩm").apply {
                                    colors = listOf(
                                        android.graphics.Color.parseColor("#03DAC5"),
                                        android.graphics.Color.parseColor("#FF5722"),
                                        android.graphics.Color.parseColor("#2196F3")
                                    )
                                    setDrawValues(true)
                                    valueTextSize = 12f
                                    valueTextColor = android.graphics.Color.BLACK
                                }
                                chart.data = BarData(dataSet)
                                chart.invalidate()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}