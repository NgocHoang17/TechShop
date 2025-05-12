package com.example.techshop.Admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techshop.Model.ItemsModel
import com.example.techshop.R
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class AddEditProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val product = intent.getParcelableExtra<ItemsModel>("PRODUCT")
        val categoryId = intent.getStringExtra("CATEGORY_ID")
        setContent {
            AddEditProductScreen(
                product = product,
                categoryId = categoryId,
                onBackClick = { finish() },
                onSave = { updatedProduct ->
                    saveProduct(updatedProduct)
                    finish()
                }
            )
        }
    }

    private fun saveProduct(product: ItemsModel) {
        val database = FirebaseDatabase.getInstance().getReference("Items")
        val productId = if (product.id.isEmpty()) database.push().key ?: "" else product.id
        database.child(productId).setValue(product.copy(id = productId))
            .addOnSuccessListener {
                Toast.makeText(this, "Lưu sản phẩm thành công", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lưu sản phẩm thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    product: ItemsModel?,
    categoryId: String?,
    onBackClick: () -> Unit,
    onSave: (ItemsModel) -> Unit
) {
    val formatSymbols = DecimalFormatSymbols().apply { groupingSeparator = '.' }
    val formatter = DecimalFormat("#,###", formatSymbols)

    var title by remember { mutableStateOf(product?.title ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var price by remember { mutableStateOf(product?.price?.let { formatter.format(it) } ?: "") }
    var categoryIdState by remember { mutableStateOf(product?.categoryId ?: (categoryId ?: "")) }
    var rating by remember { mutableStateOf(product?.rating?.toString() ?: "") }
    var showRecommended by remember { mutableStateOf(product?.showRecommended ?: false) }
    var imageUrl by remember { mutableStateOf(if (product?.picUrl?.isNotEmpty() == true) product.picUrl[0] else "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (product == null) "Thêm sản phẩm" else "Sửa sản phẩm",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tiêu đề") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = price,
                onValueChange = { input ->
                    price = input.filter { it.isDigit() || it == '.' }
                },
                label = { Text("Giá (VD: 10.000.000)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = categoryIdState,
                onValueChange = { categoryIdState = it },
                label = { Text("ID danh mục") },
                modifier = Modifier.fillMaxWidth(),
                enabled = product == null && categoryId != null // Chỉ cho phép chỉnh sửa nếu là sản phẩm mới
            )
            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it },
                label = { Text("Đánh giá") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL ảnh") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = showRecommended,
                    onCheckedChange = { showRecommended = it }
                )
                Text("Hiển thị trong đề xuất")
            }
            Button(
                onClick = {
                    val parsedPrice = price.replace(".", "").toDoubleOrNull() ?: 0.0
                    val newProduct = ItemsModel(
                        id = product?.id ?: "",
                        title = title,
                        description = description,
                        price = parsedPrice,
                        categoryId = categoryIdState,
                        rating = rating.toDoubleOrNull() ?: 0.0,
                        showRecommended = showRecommended,
                        picUrl = if (imageUrl.isNotEmpty()) arrayListOf(imageUrl) else arrayListOf(),
                        model = product?.model ?: arrayListOf()
                    )
                    onSave(newProduct)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.purple),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Lưu",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}