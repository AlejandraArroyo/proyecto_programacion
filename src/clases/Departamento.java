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
public class Departamento {
    private String nombre, descripcion;
  
    private ColaAtencion colaAtencion;

    public Departamento(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.colaAtencion = new ColaAtencion(this); 
    }
    
    
    
}
