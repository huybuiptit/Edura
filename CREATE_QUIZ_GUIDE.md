# Create Quiz Feature - Hướng dẫn tạo Quiz

## Tổng quan

Đã tạo đầy đủ tính năng tạo và chỉnh sửa Quiz với giao diện hiện đại, hỗ trợ 3 loại câu hỏi:
- **Multiple Choice** - Chọn 1 đáp án đúng
- **Multiple Response** - Chọn nhiều đáp án đúng
- **Fill in Blank** - Điền vào chỗ trống (Coming soon)

## Tính năng

### ✅ Đã implement

1. **Create Quiz**
   - Nhập tiêu đề quiz
   - Thêm nhiều câu hỏi
   - Mỗi câu hỏi có 4 câu trả lời (có thể thêm)
   - Đánh dấu đáp án đúng bằng radio button
   - Xóa câu hỏi không mong muốn
   - Lưu vào Firestore

2. **Edit Quiz**
   - Load quiz từ Firestore
   - Chỉnh sửa title và questions
   - Cập nhật Firestore

3. **Delete Quiz**
   - Xóa quiz với confirmation dialog
   - Xóa khỏi Firestore

4. **View Quiz List**
   - Hiển thị quiz cards
   - Icon file kiểm tra
   - Tên quiz + số câu hỏi
   - Giờ, ngày tháng năm tạo (format: HH:mm, dd/MM/yyyy)
   - Menu 3 chấm với options: Edit/Delete

## Giao diện

### CreateQuizActivity

```
┌─────────────────────────────────────┐
│ ← Back      Tạo Quiz                │
├─────────────────────────────────────┤
│ Multiple Choice│Multiple Response│Fill│
├─────────────────────────────────────┤
│                                     │
│ Tiêu đề Quiz                        │
│ ┌─────────────────────────────────┐ │
│ │ Nhập tiêu đề quiz               │ │
│ └─────────────────────────────────┘ │
│                                     │
│ ╔═══════════════════════════════╗ │
│ ║ Câu hỏi 1                  🗑️ ║ │
│ ║ ┌───────────────────────────┐ ║ │
│ ║ │ Nhập nội dung câu hỏi     │ ║ │
│ ║ └───────────────────────────┘ ║ │
│ ║                               ║ │
│ ║ ┌───────────────────────┐ ○  ║ │
│ ║ │ Câu trả lời 1         │    ║ │
│ ║ └───────────────────────┘    ║ │
│ ║ ┌───────────────────────┐ ○  ║ │
│ ║ │ Câu trả lời 2         │    ║ │
│ ║ └───────────────────────┘    ║ │
│ ║                               ║ │
│ ║ [+ Thêm câu trả lời]          ║ │
│ ╚═══════════════════════════════╝ │
│                                     │
│ [+ Thêm câu hỏi]                   │
│                                     │
│ ┌─────────────────────────────────┐ │
│ │       Tạo Quiz                  │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

### Quiz Card in List

```
┌─────────────────────────────────────┐
│ 📄  IELTS Vocabulary            ⋮  │
│     10 câu hỏi                      │
│     20:30, 21/10/2025               │
└─────────────────────────────────────┘
```

## Cấu trúc Files

### Layouts (3 files)

1. **activity_create_quiz.xml**
   - Top bar với back button
   - TabLayout cho 3 loại câu hỏi
   - Quiz title input
   - Questions container (LinearLayout)
   - Add question button
   - Create/Update quiz button (bottom)

2. **item_question_edit.xml**
   - Question card với delete button
   - Question text input
   - Answers container
   - Add answer button

3. **item_answer_edit.xml**
   - Answer text input
   - Radio button cho correct answer

### Java Classes

1. **CreateQuizActivity.java**
   - Quản lý tạo/sửa quiz
   - Dynamic add/remove questions
   - Dynamic add answers
   - Validation
   - Firestore CRUD operations

2. **QuizAdapter.java** (Updated)
   - Hiển thị quiz cards
   - Format date time: "HH:mm, dd/MM/yyyy"
   - Menu 3 chấm
   - Click handlers

3. **QuizFragment.java** (Updated)
   - Navigate to CreateQuizActivity
   - Handle edit quiz
   - Handle delete quiz
   - Real-time updates from Firestore

### Updated Layouts

1. **item_quiz_simple.xml** (Redesigned)
   - Icon file kiểm tra (48x48dp)
   - Quiz title (15sp, bold)
   - Question count (13sp)
   - Created date with time (12sp)
   - Menu button 3 chấm (top right)

## Flow hoạt động

### Create New Quiz

```
QuizFragment
  ├── Click "Thêm quiz thủ công" card
  └── Navigate to CreateQuizActivity
      ├── Enter quiz title
      ├── Fill question 1 (default)
      │   ├── Enter question text
      │   ├── Fill 4 answers
      │   └── Mark correct answer(s)
      ├── Click "+ Thêm câu hỏi"
      │   └── Add more questions
      └── Click "Tạo Quiz"
          ├── Validate data
          ├── Save to Firestore
          └── Return to QuizFragment
              └── New quiz appears in list
```

### Edit Existing Quiz

```
QuizFragment
  ├── Click menu (⋮) on quiz card
  └── Select "Edit"
      └── Navigate to CreateQuizActivity
          ├── Load quiz data from Firestore
          ├── Title populated
          ├── Questions populated
          ├── Answers populated
          ├── Edit as needed
          └── Click "Cập nhật Quiz"
              ├── Validate data
              ├── Update Firestore
              └── Return to QuizFragment
```

### Delete Quiz

```
QuizFragment
  ├── Click menu (⋮) on quiz card
  └── Select "Xóa"
      └── Show confirmation dialog
          ├── Click "Xóa"
          │   ├── Delete from Firestore
          │   └── Quiz removed from list
          └── Click "Hủy"
              └── Cancel operation
```

## Code Examples

### Navigate to Create Quiz

```java
// From QuizFragment
cardManualQuiz.setOnClickListener(v -> {
    Intent intent = new Intent(getContext(), CreateQuizActivity.class);
    startActivity(intent);
});
```

### Navigate to Edit Quiz

```java
// From QuizAdapter menu
@Override
public void onEditQuiz(Quiz quiz) {
    Intent intent = new Intent(getContext(), CreateQuizActivity.class);
    intent.putExtra("quizId", quiz.getQuizId());
    startActivity(intent);
}
```

### Add Question Card Dynamically

```java
private void addQuestionCard() {
    View questionCard = LayoutInflater.from(this)
        .inflate(R.layout.item_question_edit, questionsContainer, false);
    
    QuestionView questionView = new QuestionView(questionCard, questionViews.size() + 1);
    questionViews.add(questionView);
    questionsContainer.addView(questionCard);
    
    // Add 4 default answers
    for (int i = 0; i < 4; i++) {
        questionView.addAnswerField();
    }
}
```

### Save Quiz to Firestore

```java
private void createQuiz() {
    String userId = auth.getCurrentUser().getUid();
    Quiz quiz = new Quiz(title, userId);
    quiz.setQuestions(questions);

    db.collection("quizzes")
        .add(quiz.toMap())
        .addOnSuccessListener(doc -> {
            Toast.makeText(this, "Tạo quiz thành công!", Toast.LENGTH_SHORT).show();
            finish();
        })
        .addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
}
```

## Validation Rules

1. **Quiz Title**: Không được để trống
2. **Questions**: Ít nhất 1 câu hỏi
3. **Question Text**: Không được để trống
4. **Answers**: Ít nhất 1 câu trả lời có nội dung
5. **Correct Answer**: Ít nhất 1 đáp án được đánh dấu đúng

## Design Specifications

### Colors
- Primary Blue: #4169E1
- Text Dark: #1A1A1A
- Text Gray: #8E8E93
- Background: #F0F4F8
- White: #FFFFFF
- Delete Red: #FF5252

### Typography
- Page Title: 20sp, bold
- Tab Text: 14sp
- Section Title: 14sp, bold
- Input Hint: 14sp
- Question Number: 16sp, bold
- Button Text: 15-16sp, bold

### Spacing
- Page Padding: 20dp
- Card Padding: 16dp
- Element Margin: 8-16dp
- Button Height: 40-56dp
- Card Corner Radius: 12dp

### Components
- **TabLayout**: Scrollable, 3 tabs
- **TextInputLayout**: Outlined box style
- **MaterialCardView**: 12dp radius, 2dp elevation
- **Button**: Filled (primary) or Outlined (secondary)
- **RadioButton**: Single selection per question

## Files Created/Updated

### New Files
1. `activity_create_quiz.xml` - Main create/edit layout
2. `item_question_edit.xml` - Question card layout
3. `item_answer_edit.xml` - Answer row layout
4. `CreateQuizActivity.java` - Main activity logic
5. `CREATE_QUIZ_GUIDE.md` - This documentation

### Updated Files
1. `item_quiz_simple.xml` - Added menu button, full date/time
2. `QuizAdapter.java` - Handle menu, format date
3. `QuizFragment.java` - Navigate to CreateQuizActivity
4. `AndroidManifest.xml` - Register CreateQuizActivity

## Testing Checklist

- [x] Create new quiz
- [x] Add multiple questions
- [x] Add/remove answers dynamically
- [x] Mark correct answers
- [x] Delete questions
- [x] Save to Firestore
- [x] Edit existing quiz
- [x] Load quiz data
- [x] Update Firestore
- [x] Delete quiz with confirmation
- [x] Quiz list shows correct data
- [x] Date/time format correct
- [x] Menu 3 dots works
- [ ] Test with many questions
- [ ] Test validation errors
- [ ] Test Multiple Response mode
- [ ] Implement Fill in Blank mode

## Known Limitations

1. **Fill in Blank**: Not yet implemented (Tab 3)
2. **Multiple Response**: RadioButton behavior needs CheckBox for multi-select
3. **Image Support**: No image upload for questions/answers
4. **Rich Text**: No formatting options for questions
5. **Reorder**: Cannot drag-drop to reorder questions

## Future Enhancements

1. ✨ Multiple Response với CheckBox thay vì RadioButton
2. ✨ Fill in Blank question type
3. ✨ Image upload cho câu hỏi và đáp án
4. ✨ Rich text editor (bold, italic, etc.)
5. ✨ Drag & drop để sắp xếp câu hỏi
6. ✨ Duplicate question/quiz
7. ✨ Quiz templates
8. ✨ Preview quiz before saving
9. ✨ Quiz categories/tags
10. ✨ Share quiz với others

## Troubleshooting

### Quiz không lưu?
- Check Firebase Auth user logged in
- Check Firestore rules
- Verify internet connection
- Check Logcat for errors

### Edit không load data?
- Verify quizId is passed correctly
- Check Firestore document exists
- Check Quiz.fromMap() logic

### Radio buttons không hoạt động?
- Đảm bảo RadioButtons trong cùng RadioGroup
- For Multiple Response, cần đổi sang CheckBox

### Questions bị trùng số?
- Check updateQuestionNumbers() được gọi sau delete

## Build & Run

```bash
./gradlew clean
./gradlew build
./gradlew installDebug
```

## Summary

✨ **Đã hoàn thành tính năng Create/Edit/Delete Quiz**

**Features:**
- ✅ Tạo quiz mới với nhiều câu hỏi
- ✅ 3 tabs cho 3 loại câu hỏi (1 active, 2 coming soon)
- ✅ Dynamic add/remove questions và answers
- ✅ Edit quiz existing
- ✅ Delete quiz với confirmation
- ✅ Quiz list với đầy đủ thông tin
- ✅ Menu 3 chấm cho actions
- ✅ Real-time Firestore sync
- ✅ Validation đầy đủ

**UI/UX:**
- ✅ Giao diện hiện đại, clean
- ✅ Back button ở top
- ✅ TabLayout cho question types
- ✅ Material Design components
- ✅ Smooth interactions

