package com.utt.gymbros;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.utt.gymbros.model.MembershipModel;

public class FullscreenDialogEditMembership extends AppCompatDialogFragment {
    public static final String ARG_MEMBERSHIP = "membership";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_membership_admin, null); // Tu layout XML

        // Obtener referencias a los elementos del layout
        MaterialToolbar toolbar = dialogView.findViewById(R.id.toolbar);
        TextInputEditText productNameEditText = dialogView.findViewById(R.id.productNameEditText);
        TextInputEditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
        TextInputEditText priceEditText = dialogView.findViewById(R.id.priceEditText);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);
        MaterialButton saveButton = dialogView.findViewById(R.id.saveButton);

        // Cargar datos de la membresía en los campos de texto

        // Manejar eventos de clic de los botones
        toolbar.setNavigationOnClickListener(v -> dismiss());
        cancelButton.setOnClickListener(v -> dismiss());
        saveButton.setOnClickListener(v -> {
            // Obtener los valores editados de los campos de texto
            // Actualizar el modelo membership y el adapter
            // Cerrar el diálogo (dismiss())
        });

        builder.setView(dialogView);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            // Configurar el diálogo a pantalla completa (si es necesario)
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
