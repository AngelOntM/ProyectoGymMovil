package com.utt.gymbros;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.OrderModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersAdminFragment extends Fragment {

    private static final String ARG_TOKEN = "token";
    private static final int FETCH_INTERVAL_MS = 10000; // Intervalo de 10 segundos

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderModel.Order> orderList;
    private Handler handler;
    private Runnable fetchTask;
    private Snackbar snackbar;
    private Button btnAddOrder;

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
        View view = inflater.inflate(R.layout.fragment_orders_admin, container, false);

        orderRecyclerView = view.findViewById(R.id.recycler_orders);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getContext(), orderList);
        orderRecyclerView.setAdapter(orderAdapter);

        handler = new Handler(Looper.getMainLooper());
        fetchTask = this::fetchOrders;

        startFetching();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopFetching();
    }

    private void startFetching() {
        handler.post(fetchTask);
    }

    private void stopFetching() {
        handler.removeCallbacks(fetchTask);
    }

    private void fetchOrders() {
        String token = "Bearer " + getArguments().getString(ARG_TOKEN);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        apiService.getAllOrders(token).enqueue(new Callback<List<OrderModel.Order>>() {
            @Override
            public void onResponse(Call<List<OrderModel.Order>> call, Response<List<OrderModel.Order>> response) {
                if (response.isSuccessful()) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    orderAdapter.notifyDataSetChanged();

                    if (snackbar != null && snackbar.isShown()) {
                        snackbar.dismiss();
                    }
                } else {
                    showPersistentSnackbar("Error al obtener órdenes");
                }

                scheduleNextFetch();
            }

            @Override
            public void onFailure(Call<List<OrderModel.Order>> call, Throwable t) {
                showPersistentSnackbar("Error en la solicitud de órdenes");
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

    public static OrdersAdminFragment newInstance(String token) {
        OrdersAdminFragment fragment = new OrdersAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }
}
