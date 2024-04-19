package com.example.proyectodam.empleados;

import static com.example.proyectodam.MainActivity.fireBaseAuth;
import static com.example.proyectodam.R.color.white;
import static com.example.proyectodam.Vehiculos.requestQueue;
import static com.example.proyectodam.comons.Constants.ACTUALIZA_PERFIL;
import static com.example.proyectodam.comons.Constants.CAMERA_PERMISSION_CODE;
import static com.example.proyectodam.comons.Constants.EMAIL;
import static com.example.proyectodam.comons.Constants.PASSWORD;
import static com.example.proyectodam.comons.Constants.PASS_MODIFICADA;
import static com.example.proyectodam.comons.Constants.PICK_IMAGE;
import static com.example.proyectodam.comons.Constants.URL_BASICA;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectodam.Cliente;
import com.example.proyectodam.MainActivity;
import com.example.proyectodam.R;
import com.example.proyectodam.Registro;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class empleadosPerfil extends AppCompatActivity implements View.OnClickListener {
    //Atributos de campo:
    private Cliente cliente;
    private Registro registro;
    private TextInputLayout ilNombre, ilDni,ilDireccion,ilLocalidad,ilCodPostal, ilProvincia,ilTelefono,ilMail,ilPass,ilPass2;
    private TextInputEditText etNombre,etDni,etDireccion,etLocalidad,etCodPostal,etProvincia,etTelefono,etMail,etPass,etPass2;
    private MaterialButton btEditar,btGrabar,btVolver;
    private ProgressBar progressBar;
    private ShapeableImageView imagen;
    private Uri uri;
    private MaterialToolbar toolbar;
    private FirebaseUser user;
    private String emailVigente, passVigente,idToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empleados_perfil);
        //CODIGO PARA PONER BARRA NOTIFICACIONES EN UN COLOR CONCRETO:
        getWindow().setStatusBarColor(getResources().getColor(white, getTheme()));

        if(getIntent().hasExtra("cliente")){
            cliente=getIntent().getParcelableExtra("cliente");
        }
        iniciarComponentes();
        if(cliente!=null){
            cargarDatos();
            //En función de si es empleado o administrador, inflo un menu u otro:
            if(cliente.getEmpleado()==2){
                toolbar.inflateMenu(R.menu.menu_app_bar_admon);
            }else{
                toolbar.inflateMenu(R.menu.menu_app_bar);
            }
            obtenerTokenActualizado();
           toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
               @Override
               public boolean onMenuItemClick(MenuItem item) {
                   if(item.getItemId()==R.id.menuPrincipal){
                       Intent intent=new Intent(empleadosPerfil.this, ActivityInicioEmpleado.class);
                       intent.putExtra("cliente",cliente);
                       startActivity(intent);
                       return true;
                   }else if(item.getItemId()==R.id.menuCitas){
                       Intent intent=new Intent(empleadosPerfil.this, empleadosCitas.class);
                       startActivity(intent);
                       return true;
                   }else if(item.getItemId()==R.id.menuOrdenes) {
                       Intent intent = new Intent(empleadosPerfil.this, empleadosReparaciones.class);
                       startActivity(intent);
                       return true;
                   }else if(item.getItemId()==R.id.menuDocumentos){
                       Intent intent=new Intent(empleadosPerfil.this, empleadosDocumentos.class);
                       startActivity(intent);
                       return true;
                   }else if(item.getItemId()==R.id.menuEmpleados){
                       Intent intent=new Intent(empleadosPerfil.this, empleadosGestion.class);
                       startActivity(intent);
                       return true;
                   }else if(item.getItemId()==R.id.menuListados){
                       Intent intent=new Intent(empleadosPerfil.this, empleadosListados.class);
                       startActivity(intent);
                       return true;
                   }else if(item.getItemId()==R.id.menuPromociones){
                       Intent intent=new Intent(empleadosPerfil.this, empleadosPromociones.class);
                       startActivity(intent);
                       return true;
                   }else {
                       Intent intent=new Intent(empleadosPerfil.this, empleadosComunicaciones.class);
                       startActivity(intent);
                       return true;
                   }
               }
           });
        }
        cargarImagen();
        imagen.setOnClickListener(this);
        btEditar.setOnClickListener(this);
        btGrabar.setOnClickListener(this);
        btVolver.setOnClickListener(this);
        registro=new Registro();
    }

    /**
     * Método para cargar los datos del formulario y establecer los setEnabled(false) para no poder editar los campos
     * por descuido, a no ser que se pulse el boton de editar.
     */

    private void cargarDatos() {
        etNombre.setText(cliente.getNombre());
        etNombre.setTextColor(getColor(R.color.grisOscuro));
        etNombre.setEnabled(false);
        ilNombre.setBoxBackgroundColor(getColor(R.color.blancoRoto));
        etDni.setText(cliente.getDni());
        etDni.setTextColor(getColor(R.color.grisOscuro));
        ilDni.setBoxBackgroundColor(getColor(R.color.blancoRoto));
        etDni.setEnabled(false);
        etDireccion.setText(cliente.getDireccion());
        etDireccion.setTextColor(getColor(R.color.grisOscuro));
        ilDireccion.setBoxBackgroundColor(getColor(R.color.blancoRoto));
        etDireccion.setEnabled(false);
        etLocalidad.setText(cliente.getLocalidad());
        etLocalidad.setTextColor(getColor(R.color.grisOscuro));
        ilLocalidad.setBoxBackgroundColor(getColor(R.color.blancoRoto));
        etLocalidad.setEnabled(false);
        etCodPostal.setText(String.valueOf(cliente.getCodPostal()));
        etCodPostal.setTextColor(getColor(R.color.grisOscuro));
        ilCodPostal.setBoxBackgroundColor(getColor(R.color.blancoRoto));
        etCodPostal.setEnabled(false);
        etProvincia.setText(cliente.getProvincia());
        etProvincia.setTextColor(getColor(R.color.grisOscuro));
        ilProvincia.setBoxBackgroundColor(getColor(R.color.blancoRoto));
        etProvincia.setEnabled(false);
        etTelefono.setText(String.valueOf(cliente.getTelefono()));
        etTelefono.setTextColor(getColor(R.color.grisOscuro));
        ilTelefono.setBoxBackgroundColor(getColor(R.color.blancoRoto));
        etTelefono.setEnabled(false);
        etMail.setText(cliente.getEmail());
        etMail.setTextColor(getColor(R.color.grisOscuro));
        ilMail.setBoxBackgroundColor(getColor(R.color.blancoRoto));
        etMail.setEnabled(false);
        etPass.setText(cliente.getPassword());
        etPass.setTextColor(getColor(R.color.grisOscuro));
        ilPass.setBoxBackgroundColor(getColor(R.color.blancoRoto));
        ilPass.setPasswordVisibilityToggleEnabled(false);
        etPass.setEnabled(false);
        ilPass2.setBoxBackgroundColor(getColor(R.color.blancoRoto));
        ilPass2.setPasswordVisibilityToggleEnabled(false);
        etPass2.setText(cliente.getPassword());
        etPass2.setTextColor(getColor(R.color.grisOscuro));
        emailVigente=cliente.getEmail();
        passVigente=cliente.getPassword();
    }

    /**
     * Método encapsulado dode se recoge la instaciación e inicialización de los los atributos de campo.
     */

    private void iniciarComponentes() {
         toolbar=findViewById(R.id.toolPerfil);
         imagen=findViewById(R.id.ivFoto);
         ilNombre=findViewById(R.id.etNombrePerfil);
         ilDni=findViewById(R.id.etDni1Perfil);
         ilDireccion=findViewById(R.id.etDireccionPerfil);
         ilLocalidad=findViewById(R.id.etLocalidadPerfil);
         ilCodPostal=findViewById(R.id.etPostalPerfil);
         ilProvincia=findViewById(R.id.etProvincia1Perfil);
         ilTelefono=findViewById(R.id.etPhonePerfil);
         ilMail=findViewById(R.id.etMailPerfil);
         ilPass=findViewById(R.id.etPasswordPerfil);
         ilPass2=findViewById(R.id.etPasswordRepitePerfil);
         etNombre=findViewById(R.id.etNombre2Perfil);
         etDni=findViewById(R.id.etDni2Perfil);
         etDireccion=findViewById(R.id.etDireccion2Perfil);
         etLocalidad=findViewById(R.id.etLocalidad2Perfil);
         etCodPostal=findViewById(R.id.etPostal2Perfil);
         etProvincia=findViewById(R.id.etProvincia2Perfil);
         etTelefono=findViewById(R.id.etPhone2Perfil);
         etMail=findViewById(R.id.etMail2Perfil);
         etPass=findViewById(R.id.etPassword2Perfil);
         etPass2=findViewById(R.id.etPassword2Repite);
         btEditar=findViewById(R.id.btEditarPerfil);
         btGrabar=findViewById(R.id.btGrabarPerfil);
         btGrabar.setVisibility(View.INVISIBLE);
         btVolver=findViewById(R.id.btVolverPerfil);
         progressBar=findViewById(R.id.progressPerfil);
    }

    /**Recoge las acciones a realizar cuando se clickea sobre alguno de ellos.
     *
     * @param view Se pasa por parámetro la vista sobre la cual se clickea.
     */

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btEditarPerfil){
            desmarcarCampos();
        }else if(view.getId()==R.id.btGrabarPerfil){
           boolean correcto=compruebaTodos();
           boolean emailCoincide=etPass.getText().toString().equals(etPass2.getText().toString());
           if(correcto && emailCoincide){
                if(emailVigente.equals(etMail.getText().toString()) && passVigente.equals(etPass.getText().toString())){
                    progressBar.setVisibility(View.VISIBLE);
                    actualizaUsuario(false);
                }else{
                    if(etPass.getText().toString().equals(etPass2.getText().toString())) {
                        if (!emailVigente.equals(etMail.getText().toString())) {
                            //TODO COMPROBAR CODIGO
                            progressBar.setVisibility(View.VISIBLE);
                            actualizarEmailFirebase();
                            obtenerTokenActualizado();
                        }
                        if (!passVigente.equals(etPass2.getText().toString())) {
                            progressBar.setVisibility(View.VISIBLE);
                            actualizaPassFirebase();
                            obtenerTokenActualizado();
                        }
                        actualizaUsuario(true);

                    }else{
                        registro.toast(empleadosPerfil.this,"Deben de coincidir ambas contraseñas.");
                    }
                }
           }else{
               if(!correcto){
                   registro.toast(empleadosPerfil.this,"Revise que los datos sean correctos.");
               }
               if(!emailCoincide){
                   registro.toast(empleadosPerfil.this,"Deben de coincidir ambas contraseñas.");
               }
           }
        }else if(view.getId()==R.id.ivFoto){
            muestraDialogPermisos();
        }
        else{
            Intent intent=new Intent(this, ActivityInicioEmpleado.class);
            intent.putExtra("cliente",cliente);
            startActivity(intent);
        }
    }

    private void mostrarProgress() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            });
    }

    private void cogerDatosNuevos() {
        cliente.setNombre(etNombre.getText().toString());
        cliente.setDni(etDni.getText().toString());
        cliente.setDireccion(etDireccion.getText().toString());
        cliente.setLocalidad(etLocalidad.getText().toString());
        cliente.setCodPostal(Integer.parseInt(etCodPostal.getText().toString()));
        cliente.setProvincia(etProvincia.getText().toString());
        cliente.setTelefono(Integer.parseInt(etTelefono.getText().toString()));
        cliente.setEmail(etMail.getText().toString());
        cliente.setPassword(etPass2.getText().toString());
    }

    /**Método creado para desmarcar todos los campos y poner los campos como Focusables o editables (Enabled) cuando el usuario
     * pulsa en el boton editar.
     */

    private void desmarcarCampos() {
        etNombre.setTextColor(getColor(R.color.azulOscuro));
        etNombre.setEnabled(true);
        ilNombre.setBoxBackgroundColor(getColor(R.color.white));
        etDni.setTextColor(getColor(R.color.azulOscuro));
        ilDni.setBoxBackgroundColor(getColor(R.color.white));
        etDni.setEnabled(true);
        etDireccion.setTextColor(getColor(R.color.azulOscuro));
        ilDireccion.setBoxBackgroundColor(getColor(R.color.white));
        etDireccion.setEnabled(true);
        etLocalidad.setTextColor(getColor(R.color.azulOscuro));
        ilLocalidad.setBoxBackgroundColor(getColor(R.color.white));
        etLocalidad.setEnabled(true);
        etCodPostal.setTextColor(getColor(R.color.azulOscuro));
        ilCodPostal.setBoxBackgroundColor(getColor(R.color.white));
        etCodPostal.setEnabled(true);
        etProvincia.setTextColor(getColor(R.color.azulOscuro));
        ilProvincia.setBoxBackgroundColor(getColor(R.color.white));
        etProvincia.setEnabled(true);
        etTelefono.setTextColor(getColor(R.color.azulOscuro));
        ilTelefono.setBoxBackgroundColor(getColor(R.color.white));
        etTelefono.setEnabled(true);
        etMail.setTextColor(getColor(R.color.azulOscuro));
        ilMail.setBoxBackgroundColor(getColor(R.color.white));
        etMail.setEnabled(true);
        ilPass.setBoxBackgroundColor(getColor(R.color.white));
        ilPass.setPasswordVisibilityToggleEnabled(true);
        etPass.setTextColor(getColor(R.color.azulOscuro));
        etPass.setEnabled(true);
        ilPass2.setBoxBackgroundColor(getColor(R.color.white));
        ilPass2.setPasswordVisibilityToggleEnabled(true);
        etPass2.setTextColor(getColor(R.color.azulOscuro));
        btGrabar.setVisibility(View.VISIBLE);

    }

    /**
     * Método para construir un AlerDialog para pedir permisos al usuario:
     */

    private void muestraDialogPermisos() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            muestraOpcionesFoto();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }
    }

    /**
     * Método que crea y personaliza el AlertBuilder para solicitar al usuario si quiere cargar la imagen a través de las
     * fotos de galeria o si por el contrario, prefiere utilizar foto de la cámara.
     */

    private void muestraOpcionesFoto() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Carga de imagenes");
        builder.setMessage("Selecciona un método para cargar la imagen");
        builder.setPositiveButton("CAMARA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                abrirCamara();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("GALERIA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                abrirGaleria();
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    /**Método que configura la apertura de galería, la selección de la imagen, y establece la respuesta de la misma
     * por parte del usuario.
     */

    private void abrirGaleria() {
        Intent intent=new Intent();
        //Filtrado solo por imagen:
        intent.setType("image/*");
        //Elección simple de todos en el selector:
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        //Acción a ejecutar:
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //Esperamos el resultado y e pasamos mediante createChooser el intent, el título y el permiso:
        startActivityForResult(Intent.createChooser(intent,"SELECCIONA LAS IMAGENES"),PICK_IMAGE);
    }

    /**Método que recoge y establece la imagen elegida por el usuario en ShapeableImage.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Se crea el ClipData y se coje la foto:
        try{
            ClipData clipData=data.getClipData();
            if(resultCode==RESULT_OK && requestCode==PICK_IMAGE && data!=null){

                    Uri uriGaleria=data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriGaleria);
                    imagen.setImageBitmap(bitmap);
                    //imagen.setImageURI(uriGaleria);
                    String stringBitmap=convertirUriToBase64(bitmap);
                    guardarImagen(stringBitmap);

            }
        }catch(NullPointerException ex){
            Log.i("TAG",ex.getMessage());
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**Método utilizado para guardar la imagen en base como String para cargarla posteriormente, cuando
     * se vuelva a acceder de nuevo a la actividad, y evitar la modificación de permisos de la API OPEN_DOCUMENT_DATA
     * que se establece en Android a partir de la api 33 como mecanismo de seguridad.
     * @param imagenBitmap Se pasa por parámetro la imagen convertida en base64.
     */

    private void guardarImagen(String imagenBitmap) {

        SharedPreferences.Editor editor=getPreferences(MODE_PRIVATE).edit();
        editor.putString("imagen",imagenBitmap);
        editor.apply();
    }

    /**Método que carga el string de base64 de la imagen que se ha guardado en las SharedPreference cada vez que
     * se inicia la actividad, estableciendo y cargando la misma en el elemento ShapeableView.David.
     */


    private void cargarImagen(){
        SharedPreferences preferences=getPreferences(MODE_PRIVATE);
        String imagenBase64= preferences.getString("imagen",null);
        if(imagenBase64!=null){
            byte[] decodedString = Base64.decode(imagenBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imagen.setImageBitmap(decodedByte);
        }
    }

    /**Método que configura y acciona el uso de la camara por parte del usuario para realizar una instantanea y ponerla como perfil
     *
     */

    private void abrirCamara() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Titulo");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Descripcion");
        uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent intentCamara=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamara.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        camara.launch(intentCamara);

    }

    /**Método que utilizo para convertir o codificar una imagen Bitmap a base64 para guardarlo porteriormente en las SharedPreferences.
     *
     * @param bitmap Se pasa por parámetro la imagen en formato Bitmap.
     * @return
     */
    public String convertirUriToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    /**
     * Método que recoge el resultado de la utilización de la camara por parte del usuario para establecer la foto desde la fotografia realizada en la camara
     * de su dispositivo, y que establece y guarda la imagen posteriormente en las SharedPreferences.
     */

    private ActivityResultLauncher<Intent> camara=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()== Activity.RESULT_OK){
                imagen.setImageURI(uri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    String stringBitmap=convertirUriToBase64(bitmap);
                    guardarImagen(stringBitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }else{
                Log.i("TAG","Error en ActivityResultIntent en Camara");
            }
        }
    });


    /**Método creado para encapsular todas las comprobaciones de todos los campos de formulario de la actividad para poder llevar a cabo la modificación del registro en
     * la base de datos. @return resultado: Devuelve true si todos los datos se ajustan a su patrón o pattern, y false en caso contrario.
     */

    public boolean compruebaTodos(){
        boolean resultado;
        resultado=registro.compruebaPatron(ilNombre,1,"Debe de introducir un nombre,",etNombre,"vacio",cliente);
        if(!cliente.getDni().startsWith("A") || !cliente.getDni().startsWith("a") || !cliente.getDni().startsWith("B") || !cliente.getDni().startsWith("b")){
            resultado=registro.compruebaPatron(ilDni,2,"Introduzca un Dni válido sin guiones ni puntos,",etDni,"[0-9]{8}[a-zA-Z]",cliente);
        }else{
            resultado=registro.compruebaPatron(ilDni,2,"Introduzca un Cif válido sin guiones ni puntos con letra en mayúscula,",etDni,"[a-zA-Z]{1}[0-9]{8}",cliente);
        }
        resultado=registro.compruebaPatron(ilDireccion,3,"Introduzca una direccion válida",etDireccion,"vacio",cliente);
        resultado=registro.compruebaPatron(ilLocalidad,4,"Introduzca una localidad válida,",etLocalidad,"vacio",cliente);
        resultado=registro.compruebaPatron(ilCodPostal,5,"Introduzca un código postal válido,",etCodPostal,"[0-9]{5}",cliente);
        resultado=registro.compruebaPatron(ilProvincia,6,"Introduzca una provincia válida,",etProvincia,"[a-zA-Z]{1,}",cliente);
        resultado=registro.compruebaPatron(ilTelefono,7,"Introduzca un número sin guiones ni puntos,",etTelefono,"[0-9]{9}",cliente);
        //[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*@[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[.][a-zA-Z]{2,5} --> EMAIL
        resultado=registro.compruebaPatron(ilMail,8,"Introduzca una dirección de email válida,",etMail,"[a-zA-Z0-9]{1,}@[a-zA-Z0-9]{1,}.[a-zA-Z]{2,3}",cliente);
        resultado=registro.compruebaPatron(ilPass,9,"Introduzca una contraseña válida",etPass,"[A-Z]{1}[a-z]{1,}_[0-9]{1,}",cliente);
        resultado=registro.compruebaPatron(ilPass2,9,"Introduzca una contraseña válida",etPass2,"[A-Z]{1}[a-z]{1,}_[0-9]{1,}",cliente);
        return resultado;

    }

    /**Método para actualizar los datos del cliente modificados en API REST Volley por método POST.
     *
     */
    //TODO Falta probar y modificar para adaptarlo.SIN PROBAR.
    private void actualizaUsuario(boolean reiniciar){
        String url=URL_BASICA+ACTUALIZA_PERFIL;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, response -> {
            if(response.contains("correctamente")){
                mostrarProgress();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                progressBar.setVisibility(View.INVISIBLE);
                AlertDialog.Builder builder=new AlertDialog.Builder(empleadosPerfil.this);
                builder.setTitle("REGISTRO ACTUALIZADO");
                builder.setIcon(R.drawable.iconomegacar);
                String mensaje;
                if(reiniciar){
                   mensaje="El registro ha sido actualizado correctamente, a continuación se deberá loguear de nuevo para finalizar los cambios.";
                }else{
                    mensaje="El registro ha sido actualizado correctamente en nuestra base de datos.";
                }
                builder.setMessage(mensaje);
                builder.setCancelable(false);
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(reiniciar) {
                            //Cerramos sesion en Firebase:
                            fireBaseAuth.getInstance().signOut();
                            Intent intent = new Intent(empleadosPerfil.this, MainActivity.class);
                            intent.putExtra(PASS_MODIFICADA, true);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                builder.create().show();
            }else{
                Log.i("TAG", "No se ha realizado la actualización del usuario.");
            }

        }, error -> {
            Toast.makeText(empleadosPerfil.this,"ERROR al modificar el registro.",Toast.LENGTH_SHORT).show();
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("email",etMail.getText().toString());
                parametros.put("nombre", etNombre.getText().toString());
                parametros.put("telefono",String.valueOf(etTelefono.getText()));
                parametros.put("direccion",etDireccion.getText().toString());
                parametros.put("cod_postal", String.valueOf(etCodPostal.getText()));
                parametros.put("localidad",etLocalidad.getText().toString());
                parametros.put("provincia",etProvincia.getText().toString());
                parametros.put("pass",etPass.getText().toString());
                parametros.put("firebase",idToken);
                parametros.put("dni_cif", etDni.getText().toString());
                parametros.put("id_usuario",String.valueOf(cliente.getId()));
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(empleadosPerfil.this);
        requestQueue.add(stringRequest);
    }

    /**Método para actualizar la contraseña en Firebase, en caso de que el usuario no modifique el email y modifique únicamente la password en su perfil.
     *
     */
    //TODO PROBAR, faltaria una vez echo los cambios tanto en Firebase como en nuestra Bdd, hacer Firebase.singOut();
    public void actualizaPassFirebase(){
        String passNueva=etPass.getText().toString();
        user=fireBaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential= EmailAuthProvider.getCredential(emailVigente,passVigente);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                user.updatePassword(passNueva);
                Log.i("TAG","Contraseña cambiada en Firebase");
                SharedPreferences preferences=getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString(PASSWORD,passNueva);
                editor.apply();
                obtenerTokenActualizado();
                passVigente=passNueva;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(empleadosPerfil.this,"No se ha actualizado pass en Firebase",Toast.LENGTH_SHORT).show();
                Log.i("TAG","No se ha podido actualizar la pass en Firebase. Razón: ".concat(e.getMessage()));
            }
        });
    }

    public void obtenerTokenActualizado() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        cliente.setToken("no existe");
                        Log.w("TAG", "No se ha podido otener el token".concat( task.getException().toString()));
                    }else{
                        idToken = task.getResult();
                        cliente.setToken(idToken);
                        //Si la respuesta es positiva, guardamos el token como shared preferences.
                        SharedPreferences preferences =getSharedPreferences("token", Context.MODE_PRIVATE);
                        SharedPreferences.Editor sharedEditor = preferences.edit();
                        sharedEditor.putString("token", idToken);
                        sharedEditor.apply();
                        Log.i("TAG","Token del cliente: ".concat(idToken));
                    }
                });
    }


    /**Método para actualizar el email en Firebase, en caso de que el usuario no modifique la password en su perfil.
     *
     */
    private void actualizarEmailFirebase(){
        String emailNuevo=etMail.getText().toString();
        FirebaseUser usuario=FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential=EmailAuthProvider.getCredential(emailVigente,passVigente);
        usuario.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                usuario.updateEmail(emailNuevo);
                SharedPreferences preferences=getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor= preferences.edit();
                editor.putString(EMAIL,emailNuevo);
                editor.apply();
                obtenerTokenActualizado();
                emailVigente=emailNuevo;
                fireBaseAuth.getInstance().signOut();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                loguearFirebase(emailVigente,passVigente);
                Log.i("TAG","Email actualizado en firebase con exito.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(empleadosPerfil.this,"No se ha actualizado email en Firebase",Toast.LENGTH_SHORT).show();
                Log.i("TAG","No se ha podido actualizar el email en Firebase. Razón: ".concat(e.getMessage()));
            }
        });
    }
    private void loguearFirebase(String email, String pass) {
        fireBaseAuth = FirebaseAuth.getInstance();
        fireBaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(empleadosPerfil.this, task -> {
            if (task.isSuccessful()) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                Log.i("TAG", "Autentificado con éxito");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(empleadosPerfil.this, "No ha sido posible la autentificación en firebase", Toast.LENGTH_LONG).show();
                Log.i("TAG", "No loguea: " .concat(e.getMessage()));
            }
        });
    }

}
