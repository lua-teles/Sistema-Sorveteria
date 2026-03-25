package sorveteria.strategy;

public class PagamentoDinheiro implements PagamentoStrategy {
    @Override
    public void pagar(double valor) {
        System.out.printf("✅ Pagamento em Dinheiro de R$ %.2f realizado!%n", valor);
    }
}
