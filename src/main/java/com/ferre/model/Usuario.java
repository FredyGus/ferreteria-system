package com.ferre.model;

public class Usuario {

    private long id;
    private String nombre;
    private String usuario;
    private String passHash;
    private Rol rol;
    private boolean activo;

    public Usuario() {
    }

    public Usuario(long id, String nombre, String usuario, String passHash, Rol rol, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.passHash = passHash;
        this.rol = rol;
        this.activo = activo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
