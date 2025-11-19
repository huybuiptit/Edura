# Cập Nhật Navigation Bar

## Thay đổi

### Trước đây:
1. **Trang chủ** - HomeFragment
2. **Quizz** - QuizFragment
3. **Thống kê** - StatsFragment
4. **Tài khoản** - ProfileFragment

### Bây giờ:
1. **Trang chủ** - HomeFragment (giữ nguyên)
2. **Kho** - QuizFragment (nơi lưu trữ và xem các quiz)
3. **Quiz** - StatsFragment (có thể dùng cho quiz hoặc thống kê)
4. **Tài khoản** - ProfileFragment (giữ nguyên)

## Files đã cập nhật

### 1. activity_main.xml
- Đổi `navQuiz` → `navLibrary` cho tab thứ 2
- Đổi `navStats` → `navQuiz` cho tab thứ 3 (giữ lại ID nhưng đổi nội dung)
- Cập nhật icon:
  - Tab "Kho": `ic_library_grid` (icon 4 ô vuông mới)
  - Tab "Quiz": `ic_quiz`
- Cập nhật text hiển thị

### 2. MainActivity.java
- Đổi biến `navStats` → `navLibrary`
- Đổi `navStatsIcon` → `navLibraryIcon`
- Đổi `navStatsText` → `navLibraryText`
- Cập nhật `initViews()` để khởi tạo đúng các view
- Cập nhật `setupListeners()`:
  - `navLibrary` → QuizFragment (tab 1)
  - `navQuiz` → StatsFragment (tab 2)
- Cập nhật `updateNavigation()` để thay đổi màu sắc đúng

### 3. HomeFragment.java
- Cập nhật comment trong `navigateToQuiz()` để rõ ràng rằng nó navigate đến tab "Kho"

## Tab Index Mapping

| Tab Index | Tên Tab | Fragment | Mô tả |
|-----------|---------|----------|-------|
| 0 | Trang chủ | HomeFragment | Màn hình chính với AI card và recent quizzes |
| 1 | Kho | QuizFragment | Danh sách các quiz đã tạo |
| 2 | Quiz | StatsFragment | Tính năng quiz hoặc thống kê |
| 3 | Tài khoản | ProfileFragment | Thông tin người dùng và settings |

## Icon sử dụng

- **Trang chủ**: `ic_home.xml` - Icon ngôi nhà
- **Kho**: `ic_library_grid.xml` - Icon 4 ô vuông (2x2 grid) **[MỚI TẠO]**
- **Quiz**: `ic_quiz.xml` - Icon quiz
- **Tài khoản**: `ic_user.xml` - Icon người dùng

### Icon mới tạo
- `ic_library_grid.xml`: Icon với 4 ô vuông đều nhau (grid 2x2) để đại diện cho thư viện/kho quiz

## Navigation Flow

```
HomeFragment
  └── Click AI Card → navigateToFragment(1) → Kho tab (QuizFragment)

BottomNav
  ├── Tab 0 → HomeFragment
  ├── Tab 1 → QuizFragment (Kho)
  ├── Tab 2 → StatsFragment (Quiz)
  └── Tab 3 → ProfileFragment (Tài khoản)
```

## Màu sắc

- **Selected**: `#4169E1` (Royal Blue)
- **Unselected**: `#9E9E9E` (Gray)

## Notes

- Cấu trúc Single Activity với Multiple Fragments được giữ nguyên
- Navigation logic hoạt động thông qua `MainActivity.navigateToFragment(int tabIndex)`
- Tab selection được quản lý bởi `updateNavigation(int selectedIndex)`

