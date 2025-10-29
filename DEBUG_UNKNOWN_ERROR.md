# 🔍 Debug "Lỗi Không xác định"

## ❌ Lỗi

**Message:**
```
Lỗi không xác định
```

**Nguyên nhân phổ biến:**
1. Backend trả về `success: false` nhưng không có error message
2. Backend trả về format JSON không đúng
3. Backend không trả về câu hỏi nào
4. Backend lỗi nhưng vẫn return status 200

## ✅ Đã thêm Enhanced Logging & Error Handling

### 1. **Full Response Logging**

Giờ sẽ log toàn bộ response từ backend:

```java
Log.d(TAG, "=== FULL RESPONSE BODY ===");
Log.d(TAG, responseBody);
Log.d(TAG, "========================");
```

### 2. **Detailed Error Detection**

Các case được handle:

#### Case 1: Backend trả về success:false không có message
```java
if (!aiResponse.isSuccess()) {
    String error = aiResponse.getError();
    if (error == null || error.trim().isEmpty()) {
        error = "Backend báo lỗi nhưng không có thông báo chi tiết. " +
               "Response: " + responseBody.substring(0, 200);
    }
}
```

#### Case 2: Success:true nhưng không có questions
```java
if (aiResponse.getQuestions() == null || aiResponse.getQuestions().isEmpty()) {
    callback.onError("Backend không trả về câu hỏi nào. " +
                    "Vui lòng thử với context khác hoặc giảm số câu hỏi.");
}
```

#### Case 3: Parse error
```java
catch (Exception e) {
    callback.onError("Lỗi parse dữ liệu: " + e.getMessage() + 
                    ". Backend có thể trả về format không đúng.");
}
```

### 3. **Client-side Validation**

Trong `CreateAIQuizActivity`:

```java
// Validate response has questions
if (response.getQuestions() == null || response.getQuestions().isEmpty()) {
    Toast.makeText(this, 
        "Lỗi: Backend không trả về câu hỏi nào. Vui lòng thử lại.", 
        Toast.LENGTH_LONG).show();
    return;
}
```

## 🔍 Cách Debug

### Bước 1: Mở Logcat

**Android Studio:**
1. Bottom bar → Click **Logcat**
2. Filter by: `AIQuizService` hoặc `CreateAIQuiz`

**Command line:**
```bash
adb logcat | grep -E "AIQuizService|CreateAIQuiz"
```

### Bước 2: Thử lại request

1. Mở app
2. Vào "Tạo với AI"
3. Nhập context và tap "Tạo câu hỏi"
4. Xem Logcat

### Bước 3: Phân tích logs

#### Example 1: Backend không trả về câu hỏi

```
D/AIQuizService: Sending request to: https://...
D/AIQuizService: Request JSON: {"context":"Test",...}
D/AIQuizService: Response received in 35000ms, code: 200
D/AIQuizService: === FULL RESPONSE BODY ===
D/AIQuizService: {"success":true,"quizTitle":"Quiz","questions":[]}
D/AIQuizService: ========================
D/AIQuizService: Response parsed - Success: true
D/AIQuizService: Response questions count: 0
E/AIQuizService: Response has success:true but no questions!
```

**Vấn đề:** Backend trả về success nhưng `questions: []` rỗng

**Giải pháp:**
- Kiểm tra context có hợp lệ không (quá ngắn, không liên quan)
- Giảm số câu hỏi từ 20 → 5
- Thử context khác cụ thể hơn
- Check backend logs

#### Example 2: Backend trả về lỗi không có message

```
D/AIQuizService: === FULL RESPONSE BODY ===
D/AIQuizService: {"success":false}
D/AIQuizService: ========================
D/AIQuizService: Response parsed - Success: false
D/AIQuizService: Response error: null
E/AIQuizService: API returned error: Backend báo lỗi nhưng không có thông báo...
```

**Vấn đề:** Backend có lỗi nhưng không trả về error message

**Giải pháp:**
- Backend cần fix để trả về error message rõ ràng
- Tạm thời: thử với input đơn giản hơn

#### Example 3: Format không đúng

```
D/AIQuizService: === FULL RESPONSE BODY ===
D/AIQuizService: {"data":{"quiz":[...]}}
D/AIQuizService: ========================
D/AIQuizService: Response parsed - Success: false
D/AIQuizService: Response questions count: null
E/AIQuizService: Error parsing response
```

**Vấn đề:** Backend trả về structure khác với expected

**Expected format:**
```json
{
  "success": true,
  "quizTitle": "...",
  "questions": [
    {
      "questionText": "...",
      "questionType": "...",
      "answers": [...]
    }
  ]
}
```

**Giải pháp:**
- Backend cần follow đúng format
- Hoặc cập nhật AIQuizzRespond model

## 🛠️ Các lỗi phổ biến và cách fix

### 1. Context quá ngắn/không rõ ràng

**Lỗi:**
```
Backend không trả về câu hỏi nào
```

**Fix:**
```
❌ BAD:  "test"
✅ GOOD: "Lịch sử chiến tranh thế giới thứ 2, các sự kiện chính, nguyên nhân và hậu quả"

❌ BAD:  "abc xyz"
✅ GOOD: "Định luật Newton trong vật lý, bao gồm định luật 1, 2, 3 và ứng dụng"
```

### 2. Số câu hỏi quá nhiều

**Lỗi:**
```
Timeout hoặc không đủ câu hỏi
```

**Fix:**
- Giảm từ 20 → 10 câu
- Hoặc 10 → 5 câu
- Context dài hơn = cần ít câu hơn

### 3. Backend lỗi 500

**Lỗi:**
```
Backend lỗi (500). Backend có thể đang gặp vấn đề.
```

**Fix:**
- Đợi backend fix
- Thử lại sau vài phút
- Check backend logs
- Liên hệ backend team

### 4. Ngôn ngữ không match content

**Problem:**
- Context tiếng Việt nhưng chọn language = English
- AI confused và không tạo được câu hỏi

**Fix:**
- Context tiếng Việt → Language = "Tiếng Việt"
- Context tiếng Anh → Language = "English"

## 📊 Testing Checklist

### ✅ Test Cases thành công:

**Test 1: Context đơn giản**
```
Context: "Lịch sử Việt Nam"
Số câu: 5
Ngôn ngữ: Tiếng Việt
Độ khó: Dễ
→ Success with 5 questions
```

**Test 2: Context chi tiết**
```
Context: "Chiến tranh thế giới thứ 2 bắt đầu năm 1939 và kết thúc năm 1945. 
         Các nước tham chiến chính là Đức, Nhật, Ý phía Trục, và Anh, Mỹ, Liên Xô phía Đồng minh."
Số câu: 10
→ Success with 10 questions
```

**Test 3: Nhiều câu hỏi**
```
Context: Long detailed context about physics (500 words)
Số câu: 20
→ Success with 20 questions (may take 60-90s)
```

## 🚨 When to Contact Backend Team

Report to backend team if you see:

1. **Consistent empty questions:**
   ```
   Response has success:true but no questions!
   ```
   With valid context → Backend issue

2. **Format errors:**
   ```
   Response structure doesn't match expected format
   ```
   Backend changed API without notice

3. **500 errors frequently:**
   ```
   Backend lỗi (500) multiple times
   ```
   Server stability issue

4. **Slow response (>2 min):**
   ```
   Response received in 150000ms
   ```
   Backend performance issue

## 📝 Logs to Share

Khi báo bug, share những logs này:

```
1. Request JSON (from CreateAIQuiz)
   - Context
   - Language
   - Difficulty
   - Question count
   - Question type

2. Full response body (from AIQuizService)

3. Error message

4. Timestamp and duration
```

## ✨ Enhanced Error Messages

Bây giờ user sẽ thấy:

❌ **Trước:**
```
Lỗi không xác định
```

✅ **Sau:**
```
Backend không trả về câu hỏi nào. Vui lòng thử với context khác hoặc giảm số câu hỏi.

- hoặc -

Backend báo lỗi nhưng không có thông báo chi tiết. Response: {"success":false,...}

- hoặc -

Lỗi parse dữ liệu: Expected BEGIN_ARRAY but was BEGIN_OBJECT. 
Backend có thể trả về format không đúng.
```

## 🎯 Next Steps

1. ✅ Build app với logging mới
2. ✅ Test với các context khác nhau
3. ✅ Check Logcat khi gặp lỗi
4. ✅ Share logs với backend team nếu cần
5. ✅ Adjust input based on error messages

---

Build thành công! Giờ mỗi lỗi sẽ có thông báo cụ thể và logs chi tiết để debug.

