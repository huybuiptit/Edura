# Hướng dẫn Setup Firebase cho Edura

## ✅ Đã hoàn thành

- ✅ Firebase Authentication đã được tích hợp vào LoginActivity và SignUpActivity
- ✅ Các dependencies Firebase đã được thêm vào build.gradle
- ✅ Validation form đầy đủ
- ✅ Forgot Password với Firebase sendPasswordResetEmail

## 📋 Bước tiếp theo để hoàn thiện Firebase

### 1. Tạo Firebase Project

1. Truy cập [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"** hoặc chọn project có sẵn
3. Nhập tên project: **Edura**
4. Làm theo các bước để tạo project

### 2. Thêm Android App vào Firebase Project

1. Trong Firebase Console, click vào icon Android
2. Nhập **Android package name**: `com.example.edura`
3. (Optional) Nhập App nickname: `Edura`
4. (Optional) SHA-1 certificate fingerprint (cần cho Google Sign In)
   - Để lấy SHA-1, chạy lệnh:
   ```bash
   cd android
   ./gradlew signingReport
   ```
5. Click **"Register app"**

### 3. Download google-services.json

1. Download file `google-services.json` từ Firebase Console
2. Copy file này vào thư mục: `app/google-services.json`
3. **QUAN TRỌNG**: File này chứa config của Firebase project

### 4. Enable Firebase Authentication

1. Trong Firebase Console, vào **Authentication** > **Sign-in method**
2. Enable các phương thức đăng nhập:
   - ✅ **Email/Password** - Click Enable
   - 🔧 **Google** (nếu muốn dùng Google Sign In)
   - 🔧 **Facebook** (nếu muốn dùng Facebook Login)
   - 🔧 **Apple** (nếu muốn dùng Apple Sign In)

### 5. Build và Test

1. Sync Gradle trong Android Studio
2. Build project
3. Chạy app trên emulator hoặc thiết bị thật
4. Test chức năng:
   - ✅ Sign Up (đăng ký tài khoản mới)
   - ✅ Login (đăng nhập)
   - ✅ Forgot Password (reset mật khẩu)

### 6. Kiểm tra Users trong Firebase Console

Sau khi đăng ký thành công, bạn có thể xem danh sách users trong:
**Firebase Console** > **Authentication** > **Users**

## 🔥 Chức năng đã được implement

### SignUpActivity
- ✅ Validation đầy đủ (Full Name, Email, Password, Confirm Password)
- ✅ Tạo user với `auth.createUserWithEmailAndPassword()`
- ✅ Lưu Full Name vào Firebase Profile với `UserProfileChangeRequest`
- ✅ Chuyển sang LoginActivity sau khi đăng ký thành công

### LoginActivity
- ✅ Validation đầy đủ (Email, Password)
- ✅ Đăng nhập với `auth.signInWithEmailAndPassword()`
- ✅ Forgot Password với `auth.sendPasswordResetEmail()`
- ✅ Toast message hiển thị kết quả

## 🚀 Tiếp theo

Sau khi user login thành công, bạn có thể:

1. **Tạo MainActivity** - Màn hình chính sau khi đăng nhập
2. **Check auth state** - Kiểm tra user đã login chưa trong LoginActivity:
```java
@Override
protected void onStart() {
    super.onStart();
    FirebaseUser currentUser = auth.getCurrentUser();
    if (currentUser != null) {
        // User đã login, chuyển sang MainActivity
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
```

3. **Logout functionality** - Thêm nút logout:
```java
auth.signOut();
```

4. **Get user info**:
```java
FirebaseUser user = auth.getCurrentUser();
if (user != null) {
    String name = user.getDisplayName();
    String email = user.getEmail();
    String uid = user.getUid();
}
```

## 📝 Lưu ý

- ⚠️ **google-services.json** là file quan trọng, không commit lên Git public
- ⚠️ Thêm vào `.gitignore`: `app/google-services.json`
- ⚠️ Password Firebase yêu cầu tối thiểu 6 ký tự
- ⚠️ Email phải là valid email address

## ❓ Troubleshooting

### Lỗi: "Default FirebaseApp is not initialized"
- Đảm bảo file `google-services.json` đã được đặt đúng vị trí: `app/google-services.json`
- Sync Gradle lại

### Lỗi: "An internal error has occurred"
- Kiểm tra internet connection
- Kiểm tra Email/Password đã được enable trong Firebase Console

### Lỗi compile
- Đảm bảo đã sync Gradle
- Clean Project: **Build** > **Clean Project**
- Rebuild Project: **Build** > **Rebuild Project**

## 📚 Tài liệu tham khảo

- [Firebase Authentication - Android](https://firebase.google.com/docs/auth/android/start)
- [Email/Password Authentication](https://firebase.google.com/docs/auth/android/password-auth)
- [Google Sign-In](https://firebase.google.com/docs/auth/android/google-signin)

