package com.example.proyectodam;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperaPass extends AppCompatActivity {
    //Atributos de campo:
    private TextInputEditText etMail;
    private MaterialButton btRecuperar;

    @SuppressLint({"MissingInflatedId", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_pass);
        etMail=findViewById(R.id.etMailRecupera2);
        btRecuperar=findViewById(R.id.btRecuperar);
        btRecuperar.setOnClickListener(view -> {
            String email=validarFormato();
            if(email!=null){
                enviarEmailRecuperacion(email);
            }else{
                Toast.makeText(RecuperaPass.this,"Introduzca un email válido",Toast.LENGTH_SHORT).show();
            }
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Si pulsamos para volver atras:
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                Intent intent=new Intent(RecuperaPass.this,MainActivity.class);
                startActivity(intent);
            }
        });

        //CODIGO PARA PONER BARRA NOTIFICACIONES EN UN COLOR CONCRETO:
        getWindow().setStatusBarColor(getResources().getColor(R.color.white,getTheme()));
    }

    private void enviarEmailRecuperacion(String email) {
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                AlertDialog.Builder builder=new AlertDialog.Builder(RecuperaPass.this);
                builder.setTitle("CORREO ENVIADO");
                builder.setMessage("Revise la bandeja de entrada de su correo electrónico para acceder email de restablecimiento de contraseña.");
                builder.setCancelable(false);
                builder.setPositiveButton("Aceptar", (dialogInterface, i) -> {
                    Intent intentVolver=new Intent(RecuperaPass.this,MainActivity.class);
                    startActivity(intentVolver);
                    finish();
                });
                builder.show();
            }else{
                Toast.makeText(RecuperaPass.this,"El correo no pudo enviarse correctamente",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String validarFormato() {
        String email=etMail.getText().toString().trim();
        //Aqui uso una alternativa que nos ofrece la api Util con la clase Patters para comprobar el email:
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()){
            etMail.setError("Introduzca un email válido");
            return null;
        }else{
            return email;
        }
    }
}