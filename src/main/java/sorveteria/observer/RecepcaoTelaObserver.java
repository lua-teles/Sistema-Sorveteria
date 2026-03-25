package sorveteria.observer;

import sorveteria.model.Pedido;

public class RecepcaoTelaObserver implements Observer {

    @Override
    public void update(Pedido pedido) {
        System.out.println("\n[TELA DE RECEPÇÃO]\n");
        System.out.println("  Pedido ID  : " + pedido.getId());
        System.out.println("  Estado     : " + pedido.getEstado().getClass().getSimpleName());
        System.out.printf ("  Total      : R$ %.2f%n", pedido.calcularTotal());
        System.out.println("──────────────────────────────────────────────");
    }
}
