/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

/**
 *
 * @author Alejandra Arroyo
 */
public abstract class  Persona {
  private String nombre, correo, contra, nombreUsuario;
  private char estado;
  private Rol rol;
  private int identificacion;

    public Persona() {
    }

    public Persona(String nombre, String correo, String contra, String nombreUsuario,  Rol rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.contra = contra;
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;
    }

    public void setIdentificacion(int identificacion) {
        this.identificacion = identificacion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContra() {
        return contra;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public char getEstado() {
        return estado;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setEstado(char estado) {
        this.estado = estado;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Rol getRol() {
        return rol;
    }

    public int getIdentificacion() {
        return identificacion;
    }

  
  
}
