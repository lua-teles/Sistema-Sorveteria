package sorveteria.view.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import sorveteria.facade.SorveteriaFacade;
import sorveteria.model.Pedido;
import sorveteria.observer.Observer;
import sorveteria.state.PedidoAbertoState;
import sorveteria.state.PedidoFinalizadoState;
import sorveteria.state.PedidoPreparoState;

import java.net.URL;
import java.util.ResourceBundle;

/*
controller da tela de preparo kanban (preparo.fxml).
 - implementa Observer: é notificado pelo PedidoManagerSubject toda vez que
   um pedido muda de estado, recarregando as colunas do kanban.
 - implementa facadeAware
 - a mudança de estado (ABERTO EM_PREPARO FINALIZADO) é sempre
   delegada ao Facade o controller não manipula o estado diretamente.
 */
public class PreparoController implements Initializable, Observer {

    @FXML private VBox  listaAguardando, listaEmPreparo, listaPronto;
    @FXML private Label countAguardando, countEmPreparo, countPronto;

    private final SorveteriaFacade facade=SorveteriaFacade.getInstance(null,null,null);;

    // ---------------------------------INICIALIAZAÇÃO ---------------------------------
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // recarregar() será chamado pelo MainController via update(null) logo após o registro
    }
    /*@Override
    public void setFacade(SorveteriaFacade facade) {
        this.facade = facade;
        recarregar();
    }
     */

    // ---------------------------------OBSERVER---------------------------------

    // recebe notificação do PedidoManagerSubject e atualiza o kanban a aplicaçãp
    @Override
    public void update(Pedido pedido) {
        javafx.application.Platform.runLater(this::recarregar);
    }

    // ---------------------------------AUXILIARES---------------------------------

    // limpa as três colunas e redistribui todos os pedidos conforme seu estado atual.
    private void recarregar() {
        if (facade == null) return;
        listaAguardando.getChildren().clear();
        listaEmPreparo.getChildren().clear();
        listaPronto.getChildren().clear();
        for (Pedido p : facade.getPedidos()) {
            if      (p.getEstado() instanceof PedidoAbertoState)    listaAguardando.getChildren().add(criarCard(p));
            else if (p.getEstado() instanceof PedidoPreparoState)   listaEmPreparo .getChildren().add(criarCard(p));
            else if (p.getEstado() instanceof PedidoFinalizadoState) listaPronto   .getChildren().add(criarCard(p));
        }
        // atualiza cada coluna
        countAguardando.setText(String.valueOf(listaAguardando.getChildren().size()));
        countEmPreparo.setText(String.valueOf(listaEmPreparo.getChildren().size()));
        countPronto.setText(String.valueOf(listaPronto.getChildren().size()));
    }
    /*
     cria o card visual de um pedido para o kanban
     inclui botão de ação apenas nos estados que permitem transição
     o botão delega sempre ao facade, nunca altera o estado diretamente
     */
    private VBox criarCard(Pedido pedido) {

        VBox card = new VBox(6);
        card.getStyleClass().add("pedidoCard");
        card.setPadding(new Insets(12, 14, 12, 14));

        Label id = new Label("#" + String.format("%03d", pedido.getId()));
        Label desc = new Label(pedido.getDescricaoResumida());
        Label preco = new Label(String.format("R$ %.2f", pedido.calcularTotal()));

        id.getStyleClass().add("pedidoId");
        desc.getStyleClass().add("pedidoDescricao");
        desc.setWrapText(true);
        preco.getStyleClass().add("pedidoValor");

        card.getChildren().addAll(id, desc, preco);

        // pedidos abertos: botão para avançar para EM_PREPARO
        if (pedido.getEstado() instanceof PedidoAbertoState) {
            Button btn = new Button("Iniciar Preparo →");
            btn.getStyleClass().add("botaoConfirmar");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> facade.iniciarPreparo(pedido));
            // facade notifica observers → update() → recarregar()
            card.getChildren().add(btn);
        }

        // pedidos em preparo: botão para marcar como FINALIZADO
        if (pedido.getEstado() instanceof PedidoPreparoState) {
            Button btn = new Button("Marcar como Pronto ✓");
            btn.getStyleClass().add("botaoConfirmar");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> facade.finalizarPedido(pedido));
            card.getChildren().add(btn);
        }

        return card;
    }
}
