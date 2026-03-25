package sorveteria.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sorveteria.banco.PedidoDAO;
import sorveteria.facade.SorveteriaFacade;
import sorveteria.observer.PedidoManagerSubject;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        PedidoManagerSubject managerSubject = new PedidoManagerSubject();
        PedidoDAO pedidoDAO = new PedidoDAO();
        SorveteriaFacade facade=new SorveteriaFacade(managerSubject,pedidoDAO);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Sorveteria");
        stage.setScene(scene);

        stage.setMaximized(true);// ocupa a tela toda
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        scene.getStylesheets().add(
                getClass().getResource("styles.css").toExternalForm()
        );

        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}