package com.example.techshop.Admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techshop.Model.ItemsModel
import com.example.techshop.R
import com.example.techshop.ui.components.ListItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageProductsByCategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categoryId = intent.getStringExtra("CATEGORY_ID")
        val categoryTitle = intent.getStringExtra("CATEGORY_TITLE") ?: "Sản phẩm"
        setContent {
            ManageProductsByCategoryScreen(
                categoryId = categoryId,
                categoryTitle = categoryTitle,
                onBackClick = { finish() },
                onAddProduct = {
                    startActivity(Intent(this, AddEditProductActivity::class.java).apply {
                        putExtra("CATEGORY_ID", categoryId)
                    })
                },
                onEditProduct = { product ->
                    val intent = Intent(this, AddEditProductActivity::class.java)
                    intent.putExtra("PRODUCT", product)
                    startActivity(intent)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProductsByCategoryScreen(
    categoryId: String?,
    categoryTitle: String,
    onBackClick: () -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (ItemsModel) -> Unit
) {
    val database = FirebaseDatabase.getInstance().getReference("Items")
    var products by remember { mutableStateOf(listOf<ItemsModel>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Tạo listener để theo dõi thay đổi thời gian thực
    val valueEventListener = remember {
        object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val product = childSnapshot.getValue(ItemsModel::class.java)
                    if (product != null) {
                        productList.add(product.copy(id = childSnapshot.key ?: ""))
                    }
                }
                products = if (categoryId != null) {
                    productList.filter { it.categoryId == categoryId }
                } else {
                    productList
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        }
    }

    // Sử dụng DisposableEffect để quản lý vòng đời của listener
    DisposableEffect(Unit) {
        database.addValueEventListener(valueEventListener)
        onDispose {
            database.removeEventListener(valueEventListener)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quản lý $categoryTitle",
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
                    IconButton(onClick = onAddProduct) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Thêm sản phẩm",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (products.isEmpty()) {
                Text(
                    text = "Không có sản phẩm nào.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                ListItems(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    itemsList = products,
                    onClick = onEditProduct
                )
            }
        }
    }
}