package com.example.testfirebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;
import java.util.HashMap;

public class AuthActivity extends AppCompatActivity {
    private String emailR, passwordR, providerR;
    public EditText email, password;
    public Button signup, login;
    private SharedPreferences preferences;
    private GoogleSignInOptions googleConf;
    private GoogleSignInClient googleClient;
    private final static int GOOGLE_SIGN_IN = 100;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        setTitle("Autenticación");
        db = FirebaseFirestore.getInstance();


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signupB);
        login = findViewById(R.id.loginB);

        session();
    }

    //Inicia sesion automaticamente si hay credenciales guardadas en el SharedPreferences
    private void session() {
        preferences = (SharedPreferences) getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        emailR = preferences.getString("email", null);
        providerR = preferences.getString("provider", null);

        if(emailR != null && providerR != null){
           successI(emailR, providerR);

        }
    }

    public void recuperarContrasenia(View view){
        if(!email.getText().toString().matches("")){
            FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(view, "Se ha enviado un email para recuperar tu contraseña", Snackbar.LENGTH_LONG).show();
                            }else{
                                Snackbar.make(view, "El usuario con el correo: \"+email.getText().toString()+\" no existe", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }else{
            Snackbar.make(view, "Introduce el correo electrónico", Snackbar.LENGTH_LONG).show();
        }

    }

    //Login con google
    public void loginGoogle(View view){
        googleConf = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleClient = GoogleSignIn.getClient(this, googleConf);
        googleClient.signOut();
        startActivityForResult(googleClient.getSignInIntent(), GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            GoogleSignInAccount account = null;
            try {
                account = task.getResult(ApiException.class);


            if(account != null){
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                GoogleSignInAccount finalAccount = account;
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    db.collection("users").document(finalAccount.getEmail()).get() //hacemos un get de la base de datos para ver si el usuari ya existe, asi no sobreescribimos sus datos
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot it) {
                                                    if(it.get("email") == null){
                                                        guardarUsuario("GOOGLE",finalAccount.getEmail());
                                                        successI(finalAccount.getEmail(), "GOOGLE");
                                                    }else{
                                                        successI(finalAccount.getEmail(), "GOOGLE");
                                                    }
                                                }
                                            });

                                }else {
                                    Snackbar.make(findViewById(android.R.id.content), "Error al iniciar sesion con google", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });

            }
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    } //onActivityResul

    //Login con correo y contraseña
    public void signup(View view){
        if(!email.getText().toString().matches("") && !password.getText().toString().matches("")){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                guardarUsuario("BASIC",email.getText().toString());
                                successI(email.getText().toString(), "BASIC");
                            }else {
                                Snackbar.make(findViewById(android.R.id.content), "Error al autenticar el usuario, puede que el usuario ya exista", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }else{
            Snackbar.make(findViewById(android.R.id.content), "Tienes que rellenar todos los campos", Snackbar.LENGTH_LONG).show();
        }
    }

    public void login(View view){
        if(!email.getText().toString().matches("") && !password.getText().toString().matches("")){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                successI(email.getText().toString(), "BASIC");
                            }else{
                                Snackbar.make(findViewById(android.R.id.content), "Error al autenticar el usuario", Snackbar.LENGTH_LONG).show();
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
        hashMap.put("usuario", "");
        hashMap.put("nombre", "");
        hashMap.put("apellidos", "");
        hashMap.put("biografia", "");

        db.collection("users").document(email).set(hashMap);
    }



    public void successI(String email, String providerType){
        Intent i = new Intent(this, HomeActivityD.class);
        i.putExtra("email", email);
        i.putExtra("provider", providerType);
        startActivity(i);
    }
}