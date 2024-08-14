package com.utt.gymbros;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.UserModel;
import com.utt.gymbros.model.UserVisitModel;
import com.utt.gymbros.model.VisitUserModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StartUserFragment extends Fragment {

    public static final String ARG_TOKEN = "token";
    private TextView welcome_message;
    private TextView membership_message;
    private TextView membership_end_date;

    private RecyclerView recyclerView;
    private UserVisitAdapter userVisitAdapter;
    private List<UserVisitModel.Visit> visitList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            String token = getArguments().getString(ARG_TOKEN);
        }
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

    private void fetchUserVisits() {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        String token = "Bearer " + getArguments().getString(ARG_TOKEN);

        Call<List<UserVisitModel.Visit>> call = apiService.getUserVisits(token);
        call.enqueue(new Callback<List<UserVisitModel.Visit>>() {
            @Override
            public void onResponse(Call<List<UserVisitModel.Visit>> call, Response<List<UserVisitModel.Visit>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserVisitModel.Visit> visits = response.body();
                    // Actualiza la lista y notifica al adaptador
                    visitList.clear();
                    visitList.addAll(visits);
                    userVisitAdapter.notifyDataSetChanged();

                    // Log para depuración
                    for (UserVisitModel.Visit visit : visits) {
                        Log.d(TAG, "onResponse: " + visit.getName());
                    }
                } else {
                    // Maneja el error
                    Log.e(TAG, "Error: " + response.code() + " " + response.message());
                    Toast.makeText(getContext(), "Error al obtener las visitas del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserVisitModel.Visit>> call, Throwable t) {
                // Maneja la falla
                Log.e(TAG, "Error al obtener las visitas del usuario onFailure", t);
                Toast.makeText(getContext(), "Error al obtener las visitas del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_user, container, false);

        // Inicializa los TextViews
        welcome_message = view.findViewById(R.id.welcome_message);
        membership_message = view.findViewById(R.id.membership_status);
        membership_end_date = view.findViewById(R.id.remaining_days);

        // Inicializa el RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_visits); // Asegúrate de que este ID coincida con el de tu XML
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userVisitAdapter = new UserVisitAdapter(getContext(), visitList);
        recyclerView.setAdapter(userVisitAdapter);

        // Fetch user data and visits
        fetchUserData();
        fetchUserVisits();

        return view;
    }

    public static StartUserFragment newInstance(String token) {
        StartUserFragment fragment = new StartUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }
}