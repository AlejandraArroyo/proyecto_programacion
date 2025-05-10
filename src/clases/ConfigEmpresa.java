/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import java.util.List;

/**
 *
 * @author Alejandra Arroyo
 */
public class ConfigEmpresa {
   private String identificacion, nombre, hora;
   private byte[] logoEmpresa; 
   private String nombreArchivoLogo; 
    private String idioma;
   private int vencimiento;

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public byte[] getLogoEmpresa() {
        return logoEmpresa;
    }

    public void setLogoEmpresa(byte[] logoEmpresa) {
        this.logoEmpresa = logoEmpresa;
    }

    public String getNombreArchivoLogo() {
        return nombreArchivoLogo;
    }

    public void setNombreArchivoLogo(String nombreArchivoLogo) {
        this.nombreArchivoLogo = nombreArchivoLogo;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public int getVencimiento() {
        return vencimiento;
    }

    public void setVencimiento(int vencimiento) {
        this.vencimiento = vencimiento;
    }

    public List<String> getNivelesPrioridad() {
        return nivelesPrioridad;
    }

    public void setNivelesPrioridad(List<String> nivelesPrioridad) {
        this.nivelesPrioridad = nivelesPrioridad;
    }
   private List<String> nivelesPrioridad;
   
   
   
    public void setNombreEmpresa(String nombreEmpresa) {
       
        this.nombre = nombreEmpresa;
    }
    
  public String getZonaHoraria() {
    return hora;
}
  
     public void setZonaHoraria(String zona) {
        this.hora = zona;
    }

    
     public void setDiasVencimientoTicket(int dias) {
        
        this.vencimiento = dias;
    }
    
}
