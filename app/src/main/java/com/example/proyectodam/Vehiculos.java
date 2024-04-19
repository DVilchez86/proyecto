package com.example.proyectodam;

import static android.R.layout.simple_dropdown_item_1line;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Vehiculos {
    //Atributos de campo:
    public static RequestQueue requestQueue;
    private Context context;
    public String combustibleUser, cambioUser, matricula;
    private String annioUser = null;
    private static final String URL_BASICA = "https://www.focused-kepler.85-214-239-118.plesk.page/app/vehiculos/";
    private ArrayList<String> marcas, tipo, modelo, generacion, serie, motor, combustible, cambio;
    private ArrayList<Objeto> listadoMarcas, listadoTipo, listadoModelo, listadoGeneracion, listadoSerie, listadoMotor;
    public static int id_tipo=0, id_marca=0, id_modelo=0, id_serie=0, id_generacion=0, id_motor=0, kilometros=0;

    //Constructores:
    public Vehiculos(Context context){
        this.context=context;
        iniciarArrayListVehiculos();
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }
    //Métodos:
    /*
    El método rellenaSpinner lo utilizo para llamarlo desde dentro de los métodos Volley para ir rellenando los diferentes Spinner a utilizar:
     */
    public void rellenaSpinner(Spinner spinner, String listaString) {
        List<String> lista = new ArrayList<>();
        lista.addAll(devuelveLista(listaString));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context.getApplicationContext(), android.R.layout.simple_spinner_item, lista);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0);
    }
    /*
    Método Consulta: Utilizado para obtener información de la base de datos para aquellas consultas que solo tienen un parámetro get:
     */
    public void consulta(int variable, String url, String id_campo, String nombreCampo, String listaObjeto, String textoSpinner, Spinner spinner) {
        String URL1 = URL_BASICA +url + "?id=" +variable;


        if (variable == 0) {
            String urlSecundaria=URL_BASICA +url;
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlSecundaria, null, response -> {
                devuelveLista(textoSpinner).add("Elije opcion");
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String id = jsonObject.getString(id_campo);
                        String marca = jsonObject.getString(nombreCampo);
                        Objeto objeto = new Objeto();
                        objeto.setId(Integer.parseInt(id));
                        objeto.setDescripcion(marca);
                        devuelveListadoObjeto(listaObjeto).add(objeto);
                        devuelveLista(textoSpinner).add(marca);
                    } catch (JSONException e) {
                        Toast.makeText(context.getApplicationContext(), "Bien pero falla json tipo", Toast.LENGTH_LONG).show();
                        Log.i("TAG", "Se ha producido un fallo pese que responde el Json de tipo");
                    }
                }
                rellenaSpinner(spinner, textoSpinner);

            }, error -> Log.i("TAG", error.getCause().toString().concat( " fallo en consulta variable 0")));
            requestQueue.add(jsonArrayRequest);
            spinner.setVisibility(View.VISIBLE);


        } else {
            if (variable == 9999) {
                URL1 = url;
            }
            //PARA JSON DE VARIOS ARRAYS EN RESPUESTA:
            JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, URL1, null, response -> {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String id = jsonObject.getString(id_campo);
                        String marca = jsonObject.getString(nombreCampo);
                        Objeto objeto = new Objeto();
                        objeto.setId(Integer.parseInt(id));
                        objeto.setDescripcion(marca);
                        devuelveListadoObjeto(listaObjeto).add(objeto);
                        devuelveLista(textoSpinner).add(marca);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                rellenaSpinner(spinner, textoSpinner);

            }, error -> Log.i("TAG", error.getMessage().concat(" fallo en consulta")));
            requestQueue.add(jsonArrayRequest);
            spinner.setVisibility(View.VISIBLE);
        }
    }
    /*
    Método ConsultaMultiple: Utilizado para obtener información de la base de datos para aquellas consultas que tienen varios parámetros get:
     */

    public void consultaMultiple(int variable1, int variable2, String nomVariable1, String nombVariable2, String url, String id_campo, String nombreCampo, String listaObjeto, String textoSpinner, Spinner spinner) {

        String URL1 = URL_BASICA +url + "?" + nomVariable1 + "=" + variable1 + "&" + nombVariable2 + "=" + variable2;

        //VACIO LA LISTA POR SI TUVIESE ALGUN REGISTRO PREVIO:
        if(devuelveListadoObjeto(listaObjeto).size()>0){
            devuelveListadoObjeto(listaObjeto).clear();
            devuelveLista(listaObjeto).clear();
        }


        //PARA JSON DE VARIOS ARRAYS EN RESPUESTA:
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, URL1, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    String id = jsonObject.getString(id_campo);
                    String marca = jsonObject.getString(nombreCampo);
                    Objeto objeto = new Objeto();
                    objeto.setId(Integer.parseInt(id));
                    objeto.setDescripcion(marca);
                    devuelveListadoObjeto(listaObjeto).add(objeto);
                    devuelveLista(textoSpinner).add(marca);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            rellenaSpinner(spinner, textoSpinner);
        }, error -> Log.i("TAG", error.getMessage().concat(" consulta multiple.")));
        requestQueue.add(jsonArrayRequest);
        spinner.setVisibility(View.VISIBLE);

    }

    public int bucleBusqueda(ArrayList<Objeto> listado, AdapterView<?> adapterView) {
        int campo = 0;
        for (int y = 0; y < listado.size(); y++) {
            String seleccionado=adapterView.getSelectedItem().toString();
            if (listado.get(y).getDescripcion().equalsIgnoreCase(seleccionado)) {
                campo = listado.get(y).getId();
            }
        }
        return campo;
    }

    public int obtenId(AdapterView adapterView, String objetoParametros, String objetoAImplementar, String listado) {
        ArrayList<Objeto>objetoParametro=new ArrayList<>();
        objetoParametro.addAll(devuelveListadoObjeto(objetoParametros));
        ArrayList<Objeto>objetoImplementar=new ArrayList<>();
        objetoImplementar.addAll(devuelveListadoObjeto(objetoAImplementar));
        ArrayList<String>lista=new ArrayList<>();
        lista.addAll(devuelveLista(listado));
        int id = bucleBusqueda(objetoParametro, adapterView);
        if (objetoImplementar.size() > 0 || lista.size() > 0) {
            objetoImplementar.clear();
            lista.clear();
        }
        return id;
    }

    public int obtenIdSinParametros(AdapterView adapterView, String objetoParametro) {
        return bucleBusqueda(devuelveListadoObjeto(objetoParametro), adapterView);
    }

    public int obtenIdPredictivo(AutoCompleteTextView autoCompleteTextView){
        int campo = 0;
        for (int y = 0; y < listadoMarcas.size(); y++) {
            String seleccionado = autoCompleteTextView.getText().toString();
            if (listadoMarcas.get(y).getDescripcion().equalsIgnoreCase(seleccionado)) {
                campo = listadoMarcas.get(y).getId();
            }

        }
        return campo;
    }
    /*
    Método iniciarArrayListVehiculos(): Este método encapsula la inicialización de todos los arrays instanciándolos.
     */
    private void iniciarArrayListVehiculos() {
        listadoMarcas = new ArrayList<>();
        listadoGeneracion = new ArrayList<>();
        listadoModelo = new ArrayList<>();
        listadoSerie = new ArrayList<>();
        listadoTipo = new ArrayList<>();
        listadoMotor = new ArrayList<>();
        marcas = new ArrayList<>();
        tipo = new ArrayList<>();
        modelo = new ArrayList<>();
        generacion = new ArrayList<>();
        serie = new ArrayList<>();
        motor = new ArrayList<>();

    }

    public ArrayList<Objeto>devuelveListadoObjeto(String tipo){
        if(tipo.contains("marcas")){
            return listadoMarcas;
        }else if(tipo.contains("tipo")){
            return listadoTipo;
        }else if(tipo.contains("modelo")){
            return listadoModelo;
        }else if(tipo.contains("generacion")){
            return listadoGeneracion;
        }else if(tipo.contains("serie")){
            return listadoSerie;
        }else{
            return listadoMotor;
        }
    }

    public ArrayList<String>devuelveLista(String tipoString){
        if(tipoString.contains("marcas")){
            return marcas;
        }else if(tipoString.contains("tipo")){
            return tipo;
        }else if(tipoString.contains("modelo")){
            return modelo;
        }else if(tipoString.contains("generacion")){
            return generacion;
        }else if(tipoString.contains("serie")){
            return serie;
        }else if(tipoString.contains("motor")){
            return motor;
        }else if(tipoString.contains("combustible")){
            return combustible;
        }else{
            return cambio;
        }
    }
    public void consultaPredictivo(int variable, String url, String id_campo, String nombreCampo, String listadoObjeto, String textoSpinner, AutoCompleteTextView autoCompleteTextView) {
        String URL1 = URL_BASICA+ url;

        if(devuelveListadoObjeto(listadoObjeto).size()>0){
            devuelveListadoObjeto(listadoObjeto).clear();
            devuelveLista(textoSpinner).clear();
        }

        if (variable!=9999) {

            JsonArrayRequest jsonObjectRequest=new JsonArrayRequest(Request.Method.GET, URL1, null, response -> {
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject=response.getJSONObject(i);
                        String id = jsonObject.getString(id_campo);
                        String marca = jsonObject.getString(nombreCampo);
                        Objeto objeto = new Objeto();
                        objeto.setId(Integer.parseInt(id));
                        objeto.setDescripcion(marca);
                        devuelveListadoObjeto(listadoObjeto).add(objeto);
                        devuelveLista(textoSpinner).add(marca);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context.getApplicationContext(),
                        simple_dropdown_item_1line, devuelveLista(textoSpinner));
                autoCompleteTextView.setAdapter(adapter);
            }, error -> Log.e("TAG", error.getMessage()));
            requestQueue.add(jsonObjectRequest);

        } else {
            if (variable == 9999) {
                URL1 = url;
            }
            //PARA JSON DE VARIOS ARRAYS EN RESPUESTA:
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL1, null, response -> {
                int names = response.length();
                devuelveLista(textoSpinner).add("Elije opcion");
                for (int i = 0; i < names; i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(String.valueOf(i + 1));
                        String id = jsonObject.getString(id_campo);
                        String marca = jsonObject.getString(nombreCampo);
                        Objeto objeto = new Objeto();
                        objeto.setId(Integer.parseInt(id));
                        objeto.setDescripcion(marca);
                        devuelveListadoObjeto(listadoObjeto).add(objeto);
                        devuelveLista(textoSpinner).add(marca);
                    } catch (JSONException e) {
                        Log.i("TAG", "Se ha producido un fallo pese que responde el Json: ".concat(e.getMessage()));
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context.getApplicationContext(),
                        simple_dropdown_item_1line, devuelveLista(textoSpinner));
                autoCompleteTextView.setAdapter(adapter);
            }, error -> Toast.makeText(context.getApplicationContext(), "Esta entrando en error", Toast.LENGTH_SHORT).show());
            requestQueue.add(jsonObjectRequest);
        }
    }

    public boolean compruebaDatos(){
        boolean resultado=true;
        String cadena="";
        if(id_tipo==0){
            cadena="Debe de introducir un tipo de vehiculo";
            resultado=false;
        }else if(id_marca==0){
            cadena="Debe de introducir una marca";
            resultado=false;
        }else if(id_modelo==0){
            cadena="Debe de introducir un modelo";
            resultado=false;
        }else if(id_generacion==0){
            cadena="Debe de introducir una generación";
            resultado=false;
        }else if(id_serie==0){
            cadena="Debe de introducir una serie";
            resultado=false;
        }else if(id_motor==0){
            cadena="Debe de introducir una motorización";
            resultado=false;
        }else if(annioUser==null){
            cadena="Debe de introducir el año de su vehiculo";
            resultado=false;
        }else if(kilometros==0){
            cadena="Debe de introducir el kilometraje actual del vehiculo";
            resultado=false;
        }else if(matricula==null){
            cadena="Debe de introducir la matricula de su vehículo";
            resultado=false;
        }else if(cambioUser==null){
            cadena="Debe de introducir el tipo de cambio de su vehiculo";
            resultado=false;
        }else{
            resultado=true;
        }
        if(!resultado){
            Toast.makeText(context,cadena,Toast.LENGTH_SHORT).show();
        }
        return resultado;
    }


    //Setters y getters:
    public String getAnnioUser() {
        return annioUser;
    }

    public void setAnnioUser(String annioUser) {
        this.annioUser = annioUser;
    }

    public String getCombustibleUser() {
        return combustibleUser;
    }

    public void setCombustibleUser(String combustibleUser) {
        this.combustibleUser = combustibleUser;
    }

    public String getCambioUser() {
        return cambioUser;
    }

    public void setCambioUser(String cambioUser) {
        this.cambioUser = cambioUser;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public static void setId_motor(int id_motor) {
        Vehiculos.id_motor = id_motor;
    }

    public int getListadoMotor(){
        return id_motor;
    }

    public void muestraListaMotor(){
        if(!motor.isEmpty()){
            for(int i=0;i<motor.size();i++){
                Log.i("TAG", motor.get(i));
            }
        }
    }


}
