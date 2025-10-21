# Quiz Screen Redesign

## Tổng quan
Đã thiết kế lại màn hình Quiz theo design mới với giao diện clean, hiện đại và tập trung vào trải nghiệm người dùng.

## Thay đổi chính

### 1. Layout (activity_quiz.xml)

#### Header
- **Title**: "Quiz" (32sp, bold, dark_text)
- **Position**: Lệch sang mép bên trái với margin left 20dp

#### Action Cards

**1. Tạo câu hỏi với AI Card**
- Icon: AI sparkles trong background xanh (#4169E1), bo góc 12dp
- Title: "Tạo câu hỏi với AI" (16sp, bold)
- Description: "Tạo câu hỏi trắc nghiệm tự động" (13sp, gray)
- Click: Hiển thị toast (TODO: Navigate to AI creator)

**2. Thêm quiz thủ công Card**
- Icon: Dấu + trong circle màu dark gray (#2D3748)
- Title: "Thêm quiz thủ công" (16sp, bold)
- Description: "Tự tạo câu hỏi trắc nghiệm" (13sp, gray)
- Click: Mở dialog tạo quiz mới

#### Quiz của bạn Section
- **Title**: "Quiz của bạn" (20sp, bold)
- **List**: RecyclerView hiển thị danh sách quiz
- **Empty State**: Icon + text "Chưa có quiz nào"

#### Quiz Item Card
- Icon container: 48x48dp, background light blue (#E8EEFF), bo góc 10dp
- Quiz icon: 28x28dp, màu xanh (#6B8AFF)
- Title: Bold, 15sp (ví dụ: "IELTS Vocabulary")
- Info: "10 câu hỏi · Hôm qua" (13sp, gray)
- Click: Navigate to QuestionsListActivity

#### Bottom Navigation
- **4 items**: Trang chủ, Quizz (selected), Thống kê, Tài khoản
- **Selected**: Quizz màu xanh (#4169E1)
- **Unselected**: Màu xám (#9E9E9E)
- **Layout**: Icon 24x24dp + Text 11sp

### 2. New Layouts

**item_quiz_simple.xml**
- MaterialCardView đơn giản
- Horizontal layout: Icon | Title + Info
- Corner radius: 12dp
- Margin bottom: 12dp

### 3. New Drawables

**bg_ai_icon.xml**
- Shape: Rectangle
- Color: #4169E1 (royal blue)
- Corner radius: 12dp

**bg_manual_icon.xml**
- Shape: Oval (circle)
- Color: #2D3748 (dark gray)

**bg_quiz_item_icon.xml**
- Shape: Rectangle
- Color: #E8EEFF (light blue)
- Corner radius: 10dp

**ic_categories.xml** (Updated)
- Quiz/checklist icon with checkmark
- Color: #4169E1

### 4. QuizActivity.java

**Simplified Structure:**
- Removed FAB button
- Removed quiz count TextView
- Added 2 action cards (AI and Manual)
- Bottom navigation with 4 items

**Click Handlers:**
- `cardAiQuiz`: Toast "Đang phát triển"
- `cardManualQuiz`: Open create quiz dialog
- `navHome`: Navigate to MainActivity
- `navCategories`: Toast "Đang ở màn hình Quiz"
- `navTrophy`: Toast "Đang phát triển"
- `navProfile`: Toast "Đang phát triển"

### 5. QuizAdapter.java

**Simplified Version:**
- Uses `item_quiz_simple.xml`
- Shows: Title + "X câu hỏi · Date"
- Click: Navigate to questions list
- Removed: Menu button, action buttons (View/Add Question)

### 6. Colors Added

```xml
<color name="dark_gray">#2D3748</color>
<color name="light_blue_bg">#E8EEFF</color>
```

## Design Specifications

### Typography
- **Screen Title**: 32sp, bold
- **Card Title**: 16sp, bold
- **Section Title**: 20sp, bold
- **Quiz Title**: 15sp, bold
- **Description**: 13sp, regular
- **Nav Text**: 11sp, regular

### Spacing
- **Screen Padding**: 20dp
- **Card Padding**: 20dp
- **Item Padding**: 16dp
- **Card Margin**: 16dp (between cards)
- **Item Margin**: 12dp (between items)

### Colors
- **Primary Blue**: #4169E1
- **Dark Gray**: #2D3748
- **Light Blue BG**: #E8EEFF
- **Icon Blue**: #6B8AFF
- **Selected Nav**: #4169E1
- **Unselected Nav**: #9E9E9E

### Icons
- **Nav Icons**: 24x24dp
- **Card Icons**: 32dp (AI sparkles), 28dp (plus)
- **Item Icons**: 28x28dp
- **Icon Container**: 56x56dp (cards), 48x48dp (items)

## Navigation Flow

```
QuizActivity
├── Card: AI Quiz → (TODO: AI Creator)
├── Card: Manual Quiz → Create Quiz Dialog → Save to Firestore
├── Quiz Items → Click → QuestionsListActivity
└── Bottom Nav
    ├── Trang chủ → MainActivity
    ├── Quizz → Current screen
    ├── Thống kê → (TODO)
    └── Tài khoản → (TODO)
```

## Features

### ✅ Implemented
- Clean modern UI design
- 2 action cards (AI & Manual)
- Quiz list from Firestore
- Real-time updates
- Empty state
- Bottom navigation (same as home)
- Create quiz dialog
- Navigate to questions list

### ⏳ TODO
- AI quiz creator screen
- Statistics screen
- Profile/Account screen
- Long press for edit/delete quiz
- Search quizzes
- Filter/Sort quizzes

## File Changes

### Modified Files
1. `app/src/main/res/layout/activity_quiz.xml` - Complete redesign
2. `app/src/main/java/com/example/edura/QuizActivity.java` - Simplified
3. `app/src/main/java/com/example/edura/adapter/QuizAdapter.java` - Simplified
4. `app/src/main/res/drawable/ic_categories.xml` - Updated icon
5. `app/src/main/res/values/colors.xml` - Added 2 colors

### New Files
1. `app/src/main/res/layout/item_quiz_simple.xml` - Simple quiz item
2. `app/src/main/res/drawable/bg_ai_icon.xml` - AI card icon bg
3. `app/src/main/res/drawable/bg_manual_icon.xml` - Manual card icon bg
4. `app/src/main/res/drawable/bg_quiz_item_icon.xml` - Quiz item icon bg
5. `QUIZ_SCREEN_REDESIGN.md` - This documentation

## UI/UX Improvements

✅ **Cleaner Design**: Removed clutter, focused on actions
✅ **Clear CTAs**: 2 prominent action cards
✅ **Consistent Navigation**: Same bottom nav as home
✅ **Better Hierarchy**: Clear sections with titles
✅ **Simplified Items**: Minimal quiz cards with essential info
✅ **Visual Consistency**: Matching colors and styles
✅ **Better Icons**: Updated icons for better representation

## Comparison: Old vs New

### Old Design
- FAB button for add quiz
- Complex quiz cards with multiple buttons
- Quiz count in header
- Menu button on each card
- Different bottom navigation

### New Design
- 2 action cards (AI + Manual)
- Simple quiz items (click to view)
- Clean header with just title
- No menu buttons (click card to view)
- Consistent bottom navigation

## Testing Checklist

- [x] Layout matches design
- [x] Navigation works
- [x] Create quiz works
- [x] Quiz list displays
- [x] Real-time updates work
- [x] Empty state shows
- [x] Bottom nav selected state
- [ ] Test on different screens
- [ ] Test with many quizzes
- [ ] Test empty state UX

## Build & Run

```bash
./gradlew clean
./gradlew build
./gradlew installDebug
```

## Dependencies

No new dependencies required. Uses existing:
- Material Components
- Firebase Firestore
- Firebase Auth
- RecyclerView

## Compatibility

- Min SDK: 33
- Target SDK: 36
- Android: 13+


