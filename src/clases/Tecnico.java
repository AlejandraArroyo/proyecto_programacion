/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

/**
 *
 * @author Alejandra Arroyo
 */
public class Tecnico extends Persona{
     private Departamento departamento;

     
      public Tecnico(String nombreCompleto, String correo, String nombreUsuario,
                   String contraseña, Rol rol, Departamento departamento) {
        super(nombreCompleto, correo, nombreUsuario, contraseña, rol);

        if (departamento == null) {
            throw new IllegalArgumentException("El departamento no puede ser nulo para un técnico.");
        }

        this.departamento = departamento;
    }

    public Departamento getDepartamento() {
        return departamento;
    }
    
    public void setDepartamento(Departamento departamento) {
    this.departamento = departamento;
}
}
