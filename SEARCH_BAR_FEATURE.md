# Search Bar Feature - Tính Năng Tìm Kiếm

## ✅ Đã Hoàn Thành

Đã thêm thanh search bar giữa phần header và World Quiz section theo yêu cầu.

---

## 🎨 Giao Diện Mới

```
┌────────────────────────────────────┐
│ Welcome                 [🔔]       │
│ Username                           │
├────────────────────────────────────┤
│ [🔍] Tìm kiếm quiz...        [X]  │  ← SEARCH BAR MỚI
├────────────────────────────────────┤
│ World Quiz           Xem thêm →   │
│                                    │
│ [History] [Programming]...         │
└────────────────────────────────────┘
```

---

## 📝 Chi Tiết Search Bar

### Thành Phần:
1. **Icon Search (🔍)**: Bên trái
2. **Input Field**: "Tìm kiếm quiz..."
3. **Clear Button (X)**: Bên phải (ẩn khi không có text)

### Tính Năng:
- ✅ Gõ text để tìm kiếm
- ✅ Hiện nút X khi có text
- ✅ Click X để xoá text
- ✅ Nhấn Enter/Search trên bàn phím để search
- ✅ Tự động ẩn bàn phím sau khi search
- ✅ Background trắng bo tròn

---

## 🔧 Implementation Details

### Layout (fragment_home.xml):

```xml
<!-- Search Bar Section -->
<LinearLayout
    android:background="@drawable/bg_search_field"
    android:paddingStart="20dp"
    android:paddingEnd="20dp"
    android:paddingTop="12dp"
    android:paddingBottom="12dp">

    <!-- Search Icon -->
    <ImageView android:src="@drawable/ic_search" />

    <!-- Search EditText -->
    <EditText
        android:id="@+id/etSearch"
        android:hint="Tìm kiếm quiz..."
        android:imeOptions="actionSearch" />

    <!-- Clear Button -->
    <ImageView
        android:id="@+id/btnClearSearch"
        android:src="@drawable/ic_remove"
        android:visibility="gone" />
</LinearLayout>
```

### Java Code (HomeFragment.java):

#### 1. Setup Search Bar
```java
private void setupSearchBar() {
    // Text change listener
    etSearch.addTextChangedListener(new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, ...) {
            // Show/hide clear button
            if (s.length() > 0) {
                btnClearSearch.setVisibility(View.VISIBLE);
            } else {
                btnClearSearch.setVisibility(View.GONE);
            }
        }
        
        @Override
        public void afterTextChanged(Editable s) {
            performSearch(s.toString());
        }
    });
    
    // Clear button
    btnClearSearch.setOnClickListener(v -> {
        etSearch.setText("");
        hideKeyboard();
    });
    
    // Search on keyboard enter
    etSearch.setOnEditorActionListener((v, actionId, event) -> {
        if (actionId == IME_ACTION_SEARCH) {
            performSearch(etSearch.getText().toString());
            hideKeyboard();
            return true;
        }
        return false;
    });
}
```

#### 2. Perform Search
```java
private void performSearch(String query) {
    if (query.isEmpty()) {
        // Show all items
        return;
    }
    
    Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
    
    // TODO: Filter categories and recent quizzes
    // filterCategories(query);
    // filterRecentQuizzes(query);
}
```

#### 3. Hide Keyboard
```java
private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) 
        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }
}
```

---

## 🎯 Cách Sử Dụng

### User Experience:

1. **Nhấn vào search bar**
   - Bàn phím hiện lên
   - Focus vào input field

2. **Gõ text tìm kiếm**
   - Nút X (clear) hiện lên
   - Real-time search (có thể thêm debounce)

3. **Nhấn nút X**
   - Text bị xoá
   - Nút X biến mất
   - Bàn phím ẩn

4. **Nhấn Enter/Search trên bàn phím**
   - Thực hiện search
   - Bàn phím tự động ẩn

---

## 🔮 Tính Năng Có Thể Thêm

### 1. Real-Time Filtering
```java
private void filterCategories(String query) {
    List<QuizCategory> filtered = new ArrayList<>();
    for (QuizCategory category : categories) {
        if (category.getName().toLowerCase().contains(query.toLowerCase())) {
            filtered.add(category);
        }
    }
    categoryAdapter.updateData(filtered);
}
```

### 2. Search History
```java
private void saveSearchHistory(String query) {
    SharedPreferences prefs = getActivity().getSharedPreferences("search", MODE_PRIVATE);
    // Save recent searches
}

private void showSearchHistory() {
    // Display recent searches as suggestions
}
```

### 3. Debounce (Giảm số lần search khi đang gõ)
```java
private Handler searchHandler = new Handler();
private Runnable searchRunnable;

@Override
public void afterTextChanged(Editable s) {
    if (searchRunnable != null) {
        searchHandler.removeCallbacks(searchRunnable);
    }
    
    searchRunnable = () -> performSearch(s.toString());
    searchHandler.postDelayed(searchRunnable, 300); // Wait 300ms
}
```

### 4. Search Suggestions
```java
private void showSuggestions(String query) {
    // Show dropdown with suggestions
    // Popular searches, recent searches, etc.
}
```

### 5. Search in Firestore
```java
private void searchInFirestore(String query) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("quizzes")
        .whereGreaterThanOrEqualTo("title", query)
        .whereLessThanOrEqualTo("title", query + "\uf8ff")
        .limit(10)
        .get()
        .addOnSuccessListener(docs -> {
            // Display search results
        });
}
```

### 6. Voice Search
```java
private void startVoiceSearch() {
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    startActivityForResult(intent, VOICE_SEARCH_REQUEST);
}
```

---

## 🎨 Tuỳ Chỉnh Giao Diện

### Thay Đổi Màu Sắc:
```xml
<!-- In colors.xml -->
<color name="search_bg">#FFFFFF</color>
<color name="search_icon">#8E8E93</color>
<color name="search_text">#1A1A1A</color>
<color name="search_hint">#8E8E93</color>
```

### Thay Đổi Bo Tròn:
```xml
<!-- In bg_search_field.xml -->
<corners android:radius="28dp" />  <!-- Change this -->
```

### Thêm Shadow:
```xml
<LinearLayout
    android:elevation="4dp"
    android:background="@drawable/bg_search_field">
```

### Thêm Border:
```xml
<!-- In bg_search_field.xml -->
<stroke
    android:width="1dp"
    android:color="@color/border_gray" />
```

---

## 🔍 Test Cases

### Manual Testing:

- [ ] Nhấn vào search bar → Keyboard hiện
- [ ] Gõ text → Nút X hiện
- [ ] Click nút X → Text xoá, keyboard ẩn
- [ ] Gõ và nhấn Enter → Search thực hiện
- [ ] Text dài → Scroll được
- [ ] Rotate màn hình → Text vẫn còn
- [ ] Back button → Keyboard ẩn trước
- [ ] Dark mode → Màu hiển thị OK

---

## 🐛 Known Issues & Solutions

### Issue 1: Keyboard không ẩn khi click ngoài
**Solution:** Thêm click listener vào ScrollView:
```java
scrollView.setOnTouchListener((v, event) -> {
    if (etSearch.hasFocus()) {
        etSearch.clearFocus();
        hideKeyboard();
    }
    return false;
});
```

### Issue 2: Search quá nhiều lần khi đang gõ
**Solution:** Thêm debounce (xem phần trên)

### Issue 3: Nút X không click được
**Solution:** Đã thêm `clickable="true"` và `focusable="true"`

---

## 📊 Performance Considerations

### 1. Tối Ưu Search
- Sử dụng debounce để giảm số lần search
- Cache search results
- Limit số lượng kết quả

### 2. Memory
- Clear old search results khi không dùng
- Không giữ quá nhiều search history

### 3. Network
- Cancel previous search request nếu user gõ tiếp
- Use pagination cho search results

---

## 🎓 Best Practices

### 1. UX
- Hiện loading indicator khi đang search
- Hiện "No results" nếu không tìm thấy
- Hiện số lượng kết quả tìm được
- Clear search khi user navigate away

### 2. Code
- Validate input (trim, lowercase)
- Handle empty/null query
- Log search queries for analytics
- Add try-catch cho error handling

### 3. Design
- Placeholder text rõ ràng
- Icon dễ nhận biết
- Feedback trực quan (ripple effect)
- Accessible (screen reader support)

---

## 📝 TODO List

- [ ] Implement real-time filtering
- [ ] Add search history
- [ ] Add debounce
- [ ] Connect to Firestore
- [ ] Add analytics
- [ ] Add voice search
- [ ] Add search suggestions
- [ ] Add "No results" screen
- [ ] Add loading indicator
- [ ] Add search result count

---

## 🚀 Summary

✅ **Đã Hoàn Thành:**
- Search bar UI với icon và clear button
- Text change listener
- Clear functionality
- Keyboard handling
- Search action on Enter

⏳ **Có Thể Thêm Sau:**
- Real filtering
- Firestore integration
- Search history
- Voice search
- Suggestions

---

## 📞 Notes

Search bar hiện tại là UI và basic functionality. Để search thực sự hoạt động với data từ Firestore, cần implement:

1. Filter function cho categories
2. Filter function cho recent quizzes
3. Tích hợp với Firestore query
4. Cache và optimize

Nhưng foundation đã có rồi, chỉ cần thêm business logic! 🎉

