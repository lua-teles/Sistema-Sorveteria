package sorveteria.observer;

import sorveteria.model.Pedido;

// muito obrigada, hânnia, vc é uma amiga! @raissa
public class PreparoTelaObserver implements Observer{

    @Override
    public void update(Pedido pedido) {
        System.out.println("\n[TELA DE PREPARO]\n");
        System.out.println("  Pedido ID  : " + pedido.getId());
        System.out.println("  Estado     : " + pedido.getEstado().getClass().getSimpleName());
        System.out.println("  Itens:");
        pedido.getItens().forEach(item ->
                System.out.printf("    - %dx %s  (R$ %.2f)%n",
                        item.getQuantidade(),
                        item.getItem().getNome(),
                        item.calcularSubtotal())
        );
        System.out.println("──────────────────────────────────────────────");
    }
}
