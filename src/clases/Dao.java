/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;
import clases.Persona;
import clases.Rol;
import clases.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alejandra Arroyo
 */


// <editor-fold defaultstate="collapsed" desc="Mi Región de Código">
public class Dao {
     public static Persona buscarPorCredenciales(String usuario, String contrasena) {
    String sql = "SELECT p.*, r.nombre AS rol_nombre, r.descripcion AS rol_descripcion " +
                 "FROM persona p " +
                 "JOIN rol r ON p.rol_id = r.id " +
                 "WHERE p.nombre_usuario = ? AND p.contra = ? and estado = 'A' ";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, usuario);
        ps.setString(2, contrasena);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Rol rol = new Rol(
                rs.getString("rol_nombre"),
                rs.getString("rol_descripcion")
            );

            Persona persona = new Persona(
                rs.getString("nombre"),
                rs.getString("correo"),
                rs.getString("contra"),
                rs.getString("nombre_usuario"),
                rol
            ) {};

   
            return persona;
        }

    } catch (SQLException e) {
        System.err.println("Error en el login: " + e.getMessage());
    }

    return null;
}
     
     // </editor-fold>
     
     
     
     public static List<Rol> listarRoles() {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion FROM rol";

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rol rol = new Rol(rs.getString("nombre"), rs.getString("descripcion"));
                rol.setId(rs.getInt("id"));
                roles.add(rol);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar roles: " + e.getMessage());
        }

        return roles;
    }
     
     
     
      public static List<Departamento> listarDepartamentos() {
        List<Departamento> departamentos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion FROM departamento";

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
            Departamento d = new Departamento(
                rs.getString("nombre"),
                rs.getString("descripcion")
            );
            d.setId(rs.getInt("id"));
            departamentos.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar departamentos: " + e.getMessage());
        }

        return departamentos;
    }
      
       public static void insertarUsuario(Persona persona) {
        String sql = "INSERT INTO persona (nombre, correo, contra, nombre_usuario, estado, rol_id, departamento_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getCorreo());
            ps.setString(3, persona.getContra());
            ps.setString(4, persona.getNombreUsuario());
            ps.setString(5, "A");
            ps.setInt(6, persona.getRol().getId());

            
            if (persona instanceof Tecnico) {
                Tecnico tecnico = (Tecnico) persona;
                ps.setInt(7, tecnico.getDepartamento().getId());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
        }
    }
       
       
    public static boolean existeCorreo(String correo) {
         String sql = "SELECT 1 FROM persona WHERE correo = ?";
         try (Connection conn = Conexion.getConexion();
              PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setString(1, correo);
             ResultSet rs = ps.executeQuery();
             return rs.next(); 
         } catch (SQLException e) {
             System.err.println("Error verificando correo: " + e.getMessage());
             return false;
         }
     }

    public static boolean existeNombreUsuario(String nombreUsuario) {
         String sql = "SELECT 1 FROM persona WHERE nombre_usuario = ?";
         try (Connection conn = Conexion.getConexion();
              PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setString(1, nombreUsuario);
             ResultSet rs = ps.executeQuery();
             return rs.next(); 
         } catch (SQLException e) {
             System.err.println("Error verificando nombre de usuario: " + e.getMessage());
             return false;
         }
     }

 public static List<Persona> listarUsuarios() {
    List<Persona> usuarios = new ArrayList<>();

    String sql = "SELECT p.id, p.nombre, p.correo, p.contra, p.nombre_usuario, p.estado, " +
                 "r.id AS rol_id, r.nombre AS nombre_rol, r.descripcion AS descripcion_rol, " +
                 "p.departamento_id, d.nombre AS nombre_departamento, d.descripcion AS descripcion_departamento " +
                 "FROM persona p " +
                 "INNER JOIN rol r ON p.rol_id = r.id " +
                 "LEFT JOIN departamento d ON p.departamento_id = d.id";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            int idPersona = rs.getInt("id");
            String nombre = rs.getString("nombre");
            String correo = rs.getString("correo");
            String contra = rs.getString("contra");
            String nombreUsuario = rs.getString("nombre_usuario");
            char estado = rs.getString("estado").charAt(0);

        
            Rol rol = new Rol(rs.getString("nombre_rol"), rs.getString("descripcion_rol"));
            rol.setId(rs.getInt("rol_id"));

            Persona persona;

            if (rol.getNombre().equalsIgnoreCase("Técnico")) {
                Departamento departamento = null;
                int idDepartamento = rs.getInt("departamento_id");
                if (!rs.wasNull()) {
                    String nombreDepto = rs.getString("nombre_departamento");
                    String descripcionDepto = rs.getString("descripcion_departamento");
                    departamento = new Departamento(nombreDepto, descripcionDepto);
                    departamento.setId(idDepartamento);
                }
                persona = new Tecnico(nombre, correo, nombreUsuario, contra, rol, departamento);
            } else if (rol.getNombre().equalsIgnoreCase("Administrador")) {
                persona = new Administrador(nombre, correo, nombreUsuario, contra, rol);
            } else if (rol.getNombre().equalsIgnoreCase("Usuario")) {
                persona = new Usuario(nombre, correo, nombreUsuario, contra, rol);
            } else {
               
                persona = new Persona(nombre, correo, nombreUsuario, contra, rol) {};
            }
            persona.setIdentificacion(rs.getInt("id"));
            persona.setEstado(estado);
            usuarios.add(persona);
        }

    } catch (SQLException e) {
        System.err.println("Error al listar usuarios: " + e.getMessage());
    }

    return usuarios;
}

 
public static void eliminarUsuario(int idPersona) {
    System.out.println("Eliminando usuario con ID: " + idPersona);
    String sql = "DELETE FROM persona WHERE id = ?";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idPersona);
        ps.executeUpdate(); 
        System.out.println("Usuario eliminado con ID: " + idPersona);

    } catch (SQLException e) {
        System.err.println("Error al eliminar usuario: " + e.getMessage());
    }
}

     
}
