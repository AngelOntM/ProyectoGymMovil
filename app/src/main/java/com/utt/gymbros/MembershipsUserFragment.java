package com.utt.gymbros;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.OrderModel;
import com.utt.gymbros.model.UserModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembershipsUserFragment extends Fragment {

    public static final String ARG_TOKEN = "token";
    private static final int FETCH_INTERVAL_MS = 10000; // Intervalo de 10 segundos

    private Button btnBuyMembership;
    private RecyclerView recyclerView;
    private MembershipOrderAdapter adapter;
    private TextView welcome_message;
    private TextView membership_message;
    private TextView membership_end_date;
    private List<OrderModel.OrderUserDetailResponse> orderUserDetailList = new ArrayList<>();
    private Handler handler;
    private Runnable fetchTask;
    private androidx.appcompat.app.AlertDialog progressDialogWaiting;
    private boolean isProgressDialogShown = false; // Variable para rastrear la visualización del diálogo

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        fetchTask = this::fetchOrders;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memberships_user, container, false);

        welcome_message = view.findViewById(R.id.welcome_message);
        membership_message = view.findViewById(R.id.membership_status);
        membership_end_date = view.findViewById(R.id.remaining_days);

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

        fetchUserData();
        startFetching();

        return view;
    }

    private void startFetching() {
        handler.post(fetchTask);
    }

    private void stopFetching() {
        handler.removeCallbacks(fetchTask);
    }

    private void fetchOrders() {
        if (!isProgressDialogShown) {
            progressDialogWaiting.show();
            isProgressDialogShown = true;
        }
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        String token = getArguments().getString(ARG_TOKEN);
        Call<List<OrderModel.OrderUserResponse>> call = apiService.getOrdersUser("Bearer " + token);
        call.enqueue(new Callback<List<OrderModel.OrderUserResponse>>() {
            @Override
            public void onResponse(Call<List<OrderModel.OrderUserResponse>> call, Response<List<OrderModel.OrderUserResponse>> response) {
                if (response.isSuccessful()) {
                    List<OrderModel.OrderUserResponse> orderUserResponseList = response.body();
                    if (orderUserResponseList != null) {
                        orderUserDetailList.clear();
                        // Filter orders with status "Pagada" and fetch their details
                        for (OrderModel.OrderUserResponse order : orderUserResponseList) {
                            if ("Pagada".equals(order.getEstado())) {
                                fetchOrderDetails(order.getId());
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (progressDialogWaiting.isShowing()) {
                        progressDialogWaiting.dismiss();
                    }
                } else {
                    Toast.makeText(getContext(), "Error al obtener las órdenes", Toast.LENGTH_SHORT).show();
                    if (progressDialogWaiting.isShowing()) {
                        progressDialogWaiting.dismiss();
                    }
                }
                scheduleNextFetch();
            }

            @Override
            public void onFailure(Call<List<OrderModel.OrderUserResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al obtener las órdenes", Toast.LENGTH_SHORT).show();
                if (progressDialogWaiting.isShowing()) {
                    progressDialogWaiting.dismiss();
                }
                scheduleNextFetch();
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

    private void fetchUserData() {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        String token = getArguments().getString(ARG_TOKEN);
        Call<UserModel.UserResponse> call = apiService.getUser("Bearer " + token);
        call.enqueue(new Callback<UserModel.UserResponse>() {
            @Override
            public void onResponse(Call<UserModel.UserResponse> call, Response<UserModel.UserResponse> response) {
                if (response.isSuccessful()) {
                    UserModel.UserResponse userResponse = response.body();
                    if (userResponse != null) {
                        UserModel.User user = userResponse.getUser();
                        UserModel.ActiveMembership activeMembership = userResponse.getActiveMembership();
                        welcome_message.setText("¡Bienvenido, " + user.getName() + "!");

                        if (activeMembership != null) {
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                LocalDate startDate = LocalDateTime.parse(activeMembership.getStartDate(), formatter).toLocalDate();
                                LocalDate endDate = LocalDateTime.parse(activeMembership.getEndDate(), formatter).toLocalDate();

                                long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), endDate);

                                membership_message.setTextColor(Color.WHITE);
                                membership_message.setText("Membresía Activa: ");
                                membership_message.append(Html.fromHtml("<font color='#80FF80'>" + activeMembership.getMembershipName() + "</font>"));

                                membership_end_date.setTextColor(Color.WHITE);
                                membership_end_date.setText("Días Restantes: ");
                                membership_end_date.append(Html.fromHtml("<font color='#80FF80'>" + daysRemaining + "</font>"));
                            } catch (DateTimeParseException e) {
                                e.printStackTrace();
                                Snackbar.make(getView(), "Error al parsear las fechas de la membresía", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        } else {
                            membership_message.setTextColor(Color.parseColor("#e24c5e"));
                            membership_message.setText("No tienes una membresía activa");
                            membership_end_date.setText(""); // Deja el campo de días restantes vacío si no hay membresía activa
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserModel.UserResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scheduleNextFetch() {
        handler.postDelayed(fetchTask, FETCH_INTERVAL_MS);
    }

    private void openBuyMembershipDialog() {
        String token = getArguments().getString(ARG_TOKEN);
        FullScreenDialogBuyMembership dialog = FullScreenDialogBuyMembership.newInstance(token);
        dialog.show(getParentFragmentManager(), "FullScreenDialogBuyMembership");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopFetching();
    }

    public static MembershipsUserFragment newInstance(String token) {
        MembershipsUserFragment fragment = new MembershipsUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }
}
