package oma.grafiikka.varausjarjestelma;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Kayttoliittyma extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Button varaustenHallinta = new Button("Varausten hallinta");
    private Button asikkaidenHallinta = new Button("Asiakkaiden hallinta");
    private Button mokkienHallinta = new Button("Mökkien hallinta");
    private Button alueidenHallinta = new Button("Alueiden hallinta");
    private Button palveluidenHallinta = new Button("Palveluiden hallinta");
    private Button lisaaMokki = new Button("Lisää mökki");
    private Button poistaMokki = new Button("Poista mökki");
    private Button muokkaaMokkeja = new Button("Muokkaa tietoja");

    @Override
    public void start(Stage primaryStage) {

        BorderPane pane = new BorderPane();

        Scene scene = new Scene(pane, 1000,500);
        primaryStage.setTitle("Varausjärjestelmä");
        primaryStage.setScene(scene);
        primaryStage.show();

        Text text = new Text("Valitse mitä haluat tehdä?");
        text.setFont(Font.font(30));

        BorderPane.setAlignment(text, Pos.TOP_CENTER);
        BorderPane.setMargin(text, new Insets(20,0,0,0));

        pane.setTop(text);
        pane.setCenter(getHbox());

        //Varausten hallinta ikkuna
        varaustenHallinta.setOnAction(e -> {
            //primaryStage.close();
            katsoVaraukset();
        });

        //Mokkien hallinta ikkua
        mokkienHallinta.setOnMouseClicked(e -> {
            mokkienHallinta();
        });
    }

    /**
     * getHbox metodi
     * hboxiin asetetaan buttonit
     * buttoneista päästää uusiin ikkunoihin
     * @return hBox
     * @author Konsta
     */
    public HBox getHbox() {

        HBox hBox = new HBox(15);

        hBox.getChildren().addAll(varaustenHallinta, asikkaidenHallinta, mokkienHallinta, alueidenHallinta,
                palveluidenHallinta);
        hBox.setAlignment(Pos.CENTER);

        return hBox;
    }

    /**
     * katsoVaraukset metodi
     * pystyy katsomaan varausten tietoja
     * varaukset avautuu listviewh näkymään
     * @author Eljas
     */
    public void katsoVaraukset() {

        BorderPane paneeli = new BorderPane();
        TextArea tiedot = new TextArea();
        ListView<String> lista = new ListView<>();

        paneeli.setLeft(lista);
        paneeli.setCenter(tiedot);

        Scene varausIkkuna = new Scene(paneeli);
        Stage varausStage = new Stage();

        varausStage.setScene(varausIkkuna);
        varausStage.show();
        varausStage.setTitle("Varausten hallinta");
    }

    /**
     * Mökkien hallinta stage ja sen ominaisuudet/toiminnot
     */
    public void mokkienHallinta() {
        // Luo uusi BorderPane
        BorderPane pane = new BorderPane();
        TextArea tiedot = new TextArea();
        ListView<String> lista = new ListView<>();

        // Luo uusi Stage-olio
        Stage mokkienHallinta = new Stage();
        Scene kehys = new Scene(pane);

        // Luo VBox painikkeille ja aseta ne vasemmalle
        VBox painikeVBox = new VBox(15);
        painikeVBox.setPadding(new Insets(15, 15, 15, 15));
        painikeVBox.getChildren().addAll(lisaaMokki, poistaMokki, muokkaaMokkeja);
        pane.setRight(painikeVBox);

        // Aseta TextArea ja ListView BorderPaneen
        pane.setCenter(tiedot);
        pane.setLeft(lista);

        // Aseta uusi Scene Stageen ja näytä ikkuna
        mokkienHallinta.setScene(kehys);
        mokkienHallinta.show();
        mokkienHallinta.setTitle("Mökkien hallinta");

        lisaaMokki.setOnMouseClicked(e -> {
            Stage lisaaMokkiStage = new Stage();
            GridPane lisaaMokkiPane = new GridPane();
            Scene lisaaMokkiScene = new Scene(lisaaMokkiPane, 300,300);
            lisaaMokkiStage.setScene(lisaaMokkiScene);
            lisaaMokkiStage.show();
            lisaaMokkiPane.setVgap(5);

            lisaaMokkiPane.add(new Label("Mökin nimi: "), 0,0);
            lisaaMokkiPane.add(new Label("Sijainti:"), 0, 1);
            lisaaMokkiPane.add(new Label("Hinta: "), 0, 2);

            TextField nimi = new TextField();
            TextField sijainti = new TextField();
            TextField hinta = new TextField();

            Button lisaaMokki = new Button("Lisää mökki");

            //Alueiden combobox
            ComboBox<String> alueComboBox = new ComboBox<>();
            alueComboBox.getItems().addAll("Ruka", "Tahko", "Ylläs");

            lisaaMokkiPane.add(new Label("Alue"), 0, 3);
            lisaaMokkiPane.add(alueComboBox, 1, 3);

            lisaaMokkiPane.add(nimi, 1,0);
            lisaaMokkiPane.add(sijainti, 1,1);
            lisaaMokkiPane.add(hinta, 1,2);
            lisaaMokkiPane.add(lisaaMokki, 1,4);

            lisaaMokkiPane.setAlignment(Pos.CENTER);
            lisaaMokkiStage.setTitle("Lisää mökki");

        });
    }
}