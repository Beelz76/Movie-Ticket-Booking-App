package com.example.myproject.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myproject.PutData;
import com.example.myproject.R;
import com.example.myproject.models.Ticket;

import java.util.ArrayList;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    Context context;
    private ArrayList<Ticket> tickets;

    public TicketAdapter(Context context, ArrayList<Ticket> tickets) {
        this.context = context;
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public TicketAdapter.TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_item, parent, false);
        return new TicketAdapter.TicketViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TicketAdapter.TicketViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Ticket ticket = tickets.get(position);
        holder.movieTitle.setText(ticket.getMovieTitle());

        Glide.with(holder.movieImage.getContext())
                .load(ticket.getMovieImage())
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_image_24)
                .into(holder.movieImage);

        holder.movieDate.setText("Дата: " + ticket.getDate());
        holder.movieDuration.setText("Время: " + ticket.getStartTime() + " - " + ticket.getEndTime());
        holder.price.setText("Цена: " + ticket.getPrice());
        holder.hallName.setText("Зал: " + ticket.getHallName());
        holder.row.setText("Ряд: " + ticket.getRow());
        holder.number.setText("Место: " + ticket.getNumber());

        holder.buttonReturnTicket.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Вы уверены, что хотите вернуть билет?")
                    .setPositiveButton("Да", (dialog, id) -> deleteTicket(String.valueOf(tickets.get(position).getTicketId()), success -> {
                        if (success && !tickets.isEmpty()) {
                            tickets.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount());
                            Toast.makeText(context.getApplicationContext(), "Вы вернули билет", Toast.LENGTH_SHORT).show();
                        }
                    }))
                    .setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        ImageView movieImage;
        TextView movieTitle, movieDate, movieDuration, price, hallName, row, number;
        Button buttonReturnTicket;
        public TicketViewHolder(@NonNull View view) {
            super(view);
            movieImage = view.findViewById(R.id.imageMovieTicket);
            movieTitle = view.findViewById(R.id.textMovieTitleTicket);
            movieDate = view.findViewById(R.id.textMovieDateTicket);
            movieDuration = view.findViewById(R.id.textMovieDurationTicket);
            price = view.findViewById(R.id.textPriceTicket);
            hallName = view.findViewById(R.id.textHallNameTicket);
            row = view.findViewById(R.id.textRowTicket);
            number = view.findViewById(R.id.textNumberTicket);
            buttonReturnTicket = view.findViewById(R.id.buttonReturnTicket);
        }
    }

    public interface DeleteTicketCallback {
        void onResult(boolean success);
    }

    public void deleteTicket(String ticketId, DeleteTicketCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = {"TicketId"};
            String[] data = {ticketId};
            PutData putData = new PutData(
                    "http://192.168.1.75/scripts/Ticket/delete_ticket.php",
                    "POST", field, data
            );

            boolean success = false;
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    if (result.contains("Delete Ticket Success")) {
                        success = true;
                    } else {
                        Toast.makeText(context.getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            callback.onResult(success);
        });
    }

}
