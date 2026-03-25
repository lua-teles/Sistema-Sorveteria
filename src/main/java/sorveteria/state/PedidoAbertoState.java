package sorveteria.state;
import sorveteria.model.Pedido;
import sorveteria.state.PedidoPreparoState;
public class PedidoAbertoState implements EstadoPedido {
    @Override
    public void adicionarItem(Pedido pedido) {
        System.out.println("Item adicionado.");
    }

    @Override
    public void iniciarPreparo(Pedido pedido) {
        System.out.println("Preparo iniciado.");
        pedido.setEstado(new PedidoPreparoState());
    }

    @Override
    public void finalizar(Pedido pedido) {
        System.out.println("Seu pedido ainda não começou a ser preparado");
    }

    @Override
    public void cancelar(Pedido pedido) {
        System.out.println("Pedido cancelado");
    }
}
