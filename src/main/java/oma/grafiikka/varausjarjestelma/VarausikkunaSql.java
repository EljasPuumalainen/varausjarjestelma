package oma.grafiikka.varausjarjestelma;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class VarausikkunaSql {

    private static final String JDBC_URL = "jbcd:mysql://localhost:3306/vn";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public ObservableList<String> varaukset = FXCollections.observableArrayList();

    public VarausikkunaSql (){
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)
        ) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select etunimi, sukunimi from asiakas");
            /*("select a.etunimi, a.sukunimi\n" +
                    "from asiakas a join varaus v\n" +
                    "where a.asiakas_id = v.asiakas_id");*/

            while (resultSet.next()) {
                // Käsittele tulokset täällä
                String column1 = resultSet.getString("etunimi");
                String column2 = resultSet.getString("sukunimi");

                // Voit muotoilla tuloksen haluamallasi tavalla

                varaukset.add(column1 + " " + column2);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void kirjoitavaraus(){
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {

            // SQL-lause, joka lisää tietoa tietokantaan
            String sql = "INSERT INTO asiakas (sarake1, sarake2) VALUES (?, ?)";

            // Valmistele lause
            PreparedStatement statement = conn.prepareStatement(sql);

            // Aseta arvot lauseeseen
            statement.setString(1, "arvo1");
            statement.setString(2, "arvo2");

            // Suorita lause
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Tietojen lisäys onnistui!");
            } else {
                System.out.println("Tietojen lisääminen epäonnistui.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
