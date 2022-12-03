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
import com.example.testfirebase.models.User;

import java.util.ArrayList;

public class AdapterUsuarios extends RecyclerView.Adapter<AdapterUsuarios.ViewHolderUsuarios> {

    ArrayList<User> usuarios;
    LayoutInflater inflater;

    ImageView usuarioImagen;
    TextView usuarioN, usuarioDescripcion;


    public AdapterUsuarios(Context context, ArrayList<User> usuarios){
        this.usuarios = usuarios;
        this.inflater = LayoutInflater.from(context);

    }


    @NonNull
    @Override
    public AdapterUsuarios.ViewHolderUsuarios onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.usuarios_grid_layout, parent, false);
        return new ViewHolderUsuarios(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUsuarios.ViewHolderUsuarios holder, int position) {
        if(!usuarios.get(position).getUsuario().equals("")){
            holder.usuarioN.setText(usuarios.get(position).getUsuario());
        }else{
            holder.usuarioN.setText(usuarios.get(position).getEmail());
        }

        if(!usuarios.get(position).getBiografia().equals("")){
            holder.usuarioDescripcion.setText(usuarios.get(position).getBiografia());
        }else{
            holder.usuarioDescripcion.setText("Este usuario no tiene descripción.");
        }

        try {
            if(!usuarios.get(position).getFoto().equals("")){
                Glide.with(holder.usuarioImagen)
                        .load(usuarios.get(position).getFoto())
                        .into(holder.usuarioImagen);
            }else{
                Glide.with(holder.usuarioImagen)
                        .load(R.drawable.ic_baseline_person_24)
                        .into(holder.usuarioImagen);
            }
        } catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public class ViewHolderUsuarios extends RecyclerView.ViewHolder {
        ImageView usuarioImagen;
        TextView usuarioN;
        TextView usuarioDescripcion;


        public ViewHolderUsuarios(@NonNull View itemView) {
            super(itemView);
            usuarioImagen = itemView.findViewById(R.id.usuarioImagen);
            usuarioN = itemView.findViewById(R.id.usuarioN);
            usuarioDescripcion = itemView.findViewById(R.id.descripcionN);

        }
    }
}
