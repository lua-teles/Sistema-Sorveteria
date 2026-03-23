package sorveteria;

import sorveteria.banco.DataBaseConnectionSingleton;
import sorveteria.banco.EstoqueManagerSingleton;
import sorveteria.banco.IngredienteDAO;

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

        // 5. Fecha conexão
        banco.fechar();
        System.out.println("\n✅ Todos os testes concluídos!");
    }
}
