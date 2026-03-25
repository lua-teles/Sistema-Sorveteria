package sorveteria.model;

import sorveteria.banco.EstoqueManagerSingleton;
import sorveteria.strategy.PagamentoStrategy;

import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int id;
    private final List<ItemPedido> itens = new ArrayList<>();
    private PagamentoStrategy pagamento;

    public Pedido() {}

    public int getId()        { return id; }
    public void setId(int id) { this.id = id; }

    public void addItem(ItemPedido item)    { itens.add(item); }
    public void removeItem(ItemPedido item) { itens.remove(item); }
    public List<ItemPedido> getItens()      { return itens; }

    public void setPagamento(PagamentoStrategy pagamento) {
        this.pagamento = pagamento;
    }

    public double calcularTotal() {
        return itens.stream().mapToDouble(ItemPedido::calcularSubtotal).sum();
    }

    // 🔥 Paga e baixa estoque corretamente
    public void pagar(double valor) {
        if (pagamento == null)
            throw new IllegalStateException("Defina uma forma de pagamento antes!");

        pagamento.pagar(valor);

        EstoqueManagerSingleton estoque = EstoqueManagerSingleton.getInstance();

        for (ItemPedido itemPedido : itens) {
            String nome = itemPedido.getItem().getNome(); // agora retorna "Chocolate"
            estoque.baixarEstoque(nome, itemPedido.getQuantidade());
        }
    }
}