# 🧪 Tạo Tài Khoản Test trong Firebase Console

## Cách 1: Tạo trong Firebase Console (Nhanh nhất)

### Bước 1: Vào Firebase Console
1. Truy cập [Firebase Console](https://console.firebase.google.com/)
2. Chọn project **edura-aeed5**

### Bước 2: Enable Email/Password Authentication
1. Vào **Authentication** > **Sign-in method**
2. Tìm **Email/Password** trong danh sách
3. Click vào **Email/Password**
4. Bật toggle **Enable** (màu xanh)
5. Click **Save**

### Bước 3: Thêm User Test
1. Vào **Authentication** > **Users** (tab)
2. Click nút **Add user** ở góc trên bên phải
3. Nhập thông tin:
   - **Email**: `test@example.com`
   - **Password**: `123456`
4. Click **Add user**

### Bước 4: Test Login
1. Mở app Edura
2. Nhập:
   - Email: `test@example.com`
   - Password: `123456`
3. Click **Login**
4. Sẽ đăng nhập thành công! ✅

---

## Cách 2: Đăng ký qua App

### Bước 1: Chạy App
1. Mở app Edura trên emulator/thiết bị

### Bước 2: Đi tới Sign Up
1. Ở màn hình Login
2. Click **"Don't have an account? Sign Up"** ở dưới cùng

### Bước 3: Điền thông tin đăng ký
- **Full Name**: `Test User`
- **Email**: `test123@gmail.com` (dùng email thật nếu muốn nhận email reset password)
- **Password**: `123456` (tối thiểu 6 ký tự)
- **Confirm Password**: `123456`

### Bước 4: Click Sign Up
1. App sẽ tạo tài khoản trong Firebase
2. Sau khi thành công, sẽ chuyển về màn hình Login
3. Login lại với email và password vừa tạo

---

## ⚠️ Lưu ý quan trọng

### Password Requirements
- ✅ Tối thiểu **6 ký tự**
- ❌ Nếu < 6 ký tự → Lỗi: "Password should be at least 6 characters"

### Email Requirements
- ✅ Phải có định dạng đúng: `user@domain.com`
- ✅ Phải có @ và domain
- ❌ Sai định dạng → Lỗi: "The email address is badly formatted"

### Tài khoản đã tồn tại
- ❌ Nếu email đã được đăng ký → Lỗi: "The email address is already in use"
- ✅ Giải pháp: Dùng email khác hoặc login bằng email đó

---

## 🐛 Troubleshooting

### Lỗi: "The supplied auth credential is incorrect"

**Nguyên nhân:**
- Email hoặc password sai
- Tài khoản chưa tồn tại trong Firebase
- Email/Password chưa được enable

**Giải pháp:**
1. Kiểm tra Email/Password authentication đã enable trong Firebase Console
2. Tạo tài khoản mới qua Sign Up
3. Hoặc tạo user trong Firebase Console như hướng dẫn trên

### Lỗi: "We have blocked all requests from this device"

**Đã fix:** Code đã có `setAppVerificationDisabledForTesting(true)`

**Nếu vẫn lỗi:**
1. Uninstall app: `adb uninstall com.example.edura`
2. Build lại: `.\gradlew clean assembleDebug`
3. Install và chạy lại

### Lỗi: "An internal error has occurred"

**Nguyên nhân:** Lỗi server Firebase hoặc internet

**Giải pháp:**
1. Kiểm tra kết nối internet
2. Thử lại sau vài phút
3. Kiểm tra Firebase Console có hoạt động không

---

## 📝 Checklist Setup Firebase Auth

- [ ] 1. Firebase project đã tạo (edura-aeed5) ✅
- [ ] 2. google-services.json đã download và đặt trong app/ ✅
- [ ] 3. Email/Password authentication đã **ENABLE** trong Firebase Console ⚠️
- [ ] 4. Đã tạo ít nhất 1 user test
- [ ] 5. INTERNET permission đã thêm vào AndroidManifest.xml ✅
- [ ] 6. setAppVerificationDisabledForTesting(true) đã thêm ✅

**Bước 3 là QUAN TRỌNG NHẤT!** Nếu chưa enable Email/Password, sẽ không thể login!

---

## 🎯 Test Account Mẫu

Sau khi setup xong, bạn có thể dùng các tài khoản này để test:

### Tài khoản 1:
- Email: `test@example.com`
- Password: `123456`

### Tài khoản 2:
- Email: `admin@edura.com`
- Password: `admin123`

### Tài khoản 3:
- Email: `user@test.com`
- Password: `password`

*(Tạo các tài khoản này trong Firebase Console như hướng dẫn ở trên)*

---

## 🔗 Links hữu ích

- [Firebase Console - edura-aeed5](https://console.firebase.google.com/project/edura-aeed5)
- [Firebase Auth Documentation](https://firebase.google.com/docs/auth/android/password-auth)
- [Troubleshooting Guide](https://firebase.google.com/docs/auth/admin/errors)

---

**Sau khi enable Email/Password và tạo user, login sẽ hoạt động bình thường!** 🎉



















