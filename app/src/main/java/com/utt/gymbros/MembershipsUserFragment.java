package com.utt.gymbros;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class MembershipsUserFragment extends Fragment {

    public static final String ARG_TOKEN = "token";

    private Button btnBuyMembership;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String token = getArguments().getString(ARG_TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_memberships_user, container, false);

        btnBuyMembership = view.findViewById(R.id.btn_buy_membership);
        btnBuyMembership.setOnClickListener(v -> openBuyMembershipDialog());
        recyclerView = view.findViewById(R.id.recycler_memberships);


        return view;
    }

    private void openBuyMembershipDialog() {
        String token = getArguments().getString(ARG_TOKEN);
        FullScreenDialogBuyMembership dialog = FullScreenDialogBuyMembership.newInstance(token);
        dialog.show(getParentFragmentManager(), "FullScreenDialogBuyMembership");
    }

    public static MembershipsUserFragment newInstance(String token) {
        MembershipsUserFragment fragment = new MembershipsUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }
}