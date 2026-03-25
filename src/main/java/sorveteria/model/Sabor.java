package sorveteria.model;

//enum que centraliza todos os sabores de sorvete disponíveis
//MontagemController mapeia cada Button de sabor para uma constante deste enum

public enum Sabor {

    CHOCOLATE("Chocolate",3.00),
    MORANGO("Morango",3.00),
    BAUNILHA("Baunilha",3.00),
    CREME("Creme",3.00),
    MENTA("Menta",3.50);

    private final String nome;
    private final double preco;

    Sabor(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome(){ return nome;  }
    public double getPreco(){ return preco; }

    //bsca a constante pelo texto do Button no FXML
    public static Sabor porNome(String nome) {
        for (Sabor s : values()) {
            if (s.nome.equalsIgnoreCase(nome)) return s;
        }
        return null;
    }
}
