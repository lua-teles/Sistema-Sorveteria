package sorveteria.composite;

import java.util.ArrayList;
import java.util.List;

// COMPOSITE — representa um sorvete que pode ter vários extras
// O preço final = preço base + soma de todos os extras
public class ProdutoComposite implements ProdutoComponent {
    private final String nome;
    private final String sabor;
    private final double precoBase;
    private final List<ProdutoComponent> componentes = new ArrayList<>();

    public ProdutoComposite(String nome, String sabor, double precoBase) {
        this.nome      = nome;
        this.sabor     = sabor;
        this.precoBase = precoBase;
    }

    public void addComponente(ProdutoComponent c)    { componentes.add(c); }
    public void removeComponente(ProdutoComponent c) { componentes.remove(c); }
    public List<ProdutoComponent> getComponentes()   { return componentes; }

    // 🔥 CORREÇÃO: agora retorna apenas o sabor (igual ao estoque)
    @Override
    public String getNome() {
        return sabor;
    }

    // 👉 Getter do sabor (boa prática)
    public String getSabor() {
        return sabor;
    }

    @Override
    public double getPreco() {
        double total = precoBase;
        for (ProdutoComponent c : componentes) {
            total += c.getPreco();
        }
        return total;
    }
}