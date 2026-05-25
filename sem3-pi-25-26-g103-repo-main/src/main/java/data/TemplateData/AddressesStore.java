package data.TemplateData;

import Model.TemplateModel.Address;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Addresses can only be manipulated by its parent object - Client -
 * therefore save, update and delete actions are only accessible from
 * inside ClientsStore class.
 *
 * @author nunocastro
 */
public class AddressesStore implements Persistable {

    @Override
    public boolean save(DatabaseConnection databaseConnection,
                        Object object) {

        Connection connection = databaseConnection.getConnection();
        Address address = (Address) object;

        String sqlCommand = "select * from address where id = ?";
        boolean returnValue = false;
        try (PreparedStatement getAddressedPreparedStatement = connection.prepareStatement(
                sqlCommand)) {
            getAddressedPreparedStatement.setInt(1, address.getId());
            try (ResultSet addressesResultSet = getAddressedPreparedStatement.executeQuery()) {
                if (addressesResultSet.next()) {
                    sqlCommand =
                            "update address set street = ?, postalcode = ?, city = ? where id = ?";
                } else {
                    sqlCommand =
                            "insert into address(street, postalcode, city, id) values (?, ?, ?, ?)";
                }

                try (PreparedStatement saveAddressPreparedStatement = connection.prepareStatement(
                        sqlCommand)) {
                    saveAddressPreparedStatement.setString(1,
                            address.getStreet());
                    saveAddressPreparedStatement.setString(2,
                            address.getPostalcode());
                    saveAddressPreparedStatement.setString(3,
                            address.getCity());
                    saveAddressPreparedStatement.setInt(4, address.getId());
                    saveAddressPreparedStatement.executeUpdate();
                    returnValue = true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClientsStore.class.getName())
                    .log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
            returnValue = false;
        }
        return returnValue;
    }

    @Override
    public boolean delete(DatabaseConnection databaseConnection,
                          Object object) {

        Connection conn = databaseConnection.getConnection();
        Address address = (Address) object;

        boolean returnValue = false;
        try {
            String sqlCommand;
            sqlCommand = "delete from address where id = ?";
            try (PreparedStatement deleteAddressPreparedStatement = conn.prepareStatement(
                    sqlCommand)) {
                deleteAddressPreparedStatement.setInt(1, address.getId());
                deleteAddressPreparedStatement.executeUpdate();
                returnValue = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClientsStore.class.getName())
                    .log(Level.SEVERE, null, ex);
            databaseConnection.registerError(ex);
            returnValue = false;
        }
        return returnValue;
    }
}
