package com.example.proyectodam;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityInicio extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private MaterialTextView etTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
//Atributos de campo:
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView nv = findViewById(R.id.nav_view);
        //CODIGO PARA PONER BARRA NOTIFICACIONES EN UN COLOR CONCRETO:
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));
        //Creo el toolbar:
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Pongo el capturador de eventos de la vista navigationView:
        nv.setNavigationItemSelectedListener(this);
        nv = findViewById(R.id.nav_view);
        View navHeader = nv.getHeaderView(0);
        etTelefono = navHeader.findViewById(R.id.etTelefono);
        etTelefono.setOnClickListener(view -> {
            String num = etTelefono.getText().toString();
            if (!num.equals("")) {
                Uri uri = Uri.parse("tel:" + num);
                Intent intentLlamada = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intentLlamada);
            }
        });

        nv.setBackgroundResource(R.color.naranja);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.nav_logout){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(ActivityInicio.this, "SE HA CERRADO LA SESION", Toast.LENGTH_SHORT).show();
            Intent intent3 = new Intent(ActivityInicio.this, MainActivity.class);
            startActivity(intent3);
        }else if(item.getItemId()==R.id.nav_salir){
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        return false;
    }
}