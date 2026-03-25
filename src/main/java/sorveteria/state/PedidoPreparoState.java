package sorveteria.state;
import sorveteria.model.Pedido;
import sorveteria.state.PedidoFinalizadoState;
import java.sql.SQLOutput;

public class PedidoPreparoState implements EstadoPedido {

    @Override
    public void adicionarItem(Pedido pedido) {
        throw new IllegalStateException("Não é possível adicionar itens. Pedido em preparo.");
    }

    @Override
    public void iniciarPreparo(Pedido pedido) {
        System.out.println("Seu pedido já está em preparo");
    }

    @Override
    public void finalizar(Pedido pedido) {
        System.out.println("Pedido finalizado.");
        pedido.setEstado(new PedidoFinalizadoState());
    }

    @Override
    public void cancelar(Pedido pedido) {
        System.out.println("Pedido cancelado");
    }
}
