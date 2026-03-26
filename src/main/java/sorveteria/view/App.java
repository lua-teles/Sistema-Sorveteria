package sorveteria.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sorveteria.banco.IngredienteDAO;
import sorveteria.banco.PedidoDAO;
import sorveteria.facade.SorveteriaFacade;
import sorveteria.model.Pedido;
import sorveteria.observer.PedidoManagerSubject;
import sorveteria.view.controller.MainController;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        PedidoManagerSubject managerSubject = new PedidoManagerSubject();
        PedidoDAO pedidoDAO = new PedidoDAO();
        IngredienteDAO ingredienteDAO = new IngredienteDAO();
        SorveteriaFacade facade= SorveteriaFacade.getInstance(managerSubject,pedidoDAO,ingredienteDAO);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/sorveteria/view/controller/main.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Sorveteria");
        stage.setScene(scene);

        stage.setMaximized(true);// ocupa a tela toda
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        scene.getStylesheets().add(
                getClass().getResource("/sorveteria/view/controller/styles.css").toExternalForm()
        );

        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}