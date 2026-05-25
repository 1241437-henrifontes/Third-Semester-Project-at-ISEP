package Demo;

import data.TemplateData.*;
import Model.TemplateModel.Address;
import Model.TemplateModel.Client;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
public class IntegrationDemoTest {

    /**
     * Worst possible way to use it for testing, by rollbacking all the
     * changes to the database.
     * This approach requires the database and should only be used for
     * demonstration purposed. For this reason, if you have several scenarios
     * for demonstration, commits are advisable, otherwise your system will
     * loose information.
     */
    @Test
    public void scenarioOneWithDatabaseAccess() {
        DatabaseConnection databaseConnection = null;
        try {
            databaseConnection = ConnectionFactory.getInstance()
                    .getDatabaseConnection();
        } catch (IOException exception) {
            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.SEVERE, null, exception);
        }

        Connection connection = databaseConnection.getConnection();
        Logger.getLogger(IntegrationDemoTest.class.getName())
                .log(Level.INFO, "Connected to the database!");

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


            boolean result = clientsStore.save(databaseConnection, client);
            assertTrue(result);


            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Added Client!");

            //Change only the client's name.
            client.setName("abcd");
            result = clientsStore.save(databaseConnection, client);
            assertTrue(result);


            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Changed Client!");

            //Change the 1st address.
            address = client.getAddressList().get(0);
            address.setStreet("Rua de S. Tomé, 231");

            result = clientsStore.save(databaseConnection, client);
            assertTrue(result);

            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Changed Address!");

            //Delete the client and it's address associations (not the addresses).
            result = clientsStore.delete(databaseConnection, client);
            assertTrue(result);

            //connection.commit();
            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Deleted Client!");

            //Try to recreate the client, but with an invalid address...
            //...to verify that none of the changes are persisted in the database.
            //Note: the maximum length of the street field in the database is 30...
            //...so we try to add a street with more than 30 characters.
            address = client.getAddressList().get(0);
            address.setStreet(
                    "Rua de S. Tomé, 231, Porto, Portugal"); //36 characters

            result = clientsStore.save(databaseConnection, client);
            assertFalse(result);

            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Tried adding and invalid Address!");

        } catch (SQLException ex) {
            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.SEVERE, null, ex);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(IntegrationDemoTest.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(IntegrationDemoTest.class.getName())
                        .log(Level.SEVERE, null, ex1);
            }
        }
    }

    /**
     * Using in-memory stores.
     */
    @Test
    public void scenarioOneWithInMemoryDatabase() {
        DatabaseConnection databaseConnection = null;

        Connection connection = null;

        try {

            //Clients DAL.
            Persistable clientsStore = new ClientsStoreInMemory();

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

            boolean result = clientsStore.save(databaseConnection, client);
            assertTrue(result);

            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Added Client!");

            //Change only the client's name.
            client.setName("abcd");
            result = clientsStore.save(databaseConnection, client);
            assertTrue(result);

            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Changed Client!");

            //Change the 1st address.
            address = client.getAddressList().get(0);
            address.setStreet("Rua de S. Tomé, 231");

            result = clientsStore.save(databaseConnection, client);
            assertTrue(result);

            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Changed Address!");

            //Delete the client and it's address associations (not the addresses).
            result = clientsStore.delete(databaseConnection, client);
            assertTrue(result);

            //connection.commit();
            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Deleted Client!");

            //Try to recreate the client, but with an invalid address...
            //...to verify that none of the changes are persisted in the database.
            //Note: the maximum length of the street field in the database is 30...
            //...so we try to add a street with more than 30 characters.
            address = client.getAddressList().get(0);
            address.setStreet(
                    "Rua de S. Tomé, 231, Porto, Portugal"); //36 characters

            result = clientsStore.save(databaseConnection, client);
            assertFalse(result);

            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Tried adding and invalid Address!");

        } catch (Exception ex) {
            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Better approach that mocks connections and results from the database.
     */
    @Test
    public void scenarioOneWithDatabaseMocking() {

        DatabaseConnection databaseConnection = mock(DatabaseConnection.class);

        Connection connection = mock(Connection.class);

        try {
            connection.setAutoCommit(false);

            //Clients DAL.
            ClientsStore clientsStore = mock(ClientsStore.class);

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

            when(clientsStore.save(databaseConnection, client)).thenReturn(
                    true);
            boolean result = clientsStore.save(databaseConnection, client);
            assertTrue(result);

            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Added Client!");

            //Change only the client's name.
            client.setName("abcd");
            result = clientsStore.save(databaseConnection, client);
            assertTrue(result);

            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Changed Client!");

            //Change the 1st address.
            address = client.getAddressList().get(0);
            address.setStreet("Rua de S. Tomé, 231");

            result = clientsStore.save(databaseConnection, client);
            assertTrue(result);

            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Changed Address!");

            //Delete the client and it's address associations (not the addresses).
            when(clientsStore.delete(databaseConnection, client)).thenReturn(
                    true);
            result = clientsStore.delete(databaseConnection, client);
            assertTrue(result);

            //connection.commit();
            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Deleted Client!");

            //Try to recreate the client, but with an invalid address...
            //...to verify that none of the changes are persisted in the database.
            //Note: the maximum length of the street field in the database is 30...
            //...so we try to add a street with more than 30 characters.
            address = client.getAddressList().get(0);
            address.setStreet(
                    "Rua de S. Tomé, 231, Porto, Portugal"); //36 characters

            when(clientsStore.save(databaseConnection, client)).thenReturn(
                    false);
            result = clientsStore.save(databaseConnection, client);
            assertFalse(result);

            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.INFO, "Tried adding and invalid Address!");

        } catch (Exception ex) {
            Logger.getLogger(IntegrationDemoTest.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}