package com.utt.gymbros;

import static com.utt.gymbros.ManagementAdminFragment.ARG_TOKEN;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapterManagementAdmin extends FragmentStateAdapter {

    private final String token;

    public ViewPagerAdapterManagementAdmin(@NonNull Fragment fragment) {
        super(fragment);
        token = fragment.getArguments() != null ? fragment.getArguments().getString(ARG_TOKEN) : "";
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return OrdersAdminFragment.newInstance(token);
            case 1:
                return ProductsAdminFragment.newInstance(token);
            case 2:
                return MembershipsAdminFragment.newInstance(token);
            default:
                return new ProductsAdminFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
