package com.together.traveler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.together.traveler.model.User;
import com.together.traveler.retrofit.ApiClient;
import com.together.traveler.retrofit.ApiService;
import com.together.traveler.ui.admin.AdminActivity;
import com.together.traveler.ui.admin.AdminActivityKt;
import com.together.traveler.ui.login.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    private static boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        isAdmin = getIntent().getBooleanExtra("admin", false);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            PreferenceScreen logoutBtn = findPreference("prefBtnLogout");
            PreferenceScreen adminBtn = findPreference("prefBtnAdmin");
            PreferenceScreen deleteBtn = findPreference("prefBtnDelete");

            if (adminBtn != null) {
                adminBtn.setVisible(isAdmin);
                adminBtn.setOnPreferenceClickListener(v->{
                    startActivity(new Intent(requireActivity(), AdminActivity.class));
                    return true;
                });
            }
            if (logoutBtn != null) {
                logoutBtn.setOnPreferenceClickListener(v->{
                    fetchLogout();

                    return true;
                });
            }
            if (deleteBtn != null) {
                deleteBtn.setOnPreferenceClickListener(v-> {
                    showDeleteConfirmDialog();
                    return true;
                });
            }
        }
        public void showDeleteConfirmDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(R.string.settings_delete_confirm_title);
            builder.setMessage(R.string.settings_delete_confirm_message);
            builder.setPositiveButton("Yes", (dialog, which) -> Toast.makeText(requireContext(), "soon", Toast.LENGTH_SHORT).show());
            builder.setNegativeButton("No", (dialog, which) -> Toast.makeText(requireContext(), "soon", Toast.LENGTH_SHORT).show());
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        private void fetchLogout(){
            ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
            apiService.logout().enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    requireActivity().finish();
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("Settings", "fetchUser request failed with error: " + t.getMessage());
                }
            });
        }
    }
}