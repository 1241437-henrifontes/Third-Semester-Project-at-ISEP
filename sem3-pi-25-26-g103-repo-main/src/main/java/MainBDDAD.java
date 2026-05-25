/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import data.TemplateData.ClientsStore;
import data.TemplateData.ConnectionFactory;
import data.TemplateData.DatabaseConnection;
import Model.TemplateModel.Address;
import Model.TemplateModel.Client;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author nunocastro
 */
public class MainBDDAD {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here      
        Logger.getLogger(ClientsStore.class.getName())
                .log(Level.SEVERE, "teste");

        DatabaseConnection databaseConnection = null;
        try {
            databaseConnection = ConnectionFactory.getInstance()
                    .getDatabaseConnection();
        } catch (IOException exception) {
            Logger.getLogger(ClientsStore.class.getName())
                    .log(Level.SEVERE, null, exception);
        }

        Connection connection = databaseConnection.getConnection();
        System.out.println("Connected to the database!");
        try {
            connection.setAutoCommit(false);

            //Clients DAL.
            ClientsStore clientsStore = new ClientsStore();

            //Create a client with 2 addresses.
            Client client = new Client();

            //TODO: como é que o cliente em Java saberá qual o próximo ID? O
            // ID não devia ser automaticamente atribuído pela base de dados?
            client.setId(1);
            client.setName("abc");
            client.setVatnr("123456789");

            Address address;
            address = new Address();
            address.setId(1);
            address.setCity("Porto");
            address.setPostalcode("4000-123");
            address.setStreet("Rua de S. Tomé");
            client.addAddress(address);

            address = new Address();
            address.setId(2);
            address.setCity("Braga");
            address.setPostalcode("4200-456");
            address.setStreet("Praça da República, 556");
            client.addAddress(address);

            if (!clientsStore.save(databaseConnection, client)) {
                throw databaseConnection.getLastError();
            }
            connection.commit();
            System.out.println("Added Client!");

            //Change only the client's name.
            client.setName("abcd");
            if (!clientsStore.save(databaseConnection, client)) {
                throw databaseConnection.getLastError();
            }
            connection.commit();
            System.out.println("Changed Client!");

            //Change the 1st address.
            address = client.getAddressList().get(0);
            address.setStreet("Rua de S. Tomé, 231");
            if (!clientsStore.save(databaseConnection, client)) {
                throw databaseConnection.getLastError();
            }
            connection.commit();
            System.out.println("Changed Address!");

            //Delete the client and it's address associations (not the addresses).
            if (!clientsStore.delete(databaseConnection, client)) {
                throw databaseConnection.getLastError();
            }
            connection.commit();
            System.out.println("Deleted Client!");

            //Try to recreate the client, but with an invalid address...
            //...to verify that none of the changes are persisted in the database.
            //Note: the maximum length of the street field in the database is 30...
            //...so we try to add a street with more than 30 characters.
            address = client.getAddressList().get(0);
            address.setStreet(
                    "Rua de S. Tomé, 231, Porto, Portugal"); //36 characters
            if (!clientsStore.save(databaseConnection, client)) {
                throw databaseConnection.getLastError();
            }
            connection.commit();
            System.out.println("Tried adding and invalid Address!");


        } catch (SQLException ex) {
            Logger.getLogger(ClientsStore.class.getName())
                    .log(Level.SEVERE, null, ex);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(ClientsStore.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        }
    }
}
