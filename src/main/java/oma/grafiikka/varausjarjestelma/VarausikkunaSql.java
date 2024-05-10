package oma.grafiikka.varausjarjestelma;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

import java.sql.*;
import java.time.LocalDate;

public class VarausikkunaSql {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/vn";
    private static final String USER = "root";
    private static final String PASSWORD = "12jh458";

    public ObservableList<String> varaukset = FXCollections.observableArrayList();

    public VarausikkunaSql() {
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

    public void lisaaVaraus(LocalDate varattu_alkupvm, LocalDate varattu_loppupvm ) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {

            // SQL-lause, joka lisää tietoa tietokantaan
            String sql = "INSERT INTO varaus WHERE varattu_alkupvm= ? AND varattu_loppupvm = ?";

            // Valmistele lause
            PreparedStatement statement = conn.prepareStatement(sql);

            // Aseta arvot lauseeseen
            statement.setDate(1, Date.valueOf(varattu_alkupvm));
            statement.setDate(2, Date.valueOf(varattu_loppupvm));


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

    public void poistaVaraus(LocalDate varattu_alkupvm, LocalDate varattu_loppupvm, LocalDate varattu_pvm) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {

            // SQL-lause, joka poistaa tiedot tietokannasta
            String sql = "DELETE FROM varaus WHERE varattu_alkupvm = ? AND varattu_loppupvm = ? AND varattu_pvm =?";

            // Valmistele lause
            PreparedStatement statement = conn.prepareStatement(sql);

            // Aseta päivämäärät lauseeseen
            statement.setDate(1, Date.valueOf(varattu_alkupvm));
            statement.setDate(2, Date.valueOf(varattu_loppupvm));
            statement.setDate(3, Date.valueOf(varattu_pvm));

            // Suorita lause
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Varaustiedot poistettiin onnistuneesti!");
            } else {
                System.out.println("Varaustietojen poistaminen epäonnistui.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public TextArea HaeVaraus(Date varattu_alkupvm, Date varattu_loppupvm, Date varattu_pvm) {
        TextArea VarausTiedot = new TextArea();
        VarausTiedot.clear();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM varaus WHERE varattu_alkupvm = ? AND varattu_loppupvm = ? AND varattu_pvm";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setDate(1, varattu_alkupvm);
            statement.setDate(2, varattu_loppupvm);
            statement.setDate(3, varattu_pvm);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Date varausalku = resultSet.getDate("varattu_alkupvm");
                Date varausloppu = resultSet.getDate("varattu_loppupvm");
                Date varattu = resultSet.getDate("varattu_pvm");

                VarausTiedot.setText("Varauksen alku: " + varausalku + "\n" +
                        "Varauksen Loppu " + varausloppu + "\n" +
                        "Varattuna " + varattu + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return VarausTiedot;
    }
}
