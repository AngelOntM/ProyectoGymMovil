package com.utt.gymbros;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utt.gymbros.model.VisitUserModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VisitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<VisitUserModel.Visit> visitList;
    private static final int VIEW_TYPE_VISIT = 1;
    private static final int VIEW_TYPE_EMPTY = 0;

    public VisitAdapter(Context context, List<VisitUserModel.Visit> visitList) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visit_empty, parent, false);
            return new EmptyViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_visit, parent, false);
            return new VisitViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VisitViewHolder) {
            VisitUserModel.Visit visit = visitList.get(position);
            ((VisitViewHolder) holder).visitDateTextView.setText(formatDate(visit.getVisitDate()));
            ((VisitViewHolder) holder).checkInTimeTextView.setText(formatTime(visit.getCheckInTime()));
            ((VisitViewHolder) holder).userNameTextView.setText(visit.getUser().getName());
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

    static class VisitViewHolder extends RecyclerView.ViewHolder {

        TextView visitDateTextView;
        TextView checkInTimeTextView;
        TextView userNameTextView;

        VisitViewHolder(View itemView) {
            super(itemView);
            visitDateTextView = itemView.findViewById(R.id.visit_date);
            checkInTimeTextView = itemView.findViewById(R.id.check_in_time);
            userNameTextView = itemView.findViewById(R.id.user_name);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
