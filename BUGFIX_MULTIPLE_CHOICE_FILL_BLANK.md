# 🔧 Fix: Hiển thị đáp án đúng cho MULTIPLE_CHOICE và FILL_IN_BLANK

## 📋 Vấn đề gốc

Khi tạo quiz từ AI, chỉ có type **SINGLE_CHOICE** hiển thị đáp án đúng. Còn các type **MULTIPLE_CHOICE** (Multiple Response) và **FILL_IN_BLANK** thì không hiển thị đáp án đúng.

## 🔍 Nguyên nhân

### 1. **AIQuizzRespond.parseQuizArray()**

- Code chỉ xử lý dạng `options` + `correct_answer` (dùng cho SINGLE_CHOICE)
- Không có logic để phân biệt và xử lý các loại câu hỏi khác nhau từ backend

### 2. **GeminiQuestion class**

- Class này chỉ hỗ trợ 3 field: `question`, `options`, `correct_answer`
- Không có field `type` để biết loại câu hỏi
- Không có field `correct_answers` (plural) cho MULTIPLE_CHOICE
- Không có field `answer`/`answers` cho FILL_IN_BLANK

### 3. **CreateQuizActivity.loadAIGeneratedQuiz()**

- Khi load FILL_IN_BLANK, chỉ lấy answer đầu tiên: `.get(0)`
- Nếu backend trả về multiple answers, chỉ hiển thị 1 cái

### 4. **CreateQuizActivity.getAnswers() cho FILL_IN_BLANK**

- Khi collect answers từ view, tạo 1 Answer duy nhất với toàn bộ text
- Không parse multiple answers được phân tách bằng dấu chấm phẩy

## ✅ Các sửa chữa

### 1. **AIQuizzRespond.parseQuizArray()** - Xử lý đúng các loại câu hỏi

```java
// Lấy type từ backend để biết loại câu hỏi
String questionType = (String) item.get("type");

// MULTIPLE_CHOICE: sử dụng correct_answers (plural)
if ("MULTIPLE_CHOICE".equalsIgnoreCase(questionType)) {
    Object correctAnswersObj = item.get("correct_answers");
    // Parse danh sách đáp án đúng
}

// FILL_IN_BLANK: sử dụng answer hoặc answers
else if ("FILL_IN_BLANK".equalsIgnoreCase(questionType)) {
    Object answerObj = item.get("answer");
    if (answerObj == null) answerObj = item.get("answers");
    // Parse danh sách đáp án đúng
}
```

### 2. **GeminiQuestion class** - Hỗ trợ tất cả các loại câu hỏi

```java
public static class GeminiQuestion {
    @SerializedName("question")
    private String question;

    @SerializedName("type")
    private String type; // NEW: để xác định loại câu hỏi

    @SerializedName("options")
    private List<String> options;

    @SerializedName("correct_answer")
    private String correctAnswer;

    @SerializedName("correct_answers")
    private List<String> correctAnswers; // NEW: cho MULTIPLE_CHOICE

    @SerializedName("answer")
    private String answer; // NEW: cho FILL_IN_BLANK

    @SerializedName("answers")
    private List<String> answerList; // NEW: cho FILL_IN_BLANK (multiple)
}
```

### 3. **CreateQuizActivity.loadAIGeneratedQuiz()** - Xử lý multiple answers cho FILL_IN_BLANK

```java
if (type == Question.QuestionType.FILL_IN_BLANK) {
    // Xử lý multiple answers
    StringBuilder answerText = new StringBuilder();
    for (int i = 0; i < question.getAnswers().size(); i++) {
        Answer answer = question.getAnswers().get(i);
        if (i > 0) {
            answerText.append("; ");
        }
        answerText.append(answer.getAnswerText());
    }
    lastQv.setFillInBlankAnswer(answerText.toString());
}
```

### 4. **CreateQuizActivity.getAnswers()** - Parse multiple answers từ FILL_IN_BLANK

```java
if (questionType == Question.QuestionType.FILL_IN_BLANK) {
    String answerText = etFillInBlankAnswer.getText().toString().trim();
    // Parse multiple answers separated by semicolon
    String[] answerArray = answerText.split(";");
    for (String ans : answerArray) {
        String trimmedAns = ans.trim();
        if (!trimmedAns.isEmpty()) {
            answers.add(new Answer(trimmedAns, true));
        }
    }
}
```

## 📝 Định dạng dữ liệu từ Backend

### SINGLE_CHOICE

```json
{
  "question": "Câu hỏi?",
  "type": "SINGLE_CHOICE",
  "options": ["A", "B", "C", "D"],
  "correct_answer": "A"
}
```

### MULTIPLE_CHOICE

```json
{
  "question": "Câu hỏi?",
  "type": "MULTIPLE_CHOICE",
  "options": ["A", "B", "C", "D"],
  "correct_answers": ["A", "C"]
}
```

### FILL_IN_BLANK

```json
{
  "question": "Câu hỏi?",
  "type": "FILL_IN_BLANK",
  "answer": "đáp án",
  "answers": ["đáp án 1", "đáp án 2"]
}
```

## 🧪 Test Cases

1. **Test MULTIPLE_CHOICE**

   - Tạo quiz từ AI với type MULTIPLE_CHOICE
   - Xác minh checkbox được tích đúng cho các đáp án đúng
   - Xác minh user có thể chọn nhiều đáp án

2. **Test FILL_IN_BLANK**

   - Tạo quiz từ AI với type FILL_IN_BLANK
   - Xác minh textbox hiển thị đúng tất cả đáp án (separated by ";")
   - Xác minh khi save, tất cả answers được lưu riêng biệt

3. **Test SINGLE_CHOICE**
   - Xác minh vẫn hoạt động như cũ

## 📌 Files được sửa

1. `d:\Edura\app\src\main\java\com\example\edura\model\AIQuizzRespond.java`

   - Sửa `parseQuizArray()` method
   - Sửa `GeminiQuestion` class để hỗ trợ tất cả fields

2. `d:\Edura\app\src\main\java\com\example\edura\CreateQuizActivity.java`
   - Sửa `loadAIGeneratedQuiz()` method
   - Sửa `getAnswers()` method trong QuestionView class

## 🎯 Kết quả

✅ SINGLE_CHOICE hiển thị đáp án đúng (cũ)
✅ MULTIPLE_CHOICE hiển thị checkbox tích cho tất cả đáp án đúng (NEW)
✅ FILL_IN_BLANK hiển thị tất cả đáp án đúng separated by ";" (NEW)
✅ Khi save quiz, tất cả đáp án được lưu đúng
