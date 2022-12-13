package com.example.testfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class PopupResigtrarse extends AppCompatActivity {

    private static final int REGISTER = 11110000;
    Button cancelarBoton, crearBoton;
    EditText usuario, email, password;
    String emailT, usuarioT, passwordT;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_resigtrarse);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*.62));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);


        db = FirebaseFirestore.getInstance();

        usuario = findViewById(R.id.textViewUser);
        email = findViewById(R.id.textViewCorreo);
        password = findViewById(R.id.textViewContra);



    }

    //Login con correo y contraseña
    public void signup(View view){
        emailT = email.getText().toString();
        passwordT = email.getText().toString();
        usuarioT = usuario.getText().toString();

        if(!emailT.matches("") && !passwordT.matches("") && !usuarioT.matches("")){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                guardarUsuario("BASIC",email.getText().toString());
                                successI(email.getText().toString(), "BASIC");
                            }else {
                                Snackbar.make(findViewById(android.R.id.content), "Error al autenticar el usuario, pruebe con otro correo", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }else{
            Snackbar.make(findViewById(android.R.id.content), "Tienes que rellenar todos los campos", Snackbar.LENGTH_LONG).show();
        }
    }

    public void guardarUsuario(String provider, String email){ //Funcion apuntes para guardar usuario y campos
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("email", email);
        hashMap.put("provider", provider);
        hashMap.put("usuario", usuarioT);
        hashMap.put("nombre", "");
        hashMap.put("apellidos", "");
        hashMap.put("biografia", "");
        hashMap.put("foto", "");

        db.collection("users").document(email).set(hashMap);
    }

    public void successI(String email, String providerType){
        Intent i = new Intent(this, HomeActivityD.class);
        i.putExtra("email", email);
        i.putExtra("provider", providerType);
        startActivityForResult(i , REGISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REGISTER){
            finish();
        }
    }

    public void finish(View view){
        finish();
    }
}