package sorveteria.model;

import sorveteria.banco.EstoqueManagerSingleton;
import sorveteria.composite.ProdutoComposite;
import sorveteria.state.EstadoPedido;
import sorveteria.state.PedidoAbertoState;
import sorveteria.strategy.PagamentoStrategy;

import java.util.ArrayList;
import java.util.List;


public class Pedido {
    private int id;
    private final List<ItemPedido> itens = new ArrayList<>();
    private PagamentoStrategy pagamento;
    private EstadoPedido estado;
    private String observacao = "";
    public void setObservacao(String obs) { this.observacao = obs; }
    public String getObservacao()         { return observacao; }

    public Pedido() {
        this.estado = new PedidoAbertoState();
    }
    private boolean pago=false;

    public int getId()        { return id; }
    public void setId(int id) { this.id = id; }

    public void addItem(ItemPedido item) {
        estado.adicionarItem(this);
        itens.add(item);
    }

    public void removeItem(ItemPedido item) { itens.remove(item); }
    public List<ItemPedido> getItens()      { return itens; }

    public void setPagamento(PagamentoStrategy pagamento) {
        this.pagamento = pagamento;
    }

    public double calcularTotal() {
        return itens.stream().mapToDouble(ItemPedido::calcularSubtotal).sum();
    }
    
    public void pagar() {
        if (pagamento == null)
            throw new IllegalStateException("Defina uma forma de pagamento antes!");

        pagamento.pagar(calcularTotal());

        EstoqueManagerSingleton estoque = EstoqueManagerSingleton.getInstance();

        for (ItemPedido itemPedido : itens) {
            // Pega o produto (que é um ProdutoComposite)
            ProdutoComposite sorvete = (ProdutoComposite) itemPedido.getItem();
            // Pega o sabor do sorvete
            String sabor = sorvete.getSabor();
            // Dá baixa no estoque usando o sabor
            estoque.baixarEstoque(sabor, itemPedido.getQuantidade());
        }

        this.pago=true;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public void iniciarPreparo() {
        estado.iniciarPreparo(this);
    }

    public void finalizar() {
        estado.finalizar(this);
    }

    public void cancelar() {
        estado.cancelar(this);
    }

    // USADO NOS CONTROLLERS

    public boolean isPago(){return this.pago;}
    public void setPago(boolean b) {this.pago=b;}

    public String getDescricaoResumida() {
        StringBuilder descricao= new StringBuilder();
        for (ItemPedido pedido : itens){
            descricao.append(pedido.toString()).append("\n");
        }
        return descricao.toString();
    }

    public Object getPagamento() { return pagamento;}
}