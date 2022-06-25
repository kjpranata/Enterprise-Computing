/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Interface.Interface;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author howard
 */
public class Server implements Interface {
    private static Server APUServer = null;
    private String serverName = null;

    public Server (String name){
        this.setServerName(name);
    }
    public String getServerName(){
        return serverName;
    }
    public void setServerName(String serverName){
        this.serverName= serverName;
    }

    public static void main (String args[]) throws RemoteException, AlreadyBoundException{

        APUServer = new Server ("APUServer");
        Registry reg = LocateRegistry.createRegistry(1098);
        Remote obj = UnicastRemoteObject.exportObject(APUServer,1098);
        reg.bind(APUServer.getServerName(),obj);
        System.out.println("APU Server Started");
    }
    public static Connection connect(){

        Connection con = null;
        try
        {
            //change to appropriate directory
            String url = "jdbc:sqlite:APUDatabase.db";
            con = DriverManager.getConnection(url);

        }
        catch (SQLException s)
        {
            System.out.println(s.getMessage());
        }
        return con;
    }
    public int login(String username, String password){
        if(username.equals("admin") && password.equals("admin")){
            return 0;
        }
        String sql = "SELECT * FROM account";


        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                String user = rs.getString("username");
                String pass = rs.getString("password");
                int id = rs.getInt("id");
                System.out.println(user + password);
                if(user.equals(username) && pass.equals(password)){
                    System.out.println("Login Successfull");
                    return id;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return -1;
    }

    public boolean verifyLogin(int user_id, String validation){
        String sql = "SELECT * FROM account";
        boolean found = false;
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                int id = rs.getInt("id");
                String valid = rs.getString("ic_passportnum");

                if(id == user_id && validation.equals(valid)){
                    found = true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return found;
    }
    
    
    public int registerAccount(String username, String password){
        String sql = "SELECT * FROM account";
                
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                String user = rs.getString("username");
                String pass = rs.getString("password");

                if(user.equals(username)){
                    return 1;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return 0;
        
    }
    public void dataInput(String firstName, String lastName, String IC, String username, String password){
        String sql = "INSERT INTO account(first_name, last_name, ic_passportnum,username,password) VALUES(?,?,?,?,?)";
          
         try (Connection conn = this.connect();
               PreparedStatement ps = conn.prepareStatement(sql)) {
              
               ps.setString(1,firstName);
               ps.setString(2,lastName);
               ps.setString(3,IC);
               ps.setString(4,username);
               ps.setString(5,password);
               ps.executeUpdate();
             
          } catch (SQLException e) {
              System.out.println(e.getMessage());
              
        }
          
        
    }
    
    public List<String> retreiveAccount(int id){
        String sql = "SELECT * FROM account";
        List<String> data = new ArrayList<>();
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                int x = rs.getInt("id");
                if(x==id){
                     String first_name = rs.getString("first_name");
                     String last_name = rs.getString("last_name");
                     String ic = rs.getString("ic_passportnum");
                     data.add(first_name);
                     data.add(last_name);
                     data.add(ic);
                     return data;
                }
               
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public List<List<String>> listAllExec() throws Exception{
        String sql = "SELECT * FROM account";
        List<List<String>> data = new ArrayList<>();
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
                
                int x = rs.getInt("id");
                String first_name = rs.getString("first_name");
                String last_name = rs.getString("last_name");
                String ic = rs.getString("ic_passportnum");
                
                List<String> temp = new ArrayList<>();
                temp.add(first_name);
                temp.add(last_name);
                temp.add(ic);
                data.add(temp);
            }
              
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return data;
    }
    
    public void storeNew(String itemName, String brand, String category, int stock, int price, String date) throws Exception{
        String sql = "INSERT INTO inventory(item_name, brand, category, stock, price, date_stored) VALUES(?,?,?,?,?,?)";
         try (Connection conn = this.connect();
               PreparedStatement ps = conn.prepareStatement(sql)) {
              
               ps.setString(1,itemName);
               ps.setString(2,brand);
               ps.setString(3,category);
               ps.setInt(4,stock);
               ps.setInt(5,price);
               ps.setString(6, date);
               ps.executeUpdate();
               
          } catch (SQLException e) {
              System.out.println(e.getMessage());
        }   
    }
    
    public void deleteExec(String del){
        String sql = "DELETE FROM account WHERE username = ?";
         try (Connection conn = this.connect();
                PreparedStatement ps = conn.prepareStatement(sql)){
               ps.setString(1, del);
               ps.executeUpdate();
               
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
