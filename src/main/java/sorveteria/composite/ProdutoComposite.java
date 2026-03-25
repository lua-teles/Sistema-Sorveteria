package sorveteria.composite;

import java.util.ArrayList;
import java.util.List;

public class ProdutoComposite implements ProdutoComponent {

    private final String nome;
    private final String base;
    private final String sabor;
    private final double precoBase;
    private final List<ProdutoComponent> componentes = new ArrayList<>();

    public ProdutoComposite(String nome, String base, String sabor, double precoBase) {
        this.nome      = nome;
        this.base      = base;
        this.sabor     = sabor;
        this.precoBase = precoBase;
    }

    public ProdutoComposite(String nome, String sabor, double precoBase) {
        this(nome, "Casquinha", sabor, precoBase);
    }

    public void addComponente(ProdutoComponent c)    { componentes.add(c); }
    public void removeComponente(ProdutoComponent c) { componentes.remove(c); }
    public List<ProdutoComponent> getComponentes()   { return componentes; }

    @Override
    public String getNome() { return nome; }

    public String getBase()  { return base; }
    public String getSabor() { return sabor; }

    @Override
    public double getPreco() {
        double total = precoBase;
        for (ProdutoComponent c : componentes) total += c.getPreco();
        return total;
    }
}