package com.example.testfirebase;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import com.example.testfirebase.models.Cancion;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

public class AdapterCanciones extends RecyclerView.Adapter<AdapterCanciones.ViewHolder> {

    private List<Cancion> cancionesList;
    private String caratula;
    private LayoutInflater inflater;
    private Button reproducir;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    public AdapterCanciones(Context context, List<Cancion> cancionesList, String caratula){
        this.cancionesList = cancionesList;
        this.caratula = caratula;
        this.inflater = LayoutInflater.from(context);
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

            holder.reproducir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            if (!mediaPlayer.isPlaying()){
                                mediaPlayer.setDataSource(cancionesList.get(position).url);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                holder.reproducir.setText("Detener");
                            }else{
                                mediaPlayer.stop();
                                mediaPlayer.release();
                                mediaPlayer = null;
                                mediaPlayer = new MediaPlayer();
                                holder.reproducir.setText("Reproducir");
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }

                }
            });


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
        TextView tituloCancion;
        ImageView imagenCancion;
        Button reproducir;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloCancion = itemView.findViewById(R.id.tituloCancion);
            imagenCancion = itemView.findViewById(R.id.imagenCancion);
            reproducir = itemView.findViewById(R.id.button3);
        }
    }

}
