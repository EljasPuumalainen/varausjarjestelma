package oma.grafiikka.varausjarjestelma;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;



public class Kayttoliittyma extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Button varaustenHallinta = new Button("Varausten hallinta");
    private Button asikkaidenHallinta = new Button("Asiakkaiden hallinta");
    private Button mokkienHallinta = new Button("Mökkien hallinta");
    private Button alueidenHallinta = new Button("Alueiden hallinta");
    private Button palveluidenHallinta = new Button("Palveluiden hallinta");

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


        varaustenHallinta.setOnAction(e -> {
            //primaryStage.close();
            katsoVaraukset();
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
    }

}
