# 🔒 Fix Firebase reCAPTCHA Error

## ❌ Lỗi gặp phải:

```
We have blocked all requests from this device due to unusual activity. Try again later.
RecaptchaAction(action=signInWithPassword)
```

## 🔍 Nguyên nhân:

Firebase phát hiện "hoạt động bất thường" và block thiết bị tạm thời vì:

1. ✅ **Test quá nhiều lần** - Đăng ký/đăng nhập nhiều lần trong thời gian ngắn
2. ✅ **Chạy trên emulator** - Firebase nghi ngờ là bot
3. ✅ **Thiết bị debug** - Không phải thiết bị thật của user
4. ✅ **App Check/reCAPTCHA** đang hoạt động strict mode

## ✅ Giải pháp đã áp dụng

### 1. Disable App Verification cho Testing

**Đã thêm code vào LoginActivity.java và SignUpActivity.java:**

```java
auth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
```

Điều này sẽ:
- ✅ Tắt reCAPTCHA verification khi develop
- ✅ Cho phép test unlimited trên emulator
- ✅ Không block thiết bị nữa

⚠️ **LƯU Ý**: Chỉ dùng trong development. Khi release app, phải remove dòng này!

## 🚀 Các giải pháp khác

### Option 2: Chờ đợi (1-2 giờ)

Firebase sẽ tự động unblock thiết bị sau 1-2 giờ.

### Option 3: Clear app data

1. Vào **Settings** trên thiết bị/emulator
2. **Apps** > **Edura**
3. **Storage** > **Clear Data**
4. Chạy lại app

### Option 4: Dùng thiết bị thật

Chạy app trên thiết bị Android thật thay vì emulator.

### Option 5: Whitelist test accounts trong Firebase Console

1. Vào [Firebase Console](https://console.firebase.google.com/)
2. Chọn project **edura-aeed5**
3. **Authentication** > **Settings** (tab)
4. Scroll xuống **Authorized domains**
5. Thêm localhost nếu test trên emulator

### Option 6: Disable Email Enumeration Protection

1. Vào [Firebase Console](https://console.firebase.google.com/)
2. **Authentication** > **Settings**
3. Tab **User actions**
4. Tìm **Email enumeration protection**
5. Tạm thời **Disable** (cho development)

## 🧪 Test sau khi fix

1. **Uninstall app** khỏi emulator/thiết bị
2. **Build lại app** với code mới
3. **Install và chạy** app
4. Test đăng ký/đăng nhập
5. Không còn lỗi reCAPTCHA nữa! ✅

## ⚠️ Quan trọng - Production

Khi deploy app lên production:

1. **XÓA dòng code này:**
   ```java
   auth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
   ```

2. **Enable App Check** trong Firebase Console:
   - Vào **App Check** trong Firebase Console
   - Setup App Check với Play Integrity API
   - Bảo vệ app khỏi abuse

3. **Enable Email Enumeration Protection** lại

## 📝 Build & Deploy Checklist

### Development Mode (Hiện tại) ✅
- [x] `setAppVerificationDisabledForTesting(true)` - ENABLED
- [x] Test unlimited trên emulator
- [x] Không bị block

### Production Mode (Khi release)
- [ ] `setAppVerificationDisabledForTesting(true)` - PHẢI XÓA
- [ ] Enable App Check trong Firebase Console
- [ ] Test trên thiết bị thật
- [ ] Enable Email Enumeration Protection

## 🔧 Troubleshooting

### Vẫn bị lỗi sau khi thêm code?

1. **Uninstall app hoàn toàn**
   ```bash
   adb uninstall com.example.edura
   ```

2. **Clean build**
   ```bash
   .\gradlew clean assembleDebug
   ```

3. **Clear emulator data**
   - Wipe data của emulator
   - Tạo emulator mới

4. **Đổi package name tạm thời**
   - Đổi `com.example.edura` thành `com.example.edura.test`
   - Build và test

### Lỗi khác liên quan đến reCAPTCHA?

- **SafetyNet API error**: Thiết bị không hỗ trợ SafetyNet (emulator cũ)
- **Play Services outdated**: Update Google Play Services trên thiết bị
- **No network**: Kiểm tra kết nối internet

## 📚 Tham khảo

- [Firebase Auth - Testing](https://firebase.google.com/docs/auth/android/test)
- [Firebase App Check](https://firebase.google.com/docs/app-check)
- [reCAPTCHA Verification](https://firebase.google.com/docs/auth/admin/verify-id-tokens)

---

**Sau khi build lại app với code mới, lỗi reCAPTCHA sẽ biến mất!** 🎉

**Nhớ remove code test khi deploy production!** ⚠️







