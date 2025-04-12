/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Alejandra Arroyo
 */
public class Rol {

    private String nombre, descripcion;
    private int id;
    private Set<Permisos> permisos;

    public Rol(String nombre, String descripcion) {

        this.nombre = nombre;
        this.descripcion = descripcion;
        this.permisos = new HashSet<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void agregarPermiso(Permisos permiso) {
        if (permiso == null) {
            throw new IllegalArgumentException("El permiso no puede ser nulo.");
        }
        permisos.add(permiso);
    }

    public void eliminarPermiso(Permisos permiso) {
        permisos.remove(permiso);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rol)) {
            return false;
        }
        Rol rol = (Rol) o;
        return Objects.equals(nombre, rol.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}
