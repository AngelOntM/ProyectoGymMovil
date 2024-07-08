package com.utt.gymbros;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class UsersAdminFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_admin, container, false);

        viewPager = view.findViewById(R.id.viewPagerUsersAdmin);
        tabLayout = view.findViewById(R.id.tabLayoutUsersAdmin);

        ViewPagerAdapterUsersAdmin adapter = new ViewPagerAdapterUsersAdmin(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Visitas");
                            tab.setIcon(R.drawable.list_icon);
                            break;
                        case 1:
                            tab.setText("Lista de Usuarios");
                            tab.setIcon(R.drawable.userdetail_icon);
                            break;
                    }
                }).attach();

        return view;
    }
}


