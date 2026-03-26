package sorveteria.view.controller;

import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import sorveteria.composite.ProdutoComposite;
import sorveteria.model.*;
import sorveteria.facade.SorveteriaFacade;

import java.io.IOException;
import java.net.URL;
import java.util.EnumSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/*
 controller de montagem.fxml.
 Responsabilidades:
   - mapear os Buttons do FXML para os enums Base, Sabor e Adicional
   - atualizar o painel de resumo a cada seleção
   - ao confirmar montar um ProdutoComposite + ExtraLeafs e delegar ao Facade
   - abrir o diálogo de pagamento (pagamento.fxml) sem lógica de pagamento
 NÃO instancia DAOs, strategies nem manipula estados de pedido diretamente
 */

public class MontagemController implements Initializable{

    @FXML private Label   labelNumeroPedido;
    @FXML private Label   resumoBase, resumoSabor, resumoExtras;
    @FXML private Label   precoBase, precoAdicionais, precoTotal;
    @FXML private Label   labelStatus;
    @FXML private Button  btnAdicionarAoPedido, btnFinalizarPedido;
    @FXML private TextArea txtObservacoes;
    @FXML private Button btnCasquinha, btnCopoP, btnCopoG, btnMilkshake;
    @FXML private Button btnChocolate, btnMorango, btnBaunilha, btnCreme, btnMenta;
    @FXML private Button btnCaldaChocolate, btnCaldaMorango, btnGranulado, btnFrutas, btnKitkat, btnPacoca;

    private Base baseSelecionada  = null;
    private Sabor saborSelecionado = null;
    private Button btnBaseSelecionado  = null;
    private Button btnSaborSelecionado = null;
    private final Set<Adicional> adicionaisSelecionados = EnumSet.noneOf(Adicional.class);

    private final SorveteriaFacade facade=SorveteriaFacade.getInstance(null,null,null);
    private MainController mainController;
    private Pedido pedidoAtual;

    // adicione <VBox fx:id="listaItens" spacing="4"/> no resumo do montagem.fxml
    @FXML private VBox listaItens;
    @FXML private Spinner<Integer> spinnerQtd;

    //--------------------------------- INICIALIZAÇÃO ---------------------------------
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (spinnerQtd != null)
            spinnerQtd.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1));
        atualizarResumo();
        btnFinalizarPedido.setDisable(true);
    }

    public void setPedido(Pedido pedido) {
        this.pedidoAtual = pedido;
        labelNumeroPedido.setText("Pedido #" + String.format("%03d", pedido.getId()));
        //habilita finalizar se o pedido já tiver itens (reaberto para edição)
        btnFinalizarPedido.setDisable(pedido.getItens().isEmpty());
        atualizarListaItens();
    }

    //referência ao MainController para navegação após pagamento
    public void setMainController(MainController mc) {
        this.mainController = mc;
    }

    //--------------------------------- TELA ESQUERDA---------------------------------

    // BOTOES BASE cria item novo se pedido for vazio e seta a base, radiobutton
    @FXML
    public void selecionarBase(ActionEvent e) {
        if (btnBaseSelecionado != null) desselecionar(btnBaseSelecionado);
        btnBaseSelecionado = (Button) e.getSource();
        baseSelecionada = Base.porNome(btnBaseSelecionado.getText()); //cria enum pelo texto do botao
        selecionar(btnBaseSelecionado);
        atualizarResumo();
    }
    // BOTES SABOR cria item novo se pedido for vazio e seta o sabor selecionado, radiobutton
    @FXML
    public void selecionarSabor(ActionEvent e) {
        if (btnSaborSelecionado != null) desselecionar(btnSaborSelecionado);
        btnSaborSelecionado = (Button) e.getSource();
        saborSelecionado = Sabor.porNome(btnSaborSelecionado.getText());
        selecionar(btnSaborSelecionado);
        atualizarResumo();
    }
    // BOTOES ADICIONAIS, toggle
    @FXML
    public void toggleAdicional(ActionEvent e) {
        Button btn = (Button) e.getSource();
        Adicional extra = Adicional.porNome(btn.getText());
        if (extra == null) return;
        if (adicionaisSelecionados.remove(extra)) {   // para edição
            desselecionar(btn);
        } else {
            adicionaisSelecionados.add(extra);
            selecionar(btn);
        }
        atualizarResumo();
    }

    //--------------------------------- AÇÕES---------------------------------

    // ADICIONAR AO PEDIDO cria novo item
    @FXML public void adicionarAoPedido(ActionEvent e) {
        if (baseSelecionada == null || saborSelecionado == null) { //valida a selecao
            status("Escolha a base e o sabor antes de adicionar.");
            return;
        }

        // FIX: quantidade vem do Spinner — permite adicionar N do mesmo item
        int qty = spinnerQtd != null ? spinnerQtd.getValue() : 1;

        ProdutoComposite produto = new ProdutoComposite(
                baseSelecionada.getNome() + " · " + saborSelecionado.getNome(),
                saborSelecionado.getNome(),
                baseSelecionada.getPreco() + saborSelecionado.getPreco()
        );
        adicionaisSelecionados.forEach(a -> produto.addComponente(a.criarFolha()));

        facade.adicionarItem(pedidoAtual, new ItemPedido(qty, produto));

        System.out.println("Nome do produto: " + produto.getNome());
        System.out.println("toString do item: " + new ItemPedido(qty, produto).toString());
        status("Item adicionado: " + qty + "x " + produto.getNome());
        btnFinalizarPedido.setDisable(false);
        limparSelecao();
        atualizarListaItens();
    }

    // FINALIZAR PEDIDO abrir pop-up de pagamento
    @FXML private void abrirPagamento(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("pagamento.fxml"));
            Parent root = loader.load();

            PagamentoController ctrl = loader.getController();
            ctrl.setPedido(pedidoAtual); // abre o dialog de pagamento passando o pedido atual

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(btnFinalizarPedido.getScene().getWindow());
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            // após fechar o dialog, verifica se o pedido foi finalizado
            if (pedidoAtual.isPago() && mainController != null) {
                mainController.irPedidos();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void atualizarListaItens() {
        if (listaItens == null || pedidoAtual == null) return;
        listaItens.getChildren().clear();

        for (ItemPedido item : pedidoAtual.getItens()) {
            HBox linha = new HBox(8);
            linha.setAlignment(Pos.CENTER_LEFT);

            Label nome  = new Label(item.toString());
            nome.setWrapText(true);
            nome.getStyleClass().add("resumoItem");
            HBox.setHgrow(nome, Priority.ALWAYS);

            Button remover = new Button("×");
            remover.getStyleClass().add("botaoCancelar");
            remover.setOnAction(ev -> {
                facade.removerItem(pedidoAtual, item);
                atualizarListaItens();
                btnFinalizarPedido.setDisable(pedidoAtual.getItens().isEmpty());
                atualizarResumo();
            });

            linha.getChildren().addAll(nome, remover);
            listaItens.getChildren().add(linha);
        }
    }

    //CANCELAR PEDIDO cancela pedido e vai pra lista de pedidos
    @FXML public void cancelarPedido(ActionEvent e) throws Exception {
        if (pedidoAtual != null) {
            facade.cancelarPedido(pedidoAtual);
        }
        if (mainController != null) {
            mainController.irPedidos();
        }
    }

    //--------------------------------- RESUMO E AUXILIARES ---------------------------------
    private void atualizarResumo() {
        resumoBase.setText(baseSelecionada  != null ? baseSelecionada.getNome()  : "—");
        resumoSabor.setText(saborSelecionado != null ? saborSelecionado.getNome() : "—");
        resumoExtras.setText(adicionaisSelecionados.isEmpty() ? "Nenhum"
                : adicionaisSelecionados.stream()
                .map(Adicional::getNome)
                .collect(Collectors.joining(", ")));

        double vBase  = (baseSelecionada  != null ? baseSelecionada.getPreco()  : 0)
                + (saborSelecionado != null ? saborSelecionado.getPreco() : 0);
        double vExtra = adicionaisSelecionados.stream()
                .mapToDouble(Adicional::getPreco).sum();

        precoBase.setText(fmt(vBase));
        precoAdicionais.setText(fmt(vExtra));
        precoTotal.setText(fmt(vBase + vExtra));
        status("");
    }

    private void status(String msg) {
        if (labelStatus != null) labelStatus.setText(msg);
    }
    private void selecionar(Button b)   { b.getStyleClass().setAll("botaoOpcaoSelecionado"); }
    private void desselecionar(Button b){ b.getStyleClass().setAll("botaoOpcao"); }
    private void dessselecionarBotaoAdicional(Button btn) {
        if (btn != null) desselecionar(btn);
    }
    private String fmt(double v) { return String.format("R$ %.2f", v); }

    private void limparSelecao() {
        if (btnBaseSelecionado  != null) { desselecionar(btnBaseSelecionado);  btnBaseSelecionado  = null; }
        if (btnSaborSelecionado != null) { desselecionar(btnSaborSelecionado); btnSaborSelecionado = null; }
        baseSelecionada  = null;
        saborSelecionado = null;

        // Desseleciona cada botão de adicional pelo nome
        for (Button b : new Button[]{btnCaldaChocolate, btnCaldaMorango, btnGranulado,
                btnFrutas, btnKitkat, btnPacoca})
            if (b != null) desselecionar(b);
        adicionaisSelecionados.clear();
        if (spinnerQtd   != null) spinnerQtd.getValueFactory().setValue(1);
        if (txtObservacoes != null) txtObservacoes.clear();
        atualizarResumo();
    }
}
