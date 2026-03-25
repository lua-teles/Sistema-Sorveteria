package sorveteria.banco;

import sorveteria.model.Ingrediente;

public class EstoqueManagerSingleton {
    private static EstoqueManagerSingleton instance;
    private final IngredienteDAO ingredienteDAO;

    private EstoqueManagerSingleton(){
        this.ingredienteDAO = new IngredienteDAO();
    }

    public static EstoqueManagerSingleton getInstance(){
        if(instance == null){
            instance = new EstoqueManagerSingleton();
        }
        return instance;
    }

    public void baixarEstoque(String nome, int qtd){
        String nomePadronizado = nome.toLowerCase();
        Ingrediente ingrediente = ingredienteDAO.buscaPorNome(nomePadronizado);

        if(ingrediente == null){
            throw new RuntimeException("ingrediente não encontrado: " + nome);
        }
        if(ingrediente.getQuantidade() < qtd){
            throw new RuntimeException("Estoque insuficiente de " + nome + " (disponível: " + ingrediente.getQuantidade() + ", pedido: " + qtd + ")");
        }

        ingrediente.setQuantidade(ingrediente.getQuantidade() - qtd);
        ingredienteDAO.atualizarQuantidade(ingrediente);

        System.out.println("  [ESTOQUE] Baixado " + qtd + "x " + nomePadronizado + " | Restante: " + ingrediente.getQuantidade());
    }

    public int verificarEstoque(String nome){
        String nomePadronizado = nome.toLowerCase();
        Ingrediente ingrediente = ingredienteDAO.buscaPorNome(nomePadronizado);
        if(ingrediente == null){
            throw new RuntimeException("Ingrediente não encontrado: " + nome);
        }
        return ingrediente.getQuantidade();
    }

    public void listarEstoque(){
        System.out.println("\n ++++++ Estoque Atual +++++++");
        ingredienteDAO.listarTodos().forEach(i -> System.out.printf("%15s: %d unidades%n", i.getNome(), i.getQuantidade()));
        System.out.println("+++++++++++++++++++++++++++++++++++++\n");
    }
}