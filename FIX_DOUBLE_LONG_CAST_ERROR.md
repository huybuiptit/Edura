# 🔧 Fix "java.lang.Double cannot be cast to java.lang.Long" Error

## ❌ Lỗi gặp phải:

```
java.lang.Double cannot be cast to java.lang.Long
```

**Nguyên nhân:** Firebase có thể trả về số dưới dạng `Double` thay vì `Long`, đặc biệt khi:
- Dữ liệu được lưu từ JavaScript/Web
- Dữ liệu được import từ JSON
- Firebase tự động convert số

## ✅ Đã sửa

### 1. **TestResult.java** - Fixed casting errors

**Trước (Lỗi):**
```java
result.setTotalQuestions(((Long) map.get("totalQuestions")).intValue());
result.setCorrectAnswers(((Long) map.get("correctAnswers")).intValue());
result.setQuestionsCompleted(((Long) map.get("questionsCompleted")).intValue());
result.setCompletionTime((Long) map.get("completionTime"));
result.setTimestamp((Long) map.get("timestamp"));
```

**Sau (Fixed):**
```java
result.setTotalQuestions(safeIntFromNumber(map.get("totalQuestions")));
result.setCorrectAnswers(safeIntFromNumber(map.get("correctAnswers")));
result.setQuestionsCompleted(safeIntFromNumber(map.get("questionsCompleted")));
result.setCompletionTime(safeLongFromNumber(map.get("completionTime")));
result.setTimestamp(safeLongFromNumber(map.get("timestamp")));
```

### 2. **Quiz.java** - Fixed casting errors

**Trước (Lỗi):**
```java
quiz.setCreatedAt(map.get("createdAt") != null ? (Long) map.get("createdAt") : 0);
quiz.setUpdatedAt(map.get("updatedAt") != null ? (Long) map.get("updatedAt") : 0);
```

**Sau (Fixed):**
```java
quiz.setCreatedAt(map.get("createdAt") != null ? safeLongFromNumber(map.get("createdAt")) : 0);
quiz.setUpdatedAt(map.get("updatedAt") != null ? safeLongFromNumber(map.get("updatedAt")) : 0);
```

### 3. **Helper Methods** - Safe conversion

**Thêm vào cả TestResult.java và Quiz.java:**

```java
// Helper method to safely convert Number to int
private static int safeIntFromNumber(Object obj) {
    if (obj == null) return 0;
    
    if (obj instanceof Long) {
        return ((Long) obj).intValue();
    } else if (obj instanceof Double) {
        return ((Double) obj).intValue();
    } else if (obj instanceof Integer) {
        return (Integer) obj;
    } else if (obj instanceof Number) {
        return ((Number) obj).intValue();
    }
    
    // Try to parse as string
    try {
        return Integer.parseInt(obj.toString());
    } catch (NumberFormatException e) {
        return 0;
    }
}

// Helper method to safely convert Number to long
private static long safeLongFromNumber(Object obj) {
    if (obj == null) return 0L;
    
    if (obj instanceof Long) {
        return (Long) obj;
    } else if (obj instanceof Double) {
        return ((Double) obj).longValue();
    } else if (obj instanceof Integer) {
        return ((Integer) obj).longValue();
    } else if (obj instanceof Number) {
        return ((Number) obj).longValue();
    }
    
    // Try to parse as string
    try {
        return Long.parseLong(obj.toString());
    } catch (NumberFormatException e) {
        return 0L;
    }
}
```

## 🧪 Test Cases

### Test Case 1: Double values from Firebase
```java
Map<String, Object> testData = new HashMap<>();
testData.put("totalQuestions", 10.0);  // Double instead of Long
testData.put("correctAnswers", 8.0);   // Double instead of Long
testData.put("completionTime", 120.0); // Double instead of Long

TestResult result = TestResult.fromMap(testData);
// Should work without ClassCastException
```

### Test Case 2: Mixed types
```java
Map<String, Object> testData = new HashMap<>();
testData.put("totalQuestions", 10);    // Integer
testData.put("correctAnswers", 8L);    // Long
testData.put("completionTime", 120.0); // Double

TestResult result = TestResult.fromMap(testData);
// Should handle all types correctly
```

### Test Case 3: Null values
```java
Map<String, Object> testData = new HashMap<>();
testData.put("totalQuestions", null);
testData.put("correctAnswers", null);

TestResult result = TestResult.fromMap(testData);
// Should return 0 for null values
```

### Test Case 4: String values (fallback)
```java
Map<String, Object> testData = new HashMap<>();
testData.put("totalQuestions", "10");
testData.put("correctAnswers", "8");

TestResult result = TestResult.fromMap(testData);
// Should parse strings to numbers
```

## 🔍 Các trường hợp được handle:

1. ✅ **Long** → int/long (original behavior)
2. ✅ **Double** → int/long (main fix)
3. ✅ **Integer** → int/long (compatibility)
4. ✅ **Number** → int/long (generic)
5. ✅ **String** → int/long (fallback parsing)
6. ✅ **null** → 0/0L (safe defaults)

## 🚀 Benefits

- **No more ClassCastException** khi Firebase trả về Double
- **Backward compatible** với dữ liệu cũ (Long)
- **Forward compatible** với dữ liệu mới (Double)
- **Robust error handling** với fallback parsing
- **Safe defaults** cho null values

## 📝 Files Modified

1. `app/src/main/java/com/example/edura/model/TestResult.java`
   - Fixed 5 casting operations
   - Added 2 helper methods

2. `app/src/main/java/com/example/edura/model/Quiz.java`
   - Fixed 2 casting operations
   - Added 1 helper method

## ✅ Testing

Để test fix này:

1. **Build app** trong Android Studio
2. **Run app** và thực hiện các action:
   - Tạo quiz mới
   - Làm bài test
   - Xem kết quả test
   - Xem lịch sử test
3. **Check Logcat** - không còn ClassCastException
4. **Verify data** - số liệu hiển thị đúng

## 🎯 Next Steps

1. ✅ Build và test app
2. ✅ Verify không còn ClassCastException
3. ✅ Check data integrity
4. ✅ Monitor logs for any remaining casting issues

---

**Fix hoàn tất!** App giờ sẽ handle được cả Long và Double từ Firebase mà không bị crash.
