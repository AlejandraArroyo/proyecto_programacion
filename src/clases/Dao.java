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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alejandra Arroyo
 */
// <editor-fold defaultstate="collapsed" desc="Mi Región de Código">
public class Dao {

   public static Persona buscarPorCredenciales(String usuario, String contrasena) {
    String sql = "SELECT p.*, r.id as rol_id, r.nombre AS rol_nombre, r.descripcion AS rol_descripcion " +
                 "FROM persona p " +
                 "JOIN rol r ON p.rol_id = r.id " +
                 "WHERE p.nombre_usuario = ? AND p.contra = ? AND p.estado = 'A'";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, usuario);
        ps.setString(2, contrasena);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int rolId = rs.getInt("rol_id");
            String rolNombre = rs.getString("rol_nombre");
            String rolDescripcion = rs.getString("rol_descripcion");

            Rol rol = new Rol(rolNombre, rolDescripcion);
            rol.setId(rolId);

            
            List<Permisos> permisos = Dao.obtenerPermisosPorRol(rolId);
            rol.getPermisos().addAll(permisos);

            Persona persona = new Persona(
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("contra"),
                    rs.getString("nombre_usuario"),
                    rol
            ) {};
            persona.setIdentificacion(rs.getInt("id"));
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

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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

    public static void insertarUsuario(Persona persona) {
        String sql = "INSERT INTO persona (nombre, correo, contra, nombre_usuario, estado, rol_id, departamento_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getCorreo());
            ps.setString(4, persona.getNombreUsuario());
            ps.setString(3, persona.getContra());
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
        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
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
        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

        String sql = "SELECT p.id, p.nombre, p.correo, p.contra, p.nombre_usuario, p.estado, "
                + "r.id AS rol_id, r.nombre AS nombre_rol, r.descripcion AS descripcion_rol, "
                + "p.departamento_id, d.nombre AS nombre_departamento, d.descripcion AS descripcion_departamento "
                + "FROM persona p "
                + "INNER JOIN rol r ON p.rol_id = r.id "
                + "LEFT JOIN departamento d ON p.departamento_id = d.id";

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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

                    persona = new Persona(nombre, correo, nombreUsuario, contra, rol) {
                    };
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

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPersona);
            ps.executeUpdate();
            System.out.println("Usuario eliminado con ID: " + idPersona);

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
    }

    public static void actualizarUsuario(Persona persona) {
        String sql = "UPDATE persona SET nombre = ?, correo = ?, contra = ?, nombre_usuario = ?, rol_id = ?, departamento_id = ? WHERE id = ?";

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, persona.getNombre());
            ps.setString(2, persona.getCorreo());
            ps.setString(4, persona.getContra());
            ps.setString(3, persona.getNombreUsuario());
            ps.setInt(5, persona.getRol().getId());

            if (persona instanceof Tecnico tecnico) {
                ps.setInt(6, tecnico.getDepartamento().getId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setInt(7, persona.getIdentificacion());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
    }

    //////////// ahora departamentos
///
///


public static List<Departamento> listarDepartamentos() {
        List<Departamento> departamentos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion FROM departamento";

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");

                Departamento departamento = new Departamento(nombre, descripcion);
                departamento.setId(id);
                departamentos.add(departamento);
            }

        } catch (SQLException e) {
            System.err.println("Error con departamentos: " + e.getMessage());
        }

        return departamentos;
    }


public static void insertarDepartamento(Departamento departamento) {
    String sqlDep = "INSERT INTO departamento (nombre, descripcion) VALUES (?, ?) RETURNING id";
    String sqlCola = "INSERT INTO cola_atencion (departamento_id) VALUES (?)";

    try (Connection conn = Conexion.getConexion()) {
        conn.setAutoCommit(false); 

        try (
            PreparedStatement psDep = conn.prepareStatement(sqlDep);
            PreparedStatement psCola = conn.prepareStatement(sqlCola)
        ) {
            psDep.setString(1, departamento.getNombre());
            psDep.setString(2, departamento.getDescripcion());

            ResultSet rs = psDep.executeQuery();
            if (rs.next()) {
                int idDepartamento = rs.getInt("id");
                departamento.setId(idDepartamento);

               
                psCola.setInt(1, idDepartamento);
                psCola.executeUpdate();
                
                 ServicioColas.crearColaParaDepartamento(idDepartamento);
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Error al insertar departamento y cola: " + e.getMessage());
        }
    } catch (SQLException e) {
        System.err.println("Error de conexión: " + e.getMessage());
    }
}


    public static void eliminarDepartamento(int idDepartamento) {
        System.out.println("Eliminando usuario con ID: " + idDepartamento);
        String sql = "DELETE FROM departamento WHERE id = ?";

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDepartamento);
            ps.executeUpdate();
            System.out.println("Departamento eliminado con ID: " + idDepartamento);

        } catch (SQLException e) {
            System.err.println("Error al eliminar departamento: " + e.getMessage());
        }
    }
    
    public static void actualizarDepartamento(Departamento dep) {
    String sql = "UPDATE departamento SET nombre = ?, descripcion = ? WHERE id = ?";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, dep.getNombre());
        ps.setString(2, dep.getDescripcion());
        ps.setInt(3, dep.getId());
        ps.executeUpdate();

    } catch (SQLException e) {
        System.err.println("Error al actualizar departamento: " + e.getMessage());
    }
}
    
    public static List<Permisos> listarPermisos() {
    List<Permisos> permisos = new ArrayList<>();
    String sql = "SELECT id, nombre, descripcion FROM permisos";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
             int id = rs.getInt("id");
             String nombre = rs.getString("nombre");
             String descripcion = rs.getString("descripcion");
             permisos.add(new Permisos(id, nombre, descripcion)); 
         }

    } catch (SQLException e) {
        System.err.println("Error al listar permisos: " + e.getMessage());
    }

    return permisos;
}

    
    ///// los roles
    
    public static void insertarRolConPermisos(Rol rol) {
    String sqlRol = "INSERT INTO rol (nombre, descripcion) VALUES (?, ?) RETURNING id";
    String sqlRelacion = "INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (?, ?)";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement psRol = conn.prepareStatement(sqlRol);
         PreparedStatement psRelacion = conn.prepareStatement(sqlRelacion)) {

        
        psRol.setString(1, rol.getNombre());
        psRol.setString(2, rol.getDescripcion());
        ResultSet rs = psRol.executeQuery();
        if (rs.next()) {
            int rolId = rs.getInt("id");
            rol.setId(rolId);

           
            for (Permisos permiso : rol.getPermisos()) {
                psRelacion.setInt(1, rolId);
                psRelacion.setInt(2, permiso.getId());
                psRelacion.addBatch();
            }
            psRelacion.executeBatch();
        }

    } catch (SQLException e) {
        System.err.println("Error al insertar rol con permisos: " + e.getMessage());
    }
}
    
 
    
    
    public static void registrarBitacora(Persona usuario, String accion, String modulo, String tipoModulo) {
    String sql = "INSERT INTO bitacora (usuario_id, accion, modulo, tipo_modulo) VALUES (?, ?, ?, ?)";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, usuario.getIdentificacion());
        ps.setString(2, accion);
        ps.setString(3, modulo);
        ps.setString(4, tipoModulo);

        ps.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Erro en la bitacora: " + e.getMessage());
    }
}
    public static List<Rol> listarRoless() {
    List<Rol> roles = new ArrayList<>();
    String sql = "SELECT * FROM rol";

    try (Connection conn = Conexion.getConexion(); 
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            int idRol = rs.getInt("id");
            String nombre = rs.getString("nombre");
            String descripcion = rs.getString("descripcion");

            Rol rol = new Rol(nombre, descripcion);
            rol.setId(idRol);

            
            List<Permisos> permisos = obtenerPermisosPorRol(idRol);
            for (Permisos p : permisos) {
                rol.agregarPermiso(p);
            }

            roles.add(rol);
        }

    } catch (SQLException e) {
        System.err.println("Error al listar roles: " + e.getMessage());
    }

    return roles;
}
    
    public static List<Permisos> obtenerPermisosPorRol(int idRol) {
    List<Permisos> permisos = new ArrayList<>();
    String sql = "SELECT p.id, p.nombre, p.descripcion " +
                 "FROM permisos p " +
                 "JOIN rol_permiso rp ON p.id = rp.permiso_id " +
                 "WHERE rp.rol_id = ?";

    try (Connection conn = Conexion.getConexion(); 
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idRol);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            permisos.add(new Permisos(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion")
            ));
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener permisos del rol: " + e.getMessage());
    }

    return permisos;
}

public static void actualizarRol(Rol rol) {
    String updateRolSql = "UPDATE rol SET nombre = ?, descripcion = ? WHERE id = ?";
    String deletePermisosSql = "DELETE FROM rol_permiso WHERE rol_id = ?";
    String insertPermisoSql = "INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (?, ?)";

    try (Connection conn = Conexion.getConexion()) {
        conn.setAutoCommit(false);

        try (PreparedStatement psUpdate = conn.prepareStatement(updateRolSql);
             PreparedStatement psDelete = conn.prepareStatement(deletePermisosSql);
             PreparedStatement psInsert = conn.prepareStatement(insertPermisoSql)) {

         
            psUpdate.setString(1, rol.getNombre());
            psUpdate.setString(2, rol.getDescripcion());
            psUpdate.setInt(3, rol.getId());
            psUpdate.executeUpdate();

           
            psDelete.setInt(1, rol.getId());
            psDelete.executeUpdate();

        
            for (Permisos permiso : rol.getPermisos()) {
                psInsert.setInt(1, rol.getId());
                psInsert.setInt(2, permiso.getId());
                psInsert.addBatch();
            }
            psInsert.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Error al actualizar el rol: " + e.getMessage());
        }

    } catch (SQLException e) {
        System.err.println("Error en la conexión al actualizar rol: " + e.getMessage());
    }
}
    
    

public static void eliminarRol(int idRol) {
    String sqlEliminarPermisos = "DELETE FROM rol_permiso WHERE rol_id = ?";
    String sqlEliminarRol = "DELETE FROM rol WHERE id = ?";

    try (Connection conn = Conexion.getConexion()) {
        conn.setAutoCommit(false);

        try (PreparedStatement psPermisos = conn.prepareStatement(sqlEliminarPermisos);
             PreparedStatement psRol = conn.prepareStatement(sqlEliminarRol)) {

            psPermisos.setInt(1, idRol);
            psPermisos.executeUpdate();

            psRol.setInt(1, idRol);
            psRol.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Error al eliminar rol: " + e.getMessage());
        }

    } catch (SQLException e) {
        System.err.println("Error en conexión al eliminar rol: " + e.getMessage());
    }
}



public static ConfigEmpresa obtenerConfiguracion() {
    String sql = "SELECT * FROM config_empresa WHERE id = 1";

    try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            ConfigEmpresa config = new ConfigEmpresa();
            config.setNombreEmpresa(rs.getString("nombre"));
            config.setLogoEmpresa(rs.getBytes("logo"));
            config.setNombreArchivoLogo(rs.getString("nombre_logo"));
            config.setIdioma(rs.getString("idioma"));
            config.setZonaHoraria(rs.getString("zona_horaria"));
            config.setDiasVencimientoTicket(rs.getInt("vencimiento_tickets"));

        

            return config;
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener configuración: " + e.getMessage());
    }

    return null;
}


public static void guardarOActualizarConfiguracion(ConfigEmpresa config) {
    String sqlExistencia = "SELECT COUNT(*) FROM config_empresa";
    String sqlUpdate = "UPDATE config_empresa SET nombre = ?, logo = ?, nombre_logo = ?, idioma = ?, zona_horaria = ?, vencimiento_tickets = ? WHERE id = 1";
    String sqlInsert = "INSERT INTO config_empresa (id, nombre, logo, nombre_logo, idioma, zona_horaria, vencimiento_tickets) VALUES (1, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement checkStmt = conn.prepareStatement(sqlExistencia);
         ResultSet rs = checkStmt.executeQuery()) {

        rs.next();
        boolean existe = rs.getInt(1) > 0;

        PreparedStatement ps = conn.prepareStatement(existe ? sqlUpdate : sqlInsert);

        ps.setString(1, config.getNombre());
        ps.setBytes(2, config.getLogoEmpresa());
        ps.setString(3, config.getNombreArchivoLogo());
        ps.setString(4, config.getIdioma());
        ps.setString(5, config.getZonaHoraria());
        ps.setInt(6, config.getVencimiento());

        ps.executeUpdate();

      

    } catch (SQLException e) {
        System.err.println("Error al guardar o actualizar configuración: " + e.getMessage());
    }
}


public static List<String> obtenerNivelesPrioridad() {
    List<String> niveles = new ArrayList<>();
    String sql = "SELECT nombre FROM nivel_prioridad";

    try (Connection conn = Conexion.getConexion();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            niveles.add(rs.getString("nombre"));
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener niveles: " + e.getMessage());
    }
    return niveles;
}

public static void insertarPrioridad(String prioridad) {
    String sql = "INSERT INTO nivel_prioridad(nombre) VALUES (?)";
    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, prioridad);
        ps.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error al insertar prioridad: " + e.getMessage());
    }
}

public static void eliminarTodosNivelesPrioridad() {
    String sql = "DELETE FROM nivel_prioridad";
    try (Connection conn = Conexion.getConexion(); Statement stmt = conn.createStatement()) {
        stmt.executeUpdate(sql);
    } catch (SQLException e) {
        System.err.println("Error al eliminar niveles: " + e.getMessage());
    }
}


public static void insertarEstadoTicket(EstadoTicket estado) {
    String sql = "INSERT INTO estado_ticket (nombre, descripcion, es_final) VALUES (?, ?, ?)";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, estado.getNombre());
        ps.setString(2, estado.getDescripcion());
        ps.setBoolean(3, estado.isFinal());

        ps.executeUpdate();

    } catch (SQLException e) {
        System.err.println("Error al insertar estado de ticket: " + e.getMessage());
    }
}

    
}
