# Main Screen Redesign - Mock Test UI

## Tổng quan
Đã thiết kế lại màn hình chính (MainActivity) theo design "Mock Test" với giao diện hiện đại, clean và đơn giản hơn.

## Thay đổi chính

### 1. Layout (activity_main.xml)

#### Header Section
- **Title**: "Mock Test" (32sp, bold, màu dark_text)
- **Avatar Icon**: Chữ "B" trong circle màu cam (#FF5722), kích thước 56x56dp
- Bố cục: RelativeLayout với title bên trái, avatar bên phải

#### Main Quiz Card
- **Design**: MaterialCardView với corner radius 16dp
- **Content**:
  - Title: "Lesson 10 – READING" (24sp, bold)
  - Icon calendar + "20 câu hỏi" (15sp, gray)
  - Icon clock + "Thời gian: 30 phút" (15sp, gray)
  - Button "Bắt đầu" (56dp height, màu #4169E1, full width)
- **Spacing**: Padding 24dp, margin bottom 32dp

#### Recent Section
- **Title**: "Gần đây" (20sp, bold)
- **Quiz Item Card**:
  - Icon container: 48x48dp với background light blue
  - Quiz info:
    - Title: "CB – Lesson 10 – READING" (15sp, bold)
    - Progress: "14/54 thẻ" (13sp, gray)
    - Author: "Tác giả: IELTSHHieuMinh" (12sp, gray)
  - Layout: Horizontal với icon bên trái, info bên phải

#### Bottom Navigation
- **Design**: 4 items với icon + text
- **Items**:
  1. **Trang chủ** (selected - màu #4169E1)
     - Icon: ic_home
     - Text: "Trang chủ"
  2. **Quizz** (unselected - màu #9E9E9E)
     - Icon: ic_categories
     - Text: "Quizz"
  3. **Thống kê** (unselected - màu #9E9E9E)
     - Icon: ic_leaderboard
     - Text: "Thống kê"
  4. **Tài khoản** (unselected - màu #9E9E9E)
     - Icon: ic_user
     - Text: "Tài khoản"
- **Styling**: 
  - Icon size: 24x24dp
  - Text size: 11sp
  - Padding: 8dp
  - Background: white với elevation 8dp

### 2. Colors (colors.xml)

Đã thêm màu mới:
```xml
<color name="orange_accent">#FF5722</color>      <!-- Icon B -->
<color name="nav_selected">#4169E1</color>       <!-- Navigation selected -->
<color name="nav_unselected">#9E9E9E</color>     <!-- Navigation unselected -->
```

### 3. Drawables

Đã tạo mới:
- **bg_orange_circle.xml**: Background circle màu cam cho icon B

### 4. MainActivity.java

Đã đơn giản hóa code:
- Removed: Các category cards (Math, Chemistry, Physics)
- Removed: Geography item
- Removed: Welcome text và points badge
- Kept: 
  - Main quiz card (playAndWinCard)
  - One recent item (biologyItem)
  - Bottom navigation (4 items)

#### Click Listeners
- **getStartedButton**: Navigate to QuizActivity
- **playAndWinCard**: Navigate to QuizActivity
- **biologyItem**: Show toast (TODO: Navigate to specific quiz)
- **navHome**: Show "Đang ở trang chủ" toast
- **navCategories**: Navigate to QuizActivity
- **navTrophy**: Show "Thống kê - Đang phát triển" toast
- **navProfile**: Show "Tài khoản - Đang phát triển" toast

## Design Specifications

### Typography
- **Title (Mock Test)**: 32sp, bold
- **Card Title**: 24sp, bold
- **Section Title**: 20sp, bold
- **Quiz Item Title**: 15sp, bold
- **Body Text**: 15sp, regular
- **Small Text**: 13sp, 12sp, 11sp (decreasing)

### Spacing
- **Screen Padding**: 20dp (horizontal), 24dp (top)
- **Card Padding**: 24dp
- **Item Padding**: 16dp
- **Card Corner Radius**: 16dp (main card), 12dp (item cards)
- **Element Margins**: 
  - Between sections: 32dp
  - Between elements: 12-20dp
  - Icon margins: 4dp

### Colors
- **Primary Blue**: #4169E1
- **Orange Accent**: #FF5722
- **Dark Text**: #1A1A1A
- **Gray Text**: #8E8E93
- **Unselected**: #9E9E9E
- **Background**: #F0F4F8
- **Card Background**: #FFFFFF

### Icons
- **Main Icons**: 24x24dp (navigation)
- **Info Icons**: 20x20dp (calendar, clock)
- **Container Icons**: 28x28dp (inside 48x48dp container)
- **Avatar**: 56x56dp

## File Changes

### Modified Files
1. `app/src/main/res/layout/activity_main.xml` - Complete redesign
2. `app/src/main/java/com/example/edura/MainActivity.java` - Simplified code
3. `app/src/main/res/values/colors.xml` - Added 3 new colors

### New Files
1. `app/src/main/res/drawable/bg_orange_circle.xml` - Orange circle background
2. `MAIN_SCREEN_REDESIGN.md` - This documentation

## UI/UX Features

✅ **Clean & Minimalist**: Removed clutter, focus on main action
✅ **Clear Hierarchy**: Bold titles, organized sections
✅ **Easy Navigation**: Bottom nav with icons + text labels
✅ **Call-to-Action**: Prominent "Bắt đầu" button
✅ **Visual Feedback**: MaterialCardView with elevation
✅ **Responsive**: ScrollView for smaller screens
✅ **Consistent Spacing**: 4dp grid system
✅ **Accessible**: Good contrast ratios, readable font sizes

## Navigation Flow

```
MainActivity (Mock Test)
├── "Bắt đầu" button → QuizActivity
├── Main Card → QuizActivity  
├── Recent Quiz Item → (TODO: Specific quiz)
└── Bottom Navigation
    ├── Trang chủ → Already here
    ├── Quizz → QuizActivity
    ├── Thống kê → (TODO: Statistics)
    └── Tài khoản → (TODO: Profile)
```

## Screenshots Reference

Màn hình được thiết kế dựa trên mockup với:
- Header: "Mock Test" + Icon "B" màu cam
- Card chính: Lesson 10 - READING với info và button
- Section "Gần đây" với quiz items
- Bottom navigation: 4 items (Trang chủ selected màu xanh)

## Testing

### Checklist
- [x] Layout matches design mockup
- [x] All click listeners work
- [x] Navigation to QuizActivity works
- [x] Bottom navigation highlights correct item
- [x] ScrollView works on small screens
- [x] Colors match specification
- [x] Icons display correctly
- [x] Text sizes are readable
- [ ] Test on different screen sizes
- [ ] Test on different Android versions

## Future Enhancements

1. **Dynamic Content**: Load quiz data from Firestore
2. **Recent Quizzes**: Show actual recent quizzes from user history
3. **Statistics Screen**: Implement statistics/leaderboard
4. **Profile Screen**: User profile with settings
5. **Quiz Details**: Navigate to specific quiz when clicking recent item
6. **Animations**: Add subtle animations for card clicks
7. **Pull-to-Refresh**: Refresh quiz list
8. **Search**: Search quizzes
9. **Filter**: Filter by category, difficulty
10. **Dark Mode**: Support dark theme

## Notes

- Background color: #F0F4F8 (light blue-gray) thay vì #F8F9FA
- Button color: #4169E1 (Royal Blue) cho consistency
- Navigation selected state: Blue (#4169E1)
- Icon "B": Có thể thay bằng user avatar thực
- Font: Sử dụng system font (Roboto)

## How to Build

1. Sync Gradle
2. Clean Project
3. Rebuild Project
4. Run on device/emulator

```bash
./gradlew clean
./gradlew build
./gradlew installDebug
```

## Dependencies

Không có dependency mới, sử dụng:
- Material Components (đã có)
- Firebase Auth (đã có)
- FirebaseFirestore (đã có)

## Compatibility

- Min SDK: 33
- Target SDK: 36
- Android Version: 13+


