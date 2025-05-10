/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

/**
 *
 * @author Alejandra Arroyo
 */
public class Sesion {
     public static Persona usuarioActual;
     
     
     public static Rol getRolActual() {
    if (usuarioActual != null) {
        return usuarioActual.getRol();
    }
    return null;
}
      public static void setUsuarioActual(Persona usuario) {
        usuarioActual = usuario;
    }

    public static Persona getUsuarioActual() {
        return usuarioActual;
    }
    
    
    public static void cerrar() {
        usuarioActual = null;
    }
     
     
}
