package com.example.testfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PopUpUsuario extends AppCompatActivity {

    private FirebaseFirestore db;
    private String email;
    private EditText emailE, usuarioE, nombreE, apellidosE, edadE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_usuario);
        db = FirebaseFirestore.getInstance();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.8), (int) (height*.7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;


        //Recogemos el email de la actividad anterior
        Intent i = getIntent();
        email = i.getStringExtra("email");


        //Encontramos los editText para luego recogemos sus valores
        emailE = findViewById(R.id.editTextEmail);
        emailE.setText(email);
        usuarioE = findViewById(R.id.editTextTextUsuario);
        nombreE = findViewById(R.id.editTextTextNombre);
        apellidosE = findViewById(R.id.editTextTextApellidos);
        edadE = findViewById(R.id.editTextTextEdad);
        traerDatos();

    }

    public void traerDatos(){
        db.collection("users").document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot it) {
                        usuarioE.setText(it.get("usuario").toString());
                        nombreE.setText(it.get("nombre").toString());
                        apellidosE.setText(it.get("apellidos").toString());
                        edadE.setText(it.get("edad").toString());
                    }
                });
    }



    public void guardarDatos(View view){
        if(!usuarioE.getText().toString().matches("")){
            db.collection("users").document(email).update("usuario", usuarioE.getText().toString());
            db.collection("users").document(email).update("nombre", nombreE.getText().toString());
            db.collection("users").document(email).update("apellidos", apellidosE.getText().toString());
            db.collection("users").document(email).update("edad", edadE.getText().toString());
            finish();
        }else{
            Toast.makeText(this, "Asegurate de llenar todos los campos", Toast.LENGTH_SHORT).show();
        }



    }

    public void volver(View view){
        finish();
    }
}