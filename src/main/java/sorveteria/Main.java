package sorveteria;

import sorveteria.banco.DataBaseConnectionSingleton;
import sorveteria.banco.EstoqueManagerSingleton;
import sorveteria.banco.IngredienteDAO;
import sorveteria.composite.ExtraLeaf;
import sorveteria.composite.ProdutoComposite;
import sorveteria.model.Pedido;
import sorveteria.strategy.PagamentoPix;
import sorveteria.model.ItemPedido;

import java.sql.Connection;

public class Main {
    public static void main(String[] args){

        // === TESTE CONEXÃO ===
        DataBaseConnectionSingleton banco = DataBaseConnectionSingleton.getInstance();
        Connection conn = banco.getConection();
        System.out.println("Conexão OK: " + (conn != null));

        DataBaseConnectionSingleton banco2 = DataBaseConnectionSingleton.getInstance();
        System.out.println("É Singleton? " + (banco == banco2));

        // === TESTE DAO ===
        System.out.println("\n=== TESTE INGREDIENTE DAO ===");
        IngredienteDAO ingredienteDAO = new IngredienteDAO();
        ingredienteDAO.listarTodos().forEach(
                i -> System.out.println("Ingrediente: " + i.getNome() + " | QTD: " + i.getQuantidade())
        );

        // === TESTE ESTOQUE ===
        System.out.println("\n=== TESTE ESTOQUE MANAGER ===");
        EstoqueManagerSingleton estoque = EstoqueManagerSingleton.getInstance();
        estoque.listarEstoque();

        // Teste de baixa direta
        System.out.println("\n=== TESTE BAIXA DE ESTOQUE ===");
        estoque.baixarEstoque("Chocolate", 2);
        System.out.println("Chocolate após baixa: " + estoque.verificarEstoque("Chocolate"));

        // === TESTE COMPOSITE ===
        System.out.println("\n=== TESTE COMPOSITE ===");

        // 🔥 Nome pode ser qualquer coisa agora, pois getNome() retorna o sabor
        ProdutoComposite sorvete = new ProdutoComposite("Sorvete", "Chocolate", 8.00);

        sorvete.addComponente(new ExtraLeaf("Granola", 2.00));
        sorvete.addComponente(new ExtraLeaf("Calda de Chocolate", 1.50));

        System.out.println("Produto (sabor): " + sorvete.getNome());
        System.out.printf("Preço total: R$ %.2f%n", sorvete.getPreco());

        // === TESTE PEDIDO + STRATEGY ===
        System.out.println("\n=== TESTE PEDIDO + PAGAMENTO ===");

        Pedido pedido = new Pedido();
        pedido.addItem(new ItemPedido(1, sorvete));

        pedido.setPagamento(new PagamentoPix());

        System.out.printf("Total do pedido: R$ %.2f%n", pedido.calcularTotal());

        // 🔥 Aqui vai baixar corretamente "Chocolate" do estoque
        pedido.pagar(pedido.calcularTotal());

        System.out.println("Pagamento realizado e estoque atualizado!");

        // === FINALIZAÇÃO ===
        banco.fechar();
        System.out.println("\n✅ Todos os testes concluídos com sucesso!");
    }
}