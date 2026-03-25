package sorveteria.strategy;

public class PagamentoPix implements PagamentoStrategy {
    private static final double DESCONTO = 0.03; // 3% de desconto

    @Override
    public void pagar(double valor) {
        double desconto = valor * DESCONTO;
        double valorFinal = valor - desconto;
        System.out.printf("Desconto PIX (3%%): R$ %.2f%n", desconto);
        System.out.printf("Pagamento via PIX de R$ %.2f realizado!%n", valorFinal);
    }
}