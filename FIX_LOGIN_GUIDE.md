# 🔧 Hướng dẫn Fix Lỗi Login

## ✅ Đã sửa

- ✅ **Đã thêm INTERNET permission** vào AndroidManifest.xml
- ✅ **Đã thêm ACCESS_NETWORK_STATE permission** vào AndroidManifest.xml
- ✅ Code đã được build thành công

## 🔥 Các bước cần làm để Login hoạt động

### 1. Enable Email/Password Authentication trong Firebase Console

**Đây là lý do chính tại sao login không thành công!**

1. Truy cập [Firebase Console](https://console.firebase.google.com/)
2. Chọn project **edura-aeed5**
3. Vào menu bên trái: **Authentication** > **Sign-in method**
4. Tìm **Email/Password** trong danh sách providers
5. Click vào **Email/Password**
6. **QUAN TRỌNG**: Bật nút **Enable** 
7. Click **Save**

### 2. Fix Google Sign In (Tùy chọn)

Hiện tại Google Sign In sẽ báo lỗi vì `google-services.json` thiếu `oauth_client`.

**Cách fix:**

1. Vào [Firebase Console](https://console.firebase.google.com/)
2. Chọn project **edura-aeed5**
3. Vào **Project Settings** (icon bánh răng)
4. Scroll xuống phần **Your apps**
5. Chọn app Android của bạn
6. Trong phần **SHA certificate fingerprints**, click **Add fingerprint**
7. Thêm SHA-1 này:
   ```
   F5:6B:4D:EC:14:29:71:47:3B:0A:EB:55:10:F8:69:4F:2C:6E:11:18
   ```
8. Click **Save**
9. Download lại file `google-services.json` mới
10. Thay thế file cũ trong `app/google-services.json`
11. Sync Gradle lại
12. Vào **Authentication** > **Sign-in method** và enable **Google**

### 3. Test Login

Sau khi enable Email/Password authentication:

1. Build lại app
2. Chạy app trên emulator hoặc thiết bị thật
3. Click **"Don't have an account? Sign Up"**
4. Đăng ký tài khoản mới:
   - Full Name: Test User
   - Email: test@example.com
   - Password: 123456
   - Confirm Password: 123456
5. Click **Sign Up**
6. Nếu thành công, sẽ chuyển về Login screen
7. Login lại với email và password vừa tạo
8. Sẽ chuyển sang MainActivity

### 4. Kiểm tra Users trong Firebase Console

Sau khi đăng ký thành công:

1. Vào [Firebase Console](https://console.firebase.google.com/)
2. Chọn project **edura-aeed5**
3. Vào **Authentication** > **Users**
4. Bạn sẽ thấy user vừa đăng ký trong danh sách

## ⚠️ Lưu ý quan trọng

### Password Requirements
- Firebase yêu cầu password **tối thiểu 6 ký tự**
- Nếu nhập password ngắn hơn, sẽ báo lỗi

### Email Requirements
- Email phải đúng định dạng (có @ và domain)
- Email không được trùng với email đã đăng ký

### Internet Connection
- App cần kết nối internet để login
- Kiểm tra internet trên thiết bị/emulator

## 🐛 Troubleshooting

### Lỗi: "An internal error has occurred"
**Nguyên nhân**: Email/Password chưa được enable trong Firebase Console
**Giải pháp**: Làm theo bước 1 ở trên

### Lỗi: "The email address is badly formatted"
**Nguyên nhân**: Email không đúng định dạng
**Giải pháp**: Nhập email có @ và domain (vd: test@example.com)

### Lỗi: "The password is invalid"
**Nguyên nhân**: 
- Password sai
- Password ngắn hơn 6 ký tự khi sign up

**Giải pháp**: 
- Kiểm tra lại password
- Đảm bảo password >= 6 ký tự

### Lỗi: "The email address is already in use"
**Nguyên nhân**: Email đã được đăng ký trước đó
**Giải pháp**: 
- Dùng email khác
- Hoặc login bằng email đó

### Lỗi: "A network error has occurred"
**Nguyên nhân**: 
- Không có kết nối internet
- Firebase service down

**Giải pháp**: 
- Kiểm tra kết nối internet
- Thử lại sau vài phút

### Google Sign In không hoạt động
**Nguyên nhân**: Thiếu oauth_client trong google-services.json
**Giải pháp**: Làm theo bước 2 ở trên

## 📝 File đã được sửa

1. ✅ `app/src/main/AndroidManifest.xml` - Thêm INTERNET permissions
2. ✅ `app/src/main/java/com/example/edura/MainActivity.java` - Fix RelativeLayout
3. ✅ Build successful

## 🎯 Tóm tắt

**Để login hoạt động ngay:**
1. Vào Firebase Console
2. Enable Email/Password trong Authentication
3. Chạy app và test

**Để Google Sign In hoạt động:**
1. Thêm SHA-1 vào Firebase Console
2. Download google-services.json mới
3. Enable Google Sign In trong Authentication

## 📱 SHA-1 Fingerprint của bạn

```
SHA1: F5:6B:4D:EC:14:29:71:47:3B:0A:EB:55:10:F8:69:4F:2C:6E:11:18
SHA-256: 2E:76:D9:21:20:07:A1:57:67:4E:6A:E6:CD:46:A3:60:B4:D6:74:9F:B4:CF:D0:75:FC:06:5A:50:49:04:D7:80
```

Dùng SHA-1 này để thêm vào Firebase Console cho Google Sign In.

---

**Sau khi làm xong các bước trên, login sẽ hoạt động bình thường!** 🎉












