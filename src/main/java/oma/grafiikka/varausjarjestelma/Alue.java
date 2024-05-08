package oma.grafiikka.varausjarjestelma;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class Alue {


    private final StringProperty nimi;

    public Alue(String nimi) {

        this.nimi = new SimpleStringProperty(nimi);
    }


    public String getNimi() {
        return nimi.get();
    }

    public StringProperty nimiProperty() {
        return nimi;
    }

    public static ObservableList<Alue> haeAlueetTietokannasta(Connection connection) throws SQLException {
        ObservableList<Alue> alueet = FXCollections.observableArrayList();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM alue");
        while (resultSet.next()) {
            String nimi = resultSet.getString("nimi");
            Alue alue = new Alue( nimi);
            alueet.add(alue);
        }
        return alueet;
    }

    public void lisaaAlueTietokantaan(Connection connection) throws SQLException {
        String sql = "INSERT INTO alue (nimi) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, this.nimi.get());
            preparedStatement.executeUpdate();
        }
    }

    public void poistaAlueTietokannasta(Connection connection) throws SQLException {
        String sql = "DELETE FROM alue WHERE nimi = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, this.nimi.get());
            preparedStatement.executeUpdate();
        }
    }
}