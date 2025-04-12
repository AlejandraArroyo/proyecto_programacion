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
   private char idioma;
   private int vencimiento;
   private List<String> nivelesPrioridad;
   
   
   
    public void setNombreEmpresa(String nombreEmpresa) {
       
        this.nombre = nombreEmpresa;
    }
    
    public void setZonaHoraria(String zonaHoraria) {
       
        this.hora = zonaHoraria;
    }
    
     public void setDiasVencimientoTicket(int dias) {
        
        this.vencimiento = dias;
    }
    
}
