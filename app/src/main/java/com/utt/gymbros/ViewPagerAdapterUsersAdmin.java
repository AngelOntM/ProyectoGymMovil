package com.utt.gymbros;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapterUsersAdmin extends FragmentStateAdapter {
    public ViewPagerAdapterUsersAdmin(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ListUserAdminFragment();
            default:
                return new VisitUserAdminFragment();
        }
    }
    @Override
    public int getItemCount() {
        return 2;
    }
}
