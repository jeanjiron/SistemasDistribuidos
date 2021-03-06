package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author juancarlos
 */
public class SQLConexion extends Thread {

    private static final String TABLE_CAT = "TABLE_CAT";

    private static final String ERROR_CONEXION = "Error al conectar base de datos";
    private static final String CONEXION_EXITOSA = "Conexion exitosa";
    private static final String CONEXION_CERRADA = "Conexion cerrada";
    private static final String ERROR_CLOSE_CALLABLE = "Error al cerrar CallableStatement";
    private static final String ERROR_CLOSE_RESULTSET = "Error al cerrrar ResultSet";

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String PORT = "3306";
    private static final String HOST = "localhost";
    private static final String DATABASE_NAME = "tarea4";

    //  Database credentials
    private static final String USER = "root";
    private static final String PASS = "root";
    private static final String DB_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/";
    private Connection connection;

    //Sockets
    protected Socket socketCliente;
    protected ObjectInputStream entrada;
    protected ObjectOutputStream salida;
    protected String[] peticion;

    public SQLConexion(Socket socketCliente) {
        this.socketCliente = socketCliente;
        try {
            entrada = new ObjectInputStream(socketCliente.getInputStream());

            peticion = (String[]) entrada.readObject();
            System.out.println("Consulta a ejecutar: " + peticion.toString() + ";");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        start();
    }

    public SQLConexion() {
    }

    public void run() {
        try {
            switch (peticion[0]) {
                case Peticiones.PETICION_SHOW_DATABASES:
                    List<String> list1 = getDatabases();
                    salida = new ObjectOutputStream(socketCliente.getOutputStream());
                    salida.writeObject(list1);
                    salida.flush();
                    list1.forEach(action -> System.out.println(action));
                    break;

                case Peticiones.PETICION_SHOW_TABLES:
                    List<String> list2 = getTables(peticion[1]);
                    salida.writeObject(list2);
                    salida.flush();
                    break;

                case Peticiones.PETICION_SHOW_CREATE_TABLE:
                    List<String> list3 = showCreateTable(peticion[1], peticion[2]);
                    salida.writeObject(list3);
                    salida.flush();
                    break;

                case Peticiones.PETICION_QUERY:
                    ArrayList<String[]> list4 = getTable(peticion[1], peticion[2]);
                    salida.writeObject(list4);
                    salida.flush();
                    break;
            }
        } catch (IOException e) {
        } finally {
            try {
                entrada.close();
                salida.close();
                socketCliente.close();
            } catch (IOException e) {

            }
        }
    }

    public ArrayList<String[]> getTable(String database, String q) {
        ArrayList<String[]> t = new ArrayList<String[]>();
        try {
            getConnection(database);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(q);

            t = getTableFromResultSet(rs);

            closeResultSet(rs);
            closeStatement(stmt);
            closeConnection(connection);
        } catch (SQLException ex) {
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return t;
    }

    private ArrayList<String[]> getTableFromResultSet(ResultSet rs) {
        String[] col = null;
        String[][] t = null;
        ArrayList<String[]> table = new ArrayList<>();

        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            col = new String[columnCount];
// The column count starts from 1
            for (int i = 1; i <= columnCount; i++) {
                col[i - 1] = rsmd.getColumnName(i);
            }
            table.add(col);
            while (rs.next()) {
                col = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    col[i - 1] = rs.getString(i);
                }
                table.add(col);
            }

        } catch (SQLException ex) {
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }

    public List<String> getDatabases() {
        List<String> list = new ArrayList();
        getConnection();
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet res = meta.getCatalogs();
            while (res.next()) {
                String db = res.getString(TABLE_CAT);
                list.add(db);
            }
            closeResultSet(res);
            closeConnection(connection);
        } catch (SQLException ex) {
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    public List<String> showCreateTable(String database, String table) {
        List<String> list = new ArrayList();
        getConnection(database);
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("show create table " + database + "." + table);
            if (rs.next()) {
                list.add(rs.getString(1));
                list.add(rs.getString(2));
            }
            closeResultSet(rs);
            closeStatement(stmt);
        } catch (SQLException ex) {
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<String> showCreateTableE(String database, String query) {
        List<String> list = new ArrayList();
        getConnection(database);
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            closeResultSet(rs);
            closeStatement(stmt);
        } catch (SQLException ex) {
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<String> getTables(String database) {
        List<String> list = new ArrayList();
        getConnection(database);

        try {
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                list.add(rs.getString(3));
            }
            closeResultSet(rs);
            closeConnection(connection);
        } catch (SQLException ex) {
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    private void getConnection() {
        connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            //System.out.println(CONEXION_EXITOSA);

        } catch (SQLException ex) {
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ERROR_CONEXION);
        }
    }

    private void getConnection(String database) {
        connection = null;
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            connection = DriverManager.getConnection(DB_URL + database, USER, PASS);
            //System.out.println(CONEXION_EXITOSA);

        } catch (SQLException ex) {
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ERROR_CONEXION);
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
            //System.out.println(CONEXION_CERRADA);
        } catch (SQLException ex) {
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void closeResultSet(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException ex) {
            System.out.println(ERROR_CLOSE_RESULTSET);
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void closeStatement(CallableStatement cs) {
        try {
            cs.close();
        } catch (SQLException ex) {
            System.out.println(ERROR_CLOSE_CALLABLE);
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void closeStatement(PreparedStatement ps) {
        try {
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ERROR_CLOSE_CALLABLE);
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void closeStatement(Statement s) {
        try {
            s.close();
        } catch (SQLException ex) {
            System.out.println(ERROR_CLOSE_CALLABLE);
            Logger.getLogger(SQLConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
