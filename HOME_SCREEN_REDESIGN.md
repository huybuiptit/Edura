# Home Screen Redesign - Hướng Dẫn

## Tổng Quan
Giao diện trang chủ đã được thiết kế lại hoàn toàn theo yêu cầu với 3 phần chính:

1. **Header** - Chào mừng và thông báo
2. **World Quiz** - ViewPager trượt ngang với các category
3. **Gần đây** - Danh sách quiz gần đây

---

## Cấu Trúc Chi Tiết

### 1. Header Section (Phần Đầu)
**File:** `fragment_home.xml` (Lines 16-87)

#### Các Thành Phần:
- **Chữ Welcome**: Hiển thị "Welcome" nhỏ ở trên
- **Tên Username**: Hiển thị tên người dùng lớn và đậm bên dưới
- **Icon Thông Báo**: Nút tròn ở góc phải với icon chuông
  - Có badge đỏ nhỏ (có thể bật/tắt) để báo thông báo mới
  - Click vào sẽ mở trang thông báo

#### Code Liên Quan:
```java
private void updateUsername() {
    FirebaseUser currentUser = auth.getCurrentUser();
    if (currentUser != null) {
        String userName = // Lấy từ displayName hoặc email
        tvUsername.setText(userName);
    }
}
```

---

### 2. World Quiz Section (ViewPager)
**File:** `fragment_home.xml` (Lines 89-136)

#### Các Thành Phần:
- **Tiêu Đề "World Quiz"**: Bên trái, chữ đậm
- **"Xem thêm"**: Bên phải, màu xanh, có thể click
- **ViewPager2**: Trượt ngang hiển thị các category quiz

#### Categories Hiện Tại:
1. History (có badge "🔥 Hot")
2. Programming  
3. Architecture
4. Art
5. Soccer (có badge "🔥 Hot")
6. Math
7. Biology
8. Chemistry

#### Layout Item Category:
**File:** `item_quiz_category.xml`
- Card 280x160dp
- Icon trong container tròn
- Tên category và số lượng quiz
- Badge "Hot" (tuỳ chọn)

#### Code Quan Trọng:
```java
private void setupCategories() {
    categories = new ArrayList<>();
    categories.add(new QuizCategory("History", R.drawable.ic_brain, 25, true));
    // ... thêm các category khác
    
    categoryAdapter = new CategoryAdapter(categories, category -> {
        // Xử lý khi click vào category
    });
    
    viewPagerCategories.setAdapter(categoryAdapter);
    // Setup page transformer để có hiệu ứng đẹp
}
```

---

### 3. Recent Section (Gần Đây)
**File:** `fragment_home.xml` (Lines 138-165)

#### Các Thành Phần:
- **Tiêu đề "Gần đây"**: Chữ đậm
- **Container**: LinearLayout chứa các quiz item
- **Quiz Items**: Tối đa 3 quiz gần đây nhất

#### Layout Item Recent Quiz:
**File:** `item_recent_quiz.xml`
- Card với icon quiz
- Tiêu đề quiz
- Progress (VD: "14/54 thẻ")
- Tác giả
- Mũi tên bên phải

#### Code:
```java
private void setupRecentQuizzes() {
    recentQuizzes = new ArrayList<>();
    recentQuizzes.add(new RecentQuiz(...));
    // Thêm các quiz gần đây
    
    // Inflate và add vào container
    for (RecentQuiz quiz : recentQuizzes) {
        View quizItemView = LayoutInflater.from(getContext())
            .inflate(R.layout.item_recent_quiz, recentQuizzesContainer, false);
        // Setup view và add
        recentQuizzesContainer.addView(quizItemView);
    }
}
```

---

## Files Mới Được Tạo

### Layouts:
1. `fragment_home.xml` - Layout chính (đã cập nhật)
2. `item_quiz_category.xml` - Layout cho category card
3. `item_recent_quiz.xml` - Layout cho recent quiz item

### Java Classes:

#### Models:
1. **QuizCategory.java** (`app/src/main/java/com/example/edura/model/`)
   - `name`: Tên category
   - `iconResId`: Resource ID của icon
   - `quizCount`: Số lượng quiz
   - `isHot`: Có badge "Hot" hay không

2. **RecentQuiz.java** (`app/src/main/java/com/example/edura/model/`)
   - `title`: Tiêu đề quiz
   - `progress`: Tiến độ (VD: "14/54 thẻ")
   - `author`: Tác giả
   - `iconResId`: Resource ID của icon

#### Adapter:
1. **CategoryAdapter.java** (`app/src/main/java/com/example/edura/adapter/`)
   - Adapter cho ViewPager2
   - Hiển thị các category card
   - Xử lý click event

#### Fragment:
1. **HomeFragment.java** (đã cập nhật)
   - `setupCategories()`: Khởi tạo ViewPager với categories
   - `setupRecentQuizzes()`: Tạo danh sách quiz gần đây
   - `updateUsername()`: Cập nhật tên người dùng
   - `setupListeners()`: Xử lý các sự kiện click

---

## Dependencies Mới

### build.gradle.kts:
```kotlin
implementation("androidx.viewpager2:viewpager2:1.0.0")
```

---

## Cách Tích Hợp Với Firebase/Firestore

### Để lấy dữ liệu thật từ Firestore:

#### 1. Categories:
```java
private void loadCategoriesFromFirestore() {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("categories")
        .get()
        .addOnSuccessListener(queryDocumentSnapshots -> {
            categories.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                QuizCategory category = doc.toObject(QuizCategory.class);
                categories.add(category);
            }
            categoryAdapter.notifyDataSetChanged();
        });
}
```

#### 2. Recent Quizzes:
```java
private void loadRecentQuizzes() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user == null) return;
    
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("users")
        .document(user.getUid())
        .collection("recentQuizzes")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .limit(3)
        .get()
        .addOnSuccessListener(queryDocumentSnapshots -> {
            recentQuizzes.clear();
            recentQuizzesContainer.removeAllViews();
            
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                RecentQuiz quiz = doc.toObject(RecentQuiz.class);
                recentQuizzes.add(quiz);
                // Add view to container
            }
        });
}
```

---

## Hướng Dẫn Sử Dụng

### Chạy App:
1. Mở Android Studio
2. Sync Project with Gradle Files (Ctrl+Shift+O / Cmd+Shift+O)
3. Clean Project (Build > Clean Project)
4. Rebuild Project (Build > Rebuild Project)
5. Run app (Shift+F10 / Ctrl+R)

### Nếu App Crash:
1. Kiểm tra Logcat để xem error message
2. Đảm bảo tất cả dependencies đã được sync
3. Kiểm tra xem Firebase đã được setup đúng chưa
4. Verify rằng user đã đăng nhập

### Debug Tips:
- Thêm try-catch và logging trong các method setup
- Kiểm tra getContext() != null trước khi sử dụng
- Verify rằng tất cả resource files (drawable, layout) tồn tại

---

## Tuỳ Chỉnh

### Thay Đổi Categories:
Trong `HomeFragment.setupCategories()`:
```java
categories.add(new QuizCategory("Tên Category", R.drawable.icon, 25, false));
```

### Thay Đổi Số Lượng Recent Quizzes:
Trong `HomeFragment.setupRecentQuizzes()`, thay đổi số lượng item add vào list.

### Tuỳ Chỉnh Màu Sắc:
Xem file `res/values/colors.xml` để thay đổi màu.

### Tuỳ Chỉnh Kích Thước:
- Category card: `item_quiz_category.xml` - width/height
- Recent quiz: `item_recent_quiz.xml` - padding/margin

---

## Troubleshooting

### App không hiển thị gì:
1. Kiểm tra Firebase Auth - user có logged in không?
2. Check Logcat cho exceptions
3. Verify ViewPager2 dependency đã được add

### ViewPager không trượt:
1. Kiểm tra adapter có data không (categories.size() > 0)
2. Verify layout_height của ViewPager2 không phải 0dp

### Recent quizzes không hiện:
1. Check recentQuizzesContainer có được findViewById đúng không
2. Verify layout inflation không bị lỗi
3. Kiểm tra getContext() có null không

---

## Next Steps

### Tính Năng Có Thể Thêm:
1. **Notification System**: Thực sự hiển thị thông báo
2. **Category Detail Page**: Click vào category mở trang chi tiết
3. **Recent Quiz từ Firestore**: Load dữ liệu thật từ database
4. **Infinite Scroll**: Thêm "Xem thêm" cho recent quizzes
5. **Search**: Thêm thanh search ở đầu trang
6. **User Stats**: Hiển thị điểm số, streak, achievements

---

## Liên Hệ
Nếu có vấn đề hoặc cần hỗ trợ, vui lòng kiểm tra:
1. Android Studio Logcat
2. Build output
3. Firebase Console (Authentication, Firestore)

