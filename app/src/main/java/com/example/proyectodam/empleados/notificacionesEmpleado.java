package com.example.proyectodam.empleados;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectodam.R;

public class notificacionesEmpleado extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones_empleado);
        //CODIGO PARA PONER BARRA NOTIFICACIONES EN UN COLOR CONCRETO:
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));
    }
}
