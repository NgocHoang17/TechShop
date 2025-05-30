# 🛍️ Ecommerce - TechShop


## 📖 Giới thiệu

TechShop là một ứng dụng thương mại điện tử hiện đại được phát triển trên nền tảng Android, chuyên về bán các sản phẩm công nghệ. Ứng dụng được xây dựng với kiến trúc MVVM (Model-View-ViewModel) và sử dụng các công nghệ mới nhất của Android.

## 🚀 Tính năng

### 👤 Người dùng
- Đăng nhập/Đăng ký tài khoản (hỗ trợ đăng nhập bằng Google)
- Xem và tìm kiếm sản phẩm theo danh mục
- Quản lý giỏ hàng
- Thanh toán đơn hàng
- Theo dõi lịch sử đơn hàng
- Yêu thích sản phẩm
- Quản lý thông tin cá nhân

### 👨‍💼 Quản trị viên
- Quản lý sản phẩm (thêm, sửa, xóa)
- Quản lý danh mục
- Quản lý đơn hàng
- Quản lý người dùng
- Xem thống kê doanh thu

## 🛠 Công nghệ sử dụng

- **Ngôn ngữ:** Kotlin
- **Minimum SDK:** 27
- **Target SDK:** 34
- **Kiến trúc:** MVVM (Model-View-ViewModel)

### 📚 Thư viện chính
- Jetpack Compose UI
- Firebase Authentication
- Firebase Realtime Database
- Google Play Services Auth
- Coil for Image Loading
- MPAndroidChart for Statistics
- Accompanist for UI Components
- Kotlin Coroutines
- Material Design 3

## 🏗 Cấu trúc dự án

```
app/src/main/
├── java/com/example/techshop/
│   ├── Activity/          # Các màn hình chính
│   ├── Admin/             # Phần quản trị
│   ├── Model/             # Data models
│   ├── ViewModel/         # ViewModels
│   ├── Helper/            # Các lớp tiện ích
│   └── utils/             # Utility functions
└── res/
    ├── layout/           # Layout XML files
    ├── drawable/         # Images và icons
    ├── values/           # Resources (strings, colors, etc.)
    └── ...
```

## ⚙️ Cài đặt và Chạy

1. Clone repository:
```bash
git clone https://github.com/NgocHoang17/techshop.git
```

2. Mở project trong Android Studio

3. Sync Gradle và cài đặt dependencies

4. Cấu hình Firebase:
   - Tạo project trên Firebase Console
   - Tải file `google-services.json` và đặt vào thư mục `app/`
   - Kích hoạt Authentication và Realtime Database

5. Build và chạy ứng dụng

## 🔧 Yêu cầu hệ thống

- Android Studio Hedgehog | 2023.1.1 trở lên
- JDK 11 trở lên
- Android SDK với API level 27 trở lên

## 📱 Screenshots

<img src="https://github.com/user-attachments/assets/46ee082a-4dc3-44c0-90f8-a0f137cb8b9e" width="150" style="margin-right: 100px;"/>
<img src="https://github.com/user-attachments/assets/0100041c-9b27-403c-b013-88e98d6c31e1" width="150" style="margin-right: 100px;"/>
<img src="https://github.com/user-attachments/assets/75be03d9-4173-4f82-9c57-e6d3828211d9" width="150" style="margin-right: 100px;"/>
<img src="https://github.com/user-attachments/assets/005bf2c4-0e24-4d79-bdb0-732f64e30d2d" width="150"/>



## 👥 Đóng góp

Chúng tôi rất hoan nghênh mọi đóng góp từ cộng đồng. Nếu bạn muốn đóng góp, vui lòng:

1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit thay đổi (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request



## 📞 Liên hệ



---

<div align="center">
  Made with ❤️ by Ngọc Hoàng
</div>

