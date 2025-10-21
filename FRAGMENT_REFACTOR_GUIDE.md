# Fragment Refactor Guide - Single Activity Architecture

## Vấn đề đã giải quyết

### Vấn đề trước đây:
❌ **Multiple Activities**: MainActivity, QuizActivity riêng biệt
❌ **Activity Stack Issues**: Activities chồng lên nhau khi navigate
❌ **Duplicate Code**: Bottom navigation ở mỗi activity
❌ **Memory Issues**: Nhiều Activity instances trong memory
❌ **Navigation Complexity**: Phải quản lý nhiều Activity lifecycles
❌ **Inconsistent State**: Bottom nav state không đồng bộ

### Giải pháp hiện tại:
✅ **Single Activity**: Chỉ có 1 MainActivity
✅ **Multiple Fragments**: HomeFragment, QuizFragment, StatsFragment, ProfileFragment
✅ **Shared Bottom Nav**: Bottom navigation chung, state đồng bộ
✅ **Better Performance**: Ít memory overhead hơn
✅ **Smoother Transitions**: Fragment transactions nhanh hơn Activity transitions
✅ **Consistent UX**: Navigation flow mượt mà, không bị flash màn hình

## Kiến trúc mới

```
MainActivity (Single Activity)
├── FrameLayout (Fragment Container)
│   ├── HomeFragment (Tab 0)
│   ├── QuizFragment (Tab 1)
│   ├── StatsFragment (Tab 2)
│   └── ProfileFragment (Tab 3)
└── Bottom Navigation (Shared)
    ├── Trang chủ → load HomeFragment
    ├── Quizz → load QuizFragment
    ├── Thống kê → load StatsFragment
    └── Tài khoản → load ProfileFragment
```

## Files đã tạo mới

### Fragments (Java)
1. **HomeFragment.java**
   - Hiển thị màn hình home
   - AI card → navigate to QuizFragment
   - Recent quizzes

2. **QuizFragment.java**
   - Hiển thị danh sách quiz
   - CRUD operations với Firestore
   - AI quiz card
   - Manual quiz card

3. **StatsFragment.java**
   - Placeholder cho tính năng thống kê
   - TODO: Implement stats features

4. **ProfileFragment.java**
   - Placeholder cho tính năng profile
   - TODO: Implement profile features

### Layouts (XML)
1. **fragment_home.xml**
   - Layout cho HomeFragment
   - AI card + Recent section

2. **fragment_quiz.xml**
   - Layout cho QuizFragment
   - Action cards + Quiz list

3. **fragment_stats.xml**
   - Placeholder layout
   - Coming soon message

4. **fragment_profile.xml**
   - Placeholder layout
   - Coming soon message

5. **activity_main.xml** (Updated)
   - Fragment container (FrameLayout)
   - Bottom navigation (Shared)

### Java Classes (Updated)
1. **MainActivity.java** (Refactored)
   - Fragment manager
   - Navigation logic
   - Tab selection handling
   - Public method `navigateToFragment(int)` for inter-fragment navigation

## Cách hoạt động

### 1. App Startup
```
LoginActivity → (Auth success) → MainActivity
MainActivity.onCreate()
  ├── Initialize views
  ├── Setup listeners
  └── Load HomeFragment (default)
```

### 2. Navigation Flow
```
User clicks bottom nav item
  ├── navHome.onClick() → loadFragment(HomeFragment, 0)
  ├── navQuiz.onClick() → loadFragment(QuizFragment, 1)
  ├── navStats.onClick() → loadFragment(StatsFragment, 2)
  └── navProfile.onClick() → loadFragment(ProfileFragment, 3)

loadFragment()
  ├── FragmentTransaction.replace()
  ├── Update currentTabIndex
  └── updateNavigation() → Update icon/text colors
```

### 3. Inter-Fragment Navigation
```java
// From HomeFragment to QuizFragment
((MainActivity) getActivity()).navigateToFragment(1);
```

### 4. Fragment Lifecycle
```
Fragment created
  ├── onCreateView() → Inflate layout
  ├── initViews() → Find views
  ├── setupListeners() → Setup click handlers
  └── loadData() → Load from Firestore (if needed)
```

## Code Examples

### MainActivity - Load Fragment
```java
private void loadFragment(Fragment fragment, int tabIndex) {
    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft = fm.beginTransaction();
    ft.replace(R.id.fragmentContainer, fragment);
    ft.commit();
    
    currentTabIndex = tabIndex;
    updateNavigation(tabIndex);
}
```

### MainActivity - Update Navigation
```java
private void updateNavigation(int selectedIndex) {
    int unselectedColor = Color.parseColor("#9E9E9E");
    int selectedColor = Color.parseColor("#4169E1");
    
    // Reset all to unselected
    // Set selected tab to selected color
}
```

### HomeFragment - Navigate to Quiz
```java
private void navigateToQuiz() {
    if (getActivity() != null && getActivity() instanceof MainActivity) {
        ((MainActivity) getActivity()).navigateToFragment(1);
    }
}
```

### QuizFragment - Firestore Integration
```java
private void loadQuizzes() {
    db.collection("quizzes")
        .whereEqualTo("createdBy", userId)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .addSnapshotListener((value, error) -> {
            // Update quiz list
            updateUI();
        });
}
```

## Benefits

### 1. Performance
- ✅ Faster navigation (Fragment transactions vs Activity launches)
- ✅ Less memory usage (Single Activity instance)
- ✅ Shared resources (Bottom nav, Firebase instances)

### 2. User Experience
- ✅ Smoother transitions
- ✅ No flashing between screens
- ✅ Consistent bottom navigation state
- ✅ Faster app feel

### 3. Development
- ✅ Easier to maintain
- ✅ Less code duplication
- ✅ Simpler navigation logic
- ✅ Better separation of concerns

### 4. Best Practices
- ✅ Follows Android Single Activity pattern
- ✅ Modern architecture
- ✅ Scalable for future features

## Files Deleted

1. ❌ **QuizActivity.java** → Replaced by QuizFragment
2. ❌ **activity_quiz.xml** → Replaced by fragment_quiz.xml

## Files Modified

1. ✏️ **MainActivity.java** → Complete refactor for Fragment management
2. ✏️ **activity_main.xml** → Added FrameLayout container
3. ✏️ **AndroidManifest.xml** → Removed QuizActivity declaration

## Migration Notes

### Before (Multiple Activities)
```
MainActivity → QuizActivity → MainActivity (Stack issues)
Each activity has own bottom nav
Activities chồng lên nhau
```

### After (Single Activity + Fragments)
```
MainActivity (only)
  ├── Fragments switch trong container
  └── Shared bottom navigation
No stack issues
```

## Testing Checklist

- [x] Home fragment loads correctly
- [x] Quiz fragment loads correctly
- [x] Stats fragment loads correctly
- [x] Profile fragment loads correctly
- [x] Bottom navigation works
- [x] Tab selection updates correctly
- [x] Navigation colors update
- [x] No activity stack issues
- [x] Firestore integration works
- [x] Create quiz works
- [ ] Test on different screen sizes
- [ ] Test memory usage
- [ ] Test navigation performance

## Future Enhancements

1. **Navigation Component**: Migrate to Jetpack Navigation Component
2. **ViewModel**: Add ViewModels for better state management
3. **LiveData**: Use LiveData for reactive UI updates
4. **Saved State**: Handle fragment state restoration
5. **Animations**: Add custom fragment transitions
6. **Deep Linking**: Support deep links to specific fragments

## Troubleshooting

### Fragment not showing?
- Check `FragmentTransaction.commit()` is called
- Verify `R.id.fragmentContainer` exists in layout
- Check fragment's `onCreateView()` returns view

### Bottom nav not updating?
- Check `updateNavigation()` is called after fragment load
- Verify icon and text references are correct
- Check color values are valid

### Firestore data not loading?
- Check Fragment lifecycle (load in `onCreateView()` or `onViewCreated()`)
- Verify Firebase Auth user is logged in
- Check Firestore rules and permissions

## Build & Run

```bash
# Clean old builds
./gradlew clean

# Build project
./gradlew build

# Install on device
./gradlew installDebug
```

## Summary

✨ **Đã refactor thành công từ Multiple Activities sang Single Activity + Fragments**

**Kết quả:**
- 🎯 Không còn vấn đề activities chồng lên nhau
- 🚀 Navigation mượt mà hơn
- 💪 Code gọn gàng, dễ maintain hơn
- ✅ Theo best practices của Android

**Next Steps:**
1. Test kỹ navigation flow
2. Implement StatsFragment và ProfileFragment
3. Add animations cho fragment transitions
4. Consider Jetpack Navigation Component


