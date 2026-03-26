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
    public String getObservacao() { return observacao; }

    private String descricaoPersistida = "";
    private double totalPersistido = 0.0;

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
        if (!itens.isEmpty())
            return itens.stream().mapToDouble(ItemPedido::calcularSubtotal).sum();
        return totalPersistido; // fallback para pedidos carregados do banco
    }
    
    public void pagar() {
        if (pagamento == null)
            throw new IllegalStateException("Defina uma forma de pagamento antes!");

        pagamento.pagar(calcularTotal());

        EstoqueManagerSingleton estoque = EstoqueManagerSingleton.getInstance();

        for (ItemPedido itemPedido : itens) {
            try {
                ProdutoComposite sorvete = (ProdutoComposite) itemPedido.getItem();
                estoque.baixarEstoque(sorvete.getSabor(), itemPedido.getQuantidade());
            } catch (RuntimeException e) {
                // ingrediente não cadastrado: loga mas não interrompe o pagamento
                System.out.println("[ESTOQUE] " + e.getMessage());
            }
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

    public void setDescricaoPersistida(String d) { this.descricaoPersistida = d; }

    public void setTotalPersistido(double t)     { this.totalPersistido = t; }

    // USADO NOS CONTROLLERS

    public boolean isPago(){return this.pago;}
    public void setPago(boolean b) {this.pago=b;}

    public String getDescricaoResumida() {
        // se tem itens em memória, usa eles (pedido recém-montado)
        if (!itens.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ItemPedido i : itens) sb.append(i.toString()).append("\n");
            return sb.toString().trim();
        }
        // senão usa o que veio do banco (pedido carregado na inicialização)
        return descricaoPersistida != null && !descricaoPersistida.isBlank()
                ? descricaoPersistida : "—";
    }

    public Object getPagamento() { return pagamento;}
}