package com.utt.gymbros;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ManagementAdminFragment extends Fragment {

    public  static final String ARG_TOKEN = "token";

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String token = getArguments().getString(ARG_TOKEN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_management_admin, container, false);

        viewPager = view.findViewById(R.id.viewPagerManagementAdmin);
        tabLayout = view.findViewById(R.id.tabLayoutManagementAdmin);

        ViewPagerAdapterManagementAdmin adapter = new ViewPagerAdapterManagementAdmin(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Ordenes");
                            tab.setIcon(R.drawable.orders_icon);
                            break;
                        case 1:
                            tab.setText("Productos");
                            tab.setIcon(R.drawable.category_icon);
                            break;
                        case 2:
                            tab.setText("Membresias");
                            tab.setIcon(R.drawable.membership_icon);
                            break;
                    }
                }).attach();

        return view;
    }

    public static ManagementAdminFragment newInstance(String token) {
        ManagementAdminFragment fragment = new ManagementAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }
}

