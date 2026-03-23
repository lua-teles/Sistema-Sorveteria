package sorveteria.model;

public class Ingrediente {
    private  int id;
    private String nome;
    private int quantidade;

    public Ingrediente() {}

    public Ingrediente(int id, String nome, int quantidade){
        this.id     = id;
        this.nome   = nome;
        this.quantidade = quantidade;
    }

    public int getId()
    {return id;}

    public String getNome(){
        return nome;
    }
    public int getQuantidade(){
        return quantidade;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome( String nome){
        this.nome = nome;
    }

    public void setQuantidade(int quantidade){
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return String.format("Ingrediente{id=%d, nome='%s', quantidade=%d}", id, nome, quantidade);
    }
}
