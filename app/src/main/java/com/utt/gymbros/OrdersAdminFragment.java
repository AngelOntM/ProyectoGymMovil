package com.utt.gymbros;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.core.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.OrderModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

    private TextInputEditText dateRangeInput;
    private SimpleDateFormat displayFormat; // Formato para mostrar en la UI
    private SimpleDateFormat apiFormat; // Formato para enviar en la API
    private String startDate, endDate;

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

        dateRangeInput = view.findViewById(R.id.date_range_input);

        // Define el formato de fecha para mostrar en la UI
        displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        // Define el formato de fecha para enviar en la API
        apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        setupDatePickers();

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

    private void setupDatePickers() {
        dateRangeInput.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("Selecciona el rango de fechas");

            // Configurar la zona horaria a la de México
            TimeZone timeZone = TimeZone.getTimeZone("America/Mexico_City");
            Calendar calendar = Calendar.getInstance(timeZone);
            long today = calendar.getTimeInMillis();

            builder.setSelection(Pair.create(today, today));
            builder.setCalendarConstraints(new CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now()) // Permitir fechas pasadas
                    .build());

            MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

            picker.addOnPositiveButtonClickListener(selection -> {
                calendar.setTimeZone(timeZone);

                // Obtener la fecha actual para compararla
                String todayDate = apiFormat.format(calendar.getTime());

                // Ajustar fecha de inicio
                calendar.setTimeInMillis(selection.first);
                startDate = apiFormat.format(calendar.getTime());
                String startDateDisplay = displayFormat.format(calendar.getTime());

                // Ajustar fecha de fin
                calendar.setTimeInMillis(selection.second);
                endDate = apiFormat.format(calendar.getTime());
                String endDateDisplay = displayFormat.format(calendar.getTime());

                // Si la fecha de inicio no es hoy, sumar un día
                if (!startDate.equals(todayDate)) {
                    calendar.setTimeInMillis(selection.first);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    startDate = apiFormat.format(calendar.getTime());
                    startDateDisplay = displayFormat.format(calendar.getTime());
                }

                // Si la fecha de fin no es hoy, sumar un día
                if (!endDate.equals(todayDate)) {
                    calendar.setTimeInMillis(selection.second);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    endDate = apiFormat.format(calendar.getTime());
                    endDateDisplay = displayFormat.format(calendar.getTime());
                }

                // Mostrar las fechas seleccionadas
                if (startDate.equals(endDate)) {
                    dateRangeInput.setText(startDateDisplay);
                } else {
                    dateRangeInput.setText(startDateDisplay + " - " + endDateDisplay);
                }

                // Hacer la solicitud a la API después de seleccionar las fechas
                fetchOrders();
            });

            picker.show(getParentFragmentManager(), picker.toString());
        });

        // Inicializar las fechas con formato de visualización
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City"));
        startDate = apiFormat.format(now.getTime());
        endDate = apiFormat.format(now.getTime());
        dateRangeInput.setText(displayFormat.format(now.getTime()));
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
        apiService.getAllOrders(token, startDate, endDate).enqueue(new Callback<List<OrderModel.Order>>() {
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
