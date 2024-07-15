package com.utt.gymbros;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.MembershipModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembershipsAdminFragment extends Fragment {

    private static final String ARG_TOKEN = "token";
    private static final int FETCH_INTERVAL_MS = 10000; // Intervalo de 10 segundos

    private RecyclerView recyclerView;
    private MembershipAdapter adapter;
    private List<MembershipModel.Membership> membershipList;
    private Handler handler;
    private Runnable fetchTask;
    private Snackbar snackbar;
    private Button btnAddMembership;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String token = getArguments().getString(ARG_TOKEN);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memberships_admin, container, false);

        recyclerView = view.findViewById(R.id.recycler_memberships);
        btnAddMembership = view.findViewById(R.id.btn_add_membership);
        btnAddMembership.setOnClickListener(v -> openCreateMembershipDialog());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        membershipList = new ArrayList<>();
        adapter = new MembershipAdapter(getContext(), membershipList, this); // Pasamos una referencia del fragmento al adaptador
        recyclerView.setAdapter(adapter);

        handler = new Handler(Looper.getMainLooper());
        fetchTask = this::fetchMemberships;

        startFetching();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopFetching();
    }

    private void openCreateMembershipDialog() {
        String token = getArguments().getString(ARG_TOKEN);
        FullScreenDialogCreateMembership dialogFragment = FullScreenDialogCreateMembership.newInstance(token);
        dialogFragment.show(getChildFragmentManager(), "FullScreenDialogCreateMembership");
    }

    private void startFetching() {
        handler.post(fetchTask);
    }

    private void stopFetching() {
        handler.removeCallbacks(fetchTask);
    }

    private void fetchMemberships() {
        String token = "Bearer " + getArguments().getString(ARG_TOKEN);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        apiService.getMemberships(token).enqueue(new Callback<List<MembershipModel.Membership>>() {
            @Override
            public void onResponse(Call<List<MembershipModel.Membership>> call, Response<List<MembershipModel.Membership>> response) {
                if (response.isSuccessful()) {
                    membershipList.clear();
                    membershipList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (snackbar != null && snackbar.isShown()) {
                        snackbar.dismiss();
                    }
                } else {
                    showPersistentSnackbar("Error al obtener membresías");
                }

                scheduleNextFetch();
            }

            @Override
            public void onFailure(Call<List<MembershipModel.Membership>> call, Throwable t) {
                showPersistentSnackbar("Error en la solicitud");
                scheduleNextFetch();
            }
        });
    }

    private void scheduleNextFetch() {
        handler.postDelayed(fetchTask, FETCH_INTERVAL_MS);
    }

    private void showPersistentSnackbar(String message) {
        if (getView() != null) {
            if (snackbar == null) {
                snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
            } else {
                snackbar.setText(message);
            }
            snackbar.show();
        }
    }

    public static MembershipsAdminFragment newInstance(String token) {
        MembershipsAdminFragment fragment = new MembershipsAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    public void updateMembershipActive(MembershipModel.Membership updatedMembershipActive) {
        int position = membershipList.indexOf(updatedMembershipActive);
        if (position != -1) {
            membershipList.set(position, updatedMembershipActive);
            adapter.notifyItemChanged(position);
        }
    }

    public void updateMembership(MembershipModel.Membership updatedMembership) {
        int position = -1;
        for (int i = 0; i < membershipList.size(); i++) {
            if (membershipList.get(i).getId() == updatedMembership.getId()) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            membershipList.set(position, updatedMembership);
            adapter.notifyItemChanged(position);
        }
    }


}