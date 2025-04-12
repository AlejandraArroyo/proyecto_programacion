/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

/**
 *
 * @author Alejandra Arroyo
 */
public class Usuario extends Persona{
    
    
      public Usuario(String nombreCompleto, String correo, String nombreUsuario,
                   String contraseña, Rol rol) {
        super(nombreCompleto, correo, nombreUsuario, contraseña, rol);

        if (!rol.getNombre().equalsIgnoreCase("Usuario")) {
            throw new IllegalArgumentException("El rol asignado no es válido para un usuario.");
        }
    }

    @Override
    public String toString() {
        return super.toString(); 
    }
}
