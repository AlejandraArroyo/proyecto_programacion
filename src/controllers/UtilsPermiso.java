/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import clases.Permisos;
import clases.Rol;
import clases.Sesion;
/**
 *
 * @author Alejandra Arroyo
 */
public class UtilsPermiso {
    
    public static boolean tienePermiso(String nombrePermiso) {
        Rol rolActual = Sesion.getRolActual();

        if (rolActual == null || rolActual.getPermisos() == null) {
            return false;
        }

        for (Permisos permiso : rolActual.getPermisos()) {
            if (permiso.getNombre().equalsIgnoreCase(nombrePermiso)) {
                return true;
            }
        }

        return false;
    }
}
