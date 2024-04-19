package com.example.proyectodam;

import static com.example.proyectodam.comons.Constants.COD_POSTAL;
import static com.example.proyectodam.comons.Constants.DIRECCION;
import static com.example.proyectodam.comons.Constants.DNI_CIF;
import static com.example.proyectodam.comons.Constants.EMAIL;
import static com.example.proyectodam.comons.Constants.ES_CLIENTE;
import static com.example.proyectodam.comons.Constants.FIREBASE;
import static com.example.proyectodam.comons.Constants.ID_USUARIO;
import static com.example.proyectodam.comons.Constants.LOCALIDAD;
import static com.example.proyectodam.comons.Constants.NOMBRE;
import static com.example.proyectodam.comons.Constants.PASSWORD;
import static com.example.proyectodam.comons.Constants.PASS_MODIFICADA;
import static com.example.proyectodam.comons.Constants.PROVINCIA;
import static com.example.proyectodam.comons.Constants.RECUPERA_PASS;
import static com.example.proyectodam.comons.Constants.ROL;
import static com.example.proyectodam.comons.Constants.ROL_USUARIO;
import static com.example.proyectodam.comons.Constants.TELEFONO;
import static com.example.proyectodam.comons.Constants.UPDATE_PASSWORD;
import static com.example.proyectodam.comons.Constants.URL_BASICA;
import static com.example.proyectodam.comons.Constants.USUARIO_BY_EMAIL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectodam.cliente.ActivityInicioCliente;
import com.example.proyectodam.empleados.ActivityInicioEmpleado;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Atributos de campo.
    public boolean validado,cargaPreferences;
    public static FirebaseAuth fireBaseAuth;
    private TextInputLayout etNombre1,verPass;
    private TextInputEditText etNombre2, etPass;
    private MaterialTextView registrarse;
    private Button btLogin, btBiometrico;
    private String email;
    public static RequestQueue requestQueue;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        validado=true;
        cargaPreferences=false;
        //Se pide permiso al usuario para recibir notificaciones en la app:
        preguntarPermisoNotificaciones();

        //si el cliente viene de recuperar password desde firebase
        if (getIntent().getExtras() != null && getIntent().getBooleanExtra(RECUPERA_PASS, false)) {
            updatePassword();
        }


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //CODIGO PARA PONER BARRA NOTIFICACIONES EN UN COLOR CONCRETO:
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));

        etNombre1 = findViewById(R.id.etEmail);
        etNombre2 = findViewById(R.id.etMail2);
        etPass = findViewById(R.id.etPassword);
        registrarse = findViewById(R.id.tvRegistrarse);
        btBiometrico = findViewById(R.id.btBiometrico);
        btLogin = findViewById(R.id.btLogin);
        cargarPreferences();
        if (getIntent().getExtras() !=null && getIntent().getBooleanExtra(PASS_MODIFICADA,false)){
            etNombre2.setText("");
            etPass.setText("");
            verPass=findViewById(R.id.tiLayout);
            verPass.setPasswordVisibilityToggleEnabled(true);
            btBiometrico.setVisibility(View.INVISIBLE);
        }
        etNombre2.requestFocus();
        registrarse.setOnClickListener(this);
        btLogin.setOnClickListener(this);
        btBiometrico.setOnClickListener(view -> activarBiometrica());


        //METODO PARA CAPTURAR CUANDO CAMBIA UN TEXTO PARA EDITTEXT:
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(cargaPreferences && EMAIL!=null){
                    verPass=findViewById(R.id.tiLayout);
                    etPass.setText("");
                    verPass.setPasswordVisibilityToggleEnabled(true);
                }
                String email = etNombre2.getText().toString().trim();
                Pattern pattern = Pattern.compile("[a-zA-Z0-9]{1,}@[a-zA-Z0-9]{1,}.[a-zA-Z]{2,3}", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(email);
                if (matcher.matches()) {
                    if(etNombre2.getText().toString()!=null && etPass.getText().toString()!=null){
                        btBiometrico.setVisibility(View.VISIBLE);
                        etNombre1.setError(null);
                        validado = true;
                    }else{
                        Toast.makeText(getApplicationContext(),"Debe hacer sesion normal al menos una vez para usar biometría",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    btBiometrico.setVisibility(View.INVISIBLE);
                    etNombre1.setError("Introduca un correo electrónico válido");
                    validado = false;
                }
            }
        };
        etNombre2.addTextChangedListener(textWatcher);
       if(cargaPreferences){
           etPass.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   etPass.setText("");
                   verPass.setPasswordVisibilityToggleEnabled(true);
               }
           });
       }
    }

    private void activarBiometrica() {
        //1ºCreo elBiometricManager y compruebo que el dispositivo cuenta y es compatible con reconocimiento biométrico:
        androidx.biometric.BiometricManager bioManager = androidx.biometric.BiometricManager.from(getApplicationContext());
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
        Executor executor = ContextCompat.getMainExecutor(getApplicationContext());

        /**Otorgamos el resultado de la validación creando una instancia con una constante de prompInfo al que le paso la actividad,
        el executor y creamos una instancia del AutenticationCallback para recoger el retorno:
         */
        final BiometricPrompt prompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Error en la autentificación", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                signInFireBase();
            }
        });

        //Ahora creo el PrompInfo y el cuadro de dialogo que solicitará al usuario la autentificación dactilar:
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Verifique su identidad")
                .setDescription("Ponga el dedo en el lector de huellas de su dispositivo").setNegativeButtonText("Cancelar").build();
        btBiometrico.setOnClickListener(view -> prompt.authenticate(promptInfo)

        );

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvRegistrarse) {
            mostrarOpciones();
        }
        if (view.getId() == R.id.btLogin) {
            if (validado) {
                //Si el correo es correcto, nos autentificamos como usuario con Firebase:
                signInFireBase();
            } else {
                Toast.makeText(MainActivity.this, "Ocurrió un error.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Se comprueba si el usuario ha sido autenticado en Firebase.
     * Si está autenticado, se comprueba el rol de usuario.
     */
    private void signInFireBase() {
        String usuario = etNombre2.getText().toString().trim();
        String password = etPass.getText().toString();
        fireBaseAuth = FirebaseAuth.getInstance();
        fireBaseAuth.signInWithEmailAndPassword(usuario, password).addOnCompleteListener(MainActivity.this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = fireBaseAuth.getCurrentUser();

                Log.i("TAG", "Autentificado con éxito");
                getRolUsuario();
            } else {
                Toast.makeText(MainActivity.this, "Error en autentificación, introduzca contraseña correcta", Toast.LENGTH_LONG).show();

            }
        });
    }

    /**
    Método creado para mostrar activity de opciones de recuperar contraseña o llevar a cabo registro nuevo para que escoja el usuario.
     */
    private void mostrarOpciones() {
        final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.menu_registro);
        dialog.show();


        Button btRegistro = dialog.findViewById(R.id.btn1);
        btRegistro.setOnClickListener(v -> {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Intent intentRegistro = new Intent(MainActivity.this, Registro.class);
            startActivity(intentRegistro);
            finish();
        });

        Button btRecuperar = dialog.findViewById(R.id.btn2);
        btRecuperar.setOnClickListener(view -> {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Intent intentRecuperar = new Intent(MainActivity.this, RecuperaPass.class);
            startActivity(intentRecuperar);
            finish();
        });

        MaterialTextView volver = dialog.findViewById(R.id.btVolver);
        volver.setOnClickListener(view -> dialog.dismiss());
    }

    /**
     * Se comprueba en bbdd el rol del ususario logado :
     * empleado / cliente
     */
    private void getRolUsuario() {
        StringRequest request = new StringRequest(Request.Method.POST, URL_BASICA+ROL_USUARIO, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    getIdUsuario(Integer.parseInt(response));
                }catch (NumberFormatException ex){
                    Toast.makeText(MainActivity.this,"Los datos del usuario no corresponden a ningún cliente.",Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(MainActivity.this, "Fallo de respuesta = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(EMAIL, etNombre2.getText().toString().trim());
                params.put(PASSWORD, etPass.getText().toString());
                return params;
            }
        };
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(request);
    }

    /**
     * Se hace una búsqueda de id de cliente o de empleado por email, dependiendo del rol del usuario.
     * Se guardan en local los datos: email, password e id (cliente o empleado),
     * y se pasa a la configuración de app cliente/empleado, según rol.
     * @param rol
     */
    private void getIdUsuario(int rol) {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_BASICA+USUARIO_BY_EMAIL+etNombre2.getText().toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                    editor.putString(EMAIL, etNombre2.getText().toString());
                    editor.putString(PASSWORD, etPass.getText().toString());
                    editor.putInt(ID_USUARIO, response.getInt(ID_USUARIO));

                    Intent i;

                    if(rol == 1){
                        i = new Intent(MainActivity.this, ActivityInicioCliente.class);
                        editor.putBoolean(ES_CLIENTE, true);
                    }else if (rol == 0){//ES EMPLEADO
                        editor.putBoolean(ES_CLIENTE, false);
                        //SE PASA AL CONTEXTO ActivityEmpleado
                        i = new Intent(MainActivity.this,ActivityInicioEmpleado.class);
                    }else {//ES ADMINISTRADOR
                        editor.putBoolean(ES_CLIENTE, false);
                        //SE PASA AL CONTEXTO ActivityEmpleado
                        i = new Intent(MainActivity.this,ActivityInicioEmpleado.class);
                    }
                    editor.apply();

                    i.putExtra(ID_USUARIO, response.getInt(ID_USUARIO));
                    i.putExtra(EMAIL, etNombre2.getText().toString());
                    i.putExtra(PASSWORD, etPass.getText().toString());
                    i.putExtra(NOMBRE, response.getString(NOMBRE));
                    i.putExtra(TELEFONO, response.getInt(TELEFONO));
                    i.putExtra(DIRECCION, response.getString(DIRECCION));
                    i.putExtra(COD_POSTAL, response.getInt(COD_POSTAL));
                    i.putExtra(LOCALIDAD, response.getString(LOCALIDAD));
                    i.putExtra(PROVINCIA, response.getString(PROVINCIA));
                    i.putExtra(FIREBASE, response.getString(FIREBASE));
                    i.putExtra(DNI_CIF, response.getString(DNI_CIF));
                    i.putExtra(ROL, response.getInt(ES_CLIENTE));
                    startActivity(i);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);

    }
        /**
         * En el caso de que el usuario venga de resetear su password desde firebase,
         * se actualiza en bbdd la contraseña a la nueva.
         */
        private void updatePassword () {
            //TODO ... PROBAR CON FIREBASE
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            StringRequest request = new StringRequest(Request.Method.POST, URL_BASICA + UPDATE_PASSWORD, response -> {
                if (response.equals("true")) {//password actualizado
                    Toast.makeText(MainActivity.this, "update de password ok", Toast.LENGTH_SHORT).show();
                } else {//error en la actualización de password
                    Toast.makeText(MainActivity.this, "error en la actualización de password", Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                // method to handle errors.
                Toast.makeText(MainActivity.this, "Fallo de respuesta = " + error, Toast.LENGTH_SHORT).show();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(EMAIL, etNombre2.getText().toString().trim());
                    params.put(PASSWORD, etPass.getText().toString());
                    return params;
                }
            };
            queue.add(request);
        }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.i("TAG","El permiso para recibir notificaciones ha sido concedido");
                } else {
                    Log.i("TAG","El permiso para pra recibir notificaciones ha sido denegado");
                }
            });

    private void preguntarPermisoNotificaciones() {
       //Este proceso solo es necesario cuando la api es superior a la api 33:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                Log.i("TAG","La aplicación tiene permisos para recibir notificaciones");
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Log.i("TAG","El permiso de notificaciones ha sido denegado");
                Toast.makeText(this, "El permiso de notificaciones ha sido denegado", Toast.LENGTH_SHORT).show();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
    public void cargarPreferences(){
        cargaPreferences=true;
        SharedPreferences preferences=getPreferences(MODE_PRIVATE);
        email=preferences.getString(EMAIL,"error");
        String pass=preferences.getString(PASSWORD,"error");
        //TODO REVISAR CARGA SHAREDPREFERENCES PARA BIOMETRIA:
        if((!email.equalsIgnoreCase("error") && email!=null)|| (!pass.equalsIgnoreCase("error")&& email!=null)){
            etNombre2.setText(email);
            etPass.setText(pass);
                verPass=findViewById(R.id.tiLayout);
            verPass.setPasswordVisibilityToggleEnabled(!email.equalsIgnoreCase(etNombre2.getText().toString()));

        }
    }

    }



