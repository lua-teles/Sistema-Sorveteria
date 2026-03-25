package sorveteria.strategy;

public class PagamentoDinheiro implements PagamentoStrategy {
    @Override
    public void pagar(double valor) {
        double valorPago = Math.ceil(valor);
        double troco = valorPago - valor;
        System.out.printf("Valor recebido: R$ %.2f%n", valorPago);
        System.out.printf("Troco: R$ %.2f%n", troco);
        System.out.printf("Pagamento em Dinheiro de R$ %.2f realizado!%n", valor);
    }
}