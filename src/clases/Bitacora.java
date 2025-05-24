/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import java.sql.Timestamp;


/**
 *
 * @author Alejandra Arroyo
 */
public class Bitacora {
     private int id;
    private Persona usuario;
    private String accion;
    private String modulo;
    private String tipoModulo;
    private Timestamp fecha;

    public Bitacora(Persona usuario, String accion, String modulo, String tipoModulo) {
        this.usuario = usuario;
        this.accion = accion;
        this.modulo = modulo;
        this.tipoModulo = tipoModulo;
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Persona getUsuario() {
        return usuario;
    }

    public void setUsuario(Persona usuario) {
        this.usuario = usuario;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getTipoModulo() {
        return tipoModulo;
    }

    public void setTipoModulo(String tipoModulo) {
        this.tipoModulo = tipoModulo;
    }

    public Timestamp getFecha() {
        return fecha;
    }

   public void setFecha(Timestamp fecha) {
    this.fecha = fecha;
}


}
