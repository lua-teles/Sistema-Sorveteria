package sorveteria.state;
import sorveteria.model.Pedido;

public interface EstadoPedido {
    public void adicionarItem(Pedido pedido);
    public void iniciarPreparo(Pedido pedido);
    public void finalizar(Pedido pedido);
    public void cancelar (Pedido pedido);
}
