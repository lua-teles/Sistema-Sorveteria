package sorveteria.model;

import sorveteria.composite.ProdutoComponent;

public class ItemPedido {
    private final int quantidade;
    private final ProdutoComponent item;

    public ItemPedido(int quantidade, ProdutoComponent item) {
        this.quantidade = quantidade;
        this.item       = item;
    }

    public double calcularSubtotal() {
        return quantidade * item.getPreco();
    }

    public ProdutoComponent getItem()  { return item; }
    public int getQuantidade()         { return quantidade; }
    @Override
    public String toString() {
        return quantidade + "x " + item.getNome()
                + " — R$ " + String.format("%.2f", calcularSubtotal());
    }
}
