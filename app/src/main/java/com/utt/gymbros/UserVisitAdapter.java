package com.utt.gymbros;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utt.gymbros.model.UserVisitModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserVisitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_EMPTY = 0;
    private static final int VIEW_TYPE_VISIT = 1;

    private Context context;
    private List<UserVisitModel.Visit> visitList;

    public UserVisitAdapter(Context context, List<UserVisitModel.Visit> visitList) {
        this.context = context;
        this.visitList = visitList;
    }

    @Override
    public int getItemViewType(int position) {
        if (visitList.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_VISIT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_user_visit_empty, parent, false);
            return new EmptyViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_user_visit, parent, false);
            return new UserVisitViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserVisitViewHolder) {
            UserVisitModel.Visit visit = visitList.get(position);

            // Formatea y asigna la fecha de la visita
            ((UserVisitViewHolder) holder).visitDateTextView.setText(formatDate(visit.getVisitDate()));
            // Formatea y asigna la hora de check-in
            ((UserVisitViewHolder) holder).checkInTimeTextView.setText(formatTime(visit.getCheckInTime()));
        }
    }

    @Override
    public int getItemCount() {
        return visitList.isEmpty() ? 1 : visitList.size();
    }

    private String formatDate(String dateStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        try {
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    private String formatTime(String timeStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Date time = inputFormat.parse(timeStr);
            return outputFormat.format(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return timeStr;
        }
    }

    static class UserVisitViewHolder extends RecyclerView.ViewHolder {

        TextView visitDateTextView;
        TextView checkInTimeTextView;

        UserVisitViewHolder(View itemView) {
            super(itemView);
            visitDateTextView = itemView.findViewById(R.id.text_visit_date);
            checkInTimeTextView = itemView.findViewById(R.id.text_check_in_time);
        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
