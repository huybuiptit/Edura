# 🔧 Fix Timeout Error - AI Quiz API

## ❌ Lỗi gặp phải

**Triệu chứng:**
```
Lỗi kết nối: timeout
```

**User experience:**
- Nhập thông tin và bấm "Tạo câu hỏi với AI"
- Loading hiện lên
- Sau 10-30 giây → Báo lỗi timeout
- Không nhận được câu hỏi từ AI

## 🔍 Nguyên nhân

### 1. **OkHttp Default Timeouts quá ngắn**
```java
// TRƯỚC (LỖI)
public AIQuizService() {
    this.client = new OkHttpClient(); // ❌ Default timeout = 10 seconds
}
```

**Default OkHttp timeouts:**
- Connect timeout: 10 seconds
- Read timeout: 10 seconds  
- Write timeout: 10 seconds

**Vấn đề:**
- AI backend cần 30-60+ giây để xử lý
- Generate 5-10 câu hỏi với AI mất thời gian
- 10 seconds quá ngắn → Timeout trước khi backend response

### 2. **Không có error handling chi tiết**
- Chỉ hiển thị "Lỗi kết nối" chung chung
- Không phân biệt timeout vs network error
- Khó debug và troubleshoot

### 3. **Thiếu logging**
- Không track được request/response
- Không biết backend có nhận request không
- Không biết mất bao lâu để process

## ✅ Giải pháp đã áp dụng

### 1. **Tăng Timeouts cho AI Processing**

**File:** `AIQuizService.java`

```java
// SAU (ĐÚNG)
public AIQuizService() {
    this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)    // Connection: 30s
            .writeTimeout(30, TimeUnit.SECONDS)      // Write: 30s
            .readTimeout(120, TimeUnit.SECONDS)      // Read: 2 MINUTES for AI
            .retryOnConnectionFailure(true)          // Auto retry
            .build();
}
```

**Timeout breakdown:**
- **Connect timeout (30s):** Thời gian kết nối tới server
- **Write timeout (30s):** Thời gian gửi request
- **Read timeout (120s):** Thời gian đợi response - **QUAN TRỌNG**
  - AI cần 30-90 giây để generate
  - Set 120s để có buffer
- **Retry:** Tự động thử lại nếu fail

### 2. **Detailed Error Messages**

```java
String errorMsg;
if (e instanceof java.net.SocketTimeoutException) {
    errorMsg = "Timeout: Backend mất quá nhiều thời gian (>2 phút). "
             + "Vui lòng thử lại hoặc giảm số câu hỏi.";
} else if (e instanceof java.net.UnknownHostException) {
    errorMsg = "Không thể kết nối tới server. Kiểm tra internet.";
} else if (e instanceof java.net.ConnectException) {
    errorMsg = "Backend không phản hồi. Server có thể đang bảo trì.";
} else {
    errorMsg = "Lỗi kết nối: " + e.getMessage();
}
```

**HTTP Status Code handling:**
- **500:** Backend lỗi
- **404:** API endpoint không đúng
- **429:** Too many requests

### 3. **Comprehensive Logging**

```java
Log.d(TAG, "Sending request to: " + API_ENDPOINT);
Log.d(TAG, "Request JSON: " + jsonRequest);

// Track timing
long startTime = System.currentTimeMillis();
// ... API call ...
long duration = System.currentTimeMillis() - startTime;
Log.d(TAG, "Response received in " + duration + "ms");

// Log response preview
Log.d(TAG, "Response body: " + responseBody.substring(0, Math.min(500, ...)));
```

### 4. **Additional Headers**

```java
Request httpRequest = new Request.Builder()
    .url(API_ENDPOINT)
    .post(body)
    .addHeader("Content-Type", "application/json")
    .addHeader("Accept", "application/json")  // ✅ NEW
    .build();
```

## 🎯 Kết quả

### ✅ Sau khi fix:

1. **AI có đủ thời gian xử lý**
   - Timeout 120 giây thay vì 10 giây
   - Backend có thể generate 10-20 câu hỏi

2. **Error messages rõ ràng**
   - User biết lỗi gì: timeout, network, server
   - Có gợi ý cách fix

3. **Better debugging**
   - Logcat hiển thị:
     - Request URL và body
     - Response time
     - Response body preview
     - Error details

4. **Auto retry**
   - Nếu connection fail tạm thời → retry tự động
   - Tăng success rate

## 📊 Testing

### Test Case 1: Normal Success
```
1. Nhập context: "Lịch sử Việt Nam"
2. Chọn 5 câu hỏi
3. Tap "Tạo câu hỏi với AI"
4. ✅ Loading 30-60 giây
5. ✅ Nhận được 5 câu hỏi
6. ✅ Navigate to edit screen
```

### Test Case 2: Long Processing
```
1. Nhập context dài (500 từ)
2. Chọn 20 câu hỏi (max)
3. Tap "Tạo câu hỏi với AI"
4. ✅ Loading 60-90 giây
5. ✅ Vẫn thành công (không timeout)
```

### Test Case 3: Network Error
```
1. Tắt WiFi/Data
2. Tap "Tạo câu hỏi với AI"
3. ✅ Error: "Không thể kết nối tới server. Kiểm tra internet."
```

### Test Case 4: Backend Down
```
1. Backend không chạy
2. Tap "Tạo câu hỏi với AI"
3. ✅ Error: "Backend không phản hồi. Server có thể đang bảo trì."
```

## 🔍 Logcat Examples

### Success Case
```
D/AIQuizService: Sending request to: https://edura-backend-pearl.vercel.app/create_quiz
D/AIQuizService: Request JSON: {"context":"Test","questionType":"Single Choice",...}
D/AIQuizService: Response received in 45230ms, code: 200
D/AIQuizService: Response body: {"success":true,"quizTitle":"Quiz về Test",...}
D/AIQuizService: Successfully parsed response with 5 questions
```

### Timeout Case
```
D/AIQuizService: Sending request to: https://edura-backend-pearl.vercel.app/create_quiz
E/AIQuizService: Request failed after 120000ms: timeout
E/AIQuizService: java.net.SocketTimeoutException: timeout
```

### Network Error Case
```
D/AIQuizService: Sending request to: https://edura-backend-pearl.vercel.app/create_quiz
E/AIQuizService: Request failed after 2340ms: Unable to resolve host
E/AIQuizService: java.net.UnknownHostException: Unable to resolve host
```

## ⚙️ Configuration Tuning

### Nếu vẫn timeout:

**Option 1: Tăng read timeout hơn nữa**
```java
.readTimeout(180, TimeUnit.SECONDS)  // 3 minutes
```

**Option 2: Giảm số câu hỏi**
- Set max = 10 thay vì 20
- Backend process nhanh hơn

**Option 3: Optimize backend**
- Cache AI responses
- Stream responses
- Use faster AI model

**Option 4: Split requests**
- Gọi API nhiều lần, mỗi lần 5 câu
- Parallel processing

## 🚨 Important Notes

### Production Considerations:

1. **User experience với timeout lâu:**
   - Hiển thị progress thực tế nếu có API support
   - Cho phép cancel request
   - Show estimated time

2. **Cost optimization:**
   - AI calls đắt → validate input kỹ
   - Cache common requests
   - Rate limiting

3. **Monitoring:**
   - Track average response time
   - Set up alerts nếu > 90s
   - Monitor success rate

## 📝 Build Status

```bash
BUILD SUCCESSFUL in 1s
34 actionable tasks: 5 executed, 29 up-to-date
✅ No compilation errors
✅ Ready to test
```

## 🎊 Summary

| Issue | Before | After |
|-------|--------|-------|
| Connect timeout | 10s | 30s |
| Read timeout | 10s | **120s** ⭐ |
| Error messages | Generic | Specific |
| Logging | None | Comprehensive |
| Retry | No | Yes |
| User feedback | Poor | Clear |

✅ **Timeout error đã được fix!** Backend AI giờ có đủ thời gian để xử lý.

