package sorveteria.facade;

import sorveteria.banco.PedidoDAO;
import sorveteria.model.ItemPedido;
import sorveteria.model.Pedido;
import sorveteria.observer.PedidoManagerSubject;

import java.util.List;

public class SorveteriaFacade {

    private final PedidoManagerSubject pedidoManager;
    private final PedidoDAO            pedidoDAO;

    public SorveteriaFacade(PedidoManagerSubject pedidoManager, PedidoDAO pedidoDAO) {
        this.pedidoManager = pedidoManager;
        this.pedidoDAO     = pedidoDAO;
    }

    public PedidoManagerSubject getPedidoManager() {
        return pedidoManager;
    }

    public PedidoDAO getPedidoDAO() {
        return pedidoDAO;
    }
    public List<Pedido> getPedidos() {
        return pedidoManager.getPedidos();
    }

    public Pedido criarPedido() {
        Pedido pedido = new Pedido();

        pedidoManager.addPedido(pedido);
        pedidoDAO.salvar(pedido);

        System.out.println("[FACADE] Pedido criado - ID: " + pedido.getId());
        return pedido;
    }

    public void adicionarItem(Pedido pedido, ItemPedido item) {
        pedido.addItem(item);
        pedidoManager.notifyObservers(pedido);

        System.out.println("[FACADE] Item adicionado ao pedido ID: " + pedido.getId());
    }

    public void iniciarPreparo(Pedido pedido) {
        pedido.iniciarPreparo();
        pedidoManager.notifyObservers(pedido);
        pedidoDAO.atualizarStatus(pedido.getId(), "PREPARO");

        System.out.println("[FACADE] Preparo iniciado - pedido ID: " + pedido.getId());
    }

    public void finalizarPedido(Pedido pedido) {
        pedido.finalizar();
        pedidoManager.notifyObservers(pedido);
        pedidoDAO.atualizarStatus(pedido.getId(), "FINALIZADO");

        System.out.println("[FACADE] Pedido finalizado - ID: " + pedido.getId());
    }

    public void cancelarPedido(Pedido pedido) {
        pedido.cancelar();
        pedidoManager.notifyObservers(pedido);
        pedidoDAO.atualizarStatus(pedido.getId(), "CANCELADO");

        System.out.println("[FACADE] Pedido cancelado - ID: " + pedido.getId());
    }
}
