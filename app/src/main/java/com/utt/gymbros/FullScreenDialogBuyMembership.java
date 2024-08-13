package com.utt.gymbros;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.MembershipModel;

import com.stripe.android.paymentsheet.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullScreenDialogBuyMembership extends DialogFragment {
    private String token;
    private List<MembershipModel.Membership> membershipList;
    private RecyclerView recyclerView;
    private BuyMembershipAdapter adapter;
    androidx.appcompat.app.AlertDialog progressDialogWaiting;

    public static FullScreenDialogBuyMembership newInstance(String token) {
        FullScreenDialogBuyMembership fragment = new FullScreenDialogBuyMembership();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.THEME_FULL_SCREEN_DIALOG);
        Bundle args = new Bundle();
        args.putString("token", token);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString("token");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_buy_membership, container, false);

        token = getArguments().getString("token");

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        progressDialogWaiting = new MaterialAlertDialogBuilder(requireContext())
                .setView(R.layout.progress_dialog_waiting)
                .setCancelable(false)
                .create();

        recyclerView = view.findViewById(R.id.recycler_memberships);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchMembershipList();

        return view;
    }

    private void fetchMembershipList() {
        progressDialogWaiting.show();
        token = getArguments().getString("token");
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<List<MembershipModel.Membership>> call = apiService.getMembershipsUser(token);
        call.enqueue(new Callback<List<MembershipModel.Membership>>() {
            @Override
            public void onResponse(Call<List<MembershipModel.Membership>> call, Response<List<MembershipModel.Membership>> response) {
                if (response.isSuccessful()) {
                    membershipList = response.body();
                    adapter = new BuyMembershipAdapter(getContext(), membershipList, FullScreenDialogBuyMembership.this);
                    recyclerView.setAdapter(adapter);
                    progressDialogWaiting.dismiss();
                } else {
                    Log.e("Membership", "Error al obtener la lista de membresías");
                    progressDialogWaiting.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<MembershipModel.Membership>> call, Throwable t) {
                Log.e("Membership", "Error al obtener la lista de membresías");
                progressDialogWaiting.dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().setCancelable(false); // Evitar cerrar al tocar fuera del diálogo
        }
        setCancelable(false);
    }
}
