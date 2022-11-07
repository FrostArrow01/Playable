package com.example.testfirebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testfirebase.models.Cancion;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    QuerySnapshot albumes;
    LayoutInflater inflater;

    public Adapter(Context context, QuerySnapshot albumes){
        this.albumes = albumes;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.albumes_grid_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tituloAlbum.setText(albumes.getDocuments().get(position).get("tituloAlbum").toString());
        holder.imagenAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Has pulsado en: "+holder.tituloAlbum.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.imagenAlbum.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(view.getContext(), "Has mantenido pulsado: "+holder.tituloAlbum.getText(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        
       if(!albumes.getDocuments().get(position).get("caratula").equals("")){
           Glide.with(holder.imagenAlbum)
                   .load(albumes.getDocuments().get(position).get("caratula"))
                   .into(holder.imagenAlbum);
        }

    }

    @Override
    public int getItemCount() {
        return albumes.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{
        TextView tituloAlbum;
        ImageView imagenAlbum;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloAlbum = itemView.findViewById(R.id.tituloAlbum);
            imagenAlbum = itemView.findViewById(R.id.imagenAlbum);
        }
    }
}
