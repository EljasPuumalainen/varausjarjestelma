package oma.grafiikka.varausjarjestelma;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VarausikkunaSql {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/vn";
    private static final String USER = "root";
    private static final String PASSWORD = "KISSAmies5";

    public ObservableList<String> varaukset = FXCollections.observableArrayList();
    public ListProperty<String> varaus = new SimpleListProperty<>(varaukset);

    public  VarausikkunaSql (){
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT a.etunimi, a.sukunimi " +
                    "FROM asiakas a JOIN varaus v ON a.asiakas_id = v.asiakas_id");

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


    public void lisaaVarausTietokantaan(int asiakasId, int mokkiId, Timestamp varattuPvm, Timestamp vahvistusPvm,
                                        Timestamp varattuAlkupvm, Timestamp varattuLoppupvm) {
        String query = "INSERT INTO varaus (asiakas_id, mokki_id, varattu_pvm, vahvistus_pvm, varattu_alkupvm, varattu_loppupvm) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, asiakasId);
            statement.setInt(2, mokkiId);


            // Aseta Timestamp-arvot PreparedStatementiin
            statement.setTimestamp(3, Timestamp.valueOf(varattuPvm.toLocalDateTime()));
            statement.setTimestamp(4, Timestamp.valueOf(vahvistusPvm.toLocalDateTime()));
            statement.setTimestamp(5, Timestamp.valueOf(varattuAlkupvm.toLocalDateTime()));
            statement.setTimestamp(6, Timestamp.valueOf(varattuLoppupvm.toLocalDateTime()));

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Varaus lisätty onnistuneesti.");
            } else {
                System.out.println("Varauksen lisääminen epäonnistui.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int haeVarausId(String etunimi, String sukunimi){
        int varausId = -1;

        String query = "SELECT v.varaus_id, a.etunimi, a.sukunimi " +
                "FROM varaus v " +
                "INNER JOIN asiakas a ON v.asiakas_id = a.asiakas_id " +
                "WHERE a.etunimi = ? AND a.sukunimi = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, etunimi);
            statement.setString(2, sukunimi);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                varausId = resultSet.getInt("varaus_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return varausId;
    }

    public TextArea haeVarauksenTiedot(int varaus_id) {
        String kysely = "select CONCAT(a.etunimi, ' ' ,a.sukunimi) as nimi, m.mokkinimi, v.varattu_pvm, " +
                "v.vahvistus_pvm, v.varattu_alkupvm, v.varattu_loppupvm " +
                "from asiakas a join varaus v on a.asiakas_id = v.asiakas_id " +
                "join mokki m on v.mokki_id = m.mokki_id " +
                "where v.varaus_id = ?";

        TextArea varaustiedot = new TextArea();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(kysely)) {

            statement.setInt(1, varaus_id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String nimi = resultSet.getString("nimi");
                String mokkinimi = resultSet.getString("mokkinimi");
                String varattuPvm = resultSet.getString("varattu_pvm");
                String vahvistusPvm = resultSet.getString("vahvistus_pvm");
                String varattuAlkupvm = resultSet.getString("varattu_alkupvm");
                String varattuLoppupvm = resultSet.getString("varattu_loppupvm");

                // Voit muokata tulostettavia tietoja tarpeidesi mukaan
                String varauksenTiedot = "Asiakkaan nimi: " + nimi + "\n"
                        + "Mökki: " + mokkinimi + "\n"
                        + "Varattu päivämäärä: " + varattuPvm + "\n"
                        + "Vahvistus päivämäärä: " + vahvistusPvm + "\n"
                        + "Varattu alkupäivämäärä: " + varattuAlkupvm + "\n"
                        + "Varattu loppupäivämäärä: " + varattuLoppupvm + "\n";

                // Kirjoita tiedot tekstikenttään
                varaustiedot.setText(varauksenTiedot);
            } else {
                varaustiedot.setText("Varausta ei löytynyt.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            varaustiedot.setText("Tietojen hakeminen epäonnistui.");
        }

        return varaustiedot;
    }



}
