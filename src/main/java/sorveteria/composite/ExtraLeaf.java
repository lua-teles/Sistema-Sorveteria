package sorveteria.composite;

public class ExtraLeaf implements ProdutoComponent {

    private final String nome;
    private final double preco;

    public ExtraLeaf(String nome, double preco) {
        this.nome  = nome;
        this.preco = preco;
    }

    @Override public String getNome()  { return nome; }
    @Override public double getPreco() { return preco; }
}