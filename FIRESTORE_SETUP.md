# Firestore Setup Guide - Edura Quiz

## Bước 1: Kích hoạt Cloud Firestore

1. Mở [Firebase Console](https://console.firebase.google.com/)
2. Chọn project Edura của bạn
3. Trong menu bên trái, click vào **"Build"** > **"Firestore Database"**
4. Click nút **"Create database"**
5. Chọn **"Start in test mode"** (hoặc production mode)
   - Test mode: Cho phép đọc/ghi trong 30 ngày (dùng cho development)
   - Production mode: Yêu cầu authentication (khuyến nghị)
6. Chọn location gần nhất (ví dụ: `asia-southeast1` cho Singapore)
7. Click **"Enable"**

## Bước 2: Cập nhật Security Rules

1. Trong Firestore Console, chọn tab **"Rules"**
2. Copy nội dung từ file `firestore.rules` trong project
3. Paste vào editor
4. Click **"Publish"**

### Security Rules giải thích:

```javascript
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    
    // Quiz collection rules
    match /quizzes/{quizId} {
      // Cho phép đọc nếu đã đăng nhập
      allow read: if request.auth != null;
      
      // Cho phép tạo nếu user là người tạo
      allow create: if request.auth != null 
                    && request.resource.data.createdBy == request.auth.uid;
      
      // Cho phép update nếu user là chủ sở hữu
      allow update: if request.auth != null 
                    && resource.data.createdBy == request.auth.uid;
      
      // Cho phép delete nếu user là chủ sở hữu
      allow delete: if request.auth != null 
                    && resource.data.createdBy == request.auth.uid;
    }
  }
}
```

## Bước 3: Tạo Composite Index

### Cách 1: Tự động (Khuyến nghị)

1. Run app và thử load danh sách quiz
2. Khi gặp lỗi về index, xem Logcat
3. Click vào link trong error message để tự động tạo index
4. Đợi vài phút để index được build

### Cách 2: Thủ công

1. Trong Firestore Console, chọn tab **"Indexes"**
2. Click **"Create Index"**
3. Điền thông tin:
   - Collection ID: `quizzes`
   - Field 1: `createdBy` - Ascending
   - Field 2: `createdAt` - Descending
4. Click **"Create"**
5. Đợi status chuyển từ "Building" sang "Enabled"

### Cách 3: Sử dụng Firebase CLI

```bash
# Install Firebase CLI (nếu chưa có)
npm install -g firebase-tools

# Login to Firebase
firebase login

# Initialize Firestore in your project
firebase init firestore

# Chọn options:
# - What file should be used for Firestore Rules? firestore.rules
# - What file should be used for Firestore indexes? firestore.indexes.json

# Deploy rules and indexes
firebase deploy --only firestore
```

## Bước 4: Kiểm tra kết nối

1. Run app trên emulator hoặc device
2. Đăng nhập vào app
3. Vào màn hình Quiz và thử tạo quiz mới
4. Quay lại Firebase Console > Firestore Database
5. Kiểm tra collection `quizzes` đã được tạo và có data

## Cấu trúc dữ liệu trong Firestore

### Collection: `quizzes`

Document structure:
```json
{
  "quizId": "auto-generated-id",
  "quizTitle": "Math Quiz",
  "createdBy": "userId123",
  "createdAt": 1697789012345,
  "updatedAt": 1697789012345,
  "questions": [
    {
      "questionId": "q1",
      "questionText": "What is 2 + 2?",
      "questionType": "SINGLE_CHOICE",
      "answers": [
        {
          "answerText": "3",
          "isCorrect": false
        },
        {
          "answerText": "4",
          "isCorrect": true
        },
        {
          "answerText": "5",
          "isCorrect": false
        },
        {
          "answerText": "6",
          "isCorrect": false
        }
      ]
    }
  ]
}
```

## Bước 5: Monitoring và Debug

### Xem dữ liệu
1. Firebase Console > Firestore Database
2. Browse collections và documents
3. Có thể edit/delete data trực tiếp

### Xem query performance
1. Firebase Console > Firestore Database > Usage tab
2. Xem số lượng reads, writes, deletes
3. Monitor costs

### Debug rules
1. Firebase Console > Firestore Database > Rules tab
2. Click **"Rules Playground"**
3. Test các rules với different scenarios

## Troubleshooting

### Lỗi: "Missing or insufficient permissions"
**Nguyên nhân**: User chưa đăng nhập hoặc security rules chặn

**Giải pháp**:
1. Kiểm tra user đã đăng nhập chưa: `FirebaseAuth.getInstance().getCurrentUser()`
2. Kiểm tra security rules đã deploy chưa
3. Kiểm tra `createdBy` field có đúng userId không

### Lỗi: "FAILED_PRECONDITION: The query requires an index"
**Nguyên nhân**: Thiếu composite index

**Giải pháp**:
1. Click vào link trong error message để tạo index
2. Hoặc tạo index thủ công theo Bước 3

### Lỗi: "DEADLINE_EXCEEDED"
**Nguyên nhân**: Network timeout hoặc query quá chậm

**Giải pháp**:
1. Kiểm tra internet connection
2. Tối ưu query (thêm limit, pagination)
3. Tạo index nếu chưa có

### Data không update real-time
**Nguyên nhân**: Offline persistence hoặc cache

**Giải pháp**:
```java
// Disable offline persistence nếu cần
FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
    .setPersistenceEnabled(false)
    .build();
db.setFirestoreSettings(settings);
```

### Costs quá cao
**Giải pháp**:
1. Sử dụng `get()` thay vì `addSnapshotListener()` nếu không cần real-time
2. Thêm pagination với `limit()`
3. Cache data locally
4. Tối ưu số lượng reads

## Best Practices

1. **Security First**: Luôn dùng security rules, không để test mode quá lâu
2. **Index Planning**: Tạo index trước khi deploy production
3. **Data Validation**: Validate data ở client trước khi save
4. **Error Handling**: Luôn handle errors từ Firestore
5. **Offline Support**: Enable offline persistence cho UX tốt hơn
6. **Pagination**: Sử dụng pagination cho lists dài
7. **Monitoring**: Theo dõi usage và costs thường xuyên

## Firebase CLI Commands

```bash
# Deploy chỉ Firestore rules
firebase deploy --only firestore:rules

# Deploy chỉ indexes
firebase deploy --only firestore:indexes

# Deploy cả rules và indexes
firebase deploy --only firestore

# Delete all data (cẩn thận!)
firebase firestore:delete --all-collections

# Backup data
firebase firestore:export gs://your-bucket/backups/$(date +%Y%m%d)

# Import data
firebase firestore:import gs://your-bucket/backups/20231020
```

## Resources

- [Firestore Documentation](https://firebase.google.com/docs/firestore)
- [Security Rules Guide](https://firebase.google.com/docs/firestore/security/get-started)
- [Query Documentation](https://firebase.google.com/docs/firestore/query-data/queries)
- [Index Documentation](https://firebase.google.com/docs/firestore/query-data/indexing)
- [Pricing](https://firebase.google.com/pricing)

## Support

Nếu gặp vấn đề:
1. Check Logcat for error messages
2. Check Firebase Console > Firestore Database
3. Check Firebase Console > Authentication (user đã đăng nhập?)
4. Read error messages carefully - they often include solutions
5. Check Stack Overflow với tag `firebase` và `firestore`


