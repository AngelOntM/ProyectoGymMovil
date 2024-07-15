package com.utt.gymbros;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.VisitUserModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitUserAdminFragment extends Fragment {

    private static final String ARG_TOKEN = "token";
    private static final int FETCH_INTERVAL_MS = 10000; // Intervalo de 10 segundos

    private RecyclerView visitRecyclerView;
    private VisitAdapter visitAdapter;
    private List<VisitUserModel.Visit> visitList;
    private Handler handler;
    private Runnable fetchTask;
    private Snackbar snackbar;

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
        View view = inflater.inflate(R.layout.fragment_visit_user_admin, container, false);

        visitRecyclerView = view.findViewById(R.id.recycler_visits);
        visitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        visitList = new ArrayList<>();
        visitAdapter = new VisitAdapter(getContext(), visitList);
        visitRecyclerView.setAdapter(visitAdapter);

        handler = new Handler(Looper.getMainLooper());
        fetchTask = this::fetchVisits;

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

    private void fetchVisits() {
        String token = "Bearer " + getArguments().getString(ARG_TOKEN);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        apiService.getAllVisits(token).enqueue(new Callback<List<VisitUserModel.Visit>>() {
            @Override
            public void onResponse(Call<List<VisitUserModel.Visit>> call, Response<List<VisitUserModel.Visit>> response) {
                if (response.isSuccessful()) {
                    visitList.clear();
                    visitList.addAll(response.body());
                    visitAdapter.notifyDataSetChanged();

                    if (snackbar != null && snackbar.isShown()) {
                        snackbar.dismiss();
                    }
                } else {
                    showPersistentSnackbar("Error al obtener visitas");
                }

                scheduleNextFetch();
            }

            @Override
            public void onFailure(Call<List<VisitUserModel.Visit>> call, Throwable t) {
                showPersistentSnackbar("Error en la solicitud de visitas");
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

    public static VisitUserAdminFragment newInstance(String token) {
        VisitUserAdminFragment fragment = new VisitUserAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }
}
