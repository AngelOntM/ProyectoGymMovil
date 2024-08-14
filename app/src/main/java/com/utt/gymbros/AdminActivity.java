package com.utt.gymbros;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class AdminActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        // Window insets handling (assumed EdgeToEdge handles this better)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_main_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        BottomNavigationView navigationView = findViewById(R.id.bottom_nav_admin);

        //Obtener token de autenticación
        SharedPreferences sharedPreferences = null;
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    "userData",
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }

        String token = sharedPreferences.getString("auth_token", "");

        enum MenuItemId{
            USERS_ADMIN(R.id.nav_users_admin),
            MANAGEMENT_ADMIN(R.id.nav_management_admin),
            ACCOUNT_ADMIN(R.id.nav_account_admin);

            private final int id;

            MenuItemId(int id){
                this.id = id;
            }

            public int getId(){
                return id;
            }
        }

        // Initial fragment setup
        if (savedInstanceState == null) {  // Avoid fragment recreation on configuration change
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.admin_main_activity, UsersAdminFragment.newInstance(token))
                    .commit();
            navigationView.setSelectedItemId(R.id.nav_users_admin);
        }

        navigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;
            MenuItemId selectedId=null;
            for (MenuItemId id : MenuItemId.values()){
                if (id.getId() == item.getItemId()){
                    selectedId = id;
                    break;
                }
            }
            if (selectedId == null){
                selectedId = MenuItemId.USERS_ADMIN;
            }
            fragment = switch (selectedId) {
                case USERS_ADMIN ->  UsersAdminFragment.newInstance(token);
                case MANAGEMENT_ADMIN ->  ManagementAdminFragment.newInstance(token);
                case ACCOUNT_ADMIN -> AccountAdminFragment.newInstance(token);
            };
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.admin_main_activity, fragment)
                    .commit();
            return true;
        });
    }

}
