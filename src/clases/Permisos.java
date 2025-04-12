/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

/**
 *
 * @author Alejandra Arroyo
 */
public class Permisos {
    private String nombre;
    private String descripcion;
    
    
     public Permisos(String nombre, String descripcion) {
        setNombre(nombre);
        this.descripcion = descripcion;
    }

    public void setNombre(String nombre) {
        if(nombre == null || nombre.length() < 3 || nombre.length() > 50)
        {
          throw new IllegalArgumentException("El nombre del permiso debe tener entre 3 y 50 caracteres.");
        }
        else{
             this.nombre = nombre;
        }
       
    }
    
}
