package sorveteria.view.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sorveteria.facade.SorveteriaFacade;
import sorveteria.model.Pedido;
import sorveteria.observer.Observer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

//acoes de cada pedido: ver, editar, cancelar
//incrementar abertos, em preparo e finalizado
//toggles todos, abertos, em preparo, prontos

/*
controller da tela de listagem de pedidos:pedidos.fxml
 -implementa Observer: é notificado pelo PedidoManagerSubject toda vez que
 -um pedido é criado ou tem seu estado alterado, recarregando a tabela.

recebe a facade injetada pelo MainController.
 */

public class PedidosController implements Initializable, Observer {

    @FXML private TableView<Pedido> tabelaPedidos;
    @FXML private TableColumn<Pedido, String> colId, colDescricao, colStatus, colPagamento, colTotal;
    @FXML private TableColumn<Pedido, Void> colAcoes;
    @FXML private Label resumoDiaAbertos, resumoDiaPreparo, resumoDiaFinalizados, resumoDiaFaturamento;

    //lista observável que alimenta a TableView
    private ObservableList<Pedido> pedidos = FXCollections.observableArrayList();

    private final SorveteriaFacade facade = SorveteriaFacade.getInstance(null,null,null);;
    private MainController   mainController;

    // ---------------------------------INICIALIAZAÇÃO ---------------------------------
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColunas();
        tabelaPedidos.setItems(pedidos);
        recarregar();
    }

    //permite que o MainController passe uma referência de si mesmo
    public void setMainController(MainController mc) {
        this.mainController = mc;
    }

    // --------------------------------- OBSERVER ---------------------------------

    /*
    chamado pelo PedidoManagerSubject sempre que qualquer pedido é criado ou alterado.
    atualiza a tabela na thread JavaFX.
     */
    @Override
    public void update(Pedido pedido) {
        javafx.application.Platform.runLater(this::recarregar);
    }

    // --------------------------------- BOTOES DE FILTRO ---------------------------------

    @FXML public void filtrarTodos(ActionEvent e) {
        tabelaPedidos.setItems(pedidos);
    }

    @FXML public void filtrarAbertos(ActionEvent e) {
        tabelaPedidos.setItems(pedidos.filtered(p -> "Aberto".equals(resolverStatus(p))));
    }

    @FXML public void filtrarPreparo(ActionEvent e) {
        tabelaPedidos.setItems(pedidos.filtered(p -> "Em Preparo".equals(resolverStatus(p))));
    }

    @FXML public void filtrarProntos(ActionEvent e) {
        tabelaPedidos.setItems(pedidos.filtered(p -> "Pronto".equals(resolverStatus(p))));
    }


    // --------------------------------- METODOS AUX ---------------------------------

    // busca todos os pedidos via facade e atualiza tabela e resumo
    private void recarregar() {
        if (facade == null) return;
        pedidos.setAll(facade.getPedidos());
        atualizarResumo();
    }

    //traduz o objeto EstadoPedido em string legível p/ GUI
    private String resolverStatus(Pedido p) {
        if (p.getEstado() == null) return "Aberto";
        return switch (p.getEstado().getClass().getSimpleName()) {
            case "PedidoPreparoState" -> "Em Preparo";
            case "PedidoFinalizadoState" -> "Pronto";
            case "PedidoCanceladoState" -> "Cancelado";
            default -> "Aberto";
        };
    }

    //recalcula e exibe o painel lateral
    private void atualizarResumo() {
        long abertos = pedidos.stream().filter(p -> "Aberto"    .equals(resolverStatus(p))).count();
        long emPreparo = pedidos.stream().filter(p -> "Em Preparo".equals(resolverStatus(p))).count();
        long finalizados = pedidos.stream().filter(p -> "Pronto"    .equals(resolverStatus(p))).count();
        double faturamento = pedidos.stream()
                .filter(p -> "Pronto".equals(resolverStatus(p)))
                .mapToDouble(Pedido::calcularTotal).sum();
        resumoDiaAbertos.setText(String.valueOf(abertos));
        resumoDiaPreparo.setText(String.valueOf(emPreparo));
        resumoDiaFinalizados.setText(String.valueOf(finalizados));
        resumoDiaFaturamento.setText(String.format("R$ %.2f", faturamento));
    }

    //onfigura cell-value-factories e cell-factories de todas as colunas
    private void configurarColunas() {
        colId.setCellValueFactory(d ->
                new SimpleStringProperty("#" + String.format("%03d", d.getValue().getId())));
        colDescricao.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getDescricaoResumida()));
        colTotal.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("R$ %.2f", d.getValue().calcularTotal())));
        colPagamento.setCellValueFactory(d -> {
            String pg = d.getValue().getPagamento() == null ? "—"
                    : d.getValue().getPagamento().getClass().getSimpleName()
                    .replace("Pagamento", "");
            return new SimpleStringProperty(pg);
        });


        // coluna Status: exibe um Label com classe CSS de badge
        colStatus.setCellValueFactory(d ->
                new SimpleStringProperty(resolverStatus(d.getValue())));
        colStatus.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setGraphic(null); return; }
                Label badge = new Label(s);
                badge.getStyleClass().add(switch (s) {
                    case "Em Preparo" -> "badgePreparo";
                    case "Pronto"     -> "badgePronto";
                    case "Cancelado"  -> "badgeCancelado";
                    default           -> "badgeAberto";
                });
                setGraphic(badge);
                setText(null);
            }
        });

        // coluna Ações: botão contextual conforme status do pedido
        colAcoes.setCellFactory(c -> new TableCell<>() {
            final Button btn = new Button();
            { btn.getStyleClass().add("botaoSecundario"); }

            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                Pedido p  = getTableView().getItems().get(getIndex());
                String st = resolverStatus(p);
                btn.setText(switch (st) {
                    case "Aberto"     -> "Montar →";
                    case "Em Preparo" -> "Ver Preparo";
                    default           -> "—";
                });
                btn.setDisable("Pronto".equals(st) || "Cancelado".equals(st));
                btn.setOnAction(e -> {
                    if ("Aberto".equals(st) && mainController != null) {
                        // abre montagem com o pedido já existente
                        mainController.carregarMontagem(p);
                    } else if ("Em Preparo".equals(st) && mainController != null) {
                        try {
                            mainController.irPreparo();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
                setGraphic(btn);
            }
        });
    }

}
