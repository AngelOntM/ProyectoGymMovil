package com.utt.gymbros;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.MembershipModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MembershipsAdminFragment extends Fragment {

    private static final String ARG_TOKEN = "token";
    private RecyclerView recyclerView;
    private MembershipAdapter adapter;
    private List<MembershipModel.Membership> membershipList;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        membershipList = new ArrayList<>();
        adapter = new MembershipAdapter(getContext(), membershipList);
        recyclerView.setAdapter(adapter);

        fetchMemberships();

        return view;
    }

    private void fetchMemberships() {
        String token = "Bearer " + getArguments().getString(ARG_TOKEN);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        apiService.getMemberships(token).enqueue(new Callback<List<MembershipModel.Membership>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<MembershipModel.Membership>> call, retrofit2.Response<List<MembershipModel.Membership>> response) {
                if (response.isSuccessful()) {
                    membershipList.clear();
                    membershipList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    // Manejar error en la respuesta
                    Snackbar.make(getView(), "Error al obtener membresías", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MembershipModel.Membership>> call, Throwable t) {
                // Manejar error en la petición
                Snackbar.make(getView(), "Error en la solicitud", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public static MembershipsAdminFragment newInstance(String token) {
        MembershipsAdminFragment fragment = new MembershipsAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

}