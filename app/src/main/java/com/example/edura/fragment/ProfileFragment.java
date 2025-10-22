package com.example.edura.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.example.edura.R;
import com.example.edura.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.EditText;

import java.util.Locale;
import android.content.res.Configuration;

public class ProfileFragment extends Fragment {

    private TextView profileNameText;
    private TextView profileEmailText;
    private TextView profileEditLink;
    private TextView languageValue;
    private SwitchCompat switchDarkMode;
    private SwitchCompat switchNotifications;
    private SwitchCompat switchSound;
    private LinearLayout rowLanguage;
    private LinearLayout rowAbout;
    private LinearLayout rowLogout;
    private LinearLayout rowDelete;

    private FirebaseAuth auth;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        auth = FirebaseAuth.getInstance();
        prefs = requireContext().getSharedPreferences("edura_settings", Context.MODE_PRIVATE);

        initViews(view);
        bindUserInfo();
        bindSettingsState();
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        profileNameText = view.findViewById(R.id.profileNameText);
        profileEmailText = view.findViewById(R.id.profileEmailText);
        profileEditLink = view.findViewById(R.id.profileEditLink);
        languageValue = view.findViewById(R.id.languageValue);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);
        switchNotifications = view.findViewById(R.id.switchNotifications);
        switchSound = view.findViewById(R.id.switchSound);
        rowLanguage = view.findViewById(R.id.rowLanguage);
        rowAbout = view.findViewById(R.id.rowAbout);
        rowLogout = view.findViewById(R.id.rowLogout);
        rowDelete = view.findViewById(R.id.rowDelete);
    }

    private void bindUserInfo() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            profileNameText.setText("User");
            profileEmailText.setText("");
            return;
        }

        String displayName = user.getDisplayName();
        String email = user.getEmail();
        String userName;
        if (displayName != null && !displayName.isEmpty()) {
            userName = displayName;
        } else if (email != null) {
            userName = email.split("@")[0];
        } else {
            userName = "User";
        }

        profileNameText.setText(userName);
        if (email != null) profileEmailText.setText(email);
    }

    private void bindSettingsState() {
        boolean dark = prefs.getBoolean("dark_mode", false);
        boolean notifications = prefs.getBoolean("notifications", true);
        boolean sound = prefs.getBoolean("sound_effects", true);
        String lang = prefs.getString("language", "en");

        switchDarkMode.setChecked(dark);
        switchNotifications.setChecked(notifications);
        switchSound.setChecked(sound);
        languageValue.setText(lang.equals("vi") ? "Tiếng Việt" : "English");

        applyNightMode(dark);
    }

    private void setupListeners() {
        profileEditLink.setOnClickListener(v -> showEditNameDialog());

        rowLanguage.setOnClickListener(v -> showLanguageDialog());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            applyNightMode(isChecked);
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications", isChecked).apply();
            Toast.makeText(requireContext(), isChecked ? "Thông báo BẬT" : "Thông báo TẮT", Toast.LENGTH_SHORT).show();
        });

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("sound_effects", isChecked).apply();
            Toast.makeText(requireContext(), isChecked ? "Âm thanh BẬT" : "Âm thanh TẮT", Toast.LENGTH_SHORT).show();
        });

        rowAbout.setOnClickListener(v -> showAboutDialog());

        rowLogout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(requireContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });

        rowDelete.setOnClickListener(v -> confirmDeleteAccount());
    }

    private void showEditNameDialog() {
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setHint("Họ và tên");
        input.setText(profileNameText.getText());

        new AlertDialog.Builder(requireContext())
                .setTitle("Chỉnh sửa tên")
                .setView(input)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = input.getText().toString().trim();
                        if (newName.isEmpty()) return;
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest req = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(newName)
                                    .build();
                            user.updateProfile(req).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    profileNameText.setText(newName);
                                    Toast.makeText(requireContext(), "Đã cập nhật tên", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showLanguageDialog() {
        String[] langs = new String[]{"English", "Tiếng Việt"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn ngôn ngữ")
                .setItems(langs, (dialog, which) -> {
                    String code = which == 1 ? "vi" : "en";
                    prefs.edit().putString("language", code).apply();
                    languageValue.setText(code.equals("vi") ? "Tiếng Việt" : "English");
                    setLocale(code);
                    requireActivity().recreate();
                })
                .show();
    }

    private void showAboutDialog() {
        String version = "";
        try {
            version = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0).versionName;
        } catch (Exception ignored) { }

        new AlertDialog.Builder(requireContext())
                .setTitle("Về Edura")
                .setMessage("Phiên bản: " + version + "\n\nỨng dụng tạo quiz với AI")
                .setPositiveButton("OK", null)
                .show();
    }

    private void confirmDeleteAccount() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.settings_delete_confirm_title)
                .setMessage(R.string.settings_delete_confirm_message)
                .setPositiveButton(R.string.delete, (d, w) -> deleteAccount())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteAccount() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;
        user.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), R.string.settings_account_deleted, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireContext(), LoginActivity.class));
                requireActivity().finish();
            } else {
                Toast.makeText(requireContext(), "Xóa thất bại. Vui lòng đăng nhập lại và thử lại.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void applyNightMode(boolean enabled) {
        AppCompatDelegate.setDefaultNightMode(
                enabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);
        Context ctx = requireContext().getApplicationContext();
        ctx.getResources().updateConfiguration(config, ctx.getResources().getDisplayMetrics());
    }
}


