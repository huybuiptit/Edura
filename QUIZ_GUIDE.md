# Quiz Management Guide - Edura App

## Tổng quan

Màn hình Quiz cho phép bạn tạo, quản lý và tổ chức các bộ câu hỏi (quiz) với đầy đủ chức năng CRUD (Create, Read, Update, Delete). Tất cả dữ liệu được lưu trữ trên Cloud Firestore.

## Cấu trúc dữ liệu

### Quiz
- **quizId**: ID duy nhất của quiz (tự động tạo bởi Firestore)
- **quizTitle**: Tiêu đề quiz
- **createdBy**: ID người tạo (Firebase User ID)
- **createdAt**: Thời gian tạo (timestamp)
- **updatedAt**: Thời gian cập nhật lần cuối (timestamp)
- **questions**: Danh sách câu hỏi

### Question
- **questionId**: ID câu hỏi
- **questionText**: Nội dung câu hỏi
- **questionType**: Loại câu hỏi
  - `SINGLE_CHOICE`: Chọn 1 đáp án đúng trong 4 đáp án
  - `MULTIPLE_CHOICE`: Chọn nhiều đáp án đúng
- **answers**: Danh sách 4 đáp án

### Answer
- **answerText**: Nội dung đáp án
- **isCorrect**: Đánh dấu đáp án đúng/sai

## Cách sử dụng

### 1. Truy cập màn hình Quiz
Từ MainActivity, bạn có thể truy cập màn hình Quiz bằng các cách sau:
- Click vào nút **"Create Now"** trong card AI Quiz Generator
- Click vào card **"AI Quiz Generator"**
- Click vào icon **Home** trong bottom navigation

### 2. Tạo Quiz mới

1. Click vào nút **+ (FAB)** ở góc dưới bên phải
2. Nhập tiêu đề quiz trong dialog
3. Click **Save**

Quiz mới sẽ được tạo và hiển thị trong danh sách.

### 3. Thêm câu hỏi vào Quiz

**Cách 1**: Từ danh sách Quiz
1. Click nút **"Add Question"** trên card quiz

**Cách 2**: Từ màn hình chi tiết câu hỏi
1. Click nút **"View Questions"** trên card quiz
2. Click nút **+ (FAB)** để thêm câu hỏi mới

**Điền thông tin câu hỏi**:
1. Nhập nội dung câu hỏi
2. Chọn loại câu hỏi:
   - **Single Choice**: Chỉ có 1 đáp án đúng
   - **Multiple Choice**: Có thể có nhiều đáp án đúng
3. Nhập 4 đáp án (Answer 1, 2, 3, 4)
4. Đánh dấu checkbox **"Correct"** cho đáp án đúng
   - Với Single Choice: Chỉ chọn được 1 checkbox
   - Với Multiple Choice: Có thể chọn nhiều checkbox
5. Click **Save**

### 4. Xem danh sách câu hỏi

1. Click nút **"View Questions"** trên card quiz
2. Danh sách câu hỏi sẽ hiển thị với:
   - Số thứ tự câu hỏi
   - Nội dung câu hỏi
   - Loại câu hỏi (badge màu xanh hoặc cam)
   - Danh sách 4 đáp án (A, B, C, D)
   - Dấu ✓ màu xanh cho đáp án đúng

### 5. Sửa Quiz

1. Click vào icon **menu (⋮)** trên card quiz
2. Chọn **"Edit"**
3. Sửa tiêu đề quiz
4. Click **Save**

**Hoặc** từ màn hình chi tiết câu hỏi:
1. Click icon **Edit (✏️)** ở góc trên bên phải
2. Sửa tiêu đề quiz
3. Click **Save**

### 6. Xóa Quiz

1. Click vào icon **menu (⋮)** trên card quiz
2. Chọn **"Delete"**
3. Xác nhận xóa trong dialog

⚠️ **Lưu ý**: Xóa quiz sẽ xóa tất cả câu hỏi trong quiz đó.

### 7. Xóa câu hỏi

1. Vào màn hình chi tiết câu hỏi
2. Click icon **Delete (🗑️)** trên card câu hỏi muốn xóa
3. Xác nhận xóa trong dialog

## Tính năng

### ✅ CRUD Quiz
- ✅ Tạo quiz mới
- ✅ Xem danh sách quiz
- ✅ Sửa tiêu đề quiz
- ✅ Xóa quiz

### ✅ CRUD Question
- ✅ Tạo câu hỏi mới
- ✅ Xem danh sách câu hỏi
- ✅ Xóa câu hỏi
- ⏳ Sửa câu hỏi (có thể thêm trong tương lai)

### ✅ Validation
- ✅ Kiểm tra tiêu đề quiz không được để trống
- ✅ Kiểm tra nội dung câu hỏi không được để trống
- ✅ Kiểm tra tất cả 4 đáp án phải được điền
- ✅ Kiểm tra phải có ít nhất 1 đáp án đúng

### ✅ UI/UX
- ✅ Material Design
- ✅ Bottom Navigation
- ✅ Floating Action Button (FAB)
- ✅ RecyclerView với LinearLayoutManager
- ✅ Empty State khi không có dữ liệu
- ✅ Popup Menu cho các action
- ✅ Dialog cho create/edit
- ✅ Toast messages cho feedback
- ✅ Confirmation dialog cho delete

### ✅ Firebase Integration
- ✅ Cloud Firestore để lưu trữ dữ liệu
- ✅ Real-time updates với addSnapshotListener
- ✅ User-specific data (chỉ hiển thị quiz của user hiện tại)

## Cấu trúc Firestore

```
quizzes (collection)
  └── {quizId} (document)
      ├── quizTitle: string
      ├── createdBy: string (userId)
      ├── createdAt: timestamp
      ├── updatedAt: timestamp
      └── questions: array
          └── [0] (object)
              ├── questionId: string
              ├── questionText: string
              ├── questionType: string ("SINGLE_CHOICE" | "MULTIPLE_CHOICE")
              └── answers: array
                  └── [0] (object)
                      ├── answerText: string
                      └── isCorrect: boolean
```

## Files đã tạo

### Model Classes
- `app/src/main/java/com/example/edura/model/Answer.java`
- `app/src/main/java/com/example/edura/model/Question.java`
- `app/src/main/java/com/example/edura/model/Quiz.java`

### Activities
- `app/src/main/java/com/example/edura/QuizActivity.java`
- `app/src/main/java/com/example/edura/QuestionsListActivity.java`

### Adapters
- `app/src/main/java/com/example/edura/adapter/QuizAdapter.java`
- `app/src/main/java/com/example/edura/adapter/QuestionAdapter.java`

### Layouts
- `app/src/main/res/layout/activity_quiz.xml`
- `app/src/main/res/layout/activity_questions_list.xml`
- `app/src/main/res/layout/item_quiz.xml`
- `app/src/main/res/layout/item_question.xml`
- `app/src/main/res/layout/item_answer.xml`
- `app/src/main/res/layout/dialog_create_quiz.xml`
- `app/src/main/res/layout/dialog_create_question.xml`

### Menus
- `app/src/main/res/menu/bottom_nav_menu.xml`
- `app/src/main/res/menu/quiz_menu.xml`

## Lưu ý kỹ thuật

1. **Java Version**: Project sử dụng Java 11, hỗ trợ Stream API
2. **Firebase**: Đảm bảo đã cấu hình Firebase và thêm google-services.json
3. **Internet Permission**: Đã có trong AndroidManifest.xml
4. **Dependencies**: Đã thêm Firestore, CoordinatorLayout, RecyclerView

## Troubleshooting

### Không thấy quiz của mình?
- Kiểm tra đã đăng nhập chưa
- Quiz chỉ hiển thị với user đã tạo nó (theo createdBy)

### Lỗi khi lưu quiz?
- Kiểm tra kết nối internet
- Kiểm tra Firebase configuration
- Xem Logcat để biết chi tiết lỗi

### Không thể thêm câu hỏi?
- Đảm bảo điền đầy đủ thông tin
- Phải có ít nhất 1 đáp án đúng
- Tất cả 4 đáp án phải được điền

## Tương lai

Các tính năng có thể mở rộng:
- ⏳ Edit question (sửa câu hỏi)
- ⏳ Reorder questions (sắp xếp lại câu hỏi)
- ⏳ Duplicate quiz/question
- ⏳ Share quiz with others
- ⏳ Quiz categories/tags
- ⏳ Quiz statistics
- ⏳ Take quiz (làm bài quiz)
- ⏳ Score calculation
- ⏳ Quiz history

## Hỗ trợ

Nếu gặp vấn đề, vui lòng kiểm tra:
1. Firebase Console để xem dữ liệu
2. Logcat để xem error messages
3. Network connection
4. Firebase Authentication status


