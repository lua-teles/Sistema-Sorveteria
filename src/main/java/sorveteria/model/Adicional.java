package sorveteria.model;

import sorveteria.composite.ExtraLeaf;

//enum que centraliza todos os adicionais disponíveis na sorveteria
// cada constante carrega o nome exibido na UI e o preço unitário
// serve como única fonte sobre itens extras, usado mo MontagemController
//mapeia cada Button do FXML para uma constante deste enum

public enum Adicional {

    CALDA_CHOCOLATE("Calda Chocolate",1.50),
    CALDA_MORANGO("Calda Morango",1.50),
    GRANULADO("Granulado",1.00),
    FRUTAS("Frutas",2.00),
    KITKAT("Kit-Kat",3.00),
    PACOCA("Paçoca",2.00);
    private final String nome;
    private final double preco;

    Adicional(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome(){ return nome;}
    public double getPreco(){ return preco; }

    // cria e retorna uma ExtraLeaf correspondente a este adicional
    public ExtraLeaf criarFolha(){
        return new ExtraLeaf(nome, preco);
    }

    //busca a constante pelo nome exibido (texto do Button no FXML).
    // retorna null se não encontrar, evitando exceções silenciosas.
    public static Adicional porNome(String nome) {
        for (Adicional a : values()) {
            if (a.nome.equalsIgnoreCase(nome)) return a;
        }
        return null;
    }
}
