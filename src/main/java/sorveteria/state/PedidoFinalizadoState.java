package sorveteria.state;
import sorveteria.model.Pedido;

public class PedidoFinalizadoState implements EstadoPedido {
    @Override
    public void adicionarItem(Pedido pedido) {
        throw new IllegalStateException("Não é possível adicionar itens. Pedido finalizado.");
    }

    @Override
    public void iniciarPreparo(Pedido pedido) {
        System.out.println("Pedido já foi finalizado");
    }

    @Override
    public void finalizar(Pedido pedido) {
        System.out.println("Pedido já foi finalizado");
    }

    @Override
    public void cancelar(Pedido pedido) {
        System.out.println("Não é possível cancelar");
    }
}
