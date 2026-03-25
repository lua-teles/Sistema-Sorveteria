package sorveteria.model;

// enum que centraliza todas as bases (recipientes) disponíveis
// montagemController mapeia cada Button de base para uma constante deste enum

public enum Base {

    CASQUINHA("Casquinha",5.00),
    COPO_P("Copo P",6.50),
    COPO_G("Copo G",8.00),
    MILKSHAKE("Milkshake",12.00);

    private final String nome;
    private final double preco;

    Base(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome(){return nome; }
    public double getPreco(){ return preco; }

    // busca a constante pelo nome exibido (texto do Button no FXML)
    public static Base porNome(String nome) {
        for (Base b : values()) {
            if (b.nome.equalsIgnoreCase(nome)) return b;
        }
        return null;
    }
}
