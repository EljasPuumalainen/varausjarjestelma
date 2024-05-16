package oma.grafiikka.varausjarjestelma;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Asiakas {


    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/vn";
    private static final String USER = "root";
    private static final String PASSWORD = "KISSAmies5";

    public ObservableList<String> asiakasdata = FXCollections.observableArrayList();
    public ListProperty<String> asiakas = new SimpleListProperty<>(asiakasdata);

    /**
     * Asiakas luokan alustaja
     * lukee tietokannasta etunimet ja sukunimet asiakas taulusta
     * lisää ne ObservableListiiin, jotta ne nimet näkyvät listviewssä käyttöliittymässä
     * @throws ClassNotFoundException
     */
    public Asiakas() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (
                Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)
        ) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select etunimi, sukunimi from asiakas");

            while (resultSet.next()) {
                // Käsittele tulokset täällä
                String column1 = resultSet.getString("etunimi");
                String column2 = resultSet.getString("sukunimi");

                asiakasdata.add(column1 + " " + column2);
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    public void kirjoitaPostiTiedot(String postinumero, String toimipaikka) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {

            // Tarkista ensin, onko postinumero jo olemassa tietokannassa
            String checkIfExistsSQL = "SELECT COUNT(*) FROM posti WHERE postinro = ?";
            PreparedStatement checkStatement = conn.prepareStatement(checkIfExistsSQL);
            checkStatement.setString(1, postinumero);
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) { // Jos postinumeroa ei löytynyt, lisää se
                // SQL-lause, joka lisää tietoa tietokantaan
                String insertSQL = "INSERT INTO posti (postinro, toimipaikka) VALUES (?, ?)";
                PreparedStatement insertStatement = conn.prepareStatement(insertSQL);

                // Aseta arvot lauseeseen
                insertStatement.setString(1, postinumero);
                insertStatement.setString(2, toimipaikka);

                // Suorita lisäyslause
                int rowsAffected = insertStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Tietojen lisäys onnistui!");
                } else {
                    System.out.println("Tietojen lisääminen epäonnistui.");
                }
            } else {
                System.out.println("Postinumero " + postinumero + " on jo olemassa tietokannassa.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void kirjoitaAsiakasTiedot(String postinro, String etunimi, String sukunimi, String lahiosoite,
                                      String email, String puhelinnro) {

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {

            // SQL-lause, joka lisää tietoa tietokantaan
            String sql = "INSERT INTO asiakas (postinro, etunimi, sukunimi, lahiosoite, email, puhelinnro) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            // Valmistele lause
            PreparedStatement statement = conn.prepareStatement(sql);

            // Aseta arvot lauseeseen
            statement.setString(1, postinro);
            statement.setString(2, etunimi);
            statement.setString(3, sukunimi);
            statement.setString(4, lahiosoite);
            statement.setString(5, email);
            statement.setString(6, puhelinnro);

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

    public void poistaAsiakas(String etunimi, String sukunimi) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {

            // Asiakas löytyi, poista hänet tietokannasta
            String deleteQuery = "DELETE FROM asiakas WHERE etunimi = ? AND sukunimi = ?";
            PreparedStatement deleteStatement = conn.prepareStatement(deleteQuery);
            deleteStatement.setString(1, etunimi);
            deleteStatement.setString(2, sukunimi);
            int rowsAffected = deleteStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Asiakkaan tiedot poistettu onnistuneesti.");
            } else {
                System.out.println("Asiakkaan tietojen poistaminen epäonnistui.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            paivitaAsiakasLista();
        }
    }


    public TextArea haeTiedot(String etunimi, String sukunimi) {
        TextArea tiijot = new TextArea();
        tiijot.clear();
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String sql = "SELECT * FROM asiakas WHERE etunimi = ? AND sukunimi = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, etunimi);
            statement.setString(2, sukunimi);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String etunimi1 = resultSet.getString("etunimi");
                String sukunimi1 = resultSet.getString("sukunimi");
                String lahiosoite = resultSet.getString("lahiosoite");
                String email = resultSet.getString("email");
                String puhelinnro = resultSet.getString("puhelinnro");

                tiijot.setText("Etunimi: " + etunimi + "\n" +
                        "Sukunimi: " + sukunimi + "\n" +
                        "Lähiosoite: " + lahiosoite + "\n" +
                        "Sähköposti: " + email + "\n" +
                        "Puhelinnumero: " + puhelinnro + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tiijot;
    }

    public void paivitaAsiakasLista() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("select etunimi, sukunimi from asiakas");

            asiakasdata.clear(); // Tyhjennä lista ennen päivitystä

            while (resultSet.next()) {
                String column1 = resultSet.getString("etunimi");
                String column2 = resultSet.getString("sukunimi");
                asiakasdata.add(column1 + " " + column2);
            }

            // Päivitä ListProperty
            asiakas.set(asiakasdata);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<String> haeAsiakasNimet() {
        ObservableList<String> asiakasNimet = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT etunimi, sukunimi FROM asiakas");

            while (resultSet.next()) {
                String etunimi = resultSet.getString("etunimi");
                String sukunimi = resultSet.getString("sukunimi");
                asiakasNimet.add(etunimi + " " + sukunimi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return asiakasNimet;
    }

    public ObservableList<String> haeMokinNimet() {
        ObservableList<String> mokinNimet = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT mokkinimi FROM mokki");

            while (resultSet.next()) {
                String mokkinimi = resultSet.getString("mokkinimi");
                mokinNimet.add(mokkinimi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mokinNimet;
    }


    public int haeAsiakasIdNimella(String etunimi, String sukunimi) {
        int asiakasId = -1; // Oletusarvo, jos asiakasta ei löydy
        String query = "SELECT asiakas_id FROM asiakas WHERE etunimi = ? AND sukunimi = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, etunimi);
            statement.setString(2, sukunimi);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                asiakasId = resultSet.getInt("asiakas_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return asiakasId;

    }

}