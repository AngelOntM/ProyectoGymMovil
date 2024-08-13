package com.utt.gymbros;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.utt.gymbros.api.ApiClient;
import com.utt.gymbros.api.ApiService;
import com.utt.gymbros.model.ClienteModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListUserAdminFragment extends Fragment {

    private static final String ARG_TOKEN = "token";
    private RecyclerView clienteRecyclerView;
    private ClienteAdapter clienteAdapter;
    private List<ClienteModel.Cliente> clienteList;

    androidx.appcompat.app.AlertDialog progressDialogLogout;

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
        View view = inflater.inflate(R.layout.fragment_list_user_admin, container, false);

        clienteRecyclerView = view.findViewById(R.id.recycler_clientes);
        clienteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        clienteList = new ArrayList<>();
        clienteAdapter = new ClienteAdapter(getContext(), clienteList);
        clienteRecyclerView.setAdapter(clienteAdapter);

        progressDialogLogout = new MaterialAlertDialogBuilder(getContext())
                .setView(R.layout.progress_dialog_waiting)
                .setCancelable(false)
                .create();

        fetchClientes();

        return view;
    }

    public void fetchClientes() {
        progressDialogLogout.show();
        String token = "Bearer " + getArguments().getString(ARG_TOKEN);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ClienteModel.ClienteResponse> call = apiService.getClientes(token);

        call.enqueue(new Callback<ClienteModel.ClienteResponse>() {
            @Override
            public void onResponse(Call<ClienteModel.ClienteResponse> call, Response<ClienteModel.ClienteResponse> response) {
                if (response.isSuccessful()) {
                    ClienteModel.ClienteResponse clienteResponse = response.body();
                    List<ClienteModel.Cliente> clientes = clienteResponse != null ? clienteResponse.getClientes() : new ArrayList<>();
                    clienteList.clear();
                    clienteList.addAll(clientes);
                    clienteAdapter.notifyDataSetChanged();
                    progressDialogLogout.dismiss();
                } else {
                    Toast.makeText(getContext(), "Error al obtener la lista de clientes", Toast.LENGTH_SHORT).show();
                    progressDialogLogout.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ClienteModel.ClienteResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error en onFailure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialogLogout.dismiss();
            }
        });
    }

    public static ListUserAdminFragment newInstance(String token) {
        ListUserAdminFragment fragment = new ListUserAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }
}
