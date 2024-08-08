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
import com.stripe.android.PaymentConfiguration;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51PiLliAvmYkVRZQNHFgKZm3BYNAodJuzqZ8ApCZScOV2L5wPp2761HfhogfqEtMmKwMCTO6tR7wrog9UMwML2KAT000HWEGzKG"
        );
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_main_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView navigationView = findViewById(R.id.bottom_nav_user);

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
            START_USER(R.id.nav_start_user),
            MEMBERSHIPS_USER(R.id.nav_membership_user),
            WORKOUTS_USER(R.id.nav_workouts_user),
            ACCOUNT_USER(R.id.nav_account_user);

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
                    .replace(R.id.home_main_activity, new StartUserFragment())
                    .commit();
            navigationView.setSelectedItemId(R.id.nav_start_user);
        }

        navigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            MenuItemId selectedItemId = null;
            for (MenuItemId menuItemId : MenuItemId.values()) {
                if (menuItemId.getId() == item.getItemId()) {
                    selectedItemId = menuItemId;
                    break;
                }
            }
            if (selectedItemId == null) {
                selectedItemId = MenuItemId.START_USER;
            }
            fragment = switch (selectedItemId) {
                case START_USER -> new StartUserFragment();
                case MEMBERSHIPS_USER ->  MembershipsUserFragment.newInstance(token);
                case WORKOUTS_USER -> new WorkoutsUserFragment();
                case ACCOUNT_USER -> AccountUserFragment.newInstance(token);
            };
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_main_activity, fragment)
                    .commit();

            return true;
        });
    }
}