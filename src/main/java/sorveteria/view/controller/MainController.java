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
    private SorveteriaFacade facade;

    public void initialize() throws Exception {
        carregarTela("pedidos.fxml");
    }

    // chamado pela classe App(MAin) logo após carregar main.fxml
    public void setFacade(SorveteriaFacade facade) throws Exception {
        this.facade = facade;
        // garante que a tela inicial já receba a facade
        carregarTela("pedidos.fxml");
    }

    private void carregarTela(String fxml) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fxml));
            container.getChildren().setAll((Node) loader.load());
            Object ctrl = loader.getController();
            // facade em qualquer controller que a declare
            if (ctrl instanceof FacadeAware fa) {
                fa.setFacade(facade);
            }
            // registra como observer no PedidoManagerSubject quando aplicável
            if (ctrl instanceof Observer obs && facade != null) {
                facade.getPedidoManager().addObserver(obs);
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
                    getClass().getResource("montagem.fxml"));
            container.getChildren().setAll((Node) loader.load());

            MontagemController ctrl = loader.getController();
            ctrl.setFacade(facade);
            ctrl.setMainController(this);
            ctrl.setPedido(pedido);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irPedidos() throws Exception {carregarTela("pedidos.fxml");}
    @FXML
    private void irMontagem() throws Exception {carregarTela("montagem.fxml");}
    @FXML
    private void irPreparo() throws Exception {carregarTela("preparo.fxml");}
    @FXML
    private void irEstoque() throws Exception {carregarTela("estoque.fxml");}
    @FXML public void novoPedido() {
        Pedido pedido = facade.criarPedido();
        carregarMontagem(pedido);
    }


}
