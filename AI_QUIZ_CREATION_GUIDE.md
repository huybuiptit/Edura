# Hướng dẫn tính năng Tạo Quiz với AI

## ✅ HOÀN THÀNH - Tích hợp API Backend

## Tổng quan
Tính năng "Tạo Quiz với AI" cho phép người dùng tạo câu hỏi tự động bằng cách cung cấp context hoặc topic cho AI xử lý. 

**API Endpoint**: `https://edura-backend-pearl.vercel.app/create_quiz`

## Các file đã tạo/sửa đổi

### 1. Activities
- **CreateAIQuizActivity.java** - Activity cho form nhập thông tin và gọi AI
- **CreateQuizActivity.java** (modified) - Hỗ trợ AI mode để edit và lưu quiz

### 2. Models
- **AIQuizzRequest.java** - Request model (đã có sẵn, đã validation)
- **AIQuizzRespond.java** - Response model với conversion logic
- **Quiz.java**, **Question.java**, **Answer.java** - Models hiện có (không đổi)

### 3. Services
- **AIQuizService.java** (NEW) - Service call API backend với OkHttp

### 4. Layout
- **activity_create_ai_quiz.xml** - UI form tạo quiz với AI

### 5. Drawable Resources
- `ic_back.xml` - Icon nút quay lại
- `ic_add.xml` - Icon tăng số lượng
- `ic_remove.xml` - Icon giảm số lượng
- `bg_rounded_white.xml` - Background trắng bo góc

### 6. Dependencies (build.gradle.kts)
- OkHttp 4.12.0 - HTTP client
- Gson 2.10.1 - JSON parsing

## Tính năng

### 1. Header
- Nút quay lại ở góc trên bên trái
- Tiêu đề "Tạo Quiz với AI"

### 2. Nhập Context/Topic
- Ô nhập text nhiều dòng
- Hỗ trợ tối đa 2000 từ
- Placeholder gợi ý ví dụ

### 3. Số lượng câu hỏi
- Nút tăng/giảm
- Giới hạn: 1-20 câu hỏi
- Mặc định: 5 câu hỏi

### 4. Chọn ngôn ngữ
- Dropdown menu
- Các tùy chọn:
  - Tiếng Việt (mặc định)
  - English
  - 中文 (Chinese)
  - 日本語 (Japanese)
  - 한국어 (Korean)

### 5. Độ khó
- 3 nút lựa chọn:
  - Dễ (Easy)
  - Trung bình (Medium)
  - Khó (Difficult)
- Mặc định: Dễ
- UI highlight nút được chọn

### 6. Loại câu hỏi
- Dropdown menu
- Các tùy chọn:
  - Trắc nghiệm 1 đáp án (Single Choice)
  - Trắc nghiệm nhiều đáp án (Multiple Choice)
  - Điền vào chỗ trống (Fill in Blank)

### 7. Loading Animation
- Overlay màn hình tối khi đang xử lý
- Progress bar kiểu AI chat
- Text động "AI đang tạo câu hỏi..."
- Hiển thị trong 3 giây (mô phỏng)

## Validation

### AIQuizzRequest Model
Đã có sẵn validation trong model `AIQuizzRequest.java`:

```java
- Context không được rỗng
- Context tối đa 2000 từ
- Phải chọn loại câu hỏi
- Phải chọn ngôn ngữ
- Phải chọn độ khó
- Số câu hỏi: 1-20
```

## Cách sử dụng

1. **Từ QuizFragment**, tap vào card "Tạo với AI"
2. **Nhập context/topic** - Nội dung hoặc chủ đề để AI tạo câu hỏi
3. **Chọn số lượng câu hỏi** - Từ 1 đến 20 câu (dùng nút +/-)
4. **Chọn ngôn ngữ** - Tiếng Việt, English, 中文, 日本語, 한국어
5. **Chọn độ khó** - Dễ / Trung bình / Khó
6. **Chọn loại câu hỏi** - Trắc nghiệm 1 đáp án / Nhiều đáp án / Điền chỗ trống
7. **Tap "Tạo câu hỏi với AI"**
8. **Đợi AI xử lý** - Loading overlay hiển thị
9. **Màn hình edit quiz** - Xem và chỉnh sửa câu hỏi AI đã tạo
10. **Chỉnh sửa nếu cần** - Sửa câu hỏi, đáp án, thêm/xóa câu hỏi
11. **Tap "Lưu Quiz"** - Lưu vào Firestore
12. **Hoàn thành!** - Quiz có thể sử dụng như quiz thông thường

## ✅ Đã hoàn thành

### 1. Tích hợp AI API ✅
- ✅ Thêm OkHttp và Gson dependencies
- ✅ Tạo AIQuizService để call API endpoint
- ✅ Xử lý request/response bất đồng bộ
- ✅ Error handling và validation

### 2. Model AIQuizzRespond ✅
- ✅ Parse JSON response từ backend
- ✅ Convert sang định dạng Quiz/Question/Answer của app
- ✅ Hỗ trợ các loại câu hỏi: Single Choice, Multiple Choice, Fill in Blank

### 3. Màn hình Edit Quiz ✅
- ✅ Sử dụng CreateQuizActivity hiện có
- ✅ Load câu hỏi AI đã tạo vào UI
- ✅ Cho phép chỉnh sửa câu hỏi, đáp án
- ✅ Thêm/xóa câu hỏi
- ✅ Nút "Lưu Quiz" thay vì "Tạo Quiz"
- ✅ Lưu vào Firestore

### 4. Loading UX ✅
- ✅ Loading overlay với progress bar
- ✅ Disable buttons khi đang xử lý
- ✅ Toast messages cho success/error

## TODO - Cải tiến trong tương lai

### 1. Cải thiện UX
- Thêm animation mượt hơn cho loading
- Typing effect cho loading text
- Progress bar hiển thị phần trăm
- Retry button nếu API fails
- Cho phép cancel request

### 2. Offline Support
- Cache requests khi offline
- Queue để gửi khi có mạng
- Show thông báo phù hợp

### 3. Advanced Features
- Preview câu hỏi trước khi lưu
- Regenerate từng câu hỏi cụ thể
- Save as draft
- History của AI generations

## Cấu trúc Code

### CreateAIQuizActivity.java
```java
- initViews() - Khởi tạo các view
- setupLanguageSpinner() - Cấu hình dropdown ngôn ngữ
- setupQuestionTypeSpinner() - Cấu hình dropdown loại câu hỏi
- setupDifficultyButtons() - Xử lý chọn độ khó
- updateDifficultyButtons() - Cập nhật UI độ khó
- generateQuiz() - Call API backend, xử lý response
- showLoading() - Hiển thị loading overlay
- hideLoading() - Ẩn loading overlay
```

### AIQuizService.java
```java
- generateQuiz(AIQuizzRequest, callback) - Async API call với OkHttp
  - Tạo HTTP POST request với JSON body
  - Parse response thành AIQuizzRespond
  - Call callback.onSuccess() hoặc callback.onError()
```

### AIQuizzRespond.java
```java
- AIQuestion (inner class) - Parse câu hỏi từ API
- AIAnswer (inner class) - Parse đáp án từ API
- toQuiz(userId) - Convert response sang Quiz model
- toQuestion() - Convert AI question sang app Question
```

### CreateQuizActivity.java (Modified)
```java
- loadAIGeneratedQuiz() - Load quiz data từ Intent
- saveAIGeneratedQuiz() - Lưu quiz vào Firestore
- Hỗ trợ 3 modes:
  1. Create new (manual)
  2. Edit existing
  3. AI generated (new)
```

### Navigation Flow
```
QuizFragment (cardAiQuiz) 
  → CreateAIQuizActivity 
    → [Call API Backend]
      → AIQuizService.generateQuiz()
        → Backend returns AIQuizzRespond
          → Convert to Quiz model
            → CreateQuizActivity (AI mode)
              → User edits if needed
                → Tap "Lưu Quiz"
                  → Save to Firestore
                    → QuizFragment (updated)
```

## API Integration Details

### Request Format
```json
POST https://edura-backend-pearl.vercel.app/create_quiz
Content-Type: application/json

{
  "context": "Lịch sử chiến tranh thế giới thứ 2",
  "questionType": "Single Choice",
  "language": "Tiếng Việt",
  "difficulty": "Medium",
  "numberOfQuestions": 5
}
```

### Expected Response Format
```json
{
  "success": true,
  "quizTitle": "Quiz về Chiến tranh thế giới thứ 2",
  "questions": [
    {
      "questionText": "Chiến tranh thế giới thứ 2 bắt đầu năm nào?",
      "questionType": "Single Choice",
      "answers": [
        {"answerText": "1939", "correct": true},
        {"answerText": "1940", "correct": false},
        {"answerText": "1941", "correct": false},
        {"answerText": "1942", "correct": false}
      ]
    }
  ]
}
```

### Error Response
```json
{
  "success": false,
  "error": "Thông báo lỗi từ backend"
}
```

## Dependencies
Các dependencies đã có sẵn trong project:
- Material Components (cho TextInputLayout, MaterialButton)
- Firebase Firestore (để lưu quiz)
- Firebase Auth (để xác thực user)

## Testing
1. Build project: `./gradlew assembleDebug`
2. Chạy app
3. Login
4. Vào tab Quiz
5. Tap "Tạo với AI"
6. Test các tính năng:
   - Nhập context
   - Tăng/giảm số câu hỏi
   - Chọn ngôn ngữ
   - Chọn độ khó
   - Chọn loại câu hỏi
   - Tap "Tạo câu hỏi"
   - Xem loading animation

## Testing Checklist

### ✅ Kiểm tra trước khi release
- [ ] Test với context ngắn (vài từ)
- [ ] Test với context dài (gần 2000 từ)
- [ ] Test với các ngôn ngữ khác nhau
- [ ] Test với các mức độ khó khác nhau
- [ ] Test với các loại câu hỏi khác nhau
- [ ] Test với số lượng câu hỏi min (1) và max (20)
- [ ] Test mất kết nối internet
- [ ] Test khi backend trả về lỗi
- [ ] Test chỉnh sửa câu hỏi AI đã tạo
- [ ] Test lưu quiz sau khi chỉnh sửa
- [ ] Test xóa/thêm câu hỏi trong AI quiz
- [ ] Test đổi loại câu hỏi (single → multiple)

## Notes
- ✅ Build thành công
- ✅ UI responsive và đẹp mắt
- ✅ Validation hoàn chỉnh
- ✅ API integration hoàn tất
- ✅ Edit và save quiz từ AI
- ✅ Quiz AI có thể dùng như quiz thông thường
- ✅ Error handling đầy đủ
- ⚠️ Cần test với backend thật để verify format

## Troubleshooting

### Lỗi "Lỗi kết nối"
- Kiểm tra internet connection
- Kiểm tra backend có đang chạy không
- Check firewall settings

### Lỗi "Không thể parse response"
- Backend trả về format khác với expected
- Check console logs để xem raw response
- Có thể cần điều chỉnh AIQuizzRespond model

### Quiz không lưu được
- Check Firebase permissions
- Verify user đã đăng nhập
- Check Firestore rules

### Loading không kết thúc
- Backend timeout hoặc không response
- Network issue
- Check API endpoint URL

