package com.utt.gymbros;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.utt.gymbros.MembershipOrderAdapter;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.OrderModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembershipsUserFragment extends Fragment {

    public static final String ARG_TOKEN = "token";
    androidx.appcompat.app.AlertDialog progressDialogWaiting;

    private Button btnBuyMembership;
    private RecyclerView recyclerView;
    private MembershipOrderAdapter adapter;
    private List<OrderModel.OrderUserDetailResponse> orderUserDetailList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Any setup or initialization can be done here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memberships_user, container, false);

        btnBuyMembership = view.findViewById(R.id.btn_buy_membership);
        btnBuyMembership.setOnClickListener(v -> openBuyMembershipDialog());

        recyclerView = view.findViewById(R.id.recycler_memberships);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MembershipOrderAdapter(orderUserDetailList);
        recyclerView.setAdapter(adapter);

        progressDialogWaiting = new MaterialAlertDialogBuilder(requireContext())
                .setView(R.layout.progress_dialog_waiting)
                .setCancelable(false)
                .create();

        fetchOrders();

        return view;
    }

    private void fetchOrders() {
        progressDialogWaiting.show();
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        String token = getArguments().getString(ARG_TOKEN);
        Call<List<OrderModel.OrderUserResponse>> call = apiService.getOrdersUser("Bearer " + token);
        call.enqueue(new Callback<List<OrderModel.OrderUserResponse>>() {
            @Override
            public void onResponse(Call<List<OrderModel.OrderUserResponse>> call, Response<List<OrderModel.OrderUserResponse>> response) {
                if (response.isSuccessful()) {
                    List<OrderModel.OrderUserResponse> orderUserResponseList = response.body();
                    if (orderUserResponseList != null) {
                        // Filter orders with status "Pagada" and fetch their details
                        for (OrderModel.OrderUserResponse order : orderUserResponseList) {
                            if ("Pagada".equals(order.getEstado())) {
                                fetchOrderDetails(order.getId());
                            }
                        }
                    }
                    progressDialogWaiting.dismiss();
                } else {
                    Toast.makeText(getContext(), "Error al obtener las órdenes", Toast.LENGTH_SHORT).show();
                    progressDialogWaiting.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<OrderModel.OrderUserResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al obtener las órdenes", Toast.LENGTH_SHORT).show();
                progressDialogWaiting.dismiss();
            }
        });
    }

    private void fetchOrderDetails(int orderId) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        String token = getArguments().getString(ARG_TOKEN);
        Call<OrderModel.OrderUserDetailResponse> call = apiService.getOrderDetailUser(orderId, "Bearer " + token);
        call.enqueue(new Callback<OrderModel.OrderUserDetailResponse>() {
            @Override
            public void onResponse(Call<OrderModel.OrderUserDetailResponse> call, Response<OrderModel.OrderUserDetailResponse> response) {
                if (response.isSuccessful()) {
                    OrderModel.OrderUserDetailResponse orderDetailResponse = response.body();
                    if (orderDetailResponse != null) {
                        orderUserDetailList.add(orderDetailResponse);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getContext(), "Error al obtener detalles de la orden", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderModel.OrderUserDetailResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error al obtener detalles de la orden", Toast.LENGTH_SHORT).show();
            }
        });
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
