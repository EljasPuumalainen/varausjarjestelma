package oma.grafiikka.varausjarjestelma;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Varauksenpalvelut {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/vn";
    private static final String USER = "root";
    private static final String PASSWORD = "KISSAmies5";


    public void lisaaVarauksenPalvelut(int varausId, int palveluId, int lkm) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO varauksen_palvelut (varaus_id, palvelu_id, lkm) VALUES (?, ?, ?)")) {
            statement.setInt(1, varausId);
            statement.setInt(2, palveluId);
            statement.setInt(3, lkm);
            statement.executeUpdate();
            System.out.println("Varauksen palvelut lisätty onnistuneesti.");
        } catch (SQLException e) {
            System.out.println("Virhe lisättäessä varauksen palveluita: " + e.getMessage());
        }
    }
}


