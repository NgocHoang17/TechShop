package com.example.techshop.Admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.techshop.Model.CategoryModel
import com.example.techshop.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageCategoriesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManageCategoriesScreen(onBackClick = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesScreen(onBackClick: () -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val categoryRef = database.getReference("Category")
    var categories by remember { mutableStateOf(listOf<CategoryModel>()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showEditCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryTitle by remember { mutableStateOf("") }
    var newCategoryId by remember { mutableStateOf("") }
    var newCategoryPicUrl by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf<CategoryModel?>(null) }
    val context = LocalContext.current

    // Listener cho danh mục
    DisposableEffect(Unit) {
        val categoryListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryList = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val category = childSnapshot.getValue(CategoryModel::class.java)
                    if (category != null) {
                        categoryList.add(category)
                    }
                }
                categories = categoryList
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                Toast.makeText(context, "Lỗi tải danh mục: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        categoryRef.addValueEventListener(categoryListener)
        onDispose { categoryRef.removeEventListener(categoryListener) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quản lý danh mục",
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
                    IconButton(onClick = { showAddCategoryDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Thêm danh mục",
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
            } else if (categories.isEmpty()) {
                Text(
                    text = "Không có danh mục nào.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categories.size) { index ->
                        val category = categories[index]
                        CategoryItem(
                            category = category,
                            onClick = {
                                val intent = Intent(context, ManageProductsByCategoryActivity::class.java)
                                intent.putExtra("CATEGORY_ID", category.id.toString())
                                intent.putExtra("CATEGORY_TITLE", category.title)
                                context.startActivity(intent)
                            },
                            onDelete = {
                                categoryRef.child(category.id.toString()).removeValue()
                            },
                            onEdit = {
                                editCategory = category
                                newCategoryTitle = category.title
                                newCategoryId = category.id.toString()
                                newCategoryPicUrl = category.picUrl
                                showEditCategoryDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialog thêm danh mục
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Thêm danh mục mới", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newCategoryTitle,
                        onValueChange = { newCategoryTitle = it },
                        label = { Text("Tên danh mục") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCategoryId,
                        onValueChange = { newCategoryId = it },
                        label = { Text("ID danh mục") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCategoryPicUrl,
                        onValueChange = { newCategoryPicUrl = it },
                        label = { Text("URL hình ảnh") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryTitle.isNotEmpty() && newCategoryId.isNotEmpty()) {
                            val newCategory = CategoryModel(
                                title = newCategoryTitle,
                                id = newCategoryId.toIntOrNull() ?: 0,
                                picUrl = newCategoryPicUrl
                            )
                            categoryRef.child(newCategoryId).setValue(newCategory)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Thêm danh mục thành công", Toast.LENGTH_SHORT).show()
                                    showAddCategoryDialog = false
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Thêm danh mục thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.purple),
                        contentColor = Color.White
                    )
                ) {
                    Text("Thêm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Dialog sửa danh mục
    if (showEditCategoryDialog && editCategory != null) {
        AlertDialog(
            onDismissRequest = { showEditCategoryDialog = false },
            title = { Text("Sửa danh mục", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newCategoryTitle,
                        onValueChange = { newCategoryTitle = it },
                        label = { Text("Tên danh mục") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCategoryId,
                        onValueChange = { newCategoryId = it },
                        label = { Text("ID danh mục") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCategoryPicUrl,
                        onValueChange = { newCategoryPicUrl = it },
                        label = { Text("URL hình ảnh") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryTitle.isNotEmpty()) {
                            val updatedCategory = CategoryModel(
                                title = newCategoryTitle,
                                id = newCategoryId.toIntOrNull() ?: 0,
                                picUrl = newCategoryPicUrl
                            )
                            categoryRef.child(newCategoryId).setValue(updatedCategory)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Sửa danh mục thành công", Toast.LENGTH_SHORT).show()
                                    showEditCategoryDialog = false
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Sửa danh mục thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.purple),
                        contentColor = Color.White
                    )
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditCategoryDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun CategoryItem(
    category: CategoryModel,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.lightGrey)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = category.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID: ${category.id}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Sửa danh mục",
                        tint = colorResource(R.color.purple)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa danh mục",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}