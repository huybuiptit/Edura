# 🔧 Fix: Loại câu hỏi luôn hiển thị là SINGLE_CHOICE

## 📋 Vấn đề

Khi tạo quiz từ AI, dù là loại MULTIPLE_CHOICE hay FILL_IN_BLANK, sau khi gen xong và chuyển sang màn hình edit, **tất cả câu hỏi đều hiển thị là SINGLE_CHOICE**.

## 🔍 Nguyên nhân gốc

Backend không trả về field `type` trong response, hoặc trả về nhưng code không xử lý đúng cách:

1. **parseQuizArray()** chỉ default thành "SINGLE_CHOICE" nếu không có field `type`
2. Không có logic auto-detect để xác định loại câu hỏi dựa trên các field khác
3. **GeminiQuestion.toAIQuestion()** cũng chỉ default thành "SINGLE_CHOICE"
4. **Question.fromMap()** có thể fail nếu `questionType` string không match enum

## ✅ Các sửa chữa

### 1. **AIQuizzRespond.parseQuizArray()** - Thêm auto-detect logic

```java
// Lấy type từ backend, nếu không có thì auto-detect
String questionType = (String) item.get("type");
if (questionType == null || questionType.isEmpty()) {
    questionType = autoDetectQuestionType(item);
}
```

### 2. **AIQuizzRespond.autoDetectQuestionType()** - NEW METHOD

```java
private String autoDetectQuestionType(Map<String, Object> item) {
    // Nếu có "correct_answers" (plural) -> MULTIPLE_CHOICE
    if (item.containsKey("correct_answers")) {
        return "MULTIPLE_CHOICE";
    }

    // Nếu có "answer" hoặc "answers" và KHÔNG có "options" -> FILL_IN_BLANK
    boolean hasAnswer = item.containsKey("answer") || item.containsKey("answers");
    boolean hasOptions = item.containsKey("options");
    if (hasAnswer && !hasOptions) {
        return "FILL_IN_BLANK";
    }

    // Default: SINGLE_CHOICE
    return "SINGLE_CHOICE";
}
```

### 3. **GeminiQuestion.toAIQuestion()** - Thêm auto-detect

```java
// Auto-detect nếu type không được specify
if (questionType == null || questionType.isEmpty()) {
    if (correctAnswers != null && !correctAnswers.isEmpty()) {
        questionType = "MULTIPLE_CHOICE";
    } else if ((answer != null && !answer.isEmpty()) ||
               (answerList != null && !answerList.isEmpty())) {
        questionType = "FILL_IN_BLANK";
    } else {
        questionType = "SINGLE_CHOICE";
    }
}
```

### 4. **Question.fromMap()** - Xử lý null/empty questionType

```java
String typeStr = (String) map.get("questionType");
if (typeStr == null || typeStr.isEmpty()) {
    typeStr = "SINGLE_CHOICE"; // Default
}
question.setQuestionType(QuestionType.valueOf(typeStr));
```

## 📊 Logic Detection

### Cách xác định loại câu hỏi từ backend data:

**MULTIPLE_CHOICE:**

```json
{
  "question": "...",
  "options": [...],
  "correct_answers": [...]  // ← Plural! Key indicator
}
```

**FILL_IN_BLANK:**

```json
{
  "question": "...",
  "answer": "..." // hoặc "answers": [...]
  // ← KHÔNG có "options"
}
```

**SINGLE_CHOICE:**

```json
{
  "question": "...",
  "options": [...],
  "correct_answer": "..."  // ← Singular
}
```

## 🧪 Test Cases

1. **Backend trả về với field `type`**

   - ✅ Sử dụng giá trị `type` từ backend

2. **Backend không trả về `type`, nhưng có `correct_answers`**

   - ✅ Auto-detect thành MULTIPLE_CHOICE

3. **Backend không trả về `type`, nhưng có `answer` (không có `options`)**

   - ✅ Auto-detect thành FILL_IN_BLANK

4. **Backend không trả về `type`, có `options` và `correct_answer`**

   - ✅ Auto-detect thành SINGLE_CHOICE (default)

5. **Deserialize từ JSON**
   - ✅ Question.fromMap() xử lý null/empty `questionType`

## 📌 Files được sửa

1. `d:\Edura\app\src\main\java\com\example\edura\model\AIQuizzRespond.java`

   - Sửa `parseQuizArray()` để gọi auto-detect
   - Thêm NEW method `autoDetectQuestionType()`
   - Sửa `GeminiQuestion.toAIQuestion()` để auto-detect

2. `d:\Edura\app\src\main\java\com\example\edura\model\Question.java`
   - Sửa `fromMap()` để handle null/empty `questionType`

## 🎯 Kết quả

✅ SINGLE_CHOICE hiển thị đúng (khi chỉ có `options` + `correct_answer`)
✅ MULTIPLE_CHOICE hiển thị đúng (khi có `correct_answers` plural)
✅ FILL_IN_BLANK hiển thị đúng (khi có `answer`/`answers`, không có `options`)
✅ Backend không cần trả về `type` field nếu data structure đủ rõ ràng
