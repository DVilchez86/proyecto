package com.example.proyectodam.models;

public class Cliente {
    //Atributos de campo:
    private int id, telefono,codPostal;
    private String Nombre, direccion,dni,email,localidad,provincia,password, es_empleado;
    //Constructores:
    public Cliente(){

    }

    public Cliente(int id, int telefono, int codPostal, String nombre, String direccion, String dni, String email, String localidad, String provincia, String password, String empleado) {
        this.id = id;
        this.telefono = telefono;
        this.codPostal = codPostal;
        Nombre = nombre;
        this.direccion = direccion;
        this.dni = dni;
        this.email = email;
        this.localidad = localidad;
        this.provincia = provincia;
        this.password = password;
        this.es_empleado = empleado;
    }

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
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
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

    public String getEs_empleado() {
        return es_empleado;
    }

    public void setEs_empleado(String es_empleado) {
        this.es_empleado = es_empleado;
    }
}
