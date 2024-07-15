package com.utt.gymbros;

import static com.utt.gymbros.UsersAdminFragment.ARG_TOKEN;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapterUsersAdmin extends FragmentStateAdapter {

    private final String token;

    public ViewPagerAdapterUsersAdmin(@NonNull Fragment fragment) {
        super(fragment);
        token = fragment.getArguments() != null ? fragment.getArguments().getString(ARG_TOKEN) : "";
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return VisitUserAdminFragment.newInstance(token);
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
