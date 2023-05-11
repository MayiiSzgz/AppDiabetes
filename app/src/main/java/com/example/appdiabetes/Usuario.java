package com.example.appdiabetes;
public class Usuario {

    private String nombre;
    private String apellidos;
    private String correoElectronico;
    private String telefono;

    public Usuario() {}

    public Usuario(String nombre, String apellidos, String correo, String telefono) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correoElectronico = correoElectronico;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public String getTelefono() {
        return telefono;
    }
}
