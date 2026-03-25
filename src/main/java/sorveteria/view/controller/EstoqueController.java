package sorveteria.view.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import sorveteria.facade.SorveteriaFacade;
import sorveteria.model.Ingrediente;

import java.net.URL;
import java.util.ResourceBundle;

// BOTAO ADICIONAR INGREDIENTE SEM ON ACTION

/*
controller da tela de controle de estoque (estoque.fxml).

responsabilidades:
- exibir a tabela de ingredientes carregada via Facade.
- permitir busca ao vivo pelo nome.
- permitir reposição (+) e redução (−) inline na tabela via Spinner.
- exibir painel de alertas para ingredientes com estoque baixo.

toda gravação de estoque é feita via Facade, o controller não chama IngredienteDAO diretamente.
 */

public class EstoqueController implements Initializable {

    private static final int LIMITE_BAIXO = 5;

    @FXML private TableView<Ingrediente> tabelaEstoque;
    @FXML private TableColumn<Ingrediente, String>  colNome, colStatus, colUnidade;
    @FXML private TableColumn<Ingrediente, Integer> colQuantidade;
    @FXML private TableColumn<Ingrediente, Void>    colAcoes;
    @FXML private TextField campoBusca;
    @FXML private HBox painelAlertas;
    @FXML private Label labelAlertas;

    private SorveteriaFacade facade = SorveteriaFacade.getInstance(null,null,null);

    // FIX: campos para adicionar novo ingrediente inline (sem dialog)
    // Adicione no estoque.fxml:
    //   <TextField fx:id="campoNovoNome" promptText="Nome do ingrediente" prefWidth="180"/>
    //   <TextField fx:id="campoNovaQtd"  promptText="Qtd." prefWidth="70"/>
    //   <Button text="+ Adicionar" onAction="#adicionarIngrediente" styleClass="botaoNovoPedido"/>
    @FXML private TextField campoNovoNome;
    @FXML private TextField campoNovaQtd;

    //lista completa, usada como base para o filtro de busca. */
    private ObservableList<Ingrediente> listaCompleta;

    // --------------------------------- INICIALIAZAÇÃO ---------------------------------

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColunas();
        carregarIngredientes();
        // filtro ao vivo aplica busca a cada tecla digitada
        campoBusca.textProperty().addListener((obs, antigo, novo) -> aplicarFiltro(novo));
    }

    // --------------------------------- HANDLERS ---------------------------------


    @FXML
    public void adicionarIngrediente() {
        if (campoNovoNome == null || campoNovoNome.getText().isBlank()) return;
        try {
            int qtd = Integer.parseInt(campoNovaQtd.getText().trim());
            Ingrediente novo = new Ingrediente(0, campoNovoNome.getText().trim(), qtd);
            facade.inserirIngrediente(novo);   // FIX: persiste no banco via Facade
            listaCompleta.add(novo);
            campoNovoNome.clear();
            campoNovaQtd .clear();
            atualizarAlertas();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /*
     filtra a tabela em tempo real conforme o texto digitado no campo de busca.
     vinculado ao listener no initialize() não precisa de @FXML.
    */
    private void aplicarFiltro(String texto) {
        if (texto == null || texto.isBlank()) {
            tabelaEstoque.setItems(listaCompleta);
        } else {
            String lower = texto.toLowerCase();
            tabelaEstoque.setItems(
                    listaCompleta.filtered(i -> i.getNome().toLowerCase().contains(lower)));
        }
    }
    /*
     vinculado ao onKeyReleased do campoBusca no FXML via "#filtrarIngredientes".
     redireciona para o listener interno.
     */
    @FXML public void filtrarIngredientes(KeyEvent e) {
        aplicarFiltro(campoBusca.getText());
    }

    //--------------------------------- AUX ---------------------------------

    //carrega todos os ingredientes do banco via facade e popula a tabela
    private void carregarIngredientes() {
        listaCompleta = FXCollections.observableArrayList(facade.listarIngredientes());
        tabelaEstoque.setItems(listaCompleta);
        atualizarAlertas();
    }

    // atualiza o painel de alertas contando quantos ingredientes estão com
    // quantidade igual ou abaixo do limite definido.

    private void atualizarAlertas() {
        long baixos = listaCompleta.stream()
                .filter(i -> i.getQuantidade() <= LIMITE_BAIXO).count();
        painelAlertas.setVisible(baixos > 0);
        painelAlertas.setManaged(baixos > 0);
        if (baixos > 0) {
            labelAlertas.setText(baixos + " ingrediente(s) com estoque baixo ou zerado.");
        }
    }

    /*
     configura todas as colunas da TableView:
       - colNome, colQuantidade, colUnidade: simples value factories.
       - colStatus: badge colorido conforme quantidade.
       - colAcoes: spinner inline + botões "+" e "−" que persistem via Facade.
     */
    private void configurarColunas() {
        colNome.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNome()));
        colQuantidade.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getQuantidade()).asObject());
        colUnidade.setCellValueFactory(d -> new SimpleStringProperty("un."));

        colStatus.setCellValueFactory(d -> {
            int q = d.getValue().getQuantidade();
            return new SimpleStringProperty(q == 0 ? "Sem estoque" : q <= LIMITE_BAIXO ? "Baixo" : "OK");
        });
        colStatus.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setGraphic(null); return; }
                Label badge = new Label(s);
                badge.getStyleClass().add(switch (s) {
                    case "Sem estoque" -> "badgeCancelado";
                    case "Baixo"       -> "badgeAberto";
                    default            -> "badgePronto";
                });
                setGraphic(badge); setText(null);
            }
        });

        // Coluna de ações: Spinner + botão repor + botão reduzir + botão deletar
        colAcoes.setCellFactory(c -> new TableCell<>() {
            final Spinner<Integer> spinner    = new Spinner<>(1, 999, 1);
            final Button           btnAdd     = new Button("+");
            final Button           btnRem     = new Button("−");
            final Button           btnDeletar = new Button("×");
            final HBox             box        = new HBox(6,
                    new Label("Qtd:"), spinner, btnAdd, btnRem, btnDeletar);

            {
                spinner.setEditable(true);
                spinner.setPrefWidth(72);
                spinner.getStyleClass().add("spinnerQtd");
                btnAdd    .getStyleClass().add("botaoSecundario");
                btnRem    .getStyleClass().add("botaoCancelar");
                btnDeletar.getStyleClass().add("botaoCancelar");
                btnDeletar.setText("×");
                box.setAlignment(Pos.CENTER_LEFT);
                box.setPadding(new Insets(2, 0, 2, 0));

                btnAdd.setOnAction(e -> ajustar(true));
                btnRem.setOnAction(e -> ajustar(false));

                // FIX: deleta o ingrediente do banco e da lista
                btnDeletar.setOnAction(e -> {
                    Ingrediente ing = getTableView().getItems().get(getIndex());
                    facade.deletarIngrediente(ing);
                    listaCompleta.remove(ing);
                    atualizarAlertas();
                });
            }

            private void ajustar(boolean somar) {
                Ingrediente ing = getTableView().getItems().get(getIndex());
                int nova = somar
                        ? ing.getQuantidade() + spinner.getValue()
                        : ing.getQuantidade() - spinner.getValue();
                if (nova < 0) return;
                ing.setQuantidade(nova);
                facade.atualizarEstoque(ing);
                tabelaEstoque.refresh();
                atualizarAlertas();
            }

            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });
    }
}
