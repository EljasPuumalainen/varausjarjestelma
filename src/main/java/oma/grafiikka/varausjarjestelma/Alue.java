package oma.grafiikka.varausjarjestelma;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class Alue {

    private final int alueId;
    private final String nimi;

    public Alue(int alueId, String nimi) {
        this.alueId = alueId;
        this.nimi = nimi;
    }

    public int getAlueId() {
        return alueId;
    }

    public String getNimi() {
        return nimi;
    }

    public IntegerProperty getAlueIdProperty() {
        return new SimpleIntegerProperty(alueId);
    }

    public StringProperty getNimiProperty() {
        return new SimpleStringProperty(nimi);
    }

    public static ObservableList<Alue> haeAlueetTietokannasta(Connection connection) throws SQLException {
        ObservableList<Alue> alueet = FXCollections.observableArrayList();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM alue");
        while (resultSet.next()) {
            int alueId = resultSet.getInt("alue_id");
            String nimi = resultSet.getString("nimi");
            Alue alue = new Alue(alueId, nimi);
            alueet.add(alue);
        }
        return alueet;
    }

    public void lisaaAlueTietokantaan(Connection connection) throws SQLException {
        String sql = "INSERT INTO alue (alue_id, nimi) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, this.alueId);
            preparedStatement.setString(2, this.nimi);
            preparedStatement.executeUpdate();
        }
    }

    public void poistaAlueTietokannasta(Connection connection) throws SQLException {
        String sql = "DELETE FROM alue WHERE alue_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, this.alueId);
            preparedStatement.executeUpdate();
        }
    }

}
