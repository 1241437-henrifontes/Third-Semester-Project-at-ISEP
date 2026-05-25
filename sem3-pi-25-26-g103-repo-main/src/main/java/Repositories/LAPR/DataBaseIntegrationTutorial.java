package Repositories.LAPR;

import Database.DataBaseConnection;
import oracle.jdbc.OracleTypes;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Scanner;

public class DataBaseIntegrationTutorial {
    public static void main(String[] args) {
        try {
            CallableStatement statement = getConnection().prepareCall("{? = call FN_HASSIDINGS(?) }");
            System.out.print("Line Id: ");
            Scanner scanner = new Scanner(System.in);
            int x = scanner.nextInt();
            statement.registerOutParameter(1, OracleTypes.CURSOR);

            statement.setInt(2, x);

            statement.execute();

            ResultSet res = (ResultSet) statement.getObject(1);
            if (res.next()){
                System.out.println("Line " + x + " has sidings.");
            }else{
                System.out.println("Line " + x + " has no sidings.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() {
        try  {
            return DataBaseConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
