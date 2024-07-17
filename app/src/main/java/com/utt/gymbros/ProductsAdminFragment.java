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
import com.utt.gymbros.model.ProductModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsAdminFragment extends Fragment {

    private static final String ARG_TOKEN = "token";
    private static final int FETCH_INTERVAL_MS = 10000; // Intervalo de 10 segundos

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<ProductModel.Product> productList;
    private Handler handler;
    private Runnable fetchTask;
    private Snackbar snackbar;
    private Button btnAddProduct;

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
        View rootView = inflater.inflate(R.layout.fragment_products_admin, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_products);
        btnAddProduct = rootView.findViewById(R.id.btn_add_product);
        btnAddProduct.setOnClickListener(v -> openCreateProductDialog());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        adapter = new ProductAdapter(getContext(), productList, this);
        recyclerView.setAdapter(adapter);

        handler = new Handler(Looper.getMainLooper());
        fetchTask = this::fetchProducts;

        startFetching();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopFetching();
    }

    private void openCreateProductDialog() {
        String token = getArguments().getString(ARG_TOKEN);
        FullScreenDialogCreateProduct dialogFragment = FullScreenDialogCreateProduct.newInstance(token);
        dialogFragment.show(getChildFragmentManager(), "FullScreenDialogCreateProduct");
    }

    private void startFetching() {
        handler.post(fetchTask);
    }

    private void stopFetching() {
        handler.removeCallbacks(fetchTask);
    }

    private void fetchProducts() {
        String token = "Bearer " + getArguments().getString(ARG_TOKEN);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        apiService.getProducts(token).enqueue(new Callback<List<ProductModel.Product>>() {
            @Override
            public void onResponse(Call<List<ProductModel.Product>> call, Response<List<ProductModel.Product>> response) {
                if (response.isSuccessful()) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (snackbar != null && snackbar.isShown()) {
                        snackbar.dismiss();
                    }
                } else {
                    showPersistentSnackbar("Error al obtener productos");
                }

                scheduleNextFetch();
            }

            @Override
            public void onFailure(Call<List<ProductModel.Product>> call, Throwable t) {
                showPersistentSnackbar("Error en la solicitud: " + t.getMessage());
                scheduleNextFetch();
            }
        });
    }

    private void scheduleNextFetch() {
        handler.postDelayed(fetchTask, FETCH_INTERVAL_MS);
    }

    private void showPersistentSnackbar(String message) {
        View rootView = getView();
        if (rootView != null) {
            if (snackbar == null) {
                snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
            } else {
                snackbar.setText(message);
            }
            snackbar.show();
        }
    }

    public static ProductsAdminFragment newInstance(String token) {
        ProductsAdminFragment fragment = new ProductsAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    public void updateProduct(ProductModel.Product updatedProduct) {
        int position = -1;
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId() == updatedProduct.getId()) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            productList.set(position, updatedProduct);
            adapter.notifyItemChanged(position);
        }
    }
}
