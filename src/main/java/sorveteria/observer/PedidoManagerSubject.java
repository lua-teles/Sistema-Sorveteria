package sorveteria.observer;

import sorveteria.model.Pedido;

import java.util.ArrayList;
import java.util.List;

public class PedidoManagerSubject {

    private final List<Observer> observers = new ArrayList<>();
    private final List<Pedido>   pedidos   = new ArrayList<>();

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers(Pedido pedido) {
        for (Observer o : observers) {
            o.update(pedido);
        }
    }

    public void addPedido(Pedido p) {
        pedidos.add(p);
        notifyObservers(p);
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

}