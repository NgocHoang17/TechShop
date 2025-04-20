package com.example.techshop.Activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techshop.Model.ItemsModel
import com.example.techshop.R
import com.example.techshop.utils.toVND
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cartItems = intent.getParcelableArrayListExtra<ItemsModel>("cartItems") ?: arrayListOf()
        val total = intent.getDoubleExtra("total", 0.0)
        setContent {
            PaymentScreen(cartItems, total)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(cartItems: ArrayList<ItemsModel>, total: Double) {
    val name = remember { mutableStateOf(TextFieldValue()) }
    val address = remember { mutableStateOf(TextFieldValue()) }
    val phone = remember { mutableStateOf(TextFieldValue()) }
    val selectedMethod = remember { mutableStateOf("Thanh toán khi nhận hàng") }
    val methods = listOf("Thanh toán khi nhận hàng", "Chuyển khoản ngân hàng", "Momo")
    var expanded by remember { mutableStateOf(false) }

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Validation states
    var nameError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    // Context for Firebase
    val context = LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? AppCompatActivity)?.finish() // Quay lại màn hình trước đó
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.purple) // Purple color
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Thông tin đơn hàng
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCE1E3))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Thông tin đơn hàng",
                        fontSize = 20.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Color(0xFF4A548D)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tổng tiền: ${total.toVND()}",
                        fontSize = 18.sp,
                        color = Color(0xFFC72216)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Thông tin người nhận
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCE1E3))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Thông tin người nhận",
                        fontSize = 18.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Color(0xFF4A548D)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Tên người nhận
                    OutlinedTextField(
                        value = name.value,
                        onValueChange = {
                            name.value = it
                            nameError = if (it.text.isBlank()) "Tên không được để trống" else null
                        },
                        label = { Text("Tên người nhận") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = nameError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4A548D),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    nameError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Địa chỉ
                    OutlinedTextField(
                        value = address.value,
                        onValueChange = {
                            address.value = it
                            addressError = if (it.text.isBlank()) "Địa chỉ không được để trống" else null
                        },
                        label = { Text("Địa chỉ") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = addressError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4A548D),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    addressError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Số điện thoại
                    OutlinedTextField(
                        value = phone.value,
                        onValueChange = {
                            phone.value = it
                            phoneError = if (it.text.isBlank()) {
                                "Số điện thoại không được để trống"
                            } else if (!it.text.matches(Regex("^[0-9]{10,11}$"))) {
                                "Số điện thoại không hợp lệ"
                            } else null
                        },
                        label = { Text("Số điện thoại") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = phoneError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4A548D),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    phoneError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phương thức thanh toán
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCE1E3))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Phương thức thanh toán",
                        fontSize = 18.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Color(0xFF4A548D)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box {
                        OutlinedTextField(
                            value = selectedMethod.value,
                            onValueChange = {},
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        tint = Color(0xFF4A548D)
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4A548D),
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            methods.forEach { method ->
                                DropdownMenuItem(
                                    text = { Text(method) },
                                    onClick = {
                                        selectedMethod.value = method
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nút xác nhận thanh toán
            Button(
                onClick = {
                    // Validation
                    nameError = if (name.value.text.isBlank()) "Tên không được để trống" else null
                    addressError = if (address.value.text.isBlank()) "Địa chỉ không được để trống" else null
                    phoneError = if (phone.value.text.isBlank()) {
                        "Số điện thoại không được để trống"
                    } else if (!phone.value.text.matches(Regex("^[0-9]{10,11}$"))) {
                        "Số điện thoại không hợp lệ"
                    } else null

                    if (nameError == null && addressError == null && phoneError == null) {
                        // Lưu vào Firebase
                        val database = FirebaseDatabase.getInstance().reference
                        val orderRef = database.child("orders").push()
                        val order = hashMapOf(
                            "name" to name.value.text,
                            "address" to address.value.text,
                            "phone" to phone.value.text,
                            "paymentMethod" to selectedMethod.value,
                            "total" to total,
                            "items" to cartItems.map {
                                mapOf(
                                    "title" to it.title,
                                    "price" to it.price,
                                    "quantity" to it.numberInCart
                                )
                            },
                            "timestamp" to System.currentTimeMillis() // Thêm timestamp
                        )

                        orderRef.setValue(order)
                            .addOnSuccessListener {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Đặt hàng thành công!")
                                }
                            }
                            .addOnFailureListener { e ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("Đặt hàng thất bại: ${e.message}")
                                }
                            }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.purple)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Xác nhận thanh toán", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}