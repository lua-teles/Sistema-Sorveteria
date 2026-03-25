package sorveteria.view.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import sorveteria.facade.SorveteriaFacade;
import sorveteria.model.Pedido;
import sorveteria.observer.Observer;

import java.io.IOException;

/*
controller da tela principal (main.fxml)
 - guardar a instância única da SorveteriaFacade
 - trocar o conteúdo do StackPane central ao clicar nos botões de navegação
 - injetar a Facade em cada sub-controller ao carregar seu FXML
 NÃO contém lógica de negócio.
 */

public class MainController {

    @FXML
    private StackPane container;

    //istância única da facade compartilhada com todos os controllers
    private SorveteriaFacade facade=SorveteriaFacade.getInstance(null,null,null);

    public void initialize() throws Exception {
        carregarTela("pedidos.fxml");
    }

    private void carregarTela(String fxml) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sorveteria/view/controller/"+fxml));
            container.getChildren().setAll((Node) loader.load());
            Object ctrl = loader.getController();
            try {
                ctrl.getClass().getMethod("setMainController", MainController.class)
                        .invoke(ctrl, this);
            } catch (NoSuchMethodException ignored) {}

            // registra como observer no PedidoManagerSubject quando aplicável
            if (ctrl instanceof Observer obs) {
                facade.getPedidoManager().addObserver(obs);
                // dispara um update imediato para popular a tela ao carregar
                obs.update(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //carrega a tela de montagem passando um Pedido já criado.
    //usado pelo botão "+ Novo Pedido" e pelo PedidosController ("Montar →").

    public void carregarMontagem(Pedido pedido) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/sorveteria/view/controller/montagem.fxml"));
            container.getChildren().setAll((Node) loader.load());

            MontagemController ctrl = loader.getController();
            ctrl.setMainController(this);
            ctrl.setPedido(pedido);
            System.out.println("Setando montagem com pedido" + pedido);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void irPedidos() throws Exception {carregarTela("pedidos.fxml");}
    @FXML
    public void irMontagem() throws Exception {carregarTela("montagem.fxml");}
    @FXML
    public void irPreparo() throws Exception {carregarTela("preparo.fxml");}
    @FXML
    public void irEstoque() throws Exception {carregarTela("estoque.fxml");}
    @FXML public void novoPedido() {
        Pedido pedido = facade.criarPedido();

        carregarMontagem(pedido);
    }


}
