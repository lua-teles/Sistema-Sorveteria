package sorveteria.observer;

import sorveteria.composite.ProdutoComposite;
import sorveteria.model.ItemPedido;
import sorveteria.model.Pedido;

public class RecepcaoTelaObserver implements Observer {

    @Override
    public void update(Pedido pedido) {
        System.out.println("\n[TELA DE RECEPÇÃO]");
        System.out.println("  Pedido ID : " + pedido.getId());
        System.out.println("  Estado    : " + pedido.getEstado().getClass().getSimpleName());
        System.out.println("  Itens:");
        for (ItemPedido item : pedido.getItens()) {
            if (item.getItem() instanceof ProdutoComposite sorvete) {
                System.out.printf("    - %dx %s (%s) | R$ %.2f%n",
                        item.getQuantidade(),
                        sorvete.getSabor(),
                        sorvete.getBase(),    // NOVO
                        item.calcularSubtotal());
            } else {
                System.out.printf("    - %dx %s | R$ %.2f%n",
                        item.getQuantidade(),
                        item.getItem().getNome(),
                        item.calcularSubtotal());
            }
        }
        System.out.printf("  TOTAL     : R$ %.2f%n", pedido.calcularTotal());
        System.out.println("──────────────────────────────────────────────");
    }
}