package com.example.proyectodam.empleados;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.proyectodam.Cliente;
import com.example.proyectodam.MainActivity;
import com.example.proyectodam.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityInicioEmpleado extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    //Atributos de campo:
    private Cliente cliente;
    private int opcion;
    private MaterialTextView etTelefono,nombreEmpleado;
    private CardView perfil,citas,ordenes,documentos,empleados,listados,promocion,comunicaciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicioempleado);
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
        nv.setBackgroundResource(R.color.azulOscuro);
        nv.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
        nv.setItemIconTintList(ColorStateList.valueOf(Color.WHITE));
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
           cliente=Cliente.traerIntentCliente(ActivityInicioEmpleado.this,bundle);
           opcion=cliente.getEmpleado();
        }
        if(getIntent().hasExtra("cliente")){
            cliente=getIntent().getParcelableExtra("cliente");
            opcion= cliente.getEmpleado();
        }
        inicializarComponentes();
    }

    /**
     * Método para inicializar los componentes CardView y sus listener de click:
     */
    private void inicializarComponentes() {
        if(cliente!=null){
            nombreEmpleado=findViewById(R.id.tvNombreEmpleado);
            nombreEmpleado.setText(cliente.getNombre());
        }
        perfil=findViewById(R.id.cardPerfil);
        citas=findViewById(R.id.cardGestionCitas);
        ordenes=findViewById(R.id.cardOrdenes);
        documentos=findViewById(R.id.cardDocumentos);
        empleados=findViewById(R.id.cardEmpleados);
        listados=findViewById(R.id.cardInformes);
        promocion=findViewById(R.id.cardPromociones);
        comunicaciones=findViewById(R.id.cardChat);

        perfil.setOnClickListener(this);
        citas.setOnClickListener(this);
        ordenes.setOnClickListener(this);
        documentos.setOnClickListener(this);
        if(opcion==0){
            empleados.setVisibility(View.INVISIBLE);
            listados.setVisibility(View.INVISIBLE);
            promocion.setVisibility(View.INVISIBLE);
            comunicaciones.setVisibility(View.INVISIBLE);
        }else{
            empleados.setOnClickListener(this);
            listados.setOnClickListener(this);
            promocion.setOnClickListener(this);
            comunicaciones.setOnClickListener(this);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.nav_logout){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(ActivityInicioEmpleado.this, "SE HA CERRADO LA SESION", Toast.LENGTH_SHORT).show();
            Intent intent3 = new Intent(ActivityInicioEmpleado.this, MainActivity.class);
            startActivity(intent3);
        }else if(item.getItemId()==R.id.nav_salir){
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.cardPerfil){
            Intent intent=new Intent(this, empleadosPerfil.class);
            if(cliente!=null){
                intent.putExtra("cliente",cliente);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);

        }else if(view.getId()==R.id.cardGestionCitas){

        }else if(view.getId()==R.id.cardOrdenes){

        }else if(view.getId()==R.id.cardDocumentos){

        }else if(view.getId()==R.id.cardEmpleados){

        }else if(view.getId()==R.id.cardInformes){

        }else if(view.getId()==R.id.cardPromociones){

        }else{
            Intent intent=new Intent(this, notificacionesEmpleado.class);
            startActivity(intent);
        }
    }
}
