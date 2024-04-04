package com.example.proyectodam;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Registro extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //Atributos de campo:
    private Vehiculos vehiculo;
    private ImageButton btRegistro;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Cliente cliente;
    private RadioButton rbFisica,rbEmpresa;
    private EditText etAnnio;
    private AutoCompleteTextView etMarca;
    private Spinner spTipo, spModelo, spGeneracion, spSerie, spMotor, spCombustible, spCambio;

    @SuppressLint({"ResourceType", "MissingInflatedId"})
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
                // Back is pressed... Finishing the activity
                Intent intent=new Intent(Registro.this,MainActivity.class);
                startActivity(intent);
            }
        });

        //CODIGO PARA PONER BARRA NOTIFICACIONES EN UN COLOR CONCRETO:
        getWindow().setStatusBarColor(getResources().getColor(R.color.azul,getTheme()));
        rbFisica=findViewById(R.id.rbFisica);
        rbEmpresa=findViewById(R.id.rbEmpresa);
        btRegistro=findViewById(R.id.btRegistrar);

        iniciarFormularioVehiculos();
        vehiculo=new Vehiculos(Registro.this);
        vehiculo.consulta(0, "obtenerTipo.php", "id_vehiculo_tipo", "tipo", "tipo", "tipo", spTipo);
        iniciarComportamientoElementos();

        btRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();

            }

            private void validarDatos() {
                boolean resultado;
                //Se inicializa la lista para recoger datos:
                cliente=new Cliente();

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
                TextInputLayout ilPass=findViewById(R.id.etPassword1);
                TextInputEditText etNombre=findViewById(R.id.etNombre2);
                TextInputEditText etDni=findViewById(R.id.etDni2);
                TextInputEditText etDireccion=findViewById(R.id.etDireccion2);
                TextInputEditText etLocalidad=findViewById(R.id.etLocalidad2);
                TextInputEditText etCodPostal=findViewById(R.id.etPostal2);
                TextInputEditText etProvincia=findViewById(R.id.etProvincia2);
                TextInputEditText etTelefono=findViewById(R.id.etPhone2);
                TextInputEditText etMail=findViewById(R.id.etMail2);
                TextInputEditText etPass=findViewById(R.id.etPassword2);

                //Se comienzan a realizar la comprobaciones de pattern de los datos introducidos por el usuario en el formulario de registro.
                resultado=compruebaPatron(ilNombre,1,"Debe de introducir un nombre,",etNombre,"[a-zA-Z]{1,}");
                if(rbFisica.isChecked()){
                    resultado=compruebaPatron(ilDni,2,"Introduzca un Dni válido sin guiones ni puntos,",etDni,"[0-9]{8}[a-zA-Z]");
                }else{
                    resultado=compruebaPatron(ilDni,2,"Introduzca un Cif válido sin guiones ni puntos con letra en mayúscula,",etDni,"[a-zA-Z}{1}[0-9]{8}");
                }
                resultado=compruebaPatron(ilDireccion,3,"Introduzca una direccion válida",etDireccion,"vacio");
                resultado=compruebaPatron(ilLocalidad,4,"Introduzca una localidad válida,",etLocalidad,"[a-zA-Z]{1,}");
                resultado=compruebaPatron(ilCodPostal,5,"Introduzca un código postal válido,",etCodPostal,"[0-9]{5}");
                resultado=compruebaPatron(ilProvincia,6,"Introduzca una provincia válida,",etProvincia,"[a-zA-Z]{1,}");
                resultado=compruebaPatron(ilTelefono,7,"Introduzca un número sin guiones ni puntos,",etTelefono,"[0-9]{9}");
                //[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*@[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[.][a-zA-Z]{2,5} --> EMAIL
                resultado=compruebaPatron(ilMail,8,"Introduzca una dirección de email válida,",etMail,"[a-zA-Z0-9]{1,}@[a-zA-Z0-9]{1,}.[a-zA-Z]{2,3}");
                resultado=compruebaPatron(ilPass,9,"Introduzca una contraseña válida",etPass,"[A-Z]{1}[a-z]{1,}_[0-9]{1,}");

                if(resultado){
                    cliente.setNombre(etNombre.getText().toString());
                    cliente.setDni(etDni.getText().toString());
                    cliente.setDireccion(etDireccion.getText().toString());
                    cliente.setLocalidad(etDireccion.getText().toString());
                    cliente.setCodPostal(Integer.parseInt(etCodPostal.getText().toString().trim()));
                    cliente.setProvincia(etProvincia.getText().toString().trim());
                    cliente.setTelefono(Integer.parseInt(etTelefono.getText().toString().trim()));
                    cliente.setEmail(etMail.getText().toString().trim());
                    cliente.setPassword(etPass.getText().toString().trim());
                    cliente.setEmpleado("SI");
                    registrarDatosCliente(cliente);
                }else{
                    Toast.makeText(getApplicationContext(), "REVISE LOS DATOS Y PULSE EN REGISTRAR DE NUEVO", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void iniciarComportamientoElementos() {
        etMarca.setOnItemSelectedListener(this);

        etMarca.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
            @Override
            public void onDismiss() {
                Vehiculos.id_marca=vehiculo.obtenIdPredictivo(etMarca);
                Toast.makeText(Registro.this, String.valueOf(Vehiculos.id_marca), Toast.LENGTH_LONG).show();
                vehiculo.consulta(Vehiculos.id_marca, "obtenModelo.php", "id_vehiculo_modelo", "modelo", "modelo", "modelo", spModelo);
            }
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

            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(Registro.this, (datePicker, year, month, day) -> {
                etAnnio.setText(month + "/" + year);
                Vehiculos.setCombustibleUser(String.valueOf(year));
                Toast.makeText(Registro.this, "Anio: " + Vehiculos.getCombustibleUser(), Toast.LENGTH_SHORT).show();

            }, annio, mes, dia);
            datePickerDialog.updateDate(annio, mes, dia);
            datePickerDialog.show();
        });

        vehiculo.populateSpinner(spCombustible, "combustible");
        vehiculo.populateSpinner(spCambio, "cambio");
        spCombustible.setSelection(0);
        spCambio.setSelection(0);
        spCombustible.setOnItemSelectedListener(this);
        spCambio.setOnItemSelectedListener(this);

    }

    private void iniciarFormularioVehiculos() {
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

    private void registrarDatosCliente(Cliente cliente) {
        //REGISTRO EN FIREBASE:
        mAuth.createUserWithEmailAndPassword(cliente.getEmail(), cliente.getPassword())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Si el registro es satisfactorio:
                        user = mAuth.getCurrentUser();
                        Toast.makeText(getApplicationContext(),"Registrado con exito",Toast.LENGTH_SHORT).show();

                    } else {
                        // Si el registro ha fallado:
                        user=mAuth.getCurrentUser();
                        if(user.getEmail().equalsIgnoreCase(cliente.getEmail())){
                            Toast.makeText(getApplicationContext(), "Ya existe un usuario con ese correo electrónico", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "Ocurrió un problema en el registro, intentelo de nuevo", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    private boolean compruebaPatron(TextInputLayout elLayout, int cajon, String motivoError, TextInputEditText etInputText, String cadena) {
        if (cadena.equalsIgnoreCase("vacio")) {
                if (etInputText.getText().toString().length() == 0) {
                    elLayout.setError(motivoError);
                    return false;
                } else {
                    elLayout.setError(null);
                    return true;
                }
        } else {
            Pattern pattern = Pattern.compile(cadena, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(etInputText.getText().toString());
            if (matcher.matches()) {
                switch (cajon) {
                    case 1:
                        elLayout.setError(null);
                        cliente.setNombre(etInputText.getText().toString());
                        break;
                    case 2:
                        elLayout.setError(null);
                        cliente.setDni(etInputText.getText().toString());
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
                        cliente.setProvincia(etInputText.getText().toString());
                        break;
                    case 7:
                        elLayout.setError(null);
                        cliente.setTelefono(Integer.parseInt(etInputText.getText().toString()));
                        break;
                    case 8:
                        elLayout.setError(null);
                        cliente.setEmail(etInputText.getText().toString());
                        break;
                    case 9:
                        elLayout.setError(null);
                        cliente.setPassword(etInputText.getText().toString());
                        break;
                }
                return true;
            } else {
                elLayout.setError(motivoError);
                return false;
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(adapterView.getId()==R.id.spTipoVehiculo) {
                Vehiculos.id_tipo = vehiculo.obtenId(adapterView, "tipo", "marcas", "marcas");
                vehiculo.consultaPredictivo(Vehiculos.id_tipo, "obtenerDatosCopia.php", "id_vehiculo_marca", "marca", "marcas", "marcas", etMarca);
            }else if(adapterView.getId()==R.id.etMarcaVehiculo){
                Vehiculos.id_marca = vehiculo.obtenId(adapterView, "marcas", "modelo", "modelo");
                vehiculo.consulta(Vehiculos.id_marca, "obtenModelo.php", "id_vehiculo_modelo", "modelo", "modelo", "modelo", spModelo);
                Toast.makeText(getApplicationContext(), "Marca: " + String.valueOf(Vehiculos.id_marca), Toast.LENGTH_SHORT).show();
            }else if(adapterView.getId()==R.id.spModeloVehiculo) {
                Vehiculos.id_modelo = vehiculo.obtenId(adapterView, "modelo", "generacion", "generacion");
                vehiculo.consulta(Vehiculos.id_modelo, "obtenGeneracion.php", "id_vehiculo_generacion", "generacion", "generacion", "generacion", spGeneracion);
            }else if(adapterView.getId()==R.id.spGeneracionVehiculo) {
                Vehiculos.id_generacion = vehiculo.obtenId(adapterView, "generacion", "serie", "serie");
                //int variable1a = Vehiculos.id_modelo;
                int variable2a = Vehiculos.id_generacion;
                if (variable2a != 0) {
                    Toast.makeText(Registro.this,String.valueOf(variable2a),Toast.LENGTH_LONG).show();
                    vehiculo.consulta(variable2a, "obtenSerie.php", "id_vehiculo_serie", "nombre", "serie", "serie",  spSerie);
                }
            }else if(adapterView.getId()==R.id.spSerieVehiculo) {
                Vehiculos.id_serie = vehiculo.obtenId(adapterView, "serie", "motor", "motor");
                int variable1 = Vehiculos.id_modelo;
                int variable2 = Vehiculos.id_serie;
                if (variable1 != 0 && variable2 != 0) {
                    vehiculo.consultaMultiple(variable1, variable2, "id", "serie", "obtenMotor.php", "id_car_trim", "name", "motor", "motor", spMotor);
                }
            }else if(adapterView.getId()==R.id.spMotorVehiculo) {
                Vehiculos.id_motor = vehiculo.obtenIdSinParametros(adapterView, "motor");
                spCombustible.setVisibility(View.VISIBLE);
                spCambio.setVisibility(View.VISIBLE);
            }else if(adapterView.getId()==R.id.spCombustibleVehiculo) {
                Vehiculos.combustibleUser = spCombustible.getSelectedItem().toString();
            }else{
                Vehiculos.cambioUser = spCambio.getSelectedItem().toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}