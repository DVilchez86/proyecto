package com.example.proyectodam;

import static com.example.proyectodam.comons.Constants.COD_POSTAL;
import static com.example.proyectodam.comons.Constants.DIRECCION;
import static com.example.proyectodam.comons.Constants.DNI_CIF;
import static com.example.proyectodam.comons.Constants.EMAIL;
import static com.example.proyectodam.comons.Constants.FIREBASE;
import static com.example.proyectodam.comons.Constants.ID_USUARIO;
import static com.example.proyectodam.comons.Constants.LOCALIDAD;
import static com.example.proyectodam.comons.Constants.NOMBRE;
import static com.example.proyectodam.comons.Constants.PASSWORD;
import static com.example.proyectodam.comons.Constants.PROVINCIA;
import static com.example.proyectodam.comons.Constants.ROL;
import static com.example.proyectodam.comons.Constants.TELEFONO;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Cliente implements Parcelable {
    //Atributos de campo:
    private Context context;

    private int id, telefono,codPostal,empleado;
    private String nombre, direccion,dni,email,localidad,provincia,password,token;
    //Constructores:
    public Cliente(Context context){
        this.context=context;
    }

    public Cliente(Context context,int id, int telefono, int codPostal, String nombre, String direccion, String dni, String email, String localidad, String provincia, String password, int empleado) {
        this.context=context;
        this.id = id;
        this.telefono = telefono;
        this.codPostal = codPostal;
        this.nombre = nombre;
        this.direccion = direccion;
        this.dni = dni;
        this.email = email;
        this.localidad = localidad;
        this.provincia = provincia;
        this.password = password;
        this.empleado = empleado;

    }

    protected Cliente(Parcel in) {
        id = in.readInt();
        telefono = in.readInt();
        codPostal = in.readInt();
        empleado = in.readInt();
        nombre = in.readString();
        direccion = in.readString();
        dni = in.readString();
        email = in.readString();
        localidad = in.readString();
        provincia = in.readString();
        password = in.readString();
        token = in.readString();
    }

    public static final Creator<Cliente> CREATOR = new Creator<Cliente>() {
        @Override
        public Cliente createFromParcel(Parcel in) {
            return new Cliente(in);
        }

        @Override
        public Cliente[] newArray(int size) {
            return new Cliente[size];
        }
    };

    //Métodos setters/getters:
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public int getCodPostal() {
        return codPostal;
    }

    public void setCodPostal(int codPostal) {
        this.codPostal = codPostal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getEmpleado() {
        return empleado;
    }

    public void setEmpleado(int empleado) {
        this.empleado = empleado;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public static Cliente traerIntentCliente(Context context, Bundle bundle){
        int id=bundle.getInt(ID_USUARIO);
        String email= bundle.getString(EMAIL);
        String pass=bundle.getString(PASSWORD);
        String nombre= bundle.getString(NOMBRE);
        int telefono=bundle.getInt(TELEFONO);
        String direccion=bundle.getString(DIRECCION);
        int codPostal=bundle.getInt(COD_POSTAL);
        String localidad=bundle.getString(LOCALIDAD);
        String provincia= bundle.getString(PROVINCIA);
        String firebase= bundle.getString(FIREBASE);
        String dni_cif= bundle.getString(DNI_CIF);
        int empleado= bundle.getInt(ROL);
        Cliente cliente=new Cliente(context,id,telefono,codPostal,nombre,direccion,dni_cif,email,localidad,provincia,pass,empleado);
        return cliente;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
            parcel.writeInt(id);
            parcel.writeInt(telefono);
            parcel.writeInt(codPostal);
            parcel.writeInt(empleado);
            parcel.writeString(nombre);
            parcel.writeString(direccion);
            parcel.writeString(dni);
            parcel.writeString(email);
            parcel.writeString(localidad);
            parcel.writeString(provincia);
            parcel.writeString(password);
            parcel.writeString(token);
    }

}
