package com.example.testfirebase;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.testfirebase.models.Album;
import com.example.testfirebase.models.Cancion;
import com.example.testfirebase.ui.MiPerfil.SlideshowFragment;
import com.example.testfirebase.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    String albumesS;
    String[] albumesA;
    ArrayList<String> albumesDespues = new ArrayList<>();
    Boolean presente = false;

    QuerySnapshot albumes;
    ArrayList<Album> albumesList = new ArrayList<>();
    ArrayList<Album> todosAlbumes = new ArrayList<>();

    LayoutInflater inflater;
    Album album;
    String emailPre;

    public Adapter(Context context, QuerySnapshot albumes, String emailPre){
        this.albumes = albumes;
        this.emailPre = emailPre;
        for (int i=0;i<albumes.size();i++){ //conversion de QuerySnapshot a objectos Album
            album = new Album();
            album.setCaratula(albumes.getDocuments().get(i).get("caratula").toString());
            album.setTitulo(albumes.getDocuments().get(i).get("tituloAlbum").toString());
            albumesList.add(album);
            todosAlbumes.add(album);
        }

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
        holder.tituloAlbum.setText(albumesList.get(position).getTitulo());
        holder.imagenAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), CancionesActivity.class);
                i.putExtra("tituloAlbum", albumesList.get(position).getTitulo());
                i.putExtra("caratula", albumesList.get(position).toString());
                view.getContext().startActivity(i);
            }
        });
        holder.imagenAlbum.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(view.getContext(), "Ahora tu album favorito es: "+holder.tituloAlbum.getText(), Toast.LENGTH_SHORT).show();
                getandSetAlbumes(holder,position);

                return true;
            }
        });
        
       if(!albumesList.get(position).getCaratula().equals("")){
           Glide.with(holder.imagenAlbum)
                   .load(albumesList.get(position).getCaratula())
                   .into(holder.imagenAlbum);
        }

    }

    @Override
    public int getItemCount() {
        return albumesList.size();
    }


    Filter filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Album> listaFiltrada = new ArrayList<>();
            if(charSequence.toString().isEmpty()){
                listaFiltrada = todosAlbumes;
            }else{
                for (Album albumi: albumesList) {
                    if(albumi.getTitulo().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        listaFiltrada.add(albumi);
                    }else{
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = listaFiltrada;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            albumesList.clear();
            albumesList.addAll((Collection<? extends Album>) filterResults.values);
            notifyDataSetChanged();
        }
    };
    @Override
    public Filter getFilter() {
        return filter;
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

    public void getandSetAlbumes(ViewHolder holder, int position){
        db.collection("users").document(emailPre).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot it) {


                    }
                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        db.collection("users").document(emailPre).update("albumes", albumes.getDocuments().get(position).get("tituloAlbum"));
                    }
                });
    }
}
