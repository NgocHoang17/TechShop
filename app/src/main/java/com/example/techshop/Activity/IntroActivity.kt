package com.example.techshop.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techshop.R

// Định nghĩa một Activity có tên `IntroActivity` kế thừa từ `BaseActivity`
@Suppress("DEPRECATION")
class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Thiết lập giao diện trạng thái hệ thống
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Thiết lập nội dung của Activity bằng Jetpack Compose
        setContent {
            IntroScreen(onClick = {
                // Khi nhấn vào nút "Let's Go", chuyển sang `MainActivity`
                startActivity(Intent(this, MainActivity::class.java))
            })
        }
    }
}

// Hàm Compose để hiển thị giao diện màn hình giới thiệu
@Composable
@Preview
fun IntroScreen(onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize() // Lấp đầy toàn bộ màn hình
            .background(Color.White) // Đặt nền màu trắng
            .verticalScroll(rememberScrollState()) // Cho phép cuộn nội dung khi cần
            .padding(16.dp), // Thêm padding 16dp
        horizontalAlignment = Alignment.CenterHorizontally // Căn giữa theo chiều ngang
    ) {
        // Hiển thị logo giới thiệu
        Image(
            painter = painterResource(id = R.drawable.intro_logo),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 48.dp) // Khoảng cách từ trên xuống 48dp
                .fillMaxWidth(), // Chiều rộng tối đa
            contentScale = ContentScale.Fit // Giữ nguyên tỉ lệ hình ảnh
        )

        Spacer(modifier = Modifier.height(32.dp)) // Khoảng trống 32dp

        // Hiển thị tiêu đề giới thiệu
        Text(
            text = stringResource(id = R.string.intro_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp)) // Khoảng trống 32dp

        // Hiển thị mô tả phụ
        Text(
            text = stringResource(id = R.string.intro_sub_title),
            modifier = Modifier.padding(top = 16.dp),
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        // Nút bấm "Let's Go"
        Button(
            onClick = { onClick() }, // Gọi hàm `onClick` khi nhấn vào nút
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 16.dp) // Thêm padding
                .fillMaxWidth() // Chiều rộng tối đa
                .height(50.dp), // Chiều cao 50dp
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.purple) // Màu nền của nút
            ),
            shape = RoundedCornerShape(10.dp) // Bo tròn góc 10dp
        ) {
            Text(
                text = stringResource(id = R.string.letgo), // Văn bản hiển thị trên nút
                color = Color.White, // Màu chữ trắng
                fontSize = 18.sp
            )
        }

        // Hiển thị tùy chọn đăng nhập
        Text(
            text = stringResource(id = R.string.sign),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
            fontSize = 18.sp
        )
    }
}
