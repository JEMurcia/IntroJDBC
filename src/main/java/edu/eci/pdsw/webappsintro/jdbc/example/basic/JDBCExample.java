/*
 * Copyright (C) 2015 hcadavid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.pdsw.webappsintro.jdbc.example.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class JDBCExample {
    
    public static void main(String args[]){
        try {
            String url="jdbc:mysql://desarrollo.is.escuelaing.edu.co:3306/bdprueba";
            String driver="com.mysql.jdbc.Driver";
            String user="bdprueba";
            String pwd="bdprueba";
                        
            Class.forName(driver);
            Connection con=DriverManager.getConnection(url,user,pwd);
            con.setAutoCommit(false);
                 
            
            System.out.println("Valor total pedido 1:"+valorTotalPedido(con, 1));
            
            List<String> prodsPedido=nombresProductosPedido(con, 1);
            
            
            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod:prodsPedido){
                System.out.println(nomprod);
            }
            System.out.println("-----------------------");
            
            
            int suCodigoECI=20134423;
            registrarNuevoProducto(con, suCodigoECI, "SU NOMBRE", 99999999);            
            con.commit();
            
            cambiarNombreProducto(con, suCodigoECI, "EL NUEVO NOMBRE");
            con.commit();
            
            
            con.close();
                                   
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    /**
     * Agregar un nuevo producto con los parámetros dados
     * @param con la conexión JDBC
     * @param codigo
     * @param nombre
     * @param precio
     * @throws SQLException 
     */
    public static void registrarNuevoProducto(Connection con, int codigo, String nombre,int precio) throws SQLException{
        //Crear preparedStatement
        //Asignar parámetros
        //usar 'execute'
        String insertar="inset table bdprueba.ORD_PRODUCTOS (codigo, nombre, "
                + "preci) values (?,?,?);";
        PreparedStatement ps= con.prepareStatement(insertar);
        ps.setInt(1, codigo);
        ps.setString(2, nombre);
        ps.setInt(3, precio);
        ps.executeUpdate();
        
//        con.commit();
        
    }
    
    /**
     * Consultar los nombres de los productos asociados a un pedido
     * @param con la conexión JDBC
     * @param codigoPedido el código del pedido
     * @return 
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido){
        List<String> np=new LinkedList<>();
        String query = "SELECT nombre FROM ORD_DETALLES_PEDIDO,ORD_PRODUCTOS WHERE pedido_fk = ? AND producto_fk = codigo;";
        try{
            //Crear prepared statement
            PreparedStatement productosPedido = con.prepareStatement(query);
            //asignar parámetros
            productosPedido.setInt(1, codigoPedido);
            //usar executeQuery
            ResultSet rs = productosPedido.executeQuery();
            //Sacar resultados del ResultSet
            //Llenar la lista y retornarla
            while(rs.next()){
                np.add(rs.getString("nombre"));
            }
            rs.close();
        }catch(SQLException ex){
            //log exception
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        return np;
    }

    
    /**
     * Calcular el costo total de un pedido
     * @param con
     * @param codigoPedido código del pedido cuyo total se calculará
     * @return el costo total del pedido (suma de: cantidades*precios)
     */
    public static int valorTotalPedido(Connection con, int codigoPedido){
        String query = "SELECT cantidad,precio FROM ORD_DETALLES_PEDIDO,ORD_PRODUCTOS WHERE pedido_fk = ? AND producto_fk = codigo;";
        int costo = 0;
        try{
            //Crear prepared statement
            PreparedStatement productosPedido = con.prepareStatement(query);
            //asignar parámetros
            productosPedido.setInt(1, codigoPedido);
            //usar executeQuery
            ResultSet rs = productosPedido.executeQuery();
            //Sacar resultados del ResultSet
            while(rs.next()){
                costo += rs.getInt("cantidad")*rs.getInt("precio");
            }
            rs.close();
        }catch(SQLException ex){
            //log exception
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return costo;
    }
    
    

    /**
     * Cambiar el nombre de un producto
     * @param con
     * @param codigoProducto codigo del producto cuyo nombre se cambiará
     * @param nuevoNombre el nuevo nombre a ser asignado
     */
    public static void cambiarNombreProducto(Connection con, int codigoProducto, String nuevoNombre){
        try {
            String query="update bdprueba.ORD_PRODUCTOS "
                    + "set bdprueba.nombre = ? "
                    + "where bdprueba.codigo = ?";
            //Crear prepared statement
            PreparedStatement ps=con.prepareStatement(query);
            //asignar parámetros
            ps.setString(1, nuevoNombre);
            ps.setInt(2, codigoProducto);
            //usar executeUpdate
            //verificar que se haya actualizado exactamente un registro
        } catch (SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    
    
}
