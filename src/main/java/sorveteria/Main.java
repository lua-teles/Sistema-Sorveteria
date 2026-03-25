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
        //Testa a conexão com o banco
        DataBaseConnectionSingleton banco = DataBaseConnectionSingleton.getInstance();
        Connection conn = banco.getConection();
        System.out.println("Conexão OK:" + (conn !=null) );

        DataBaseConnectionSingleton banco2 = DataBaseConnectionSingleton.getInstance();
        System.out.println("É Singleton? " + (banco == banco2));

        System.out.println("teste ingrediente dao");
        IngredienteDAO ingredienteDAO = new IngredienteDAO();
        ingredienteDAO.listarTodos().forEach((i->System.out.println(("Ingredienre:" +i.getNome() + "| QTD: " + i.getQuantidade()))));

        System.out.println("\n=== TESTE ESTOQUE MANAGER ===");
        EstoqueManagerSingleton estoque = EstoqueManagerSingleton.getInstance();
        estoque.listarEstoque();

        // 4. Testa baixa no estoque
        System.out.println("=== TESTE BAIXA DE ESTOQUE ===");
        estoque.baixarEstoque("Chocolate", 2);
        System.out.println("Chocolate após baixa: " + estoque.verificarEstoque("Chocolate"));

        // === TESTE COMPOSITE ===
        System.out.println("\n=== TESTE COMPOSITE ===");
        ProdutoComposite sorvete = new ProdutoComposite("Sorvete", "Chocolate", 8.00);
        sorvete.addComponente(new ExtraLeaf("Granola", 2.00));
        sorvete.addComponente(new ExtraLeaf("Calda de Chocolate", 1.50));
        System.out.println("Produto: " + sorvete.getNome());
        System.out.printf("Preco total: R$ %.2f%n", sorvete.getPreco());

        // === TESTE STRATEGY ===
        System.out.println("\n=== TESTE STRATEGY ===");
        Pedido pedido = new Pedido();
        pedido.addItem(new ItemPedido(1, sorvete));
        pedido.setPagamento(new PagamentoPix());
        System.out.printf("Total do pedido: R$ %.2f%n", pedido.calcularTotal());
        pedido.pagar(pedido.calcularTotal());


        // 5. Fecha conexão
        banco.fechar();
        System.out.println("\n✅ Todos os testes concluídos!");
    }
}
