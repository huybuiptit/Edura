# Debug Home Screen - Hướng Dẫn Khắc Phục Crash

## ✅ Những Gì Đã Làm Để Khắc Phục Crash

### 1. Thay ViewPager2 bằng RecyclerView
- ViewPager2 có thể gây crash nếu chưa setup đúng
- RecyclerView đơn giản và ổn định hơn
- Vẫn giữ được hiệu ứng scroll ngang

### 2. Thêm Try-Catch Ở Mọi Nơi
- onCreateView: Catch tất cả exceptions
- setupCategories: Catch lỗi khi setup RecyclerView
- setupRecentQuizzes: Catch lỗi khi inflate views
- updateUsername: Catch lỗi Firebase

### 3. Thêm Null Checks
- Kiểm tra getContext() != null
- Kiểm tra tất cả views != null
- Kiểm tra auth != null
- Log ra console nếu view nào null

### 4. Logging Để Debug
- Log errors vào Logcat
- Toast message hiển thị error
- Dễ dàng tìm ra vấn đề

---

## 🔍 Cách Xem Log Để Biết Lỗi Ở Đâu

### Trong Android Studio:

1. **Mở Logcat:**
   - View > Tool Windows > Logcat
   - Hoặc nhấn Alt+6 (Windows) / Cmd+6 (Mac)

2. **Filter Log:**
   - Chọn thiết bị đang chạy app
   - Filter by: "HomeFragment" hoặc "Error"
   - Level: Error (màu đỏ)

3. **Chạy App:**
   - Run app (Shift+F10)
   - Khi app crash, xem Logcat

4. **Tìm Error Message:**
   - Tìm dòng có chữ "HomeFragment"
   - Hoặc tìm "java.lang.NullPointerException"
   - Hoặc "java.lang.RuntimeException"

### Các Log Quan Trọng:

```
E/HomeFragment: One or more views are null!
```
→ Có view không tìm thấy trong layout

```
E/HomeFragment: Error setting up categories: ...
```
→ Lỗi khi setup RecyclerView categories

```
Error loading home screen: ...
```
→ Lỗi chung khi load home screen

---

## 🛠️ Các Bước Khắc Phục

### Bước 1: Sync và Rebuild
```bash
# Trong Android Studio:
File > Sync Project with Gradle Files
Build > Clean Project
Build > Rebuild Project
```

### Bước 2: Invalidate Caches
```bash
File > Invalidate Caches / Restart...
> Invalidate and Restart
```

### Bước 3: Chạy Lại App
- Uninstall app cũ trên thiết bị/emulator
- Run lại từ Android Studio

---

## 🐛 Các Lỗi Thường Gặp Và Cách Sửa

### Lỗi 1: NullPointerException - View is null
**Nguyên nhân:** Layout có ID không khớp với code

**Kiểm tra:**
```xml
<!-- fragment_home.xml phải có các ID: -->
- tvUsername
- btnNotification
- recyclerCategories
- tvSeeMoreWorld
- recentQuizzesContainer
```

**Cách sửa:**
1. Mở `fragment_home.xml`
2. Verify tất cả ID trên có trong file
3. Sync Gradle

### Lỗi 2: InflateException - Layout không load được
**Nguyên nhân:** Layout có lỗi XML syntax

**Cách sửa:**
1. Kiểm tra `fragment_home.xml` có lỗi đỏ không
2. Kiểm tra `item_quiz_category.xml`
3. Kiểm tra `item_recent_quiz.xml`
4. Fix tất cả lỗi XML

### Lỗi 3: RecyclerView không hiển thị
**Nguyên nhân:** Adapter chưa setup đúng

**Kiểm tra trong Logcat:**
```
E/RecyclerView: No adapter attached; skipping layout
```

**Đã fix:** Code đã có null check và try-catch

### Lỗi 4: Firebase Auth lỗi
**Nguyên nhân:** User chưa đăng nhập

**Cách sửa:**
1. Đảm bảo user đã login
2. Kiểm tra Firebase Console
3. Check `google-services.json` có đúng không

### Lỗi 5: Resource not found
**Nguyên nhân:** Icon drawable không tồn tại

**Kiểm tra các icon sau:**
- ic_brain
- ic_quiz
- ic_categories
- ic_diamond
- ic_flash
- ic_math
- ic_biology
- ic_chemistry
- ic_notification_bell

**Cách sửa:**
1. Verify tất cả icon tồn tại trong `res/drawable/`
2. Nếu thiếu, thay bằng icon khác có sẵn

---

## 📱 Test App Từng Phần

### Test 1: Layout Cơ Bản
Tạm thời comment out các phần setup:
```java
// setupCategories();
// setupRecentQuizzes();
```
Chạy xem header có hiển thị không.

### Test 2: Categories
Uncomment `setupCategories()`, chạy lại.

### Test 3: Recent Quizzes
Uncomment `setupRecentQuizzes()`, chạy lại.

---

## 🔴 Nếu Vẫn Crash - Gửi Log Cho Tôi

### Cách Lấy Full Stack Trace:

1. **Trong Logcat, tìm crash:**
   - Filter: Show only selected application
   - Level: Error

2. **Copy toàn bộ error:**
   - Từ dòng "FATAL EXCEPTION"
   - Đến hết stack trace (khoảng 20-30 dòng)

3. **Gửi cho tôi:**
```
E/AndroidRuntime: FATAL EXCEPTION: main
    Process: com.example.edura, PID: 12345
    java.lang.NullPointerException: ...
    at com.example.edura.fragment.HomeFragment.setupCategories(HomeFragment.java:111)
    at com.example.edura.fragment.HomeFragment.onCreateView(HomeFragment.java:53)
    ... (rest of stack trace)
```

---

## 🎯 Quick Fix Commands

### Trong Terminal Android Studio:

```bash
# Clean và rebuild
.\gradlew.bat clean assembleDebug

# Xoá cache Gradle
.\gradlew.bat --stop
.\gradlew.bat clean
```

### Uninstall và Reinstall:
```bash
# Kết nối thiết bị, chạy:
adb uninstall com.example.edura
# Rồi Run lại từ Android Studio
```

---

## 📋 Checklist Trước Khi Chạy

- [ ] Sync Project with Gradle Files
- [ ] Clean Project
- [ ] Rebuild Project
- [ ] Uninstall app cũ
- [ ] Run app
- [ ] Xem Logcat nếu crash
- [ ] Gửi error message cho tôi

---

## 💡 Mẹo Debug

### 1. Chạy trên Emulator trước
- Dễ debug hơn thiết bị thật
- Logcat rõ ràng hơn

### 2. Dùng Debug Mode
- Đặt breakpoint tại `onCreateView`
- Step through code để tìm lỗi

### 3. Kiểm tra Build Variants
- Build > Select Build Variant
- Chọn "debug" (không phải release)

---

## 📞 Hỗ Trợ

Nếu bạn cần hỗ trợ, vui lòng cung cấp:

1. **Error message từ Logcat** (đầy đủ stack trace)
2. **Screenshot của app khi crash**
3. **Android version** đang test
4. **Emulator hay thiết bị thật?**

---

## 🔄 Updates

- **v1.0**: Tạo giao diện mới với ViewPager2 ❌ Crash
- **v1.1**: Chuyển sang RecyclerView ✅ Ổn định hơn
- **v1.2**: Thêm try-catch và null checks ✅ An toàn hơn
- **v1.3**: Thêm logging để debug ✅ Dễ tìm lỗi hơn

---

## ✨ Tính Năng Hoạt Động

Nếu app chạy được, bạn sẽ thấy:

1. ✅ Header với welcome message và username
2. ✅ Nút thông báo góc phải
3. ✅ Danh sách categories scroll ngang
4. ✅ 3 quiz gần đây

Nếu không thấy gì cả → Xem Logcat và gửi error cho tôi!

