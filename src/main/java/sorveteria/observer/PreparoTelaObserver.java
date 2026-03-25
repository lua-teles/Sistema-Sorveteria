package sorveteria.observer;

import sorveteria.composite.ProdutoComposite;
import sorveteria.model.ItemPedido;
import sorveteria.model.Pedido;

public class PreparoTelaObserver implements Observer {

    @Override
    public void update(Pedido pedido) {
        System.out.println("\n[TELA DE PREPARO]");
        System.out.println("  Pedido ID : " + pedido.getId());
        System.out.println("  Estado    : " + pedido.getEstado().getClass().getSimpleName());
        System.out.println("  Itens:");
        for (ItemPedido item : pedido.getItens()) {
            if (item.getItem() instanceof ProdutoComposite sorvete) {
                System.out.printf("    - %dx %s | Base: %s | Sabor: %s%n",
                        item.getQuantidade(),
                        sorvete.getNome(),
                        sorvete.getBase(),   // NOVO
                        sorvete.getSabor());
                if (!sorvete.getComponentes().isEmpty()) {
                    System.out.print("      Adicionais: ");
                    sorvete.getComponentes()
                            .forEach(c -> System.out.print(c.getNome() + " "));
                    System.out.println();
                }
            } else {
                System.out.printf("    - %dx %s  (R$ %.2f)%n",
                        item.getQuantidade(),
                        item.getItem().getNome(),
                        item.calcularSubtotal());
            }
        }
        // Observação
        String obs = pedido.getObservacao();
        if (obs != null && !obs.isBlank()) {
            System.out.println("  Observação: " + obs);
        }
        System.out.println("──────────────────────────────────────────────");
    }
}