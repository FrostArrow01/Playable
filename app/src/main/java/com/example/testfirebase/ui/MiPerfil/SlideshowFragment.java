package com.example.testfirebase.ui.MiPerfil;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.testfirebase.R;
import com.example.testfirebase.databinding.FragmentSlideshowBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private EditText emailE, usuarioE, nombreE, apellidosE, biografiaE;
    private Button botonG;
    private FirebaseFirestore db;
    private String  emailPre, providerPre, usuario, nombre, apellidos, biografia;
    private final static int CERRAR_POPUP = 101;
    private SharedPreferences preferences;
    private TextView usuarioDrawer, biografiaDrawer;
    private ImageView imageButton;
    private int TAKE_IMAGE_CODE = 10001;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        preferences = (SharedPreferences) this.getActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        emailPre = preferences.getString("email", null);
        providerPre = preferences.getString("provider", null);

        db.collection("users").document(emailPre).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot it) {
                        usuario = it.get("usuario").toString();
                        nombre = it.get("nombre").toString();
                        apellidos = it.get("apellidos").toString();
                        biografia = it.get("biografia").toString();

                        emailE = view.findViewById(R.id.emailE);
                        emailE.setText(emailPre);
                        usuarioE = view.findViewById(R.id.usuarioE);
                        usuarioE.setText(usuario);
                        nombreE = view.findViewById(R.id.nombreE);
                        nombreE.setText(nombre);
                        apellidosE = view.findViewById(R.id.apellidosE);
                        apellidosE.setText(apellidos);
                        biografiaE = view.findViewById(R.id.biografiaE);
                        biografiaE.setText(biografia);

                    }
                });

        botonG = view.findViewById(R.id.guardarB);
        botonG.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(usuarioE.getText().toString().matches("") || nombreE.getText().toString().matches("") || apellidosE.getText().toString().matches("") || biografiaE.getText().toString().matches("")){
                    Snackbar.make(view, "Asegurate de rellenar todos los campos", Snackbar.LENGTH_LONG).show();
                }else{
                    try {
                        db.collection("users").document(emailPre).update("usuario", usuarioE.getText().toString());
                        db.collection("users").document(emailPre).update("nombre", nombreE.getText().toString());
                        db.collection("users").document(emailPre).update("apellidos", apellidosE.getText().toString());
                        db.collection("users").document(emailPre).update("biografia", biografiaE.getText().toString());
                        Snackbar.make(view, "Los datos se han guardado correctamente", Snackbar.LENGTH_LONG).show();


                    }catch (Exception e){
                        Snackbar.make(view, "¡Ha habido un error al guardar los datos!", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        imageButton = view.findViewById(R.id.profileImageB);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getPhotoUrl() != null){
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(imageButton);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE){
            switch (resultCode){
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    imageButton.setImageBitmap(bitmap);
                    handleUpload(bitmap);
            }
        }
    }

    private void handleUpload(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("FotosPerfilUsuarios")
                .child(uid + ".jpeg");

        reference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadUrl(reference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(getView(), "¡Ha habido un error al guardar la imagen!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setUserProfileUrl(uri);
            }
        });
    }

    private void setUserProfileUrl(Uri uri){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Snackbar.make(getView(), "Foto actualizada correctamente", Snackbar.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(getView(), "¡Ha habido un error al actualizar la foto!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}