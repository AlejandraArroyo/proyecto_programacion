/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import java.util.Objects;

/**
 *
 * @author Alejandra Arroyo
 */
public class Permisos {
    private int id;
    private String nombre;
    private String descripcion;

    public Permisos(String nombre, String descripcion) {
        setNombre(nombre);
        this.descripcion = descripcion;
    }

    public Permisos(int id, String nombre, String descripcion) {
        this(nombre, descripcion);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.length() < 3 || nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre del permiso debe tener entre 3 y 50 caracteres.");
        }
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permisos)) return false;
        Permisos permiso = (Permisos) o;
        return Objects.equals(nombre, permiso.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
