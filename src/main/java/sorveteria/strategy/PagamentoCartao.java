package sorveteria.strategy;

public class PagamentoCartao implements PagamentoStrategy {
    @Override
    public void pagar(double valor) {
        System.out.printf("Pagamento via Cartão de R$ %.2f realizado!%n", valor);
    }
}