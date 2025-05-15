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
import androidx.compose.material.icons.filled.Delete
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
                },
                onDelete = { productToDelete ->
                    deleteProduct(productToDelete)
                    finish()
                }
            )
        }
    }

    private fun saveProduct(product: ItemsModel) {
        val database = FirebaseDatabase.getInstance().getReference("Items")
        val newItemId = if (product.id.isEmpty()) {
            var maxId = 0
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val id = childSnapshot.key?.toIntOrNull() ?: 0
                        if (id > maxId) maxId = id
                    }
                    val nextId = maxId + 1
                    database.child(nextId.toString()).setValue(product.copy(id = nextId.toString()))
                        .addOnSuccessListener {
                            Toast.makeText(this@AddEditProductActivity, "Lưu sản phẩm thành công", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@AddEditProductActivity, "Lưu sản phẩm thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AddEditProductActivity, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
            ""
        } else {
            product.id
        }
        if (product.id.isNotEmpty()) {
            database.child(product.id).setValue(product)
                .addOnSuccessListener {
                    Toast.makeText(this, "Lưu sản phẩm thành công", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Lưu sản phẩm thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun deleteProduct(product: ItemsModel?) {
        if (product?.id?.isNotEmpty() == true) {
            val database = FirebaseDatabase.getInstance().getReference("Items")
            database.child(product.id).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this@AddEditProductActivity, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this@AddEditProductActivity, "Xóa sản phẩm thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this@AddEditProductActivity, "Không thể xóa sản phẩm: ID không hợp lệ", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    product: ItemsModel?,
    categoryId: String?,
    onBackClick: () -> Unit,
    onSave: (ItemsModel) -> Unit,
    onDelete: (ItemsModel?) -> Unit
) {
    val formatSymbols = DecimalFormatSymbols().apply { groupingSeparator = '.' }
    val formatter = DecimalFormat("#,###", formatSymbols)

    var title by remember { mutableStateOf(product?.title ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var price by remember { mutableStateOf(product?.price?.let { formatter.format(it) } ?: "") }
    var categoryIdState by remember { mutableStateOf(product?.categoryId ?: (categoryId ?: "")) }
    var rating by remember { mutableStateOf(product?.rating?.toString() ?: "") }
    var showRecommended by remember { mutableStateOf(product?.showRecommended ?: false) }
    var imageUrls by remember { mutableStateOf(product?.picUrl?.joinToString(", ") ?: "") }
    var models by remember { mutableStateOf(product?.model?.joinToString(", ") ?: "") }
    var showDeleteDialog by remember { mutableStateOf(false) } // Trạng thái hiển thị dialog xác nhận xóa

    // Hiển thị dialog xác nhận xóa
    if (showDeleteDialog && product != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa sản phẩm '${product.title}' không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(product)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Hủy")
                }
            }
        )
    }

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
                actions = {
                    if (product != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Xóa sản phẩm",
                                tint = Color.White
                            )
                        }
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
                enabled = product == null && categoryId != null
            )
            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it },
                label = { Text("Đánh giá") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = imageUrls,
                onValueChange = { imageUrls = it },
                label = { Text("URL ảnh (tách bằng dấu phẩy)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("VD: url1, url2, url3") }
            )
            OutlinedTextField(
                value = models,
                onValueChange = { models = it },
                label = { Text("Model (tách bằng dấu phẩy)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("VD: Model 1, Model 2") }
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
                    val modelList = models.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    val imageUrlList = imageUrls.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    val newProduct = ItemsModel(
                        id = product?.id ?: "",
                        title = title,
                        description = description,
                        price = parsedPrice,
                        categoryId = categoryIdState,
                        rating = rating.toDoubleOrNull() ?: 0.0,
                        showRecommended = showRecommended,
                        picUrl = if (imageUrlList.isNotEmpty()) ArrayList(imageUrlList) else ArrayList(),
                        model = if (modelList.isNotEmpty()) ArrayList(modelList) else ArrayList()
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