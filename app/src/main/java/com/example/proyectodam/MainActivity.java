package com.example.proyectodam;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Atributos de campo.
    public boolean validado;
    private FirebaseAuth fireBaseAuth;
    private TextInputLayout etNombre1;
    private TextInputEditText etNombre2, etPass;
    private MaterialTextView registrarse;
    private Button btLogin, btBiometrico;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //CODIGO PARA PONER BARRA NOTIFICACIONES EN UN COLOR CONCRETO:
        getWindow().setStatusBarColor(getResources().getColor(R.color.white,getTheme()));

        etNombre1=findViewById(R.id.etEmail);
        etNombre2=findViewById(R.id.etMail2);
        etPass=findViewById(R.id.etPassword);
        registrarse=findViewById(R.id.tvRegistrarse);
        btBiometrico=findViewById(R.id.btBiometrico);
        btLogin=findViewById(R.id.btLogin);
        registrarse.setOnClickListener(this);
        btLogin.setOnClickListener(this);
        btBiometrico.setOnClickListener(view -> activarBiometrica());


        //METODO PARA CAPTURAR CUANDO CAMBIA UN TEXTO PARA EDITTEXT:
        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email=etNombre2.getText().toString().trim();
                Pattern pattern= Pattern.compile("[a-zA-Z0-9]{1,}@[a-zA-Z0-9]{1,}.[a-zA-Z]{2,3}",Pattern.CASE_INSENSITIVE);
                Matcher matcher=pattern.matcher(email);
                if(matcher.matches()){
                    btBiometrico.setVisibility(View.VISIBLE);
                    etNombre1.setError(null);
                    validado=true;
                }else{
                    btBiometrico.setVisibility(View.INVISIBLE);
                    etNombre1.setError("Introduca un correo electrónico válido");
                    validado=false;
                }

            }
        };
        etNombre2.addTextChangedListener(textWatcher);




    }
    private void activarBiometrica() {
        //1ºCreo elBiometricManager y compruebo que el dispositivo cuenta y es compatible con reconocimiento biométrico:
        androidx.biometric.BiometricManager bioManager=androidx.biometric.BiometricManager.from(getApplicationContext());
        switch (bioManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                btBiometrico.setVisibility(View.VISIBLE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(getApplicationContext(), "Su dispositivo no es compatible con identificación biometrica", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(getApplicationContext(), "Su hardware no esta disponible para identificación biometrica", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(getApplicationContext(), "Ocurrio un error con la identificación biometrica", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                Toast.makeText(getApplicationContext(), "Ocurrio un error la seguridad requerida", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                Toast.makeText(getApplicationContext(), "Ocurrio un error biométrico no soportado", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                Toast.makeText(getApplicationContext(), "Ocurrio un error en la identificación biométrica desconocido", Toast.LENGTH_SHORT).show();
                break;
        }
        //Creo el Executor, y lo instancio pasándole el contexto:
        Executor executor= ContextCompat.getMainExecutor(getApplicationContext());

        /*Otorgamos el resultado de la validación creando una instancia con una constante de prompInfo al que le paso la actividad,
        el executor y creamos una instancia del AutenticationCallback para recoger el retorno:
         */
        final BiometricPrompt prompt=new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),"Error en la autentificación",Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent2=new Intent(MainActivity.this,ActivityInicio.class);
                startActivity(intent2);
                finish();


            }
        });

        //Ahora creo el PrompInfo y el cuadro de dialogo que solicitará al usuario la autentificación dactilar:
        final BiometricPrompt.PromptInfo promptInfo=new BiometricPrompt.PromptInfo.Builder().setTitle("Verifique su identidad")
                .setDescription("Ponga el dedo en el lector de huellas de su dispositivo").setNegativeButtonText("Cancelar").build();
        btBiometrico.setOnClickListener(view -> prompt.authenticate(promptInfo));

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.tvRegistrarse){
            mostrarOpciones();
        }
        if(view.getId()==R.id.btLogin){
            if(validado){
                //Si el correo es correcto, nos autentificamos como usuario con Firebase:
                signInFireBase();
            }else{
                Toast.makeText(MainActivity.this,"Ocurrió un error.",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void signInFireBase() {
        String usuario=etNombre2.getText().toString().trim();
        String password=etPass.getText().toString();
        fireBaseAuth=FirebaseAuth.getInstance();
        fireBaseAuth.signInWithEmailAndPassword(usuario, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = fireBaseAuth.getCurrentUser();

                    Log.i("TAG", "Autentificado con éxito");
                    Intent intentEntrar=new Intent(MainActivity.this,ActivityInicio.class);
                    startActivity(intentEntrar);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Error en autentificación, introduzca contraseña correcta", Toast.LENGTH_LONG).show();
                    //Log.e(TAG, task.getException().toString());
                }
            }
        });
    }

    /*
    Método creado para mostrar activity de opciones de recuperar contraseña o llevar a cabo registro nuevo para que escoja el usuario.
     */
    private void mostrarOpciones() {
        final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.menu_registro);
        dialog.show();


        Button btRegistro =  dialog.findViewById(R.id.btn1);
        btRegistro.setOnClickListener(v -> {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Intent intentRegistro=new Intent(MainActivity.this, Registro.class);
            startActivity(intentRegistro);
            finish();
        });

        Button btRecuperar = dialog.findViewById(R.id.btn2);
        btRecuperar.setOnClickListener(view -> {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Intent intentRecuperar=new Intent(MainActivity.this, RecuperaPass.class);
            startActivity(intentRecuperar);
            finish();
        });

        MaterialTextView volver=dialog.findViewById(R.id.btVolver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }
}


