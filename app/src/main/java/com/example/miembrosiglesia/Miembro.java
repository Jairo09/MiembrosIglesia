package com.example.miembrosiglesia;

public class Miembro {

    private String Nombre;
    private String Apellido;
    private String Sociedad;

    public Miembro() {
    }

    public Miembro(String nombre, String apellido, String sociedad) {
        Nombre = nombre;
        Apellido = apellido;
        Sociedad = sociedad;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getSociedad() {
        return Sociedad;
    }

    public void setSociedad(String sociedad) {
        Sociedad = sociedad;
    }
}
