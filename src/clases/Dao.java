/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clases;

import clases.Persona;
import clases.Rol;
import clases.Conexion;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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

    try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

    String tipoRol = rolNombre.toUpperCase();

    Persona persona;
System.out.println("Rol detectado: " + rol.getNombre());
           System.out.println(tipoRol);

    switch (rol.getNombre()) {
        
        case "Tecnico":
            Departamento dep = Dao.obtenerDepartamentoPorId(rs.getInt("departamento_id"));
            System.out.println(dep.getNombre());
            persona = new Tecnico(
                rs.getString("nombre"),
                rs.getString("correo"),
                rs.getString("nombre_usuario"),
                rs.getString("contra"),
                rol,
                dep
            );
            break;
        case "ADMIN":
            persona = new Administrador(
                rs.getString("nombre"),
                rs.getString("correo"),
                rs.getString("nombre_usuario"),
                rs.getString("contra"),
                rol
            );
            break;
        default:
            persona = new Usuario(
                rs.getString("nombre"),
                rs.getString("correo"),
                rs.getString("nombre_usuario"),
                rs.getString("contra"),
                rol
            );
            break;
    }

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
            ps.setString(4, persona.getNombreUsuario());
            ps.setString(3, persona.getContra());
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
                    PreparedStatement psDep = conn.prepareStatement(sqlDep); PreparedStatement psCola = conn.prepareStatement(sqlCola)) {
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

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

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

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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

        try (Connection conn = Conexion.getConexion(); PreparedStatement psRol = conn.prepareStatement(sqlRol); PreparedStatement psRelacion = conn.prepareStatement(sqlRelacion)) {

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

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

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

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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
        String sql = "SELECT p.id, p.nombre, p.descripcion "
                + "FROM permisos p "
                + "JOIN rol_permiso rp ON p.id = rp.permiso_id "
                + "WHERE rp.rol_id = ?";

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

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

            try (PreparedStatement psUpdate = conn.prepareStatement(updateRolSql); PreparedStatement psDelete = conn.prepareStatement(deletePermisosSql); PreparedStatement psInsert = conn.prepareStatement(insertPermisoSql)) {

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

            try (PreparedStatement psPermisos = conn.prepareStatement(sqlEliminarPermisos); PreparedStatement psRol = conn.prepareStatement(sqlEliminarRol)) {

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

        try (Connection conn = Conexion.getConexion(); PreparedStatement checkStmt = conn.prepareStatement(sqlExistencia); ResultSet rs = checkStmt.executeQuery()) {

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

        try (Connection conn = Conexion.getConexion(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
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
        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prioridad);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar prioridad: " + e.getMessage());
        }
    }
    
    
    
    public static void insertarPrioridadSiNoExiste(String nombre) {
    String sqlCheck = "SELECT COUNT(*) FROM nivel_prioridad WHERE LOWER(nombre) = LOWER(?)";
    String sqlInsert = "INSERT INTO nivel_prioridad(nombre) VALUES (?)";

    try (Connection conn = Conexion.getConexion()) {
        try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
            psCheck.setString(1, nombre);
            ResultSet rs = psCheck.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                    psInsert.setString(1, nombre);
                    psInsert.executeUpdate();
                }
            }
        }
    } catch (SQLException e) {
        System.err.println("Error al insertar prioridad (verificación previa): " + e.getMessage());
    }
}

    
    
    public static List<NivelPrioridad> listarNivelesPrioridad() {
    List<NivelPrioridad> prioridades = new ArrayList<>();
    String sql = "SELECT id, nombre FROM nivel_prioridad";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String nombre = rs.getString("nombre");
            prioridades.add(new NivelPrioridad(id, nombre));
        }

    } catch (SQLException e) {
        System.err.println("Error al listar niveles de prioridad: " + e.getMessage());
    }

    return prioridades;
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

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estado.getNombre());
            ps.setString(2, estado.getDescripcion());
            ps.setBoolean(3, estado.isFinal());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar estado de ticket: " + e.getMessage());
        }
    }

    public static List<EstadoTicket> listarEstados() {
        List<EstadoTicket> estados = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, es_final FROM estado_ticket";

        try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EstadoTicket estado = new EstadoTicket();
                estado.setId(rs.getInt("id"));
                estado.setNombre(rs.getString("nombre"));
                estado.setDescripcion(rs.getString("descripcion"));
                estado.setEsFinal(rs.getBoolean("es_final"));

                estados.add(estado);
            }
        } catch (SQLException e) {
            System.err.println("Error  " + e.getMessage());
        }

        return estados;
    }
    
    public static void insertarFlujoTrabajo(FlujoTrabajo flujo) {
    String sqlFlujo = "INSERT INTO flujo_trabajo (nombre, reglas, acciones_automaticas, creado_por) VALUES (?, ?, ?, ?) RETURNING id";
    String sqlEstado = "INSERT INTO flujo_estado (flujo_id, estado_id) VALUES (?, ?)";
    String sqlTransicion = "INSERT INTO transicion_estado (flujo_id, estado_origen_id, estado_destino_id, restriccion) VALUES (?, ?, ?, ?)";

    try (Connection conn = Conexion.getConexion()) {
        conn.setAutoCommit(false);

        try (PreparedStatement psFlujo = conn.prepareStatement(sqlFlujo);
             PreparedStatement psEstado = conn.prepareStatement(sqlEstado);
             PreparedStatement psTransicion = conn.prepareStatement(sqlTransicion)) {

            psFlujo.setString(1, flujo.getNombre());
            psFlujo.setString(2, flujo.getReglas());
            psFlujo.setString(3, flujo.getAccionesAutomaticas());
            psFlujo.setInt(4, flujo.getCreador().getIdentificacion());

            ResultSet rs = psFlujo.executeQuery();
            if (rs.next()) {
                int idFlujo = rs.getInt("id");
                flujo.setId(idFlujo);

                for (EstadoTicket estado : flujo.getEstadosInvolucrados()) {
                    psEstado.setInt(1, idFlujo);
                    psEstado.setInt(2, estado.getId());
                    psEstado.addBatch();
                }
                psEstado.executeBatch();

                for (TransicionEstado trans : flujo.getTransiciones()) {
                    psTransicion.setInt(1, idFlujo);
                    psTransicion.setInt(2, trans.getOrigen().getId());
                    psTransicion.setInt(3, trans.getDestino().getId());
                    psTransicion.setString(4, trans.getRestriccion());
                    psTransicion.addBatch();
                }
                psTransicion.executeBatch();

                conn.commit();
            } else {
                conn.rollback();
                System.err.println("Error: No se pudo obtener el ID del flujo insertado.");
            }

        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Error al insertar flujo de trabajo: " + e.getMessage());
        }

    } catch (SQLException e) {
        System.err.println("Error de conexión: " + e.getMessage());
    }
}
public static int insertarTicket(Ticket ticket) {
    String sql = "INSERT INTO ticket (titulo, descripcion, fecha_creacion, adjunto, nombre_archivo, estado_id, prioridad, departamento_id, creado_por) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, ticket.getTitulo());
        ps.setString(2, ticket.getDescripcion());
        ps.setTimestamp(3, Timestamp.valueOf(ticket.getFechaCreacion()));
        ps.setBytes(4, ticket.getArchivoAdjunto());
        ps.setString(5, ticket.getNombreArchivo());
        ps.setInt(6, ticket.getEstado().getId());
        ps.setInt(7, ticket.getPrioridad().getId());
        ps.setInt(8, ticket.getDepartamento().getId());
        ps.setInt(9, ticket.getCreador().getIdentificacion());

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int id = rs.getInt("id");
            ticket.setId(id);

           
            Ticket ticketCompleto = obtenerTicketPorId(id);

            if (ticket.getArchivoAdjunto() != null && ticket.getNombreArchivo() != null) {
                insertarArchivoTicket(id, ticket.getArchivoAdjunto(), ticket.getNombreArchivo());
            }

            int depId = ticket.getDepartamento().getId();
          //  Queue<Ticket> cola = ServicioColas.obtenerCola(depId);

          
           // if (cola != null) {
             //   cola.add(ticketCompleto);
               // System.out.println("Ticket agregado a la cola del departamento " + depId);
           // } else {
               // System.err.println("La cola del departamento " + depId + " no está inicializada.");
            //}
                System.out.println("Ticket creado con ID: " + id);
            return id;
        }

    } catch (SQLException e) {
        System.err.println("Error al insertar ticket: " + e.getMessage());
    }
    return -1;
}

public static boolean insertarNota(NotaTicket nota) {
    String sql = "INSERT INTO ticket_nota (ticket_id, contenido, creado_por, adjunto, nombre_adjunto) VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, nota.getTicketId());
        ps.setString(2, nota.getContenido());
        ps.setInt(3, nota.getCreador().getIdentificacion());
        ps.setBytes(4, nota.getAdjunto());
        ps.setString(5, nota.getNombreAdjunto());

        int filas = ps.executeUpdate();
        return filas > 0;

    } catch (SQLException e) {
        System.err.println("Error al insertar nota: " + e.getMessage());
        return false;
    }
}


public static List<String> obtenerNotasPorTicket(int ticketId) {
    List<String> notas = new ArrayList<>();
    String sql = "SELECT contenido, fecha_creacion FROM ticket_nota WHERE ticket_id = ? ORDER BY fecha_creacion DESC";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, ticketId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String linea = "[" + rs.getTimestamp("fecha_creacion").toLocalDateTime() + "] " + rs.getString("contenido");
            notas.add(linea);
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener notas: " + e.getMessage());
    }

    return notas;
}





public static void insertarArchivoTickets(int ticketId, byte[] archivo, String nombreArchivo) {
    String sql = "INSERT INTO ticket_archivo (ticket_id, archivo, nombre_archivo) VALUES (?, ?, ?)";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, ticketId);
        ps.setBytes(2, archivo);
        ps.setString(3, nombreArchivo);

        ps.executeUpdate();

    } catch (SQLException e) {
        System.err.println("Error al guardar archivo del ticket: " + e.getMessage());
    }
}

 
 public static byte[] convertirArchivoABytes(File archivo) {
    try {
        return Files.readAllBytes(archivo.toPath());
    } catch (IOException e) {
        System.err.println("Error al leer archivo adjunto: " + e.getMessage());
        return null;
    }
}
public static EstadoTicket obtenerEstadoInicial() {
    String sql = "SELECT * FROM estado_ticket WHERE id =6";
    try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            EstadoTicket estado = new EstadoTicket();
            estado.setId(rs.getInt("id"));
            estado.setNombre(rs.getString("nombre"));
            estado.setDescripcion(rs.getString("descripcion"));
            estado.setEsFinal(rs.getBoolean("es_final"));
            return estado;
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener estado inicial: " + e.getMessage());
    }
    return null;
}

public static void insertarHistorialEstado(int ticketId, EstadoTicket estado, String observacion) {
    String sql = "INSERT INTO ticket_historial (ticket_id, estado_id, fecha, comentario) VALUES (?, ?, CURRENT_TIMESTAMP, ?)";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, ticketId);
        ps.setInt(2, estado.getId());
        ps.setString(3, observacion);

        ps.executeUpdate();

    } catch (SQLException e) {
        System.err.println("Error al insertar historial de estado: " + e.getMessage());
    }
}

public static void agregarTicketACola(int ticketId, int departamentoId, LocalDateTime fecha) {
    String sql = "INSERT INTO cola_atencion_ticket (ticket_id, departamento_id, fecha_ingreso) VALUES (?, ?, ?)";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, ticketId);
        ps.setInt(2, departamentoId);
        ps.setTimestamp(3, Timestamp.valueOf(fecha));
        ps.executeUpdate();

    } catch (SQLException e) {
        System.err.println("Error al agregar ticket a la cola: " + e.getMessage());
    }
}


public static List<Ticket> obtenerTicketsDesdeCola(Persona usuario) {
    List<Ticket> tickets = new ArrayList<>();

    String sql;
    boolean esTecnico = usuario.getRol().getNombre().equalsIgnoreCase("Técnico");

    sql = "SELECT t.* FROM cola_atencion_ticket cat " +
          "JOIN ticket t ON t.id = cat.ticket_id ";

    if (esTecnico) {
        sql += "WHERE cat.departamento_id = ? ORDER BY cat.fecha_ingreso";
    } else {
        sql += "ORDER BY cat.fecha_ingreso";
    }

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        if (esTecnico) {
            Tecnico tecnico = (Tecnico) usuario;
            ps.setInt(1, tecnico.getDepartamento().getId());
        }

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Ticket ticket = new Ticket();
            ticket.setId(rs.getInt("id"));
            ticket.setTitulo(rs.getString("titulo"));
            ticket.setDescripcion(rs.getString("descripcion"));
            ticket.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
            
            tickets.add(ticket);
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener tickets desde la cola: " + e.getMessage());
    }

    return tickets;
}


public static Ticket obtenerTicketPorId(int ticketId) {
    String sql = "SELECT * FROM ticket WHERE id = ?";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, ticketId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Ticket t = new Ticket();
            t.setId(rs.getInt("id"));
            t.setTitulo(rs.getString("titulo"));
            t.setDescripcion(rs.getString("descripcion"));
            t.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
            t.setArchivoAdjunto(rs.getBytes("adjunto"));
            t.setNombreArchivo(rs.getString("nombre_archivo"));
            t.setEstado(obtenerEstadoPorId(rs.getInt("estado_id")));
            t.setPrioridad(obtenerPrioridadPorId(rs.getInt("prioridad")));
            t.setDepartamento(obtenerDepartamentoPorId(rs.getInt("departamento_id")));
            t.setCreador(obtenerPersonaPorId(rs.getInt("creado_por")));
            return t;
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener ticket por ID: " + e.getMessage());
    }

    return null;
}

public static EstadoTicket obtenerEstadoPorId(int id) {
    String sql = "SELECT * FROM estado_ticket WHERE id = ?";
    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            EstadoTicket estado = new EstadoTicket();
            estado.setId(rs.getInt("id"));
            estado.setNombre(rs.getString("nombre"));
            estado.setDescripcion(rs.getString("descripcion"));
            estado.setEsFinal(rs.getBoolean("es_final"));
            return estado;
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener estado: " + e.getMessage());
    }
    return null;
}


public static NivelPrioridad obtenerPrioridadPorId(int id) {
    String sql = "SELECT * FROM nivel_prioridad WHERE id = ?";
    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new NivelPrioridad(rs.getInt("id"), rs.getString("nombre"));
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener prioridad: " + e.getMessage());
    }
    return null;
}

public static Departamento obtenerDepartamentoPorId(int id) {
    String sql = "SELECT * FROM departamento WHERE id = ?";
    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Departamento dep = new Departamento(rs.getString("nombre"), rs.getString("descripcion"));
            dep.setId(rs.getInt("id"));
            return dep;
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener departamento: " + e.getMessage());
    }
    return null;
}

public static Persona obtenerPersonaPorId(int id) {
    String sql = "SELECT p.*, r.nombre AS rol_nombre, r.descripcion AS rol_desc " +
                 "FROM persona p JOIN rol r ON p.rol_id = r.id WHERE p.id = ?";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Rol rol = new Rol(rs.getString("rol_nombre"), rs.getString("rol_desc"));
            rol.setId(rs.getInt("rol_id"));

            String tipoRol = rol.getNombre().toLowerCase();
            System.out.println(tipoRol);
            Persona persona;

            switch (tipoRol) {
                case "Tecnico":
                case "tecnico":
                    Departamento dep = obtenerDepartamentoPorId(rs.getInt("departamento_id"));
                    persona = new Tecnico(rs.getString("nombre"), rs.getString("correo"),
                            rs.getString("nombre_usuario"), rs.getString("contra"), rol, dep);
                    break;
                case "admin":
                    persona = new Administrador(rs.getString("nombre"), rs.getString("correo"),
                            rs.getString("nombre_usuario"), rs.getString("contra"), rol);
                    break;
                default:
                    persona = new Usuario(rs.getString("nombre"), rs.getString("correo"),
                            rs.getString("nombre_usuario"), rs.getString("contra"), rol);
                    break;
            }

            persona.setIdentificacion(rs.getInt("id"));
            return persona;
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener persona: " + e.getMessage());
    }
    return null;
}

public static void cargarColasDesdeBaseDatos() {
    String sql = "SELECT ticket_id FROM cola_atencion_ticket ct " +
                    "JOIN ticket t ON ct.ticket_id = t.id " +
                    "WHERE t.estado_id = ? ORDER BY ct.fecha_ingreso";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            int ticketId = rs.getInt("ticket_id");
            int deptoId = rs.getInt("departamento_id");

            Ticket t = obtenerTicketPorId(ticketId);
            ServicioColas.obtenerCola(deptoId).add(t);
        }

    } catch (SQLException e) {
        System.err.println("Error al cargar colas desde la base de datos: " + e.getMessage());
    }
}


public static void cargarColasDesdeBaseDatoss() {
    System.out.println("Cargando colas desde la base de datos...");

    ServicioColas.colasPorDepartamento.clear();

    int estadoInicialId = Dao.obtenerEstadoPorNombre("Nuevo").getId(); 

   
    String sqlDepartamentos = "SELECT DISTINCT departamento_id FROM cola_atencion_ticket";
    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sqlDepartamentos);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            int deptId = rs.getInt("departamento_id");
            ServicioColas.crearColaParaDepartamento(deptId);
        }
    } catch (SQLException e) {
        System.err.println("Error al crear colas desde BD: " + e.getMessage());
    }

   
    String sqlTickets = "SELECT ct.ticket_id " +
                        "FROM cola_atencion_ticket ct " +
                        "JOIN ticket t ON ct.ticket_id = t.id " +
                        "WHERE t.estado_id = ? " +
                        "ORDER BY ct.fecha_ingreso";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sqlTickets)) {

        ps.setInt(1, estadoInicialId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int ticketId = rs.getInt("ticket_id");
            Ticket t = Dao.obtenerTicketPorId(ticketId);
            if (t != null) {
                int deptId = t.getDepartamento().getId();
                Queue<Ticket> cola = ServicioColas.obtenerCola(deptId);
                if (cola != null && !cola.contains(t)) {
                    cola.add(t);
                }
            }
        }
    } catch (SQLException e) {
        System.err.println("Error al cargar tickets a las colas: " + e.getMessage());
    }

    System.out.println("Total de colas cargadas: " + ServicioColas.colasPorDepartamento.size());
}


public static EstadoTicket obtenerEstadoPorNombre(String nombre) {
    String sql = "SELECT * FROM estado_ticket WHERE LOWER(nombre) = LOWER(?)";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            EstadoTicket estado = new EstadoTicket();
            estado.setId(rs.getInt("id"));
            estado.setNombre(rs.getString("nombre"));
            estado.setDescripcion(rs.getString("descripcion"));
            estado.setEsFinal(rs.getBoolean("es_final"));
            return estado;
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener estado por nombre: " + e.getMessage());
    }

    return null;
}


public static void asignarTicket(Ticket ticket) {
    String sql = "UPDATE ticket SET estado_id = ?, asignado_a = ? WHERE id = ?";
    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, ticket.getEstado().getId());
        ps.setInt(2, ticket.getAsignadoA().getIdentificacion());
        ps.setInt(3, ticket.getId());
        ps.executeUpdate();

    } catch (SQLException e) {
        System.err.println("Error al asignar ticket: " + e.getMessage());
    }
}


public static List<Ticket> obtenerTicketsPorTecnico(int tecnicoId) {
    List<Ticket> lista = new ArrayList<>();
    String sql = "SELECT * FROM ticket WHERE asignado_a = ?";

    try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, tecnicoId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Ticket t = construirTicketDesdeResultSet(rs);
            lista.add(t);
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener tickets asignados al técnico: " + e.getMessage());
    }

    return lista;
}
public static Ticket construirTicketDesdeResultSet(ResultSet rs) throws SQLException {
    Ticket t = new Ticket();
    t.setId(rs.getInt("id"));
    t.setTitulo(rs.getString("titulo"));
    t.setDescripcion(rs.getString("descripcion"));
    t.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
    t.setNombreArchivo(rs.getString("nombre_archivo"));
    t.setArchivoAdjunto(rs.getBytes("adjunto"));

    
    t.setEstado(obtenerEstadoPorId(rs.getInt("estado_id")));
    t.setPrioridad(obtenerPrioridadPorId(rs.getInt("prioridad")));
    t.setDepartamento(obtenerDepartamentoPorId(rs.getInt("departamento_id")));
    t.setCreador(obtenerPersonaPorId(rs.getInt("creado_por")));

    
    int idTecnico = rs.getInt("asignado_a");
    if (!rs.wasNull()) {
        t.setAsignadoA((Tecnico) obtenerPersonaPorId(idTecnico));
    }

    return t;
}


public static void actualizarTicket(Ticket t) {
    String sql = "UPDATE ticket SET estado_id = ?, adjunto = ?, nombre_archivo = ? WHERE id = ?";
    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, t.getEstado().getId());
        ps.setBytes(2, t.getArchivoAdjunto());
        ps.setString(3, t.getNombreArchivo());
        ps.setInt(4, t.getId());
        ps.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error al actualizar ticket: " + e.getMessage());
    }
}


public static void insertarArchivoTicket(int ticketId, byte[] contenido, String nombreArchivo) {
    String sql = "INSERT INTO ticket_archivo (ticket_id, nombre_archivo, archivo) VALUES (?, ?, ?)";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, ticketId);
        ps.setString(2, nombreArchivo);
        ps.setBytes(3, contenido);

        ps.executeUpdate();

    } catch (SQLException e) {
        System.err.println("Error al guardar archivo del ticket: " + e.getMessage());
    }
}


public static void actualizarEstadoTicket(int ticketId, EstadoTicket nuevoEstado) {
    String sql = "UPDATE ticket SET estado_id = ? WHERE id = ?";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, nuevoEstado.getId());
        ps.setInt(2, ticketId);
        ps.executeUpdate();

    } catch (SQLException e) {
        System.err.println("Error al actualizar el estado del ticket: " + e.getMessage());
    }
}



public static void cerrarTicket(int ticketId) {
    EstadoTicket estadoCerrado = obtenerEstadoPorNombre("Cerrado");

    if (estadoCerrado == null) {
        System.err.println("Estado 'Cerrado' no existe en la base de datos.");
        return;
    }

    actualizarEstadoTicket(ticketId, estadoCerrado);
    insertarHistorialEstado(ticketId, estadoCerrado, "Ticket cerrado por técnico.");
}




public static List<Ticket> listarTodosLosTickets() {
    List<Ticket> tickets = new ArrayList<>();
    String sql = "SELECT * FROM ticket";
    try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            tickets.add(construirTicketDesdeResultSet(rs));
        }
    } catch (SQLException e) {
        System.err.println("Error al listar tickets: " + e.getMessage());
    }
    return tickets;
}


public static void reasignarTicketADepartamento(int ticketId, int nuevoDepartamentoId) {
    String sql = "UPDATE ticket SET departamento_id = ? WHERE id = ?";
    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, nuevoDepartamentoId);
        ps.setInt(2, ticketId);
        ps.executeUpdate();

       
        ServicioColas.eliminarDeTodasLasColas(ticketId);

       
        Ticket actualizado = obtenerTicketPorId(ticketId);
        if (actualizado != null) {
            ServicioColas.obtenerCola(nuevoDepartamentoId).add(actualizado);
        }

    } catch (SQLException e) {
        System.err.println("Error al reasignar ticket: " + e.getMessage());
    }
}


public static List<Ticket> obtenerTicketsPorUsuario(int usuarioId) {
    List<Ticket> tickets = new ArrayList<>();
    String sql = "SELECT * FROM ticket WHERE creado_por = ?";

    try (Connection conn = Conexion.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, usuarioId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            tickets.add(obtenerTicketDesdeResultSet(rs));
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener tickets por usuario: " + e.getMessage());
    }

    return tickets;
}


public static Ticket obtenerTicketDesdeResultSet(ResultSet rs) throws SQLException {
    Ticket t = new Ticket();

    t.setId(rs.getInt("id"));
    t.setTitulo(rs.getString("titulo"));
    t.setDescripcion(rs.getString("descripcion"));
    t.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());

    t.setEstado(obtenerEstadoPorId(rs.getInt("estado_id")));
    t.setPrioridad(obtenerPrioridadPorId(rs.getInt("prioridad")));
    t.setDepartamento(obtenerDepartamentoPorId(rs.getInt("departamento_id")));
    t.setCreador(obtenerPersonaPorId(rs.getInt("creado_por")));

    
    int tecnicoId = rs.getInt("asignado_a");
    if (!rs.wasNull()) {
        t.setAsignadoA(obtenerPersonaPorId(tecnicoId));
    }

    
    t.setArchivoAdjunto(rs.getBytes("adjunto"));
    t.setNombreArchivo(rs.getString("nombre_archivo"));

    return t;
}


public static List<String> obtenerHistorialTicket(int ticketId) {
    List<String> historial = new ArrayList<>();
    String sql = """
        SELECT th.fecha, et.nombre AS estado, th.comentario as comentario
        FROM ticket_historial th
        JOIN estado_ticket et ON th.estado_id = et.id
        WHERE th.ticket_id = ?
        ORDER BY th.fecha
    """;

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, ticketId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            String entrada = String.format("[%s] Estado: %s ",
                    rs.getTimestamp("fecha").toLocalDateTime(),
                    rs.getString("estado"),
                    rs.getString("comentario"));
            historial.add(entrada);
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener historial del ticket: " + e.getMessage());
    }

    return historial;
}

public static List<String> obtenerArchivosPorTicket(int ticketId) {
    List<String> archivos = new ArrayList<>();
    String sql = "SELECT nombre_archivo FROM ticket_archivo WHERE ticket_id = ?";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, ticketId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            archivos.add(rs.getString("nombre_archivo"));
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener archivos del ticket: " + e.getMessage());
    }

    return archivos;
}


public static List<Bitacora> obtenerBitacoraPorModulo(String modulo) {
    List<Bitacora> lista = new ArrayList<>();
    String sql = "SELECT accion, usuario_id, modulo, tipo_modulo, fecha FROM bitacora WHERE modulo = ? ORDER BY fecha DESC";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, modulo);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Persona usuario = obtenerPersonaPorId(rs.getInt("usuario_id"));
            Bitacora b = new Bitacora(
                usuario,
                rs.getString("accion"),
                rs.getString("modulo"),
                rs.getString("tipo_modulo")
            );
            b.setFecha(rs.getTimestamp("fecha"));
            lista.add(b);
        }

    } catch (SQLException e) {
        System.err.println("Error al cargar bitácora: " + e.getMessage());
    }

    return lista;
}


public static List<EstadoTicket> listarEstadosTabla() {
    List<EstadoTicket> lista = new ArrayList<>();
    String sql = "SELECT * FROM estado_ticket";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            EstadoTicket e = new EstadoTicket();
            e.setId(rs.getInt("id"));
            e.setNombre(rs.getString("nombre"));
            e.setDescripcion(rs.getString("descripcion"));
            e.setEsFinal(rs.getBoolean("es_final"));
            lista.add(e);
        }

    } catch (SQLException ex) {
        System.err.println("Error al listar estados: " + ex.getMessage());
    }

    return lista;
}


public static List<Permisos> listarPermisosTabla() {
    List<Permisos> lista = new ArrayList<>();
    String sql = "SELECT id, nombre, descripcion FROM permisos";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Permisos p = new Permisos(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion")
            );
            lista.add(p);
        }

    } catch (SQLException e) {
        System.err.println("Error al listar permisos: " + e.getMessage());
    }

    return lista;
}


public static byte[] obtenerArchivo(int ticketId, String nombreArchivo) {
    String sql = "SELECT archivo FROM ticket_archivo WHERE ticket_id = ? AND nombre_archivo = ?";
    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, ticketId);
        ps.setString(2, nombreArchivo);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getBytes("archivo");
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener archivo: " + e.getMessage());
    }
    return null;
}




public static byte[] obtenerAdjuntoNota(int notaId, String nombreArchivo) {
    String sql = "SELECT archivo FROM nota_archivo WHERE nota_id = ? AND nombre_archivo = ?";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, notaId);
        ps.setString(2, nombreArchivo);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getBytes("archivo");
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener archivo adjunto de nota: " + e.getMessage());
    }

    return null;
}

public static List<NotaTicket> obtenerNotasPorTickets(int ticketId) {
    List<NotaTicket> notas = new ArrayList<>();
    String sql = "SELECT n.id, n.contenido, n.fecha_creacion, n.nombre_adjunto " +
                 "FROM ticket_nota n WHERE n.ticket_id = ? ORDER BY n.fecha_creacion ASC";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, ticketId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            NotaTicket nota = new NotaTicket(
                rs.getInt("id"),
                rs.getString("contenido"),
                rs.getTimestamp("fecha_creacion").toLocalDateTime()
            );
            nota.setNombreAdjunto(rs.getString("nombre_adjunto"));
            notas.add(nota);
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener notas del ticket: " + e.getMessage());
    }

    return notas;
}

public static byte[] obtenerAdjuntoNotas(int notaId, String nombreArchivo) {
    String sql = "SELECT adjunto FROM ticket_nota WHERE id = ? AND nombre_adjunto = ?";
    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, notaId);
        ps.setString(2, nombreArchivo);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getBytes("adjunto");
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener archivo adjunto de nota: " + e.getMessage());
    }

    return null;
}


public static List<Tecnico> obtenerTecnicosPorDepartamento(int departamentoId) {
    List<Tecnico> lista = new ArrayList<>();
    String sql = "SELECT * FROM persona WHERE rol_id = (SELECT id FROM rol WHERE LOWER(nombre) = 'tecnico') AND departamento_id = ?";

    try (Connection conn = Conexion.getConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, departamentoId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Rol rol = new Rol("Técnico", "");
            rol.setId(rs.getInt("rol_id"));
            Departamento dep = obtenerDepartamentoPorId(departamentoId);

            Tecnico tecnico = new Tecnico(
                    rs.getString("nombre"),
                    rs.getString("correo"),
                     rs.getString("contra"),
                    rs.getString("nombre_usuario")
                   ,
                    rol,
                    dep
            );
            tecnico.setIdentificacion(rs.getInt("id"));
            lista.add(tecnico);
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener técnicos del departamento: " + e.getMessage());
    }

    return lista;
}


}
