package sorveteria.facade;

import sorveteria.banco.IngredienteDAO;
import sorveteria.banco.PedidoDAO;
import sorveteria.model.Ingrediente;
import sorveteria.model.ItemPedido;
import sorveteria.model.Pedido;
import sorveteria.observer.PedidoManagerSubject;
import sorveteria.strategy.PagamentoStrategy;

import java.util.List;

public class SorveteriaFacade {

    private static SorveteriaFacade instancia;

    private final PedidoManagerSubject pedidoManager;
    private final PedidoDAO            pedidoDAO;
    private final IngredienteDAO ingredienteDAO;

    private SorveteriaFacade(PedidoManagerSubject pedidoManager, PedidoDAO pedidoDAO, IngredienteDAO ingredienteDAO) {
        this.pedidoManager = pedidoManager;
        this.pedidoDAO     = pedidoDAO;
        this.ingredienteDAO = ingredienteDAO;
    }

    public static SorveteriaFacade getInstance(PedidoManagerSubject pedidoManager, PedidoDAO pedidoDAO, IngredienteDAO ingredienteDAO) {
        if (instancia == null) {
            instancia = new SorveteriaFacade(pedidoManager, pedidoDAO, ingredienteDAO);
        }
        return instancia;
    }

    public PedidoManagerSubject getPedidoManager() {
        return pedidoManager;
    }

    public PedidoDAO getPedidoDAO() {
        return pedidoDAO;
    }
    public List<Pedido> getPedidos() {
        return pedidoDAO.listarPedidos();
    }

    public Pedido criarPedido() {
        Pedido pedido = new Pedido();

        pedidoDAO.salvar(pedido);
        pedidoManager.addPedido(pedido);


        System.out.println("[FACADE] Pedido criado - ID: " + pedido.getId());
        return pedido;
    }

    public void adicionarItem(Pedido pedido, ItemPedido item) {
        pedido.addItem(item);
        pedidoManager.notifyObservers(pedido);
        pedidoDAO.salvar(pedido);

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

    // USADO NOS CONTROLLERS

    public List<Ingrediente> listarIngredientes() {
        return ingredienteDAO.listarTodos();
    }

    public void atualizarEstoque(Ingrediente ingrediente) {
        ingredienteDAO.atualizarQuantidade(ingrediente);
        System.out.println("[FACADE] Estoque atualizado: " + ingrediente.getNome() + " → " + ingrediente.getQuantidade());
    }

    public void processarPagamento(Pedido pedido, PagamentoStrategy pagamentoStrategy){
        pedido.setPagamento(pagamentoStrategy);
        pedido.pagar();

        pedidoManager.notifyObservers(pedido);
        pedidoDAO.atualizarStatus(pedido.getId(), "FINALIZADO");
    }
}