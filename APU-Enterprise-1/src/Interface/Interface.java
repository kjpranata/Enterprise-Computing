/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author Asus
 */
public interface Interface extends Remote {
     public int login (String username, String password) throws Exception;
     public int registerAccount(String username, String password) throws Exception;
     public boolean verifyLogin(int user_id,String validation) throws Exception;
     public void dataInput(String firstName, String lastName, String IC, String username, String password) throws Exception;
     public List<String> retreiveAccount(int id) throws Exception;
     public List<List<String>> listAllExec() throws Exception;
     public void storeNew(String itemName, String brand, String category, int stock, int price, String date) throws Exception;
     public void deleteExec(String del) throws Exception;
}
