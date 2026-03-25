package sorveteria.strategy;

public class PagamentoPix implements PagamentoStrategy {
    @Override
    public void pagar(double valor) {
        System.out.printf("✅ Pagamento via PIX de R$ %.2f realizado!%n", valor);
    }
}