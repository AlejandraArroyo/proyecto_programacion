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
public class Departamento  {
    private String nombre, descripcion;

    private int id;
   
  
    private ColaAtencion colaAtencion;

    public Departamento(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.colaAtencion = new ColaAtencion(this); 
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getId() {
        return id;
    }
    
     public void setId(int id) {
        this.id = id;
    }
     
      @Override
            public String toString() {
              return nombre; 
          }
    
    
    
}
