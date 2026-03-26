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
    private final PedidoDAO pedidoDAO;
    private final IngredienteDAO ingredienteDAO;

    public SorveteriaFacade(PedidoManagerSubject pedidoManager, PedidoDAO pedidoDAO, IngredienteDAO ingredienteDAO) {
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

    public List<Pedido> getPedidos() {
        return pedidoDAO.listarPedidos();
    }

    // cria um pedido novo faz um único INSERT via salvar() e registra no manager.
    public Pedido criarPedido() {
        Pedido pedido = new Pedido();
        pedidoDAO.salvar(pedido);          // INSERT único com status/total/descricao
        pedidoManager.addPedido(pedido);
        System.out.println("[FACADE] Pedido criado - ID: " + pedido.getId());
        return pedido;
    }

    // Adiciona item ao pedido e faz UPDATE (não INSERT) para persistir descricao/total.
    public void adicionarItem(Pedido pedido, ItemPedido item) {
        pedido.addItem(item);
        pedidoDAO.atualizarDescricao(pedido);   // UPDATE — evita duplicar o pedido no banco
        pedidoManager.notifyObservers(pedido);
        System.out.println("[FACADE] Item adicionado ao pedido ID: " + pedido.getId());
    }

    // NOVO:remove item
    public void removerItem(Pedido pedido, ItemPedido item) {
        pedido.removeItem(item);
        pedidoManager.notifyObservers(pedido);
    }

    // Registra observação no pedido e notifica observers.
    public void adicionarObservacao(Pedido pedido, String obs) {
        if (obs != null && !obs.isBlank()) {
            pedido.setObservacao(obs);
            pedidoManager.notifyObservers(pedido);
            System.out.println("[FACADE] Observação registrada no pedido ID: " + pedido.getId());
        }
    }

    public void iniciarPreparo(Pedido pedido) {
        pedido.iniciarPreparo();
        pedidoDAO.atualizarStatus(pedido.getId(), "PREPARO");
        pedidoManager.notifyObservers(pedido);
        System.out.println("[FACADE] Preparo iniciado - pedido ID: " + pedido.getId());
    }

    public void finalizarPedido(Pedido pedido) {
        pedido.finalizar();
        pedidoDAO.atualizarStatus(pedido.getId(), "FINALIZADO");
        pedidoManager.notifyObservers(pedido);
        System.out.println("[FACADE] Pedido finalizado - ID: " + pedido.getId());
    }

    public void cancelarPedido(Pedido pedido) {
        pedido.cancelar();
        pedidoDAO.atualizarStatus(pedido.getId(), "CANCELADO");
        System.out.println("[FACADE] Pedido cancelado - ID: " + pedido.getId());
        deletarPedido(pedido);
    }

    public void deletarPedido(Pedido pedido) {
        pedidoDAO.deletar(pedido.getId());
        pedidoManager.getPedidos().remove(pedido);
        pedidoManager.notifyObservers(pedido);
    }

    // USADO NOS CONTROLLERS

    public List<Ingrediente> listarIngredientes() {
        return ingredienteDAO.listarTodos();
    }

    public void atualizarEstoque(Ingrediente ingrediente) {
        ingredienteDAO.atualizarQuantidade(ingrediente);
        System.out.println("[FACADE] Estoque atualizado: " + ingrediente.getNome() + " → " + ingrediente.getQuantidade());
    }

    public void inserirIngrediente(Ingrediente ing) {
        ingredienteDAO.inserir(ing);
        System.out.println("[FACADE] Ingrediente inserido: " + ing.getNome());
    }

    public void deletarIngrediente(Ingrediente ing) {
        ingredienteDAO.deletar(ing.getId());
        System.out.println("[FACADE] Ingrediente deletado: " + ing.getNome());
    }

    public void processarPagamento(Pedido pedido, PagamentoStrategy pagamentoStrategy){
        pedido.setPagamento(pagamentoStrategy);
        pedido.pagar();

        pedidoManager.notifyObservers(pedido);
        pedidoDAO.atualizarStatus(pedido.getId(), "ABERTO");
    }
}