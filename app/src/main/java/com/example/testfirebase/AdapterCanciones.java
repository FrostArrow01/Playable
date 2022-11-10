package com.example.testfirebase;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testfirebase.Utils.Utils;
import com.example.testfirebase.models.Cancion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdapterCanciones extends RecyclerView.Adapter<AdapterCanciones.ViewHolder> {

    private List<Cancion> cancionesList;
    private Context context;
    private List<String> duraciones = new ArrayList<>();
    private List<Long> duracionesLong = new ArrayList<>();

    private String caratula;
    private LayoutInflater inflater;
    private RecyclerItemClickListener listener;
    private int selectedPosition;
    private ProgressBar progressBar, pb_loader;
    private TextView tb_title, tv_duration;


    private MediaPlayer mediaPlayer = new MediaPlayer();

    public AdapterCanciones(Context context, List<Cancion> cancionesList, String caratula, RecyclerItemClickListener listener){
        this.cancionesList = cancionesList;
        this.context = context;
        for (Cancion cancion: cancionesList) { //se recogen la duracion de las canciones en dos formatos
            try {
                duraciones.add(Utils.convertDuration(cancion.getDuration()));
                duracionesLong.add(cancion.getDuration());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.caratula = caratula;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.canciones_grid_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(!caratula.equals("") && !cancionesList.get(position).getTitulo().equals("")){
            Glide.with(holder.imagenCancion)
                    .load(caratula)
                    .into(holder.imagenCancion);

            holder.tituloCancion.setText(cancionesList.get(position).titulo);
            holder.duracionCancion.setText(duraciones.get(position));

            if (selectedPosition == position){
                holder.fondo.setCardBackgroundColor(ContextCompat.getColor(context, R.color.AmarilloF) );
                holder.tituloCancion.setTextColor(ContextCompat.getColor(context, R.color.AzulMarinoF));
                holder.duracionCancion.setTextColor(ContextCompat.getColor(context, R.color.AzulMarinoF));
                holder.estasonando.setVisibility(View.VISIBLE);

            }else{
                holder.fondo.setCardBackgroundColor(ContextCompat.getColor(context, R.color.AzulMarinoFV));
                holder.tituloCancion.setTextColor(ContextCompat.getColor(context, R.color.GrisF));
                holder.duracionCancion.setTextColor(ContextCompat.getColor(context, R.color.GrisF));
                holder.estasonando.setVisibility(View.INVISIBLE);
            }

            holder.bind(cancionesList.get(position), Long.valueOf(duracionesLong.get(position)),listener);


        }
    }

    @Override
    public int getItemCount() {
        if (cancionesList.get(0).getTitulo().toString().equals("")){
            return 0;
        }else{
            return cancionesList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tituloCancion, duracionCancion, estasonando;
        ImageView imagenCancion;
        CardView fondo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloCancion = itemView.findViewById(R.id.tituloCancion);
            imagenCancion = itemView.findViewById(R.id.imagenCancion);
            duracionCancion = itemView.findViewById(R.id.duracionCancion);
            estasonando = itemView.findViewById(R.id.estasonando);
            fondo = itemView.findViewById(R.id.fondo);
        }

        public void bind(Cancion cancion, Long duracion, RecyclerItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        listener.onClickListener(cancion, duracion,getLayoutPosition());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public interface RecyclerItemClickListener{
        void onClickListener(Cancion cancion, Long duracion, int position) throws IOException;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
