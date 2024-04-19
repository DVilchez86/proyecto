package com.example.proyectodam;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.proyectodam.Vehiculos.requestQueue;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //Atributos de campo:
    public String idToken;
    private Vehiculos vehiculo;
    private ImageButton btRegistro;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Cliente usuario;
    private RadioButton rbFisica,rbEmpresa;
    private EditText etAnnio;
    private AutoCompleteTextView etMarca;
    private TextInputEditText etMatricula, etKilometros;
    private Spinner spTipo, spModelo, spGeneracion, spSerie, spMotor, spCombustible, spCambio;

    @SuppressLint({"ResourceType", "MissingInflatedId", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        mAuth= FirebaseAuth.getInstance();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Método a partir de API33 para volver atras al estar el método onBackPressed() deprecated:
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent=new Intent(Registro.this,MainActivity.class);
                startActivity(intent);
            }
        });

        //CODIGO PARA PONER BARRA NOTIFICACIONES EN UN COLOR CONCRETO:
        getWindow().setStatusBarColor(getResources().getColor(R.color.azul,getTheme()));
        rbFisica=findViewById(R.id.rbFisica);
        rbEmpresa=findViewById(R.id.rbEmpresa);
        btRegistro=findViewById(R.id.btRegistrar);
        //Inicializo el formulario:
        iniciarFormularioVehiculos();
        //Instancio los métodos a utilizar de la clase vehiculo:
        vehiculo=new Vehiculos(Registro.this);
        vehiculo.consulta(0, "tipo.php", "id_tipo", "tipo", "tipo", "tipo", spTipo);
        usuario=new Cliente(Registro.this);
        iniciarComportamientoElementos();

        btRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
            }
            /*
            Método validarDatos: Se utiliza para comprobar con patrones pattern que todos los datos introducidos respeten un patrón para
            garantizar la integridad de la base de datos, así como para cotejar que se encuentran recogidos todos los datos que se piden
            y que no falta ninguno.
             */
            private void validarDatos() {
                boolean resultado;

                /*Se instancian e inicializan los TextInputLayout para marcar los errores de pattern en le porpio textInputEditText para salvaguardar integridad
                de datos en base de datos
                 */
                TextInputLayout ilNombre=findViewById(R.id.etNombre1);
                TextInputLayout ilDni=findViewById(R.id.etDni1);
                TextInputLayout ilDireccion=findViewById(R.id.etDireccion1);
                TextInputLayout ilLocalidad=findViewById(R.id.etLocalidad1);
                TextInputLayout ilCodPostal=findViewById(R.id.etPostal1);
                TextInputLayout ilProvincia=findViewById(R.id.etProvincia1);
                TextInputLayout ilTelefono=findViewById(R.id.etPhone1);
                TextInputLayout ilMail=findViewById(R.id.etMail1);
                TextInputLayout ilMatricula=findViewById(R.id.etMatricula1);
                TextInputLayout ilKilometros=findViewById(R.id.etKms1);
                TextInputLayout ilPass=findViewById(R.id.etPassword1);
                TextInputLayout ilPass2=findViewById(R.id.etPasswordRepite);
                TextInputEditText etNombre=findViewById(R.id.etNombre2);
                TextInputEditText etDni=findViewById(R.id.etDni2);
                TextInputEditText etDireccion=findViewById(R.id.etDireccion2);
                TextInputEditText etLocalidad=findViewById(R.id.etLocalidad2);
                TextInputEditText etCodPostal=findViewById(R.id.etPostal2);
                TextInputEditText etProvincia=findViewById(R.id.etProvincia2);
                TextInputEditText etTelefono=findViewById(R.id.etPhone2);
                TextInputEditText etMail=findViewById(R.id.etMail2);
                TextInputEditText etPass=findViewById(R.id.etPassword2);
                TextInputEditText etPass2=findViewById(R.id.etPassword2Repite);
                TextInputEditText etMatricula=findViewById(R.id.etMatricula2);
                TextInputEditText etKilometros=findViewById(R.id.etKms2);

                //Se comienzan a realizar la comprobaciones de pattern de los datos introducidos por el usuario en el formulario de registro.
                resultado=compruebaPatron(ilNombre,1,"Debe de introducir un nombre,",etNombre,"vacio",usuario);
                if(rbFisica.isChecked()){
                    resultado=compruebaPatron(ilDni,2,"Introduzca un Dni válido sin guiones ni puntos,",etDni,"[0-9]{8}[a-zA-Z]",usuario);
                }else{
                    resultado=compruebaPatron(ilDni,2,"Introduzca un Cif válido sin guiones ni puntos con letra en mayúscula,",etDni,"[a-zA-Z]{1}[0-9]{8}",usuario);
                }
                resultado=compruebaPatron(ilDireccion,3,"Introduzca una direccion válida",etDireccion,"vacio",usuario);
                resultado=compruebaPatron(ilLocalidad,4,"Introduzca una localidad válida,",etLocalidad,"vacio",usuario);
                resultado=compruebaPatron(ilCodPostal,5,"Introduzca un código postal válido,",etCodPostal,"[0-9]{5}",usuario);
                resultado=compruebaPatron(ilProvincia,6,"Introduzca una provincia válida,",etProvincia,"[a-zA-Z]{1,}",usuario);
                resultado=compruebaPatron(ilTelefono,7,"Introduzca un número sin guiones ni puntos,",etTelefono,"[0-9]{9}",usuario);
                //[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*@[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[.][a-zA-Z]{2,5} --> EMAIL
                resultado=compruebaPatron(ilMail,8,"Introduzca una dirección de email válida,",etMail,"[a-zA-Z0-9]{1,}@[a-zA-Z0-9]{1,}.[a-zA-Z]{2,3}",usuario);
                resultado=compruebaPatron(ilPass,9,"Introduzca una contraseña válida",etPass,"[A-Z]{1}[a-z]{1,}_[0-9]{1,}",usuario);
                resultado=compruebaPatron(ilMatricula,10,"Introduzca una matrícula válida",etMatricula,"[0-9]{4}[a-zA-Z]{3}",usuario);
                resultado=compruebaPatron(ilKilometros,11,"Introduzca una kilometraje válido",etKilometros,"[0-9]{1,}",usuario);
                boolean coincidePass=true;
                if(!etPass.getText().toString().equals(etPass2.getText().toString())){
                    coincidePass=false;
                }
                resultado=vehiculo.compruebaDatos();
                if(resultado && coincidePass){
                    usuario.setNombre(etNombre.getText().toString());
                    usuario.setDni(etDni.getText().toString());
                    usuario.setDireccion(etDireccion.getText().toString());
                    usuario.setLocalidad(etDireccion.getText().toString());
                    usuario.setCodPostal(Integer.parseInt(etCodPostal.getText().toString().trim()));
                    usuario.setProvincia(etProvincia.getText().toString().trim());
                    usuario.setTelefono(Integer.parseInt(etTelefono.getText().toString().trim()));
                    usuario.setEmail(etMail.getText().toString().trim());
                    usuario.setPassword(etPass.getText().toString().trim());
                    usuario.setEmpleado(1);
                    registrarDatosCliente(usuario);
                     obtenerTokenCliente();
                     toast(Registro.this,"Registro creado correctamente");
                     Intent intent=new Intent(Registro.this,MainActivity.class);
                     startActivity(intent);
                     finish();
                }else{
                    if(!coincidePass){
                        toast(Registro.this,"Verifique las contraseñas, no coinciden.");
                    }
                    toast(Registro.this,"REVISE LOS DATOS Y PULSE EN REGISTRAR DE NUEVO");
                }
            }
        });
    }
    /*
    Método iniciarComportamientoElementos(): Se utiliza para inicializar de forma encapsulada el comportamiento de seleccion de
    todos los elementos de campo que intervienen en la clase Registro.class.
     */

    private void iniciarComportamientoElementos() {
        etMarca.setOnItemSelectedListener(this);
        etMarca.setOnDismissListener(() -> {
            Vehiculos.id_marca=vehiculo.obtenIdPredictivo(etMarca);
            vehiculo.consulta(Vehiculos.id_marca, "modeloByTipoMarca.php", "id_modelo", "modelo", "modelo", "modelo", spModelo);
        });
        spTipo.setOnItemSelectedListener(this);
        spSerie.setOnItemSelectedListener(this);
        spModelo.setOnItemSelectedListener(this);
        spGeneracion.setOnItemSelectedListener(this);
        spMotor.setOnItemSelectedListener(this);
        etAnnio.setOnClickListener(view -> {
            final Calendar c = Calendar.getInstance();
            int dia = c.get(Calendar.DAY_OF_MONTH);
            int mes = c.get(Calendar.MONTH);
            int annio = c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(Registro.this, (datePicker, year, month, day) -> {
                etAnnio.setText(String.valueOf(year));
                vehiculo.setAnnioUser(String.valueOf(year));
            }, annio, mes, dia);
            datePickerDialog.updateDate(annio, mes, dia);
            datePickerDialog.show();
        });

        spCombustible.setSelection(0);
        spCambio.setSelection(0);
        spCombustible.setOnItemSelectedListener(this);
        spCambio.setOnItemSelectedListener(this);

    }
    /*
    Método iniciarFormularioVehiculos(): Se utiliza para inicializar de forma encapsulada los atributos de campo de la clase Registro.class.
     */
    private void iniciarFormularioVehiculos() {
        etMatricula=findViewById(R.id.etMatricula2);
        etKilometros=findViewById(R.id.etKms2);
        etAnnio = findViewById(R.id.etAnnio);
        spCombustible = findViewById(R.id.spCombustibleVehiculo);
        spCambio = findViewById(R.id.spCambioVehiculo);
        spTipo = findViewById(R.id.spTipoVehiculo);
        etMarca = findViewById(R.id.etMarcaVehiculo);
        spSerie = findViewById(R.id.spSerieVehiculo);
        spModelo = findViewById(R.id.spModeloVehiculo);
        spGeneracion = findViewById(R.id.spGeneracionVehiculo);
        spMotor = findViewById(R.id.spMotorVehiculo);
    }

    /*
    Método compruebaPatron: Devuelve valor booleano si falta algún campo por cumplimentar, y lo pone a modo de ayuda e indicación al usuario en el
    TextInputLayout correspondiente.
     */
    public boolean compruebaPatron(TextInputLayout elLayout, int cajon, String motivoError, TextInputEditText etInputText, String cadena,Cliente cliente) {
        if (cadena.equalsIgnoreCase("vacio")) {
            if (etInputText.getText().toString().length() == 0) {
                elLayout.setError(motivoError);
                return false;
            } else {
                elLayout.setError(null);
                return true;
            }
        } else {
            Pattern pattern = Pattern.compile(cadena);
            Matcher matcher = pattern.matcher(etInputText.getText().toString());
            if (matcher.matches()) {
                switch (cajon) {
                    case 1:
                        elLayout.setError(null);
                        cliente.setNombre(etInputText.getText().toString());
                        break;
                    case 2:
                        elLayout.setError(null);
                        String dniCif=etInputText.getText().toString();
                        cliente.setDni(dniCif.toUpperCase());
                        break;
                    case 3:
                        elLayout.setError(null);
                        cliente.setDireccion(etInputText.getText().toString());
                        break;
                    case 4:
                        elLayout.setError(null);
                        cliente.setLocalidad(etInputText.getText().toString());
                        break;
                    case 5:
                        elLayout.setError(null);
                        cliente.setCodPostal(Integer.parseInt(etInputText.getText().toString()));
                        break;
                    case 6:
                        elLayout.setError(null);
                        String prov=etInputText.getText().toString();
                        cliente.setProvincia(prov);
                        break;
                    case 7:
                        elLayout.setError(null);
                        cliente.setTelefono(Integer.parseInt(etInputText.getText().toString()));
                        break;
                    case 8:
                        elLayout.setError(null);
                        String email=etInputText.getText().toString();
                        cliente.setEmail(email.toLowerCase());
                        break;
                    case 9:
                        elLayout.setError(null);
                        cliente.setPassword(etInputText.getText().toString());
                        break;
                    case 10:
                        elLayout.setError(null);
                        String mat=etMatricula.getText().toString();
                        vehiculo.setMatricula(mat.toUpperCase());
                        break;
                    case 11:
                        elLayout.setError(null);
                        Vehiculos.kilometros=Integer.parseInt(etInputText.getText().toString());
                        break;
                }
                return true;
            } else {
                elLayout.setError(motivoError);
                return false;
            }
        }
    }
    /*
    Método onItemSelected: Este método regula las acciones al pulsar cada uno de los item seleccionados:
     */

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId()==R.id.spTipoVehiculo) {
            Vehiculos.id_tipo = vehiculo.obtenId(adapterView, "tipo", "marcas", "marcas");
            vehiculo.consultaPredictivo(Vehiculos.id_tipo, "marcas.php", "id_marca", "marca", "marcas", "marcas", etMarca);
        }else if(adapterView.getId()==R.id.etMarcaVehiculo){
            Vehiculos.id_marca = vehiculo.obtenId(adapterView, "marcas", "modelo", "modelo");
        }else if(adapterView.getId()==R.id.spModeloVehiculo) {
            Vehiculos.id_modelo = vehiculo.obtenId(adapterView, "modelo", "generacion", "generacion");
            vehiculo.consulta(Vehiculos.id_modelo, "generacionByModeloYear.php", "id_generacion", "generacion", "generacion", "generacion",spGeneracion);
        }else if(adapterView.getId()==R.id.spGeneracionVehiculo) {
            Vehiculos.id_generacion = vehiculo.obtenId(adapterView, "generacion", "serie", "serie");
            int variable1a = Vehiculos.id_modelo;
            int variable2a = Vehiculos.id_generacion;
            if (variable2a != 0 && variable1a!=0) {
                vehiculo.consultaMultiple(variable1a,variable2a, "id_modelo", "id_generacion", "serieByGeneracion.php", "id_serie", "serie","serie","serie",spSerie);
            }
        }else if(adapterView.getId()==R.id.spSerieVehiculo) {
            Vehiculos.id_serie = vehiculo.obtenId(adapterView, "serie", "motor", "motor");
            int variable1 = Vehiculos.id_serie;
            int variable2 = Vehiculos.id_modelo;
            if (variable1 != 0 && variable2 != 0) {
                vehiculo.consultaMultiple(variable1, variable2, "id_serie", "id_modelo", "motorBySerieModelo.php", "id_vehiculo_trim", "version", "motor", "motor", spMotor);
            }
        }else if(adapterView.getId()==R.id.spMotorVehiculo) {
            Vehiculos.setId_motor(vehiculo.obtenIdSinParametros(adapterView, "motor"));
            spCombustible.setVisibility(View.VISIBLE);
            spCambio.setVisibility(View.VISIBLE);
        }else if(adapterView.getId()==R.id.spCombustibleVehiculo) {
            vehiculo.setCombustibleUser(spCombustible.getSelectedItem().toString());
        }else if(adapterView.getId()==R.id.spCambioVehiculo){
            vehiculo.setCambioUser(spCambio.getSelectedItem().toString());
        }else if(adapterView.getId()==R.id.etMatricula2) {
            String mat=etMatricula.getText().toString();
            vehiculo.setMatricula(mat.toUpperCase());
        }else{
            Vehiculos.kilometros=Integer.parseInt(etKilometros.getText().toString());
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void introduceDatosCliente(String tokenCliente){
        String url="https://www.focused-kepler.85-214-239-118.plesk.page/app/usuarios/nuevoUsuario.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, response -> {
            if(response.contains("correctamente")){
                obtenerIdUsuario(usuario.getEmail());
            }else{
                Log.i("TAG", "No se ha realizado el alta del cliente.");
            }

        }, error -> {
            Toast.makeText(Registro.this,"ERROR al guardar el registro.",Toast.LENGTH_SHORT).show();
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("nombre",usuario.getNombre());
                parametros.put("dni_cif", usuario.getDni());
                parametros.put("direccion",usuario.getDireccion());
                parametros.put("localidad",usuario.getLocalidad());
                parametros.put("codPostal", String.valueOf(usuario.getCodPostal()));
                parametros.put("provincia",usuario.getProvincia());
                parametros.put("telefono",String.valueOf(usuario.getTelefono()));
                parametros.put("email",usuario.getEmail());
                parametros.put("password",usuario.getPassword());
                parametros.put("firebase", tokenCliente);
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(Registro.this);
        requestQueue.add(stringRequest);
    }

    /*
    Método que se encaga de guardar los datos de los vehiculos:
     */
    private void introduceDatosVehiculo(int idClient){
        String url="https://www.focused-kepler.85-214-239-118.plesk.page/app/veh_cli/nuevoVehiculoCliente.php";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, response -> {

            if(response.contains("correctamente")) {
                Log.i("TAG", response);
            }else{
                Toast.makeText(Registro.this, response, Toast.LENGTH_SHORT).show();
                Log.i("TAG", response);
            }
        }, error -> {
            Toast.makeText(Registro.this,"ERROR al guardar el registro.",Toast.LENGTH_SHORT).show();
            Log.e("TAG",error.getMessage());
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros = new HashMap<>();
                parametros.put("id_usuario",String.valueOf(idClient));
                parametros.put("id_vehiculo_trim", String.valueOf(Vehiculos.id_motor));
                parametros.put("matricula", vehiculo.getMatricula());
                parametros.put("kilometraje",String.valueOf(Vehiculos.kilometros));
                parametros.put("combustible", vehiculo.getCombustibleUser());
                parametros.put("cambio", vehiculo.getCambioUser());
                parametros.put("anio",vehiculo.getAnnioUser());
                return parametros;
            }
        };
        requestQueue = Volley.newRequestQueue(Registro.this);
        requestQueue.add(stringRequest);

    }
        /*
        Método obtenerToken(): Sirve para rescatar el token único el cliente y poder enviarlo al registro de cliente para tenerlo identificado, y posteriormente enviarle notificaciones
        o mensajes pensonalizados.
         */

    public void obtenerToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        idToken="no existe";
                        Log.w("TAG", "No se ha podido otener el token".concat( task.getException().toString()));
                    }else{
                        idToken = task.getResult();
                        usuario.setToken(idToken);
                        //Si la respuesta es positiva, guardamos el token como shared preferences.
                        SharedPreferences preferences =getSharedPreferences("token", Context.MODE_PRIVATE);
                        SharedPreferences.Editor sharedEditor = preferences.edit();
                        sharedEditor.putString("token", idToken);
                        sharedEditor.apply();
                        Log.i("TAG","Token del cliente: ".concat(idToken));
                    }
                });
    }
    /*
    Método que se utiliza para realizar el logueo en la cuenta de Firebase si el usuario esta registrado:
     */
    private void loguearFirebase() {
        mAuth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getPassword())
                .addOnCompleteListener(Registro.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        user = mAuth.getCurrentUser();
                        Log.i(TAG, task.getResult().toString());

                    } else {
                        if(task.getException().toString().contains("CREDENTIALS")){
                            Toast.makeText(Registro.this, "Contraseña incorrecta, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, task.getException().toString());
                        }else {
                            Toast.makeText(Registro.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, task.getException().toString());
                        }
                    }
                });
    }
    /*
   Método registrarDatosCliente: Se utiliza para registrar al usuario en Firebase para posteriormente obtener un token que introduciremos en la base
   de datos a fin de poder enviar notificaciones a los clientes:
    */
    private void registrarDatosCliente(Cliente cliente) {
        //REGISTRO EN FIREBASE:
        mAuth.createUserWithEmailAndPassword(cliente.getEmail(), cliente.getPassword())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Si el registro es satisfactorio:
                        user = mAuth.getCurrentUser();

                    } else {
                        // Si el registro ha fallado:
                        if(task.getException().getMessage().contains("auth/email-already-in-use")){
                            Toast.makeText(getApplicationContext(), "Ya existe un usuario con ese correo electrónico", Toast.LENGTH_SHORT).show();
                        }
                        if(user.getEmail().equalsIgnoreCase(cliente.getEmail())){
                            Toast.makeText(getApplicationContext(), "Ya existe un usuario con ese correo electrónico", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Ocurrió un problema en el registro, intentelo de nuevo", Toast.LENGTH_SHORT).show();
                        }
                        loguearFirebase();
                    }
                });
    }
    /*
    Método encapsulado que se utiliza cada vez que queramos usar una notificación toast, pasándole como String el mensaje a mostrar en cada caso.
     */
    public void toast(Context context,String mensaje){
        Toast.makeText(context,mensaje,Toast.LENGTH_SHORT).show();
    }
   /*
   Método que se utiliza para obtener el Token ID del usuario registrado en Firebase, a fin de guardarlo en nuestra Bdd, y poder
   utilizarlo posteriormente para enviar notificaciones al cliente:
    */
    public void obtenerTokenCliente() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        usuario.setToken("no existe");
                        Log.w("TAG", "No se ha podido otener el token".concat( task.getException().toString()));
                    }else{
                        usuario.setToken(task.getResult());
                        introduceDatosCliente(usuario.getToken());
                        Log.i("TAG","ESte es el token desde cliente: ".concat(usuario.getToken()));
                    }
                });
    }
    /*
    Método usado para obtener el Id autoincremental que se ha asignado al usuario que se acaba de introducir, o para saber a partir
    de un email, el ID que corresponde a ese usuario en concreto:
     */
    public void obtenerIdUsuario(String mail){
        String url="https://www.focused-kepler.85-214-239-118.plesk.page/app/usuarios/idByEmail.php?email="+mail;

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject object=response.getJSONObject(0);
                usuario.setId(Integer.parseInt(object.getString("id_usuario")));
                introduceDatosVehiculo(usuario.getId());
                Log.i("TAG","Id desde cliente: ".concat(String.valueOf(usuario.getId())));
            } catch (JSONException e) {
                Log.e("TAG","ERROR IDUSUARIO: ".concat(e.getMessage()));
            }

        }, error -> {
            if(error.getMessage()!=null){
                Log.i("TAG",error.getMessage().concat(".Error en obtenerIdUsuario"));
            }
        });
        requestQueue = Volley.newRequestQueue(Registro.this);
        requestQueue.add(jsonArrayRequest);
    }

}
