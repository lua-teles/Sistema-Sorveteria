package sorveteria.view.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sorveteria.facade.SorveteriaFacade;
import sorveteria.model.ItemPedido;
import sorveteria.model.Pedido;
import sorveteria.strategy.PagamentoCartao;
import sorveteria.strategy.PagamentoDinheiro;
import sorveteria.strategy.PagamentoPix;

import java.net.URL;
import java.util.ResourceBundle;

/*
controller de pagamento.fxml
responsabilidades:
  - exibir o resumo do pedido (itens e total)
  - permitir escolha da forma de pagamento
  - calcular troco quando a forma for Dinheiro ?
  - delegar o processamento ao Facade ao confirmar.
NAO baixa estoque diretamente isso é feito dentro de Pedido.pagar() que é chamado pelo Facade.
 */

public class PagamentoController  implements Initializable {
    @FXML
    private Label dialogNumeroPedido, dialogTotal, dialogItens, totalComDesconto, labelTroco;
    @FXML private RadioButton radioPix, radioCartao, radioDinheiro;
    @FXML private HBox painelTroco;
    @FXML private TextField campoTroco;
    @FXML private Button btnConfirmarPagamento;

    private final SorveteriaFacade facade= SorveteriaFacade.getInstance(null,null,null);;
    private Pedido pedidoAtual;
    private double valorOriginal;


    // ---------------------------------INICIALIAZAÇÃO ---------------------------------
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnConfirmarPagamento.setDisable(true);
        if (dialogItens != null) dialogItens.setWrapText(true);
    }

    // ---------------------------------TEXTOS ---------------------------------

    //recebe o pedido a ser pago e preenche os campos de texto do dialogo
    //chamado pelo MontagemController antes de exibir o stage
    public void setPedido(Pedido pedido) {
        this.pedidoAtual  = pedido;
        this.valorOriginal = pedido.calcularTotal();
        dialogNumeroPedido.setText("Pedido #" + String.format("%03d", pedido.getId()));
        dialogTotal       .setText(fmt(valorOriginal));
        totalComDesconto  .setText(fmt(valorOriginal)); // sem desconto até escolher Pix
        // monta lista de itens como texto descritivo
        if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ItemPedido i : pedido.getItens())
                sb.append(i.toString()).append("\n");  // itemPedido.toString() corrigido
            dialogItens.setText(sb.toString().trim());
        } else {
            dialogItens.setText("—");
        }
    }

    // ---------------------------------PAGAMENTO ---------------------------------

    // escolher entre pix dinheiro ou cartao com strategy

    // chamado por qualquer radiobutton via onAction="#atualizarTotal"
    @FXML public void atualizarTotal(ActionEvent e) {
        boolean ehDinheiro = radioDinheiro.isSelected();
        painelTroco.setVisible(ehDinheiro);
        painelTroco.setManaged(ehDinheiro);

        // pix tem 5% de desconto; outros mantêm o valor original
        double total = radioPix.isSelected() ? valorOriginal * 0.95 : valorOriginal;
        totalComDesconto.setText(fmt(total));

        btnConfirmarPagamento.setDisable(false);
    }

    //calcula e exibe o troco em tempo real conforme o usuário digita
    //vinculado ao campo de troco via onKeyReleased="#calcularTroco"
    @FXML public void calcularTroco(KeyEvent e) {
        try {
            double entregue = Double.parseDouble(campoTroco.getText().replace(",", "."));
            double troco    = entregue - valorOriginal;
            labelTroco.setText(troco >= 0 ? fmt(troco) : "Insuficiente");
        } catch (NumberFormatException ex) {
            labelTroco.setText("R$ 0,00");
        }
    }

    //metodo voltar
    @FXML public void fecharDialog(ActionEvent e) {
        ((Stage) btnConfirmarPagamento.getScene().getWindow()).close();
    }

    /*
    metodo confirmar pagamento
    -cria a strategy correta conforme o RadioButton selecionado.
    -delega ao Facade (que aplica desconto se Pix, baixa estoque e persiste).
    -marca o pedido como pago e fecha o diálogo.
    */
    @FXML public void confirmarPagamento(ActionEvent e) {

        if (radioPix.isSelected()) {
            facade.processarPagamento(pedidoAtual, new PagamentoPix());
        } else if (radioCartao.isSelected()) {
            facade.processarPagamento(pedidoAtual, new PagamentoCartao());
        } else if (radioDinheiro.isSelected()) {
            facade.processarPagamento(pedidoAtual, new PagamentoDinheiro());
        } else {
            return;
        }

        pedidoAtual.setPago(true);
        fecharDialog(e);
    }

    // auxiliar, formatação
    private String fmt(double v) { return String.format("R$ %.2f", v); }


}
