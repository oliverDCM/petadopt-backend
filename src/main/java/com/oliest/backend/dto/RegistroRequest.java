package com.oliest.backend.dto;

public class RegistroRequest {
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private String ciudad;
    private String rol;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}