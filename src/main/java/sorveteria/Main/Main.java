package sorveteria.Main;

import sorveteria.banco.DataBaseConnectionSingleton;
import sorveteria.banco.EstoqueManagerSingleton;
import sorveteria.banco.PedidoDAO;
import sorveteria.composite.ExtraLeaf;
import sorveteria.composite.ProdutoComposite;
import sorveteria.facade.SorveteriaFacade;
import sorveteria.model.ItemPedido;
import sorveteria.model.Pedido;
import sorveteria.observer.PedidoManagerSubject;
import sorveteria.observer.PreparoTelaObserver;
import sorveteria.observer.RecepcaoTelaObserver;
import sorveteria.strategy.PagamentoCartao;
import sorveteria.strategy.PagamentoDinheiro;
import sorveteria.strategy.PagamentoPix;

import java.util.Scanner;

public class Main {

    static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        PedidoManagerSubject manager = new PedidoManagerSubject();
        manager.addObserver(new RecepcaoTelaObserver());
        manager.addObserver(new PreparoTelaObserver());

        PedidoDAO pedidoDAO = new PedidoDAO();
        SorveteriaFacade facade = new SorveteriaFacade(manager, pedidoDAO);

        EstoqueManagerSingleton estoque = EstoqueManagerSingleton.getInstance();

        System.out.println("\n==============================");
        System.out.println("   BEM-VINDO À SORVETERIA");
        System.out.println("==============================");
        estoque.listarEstoque();

        boolean rodando = true;
        while (rodando) {
            System.out.println("\n====== MENU PRINCIPAL ======");
            System.out.println("  1. Novo Pedido");
            System.out.println("  2. Ver Estoque");
            System.out.println("  3. Listar Pedidos da Sessão");
            System.out.println("  0. Sair");
            System.out.print("Escolha: ");

            switch (lerInt()) {
                case 1 -> fluxoPedido(facade);
                case 2 -> estoque.listarEstoque();
                case 3 -> listarPedidos(facade);
                case 0 -> rodando = false;
                default -> System.out.println("Opção inválida, tente novamente.");
            }
        }

        System.out.println("\n====== ESTOQUE FINAL ======");
        estoque.listarEstoque();

        System.out.println("\n====== PEDIDOS DA SESSÃO ======");
        facade.getPedidos().forEach(p ->
                System.out.printf("  Pedido #%d | estado: %s | total: R$ %.2f%n",
                        p.getId(),
                        p.getEstado().getClass().getSimpleName(),
                        p.calcularTotal())
        );

        DataBaseConnectionSingleton.getInstance().fechar();
        System.out.println("\nAté logo! 🍦");
    }


    private static void fluxoPedido(SorveteriaFacade facade) {

        Pedido pedido = facade.criarPedido();

        boolean adicionando = true;
        while (adicionando) {
            System.out.println("\n── Novo Item ──────────────────────────────");
            System.out.println("  Sabores: Chocolate | Morango | Baunilha | Frutas | Kit-Kat");
            System.out.print("  Sabor: ");
            String sabor = sc.nextLine().trim();

            System.out.print("  Preço base (ex: 8.00): R$ ");
            double preco = lerDouble();

            System.out.print("  Quantidade: ");
            int qtd = lerInt();

            ProdutoComposite sorvete = new ProdutoComposite("Sorvete", sabor, preco);

            System.out.println("\n  Extras: Calda (R$1,50) | Granulado (R$0,75)");
            boolean adicionandoExtra = true;
            while (adicionandoExtra) {
                System.out.print("  Adicionar extra? (calda / granulado / nao): ");
                String extra = sc.nextLine().trim().toLowerCase();

                switch (extra) {
                    case "calda"     -> sorvete.addComponente(new ExtraLeaf("Calda",     1.50));
                    case "granulado" -> sorvete.addComponente(new ExtraLeaf("Granulado", 0.75));
                    case "nao", ""   -> { adicionandoExtra = false; continue; }
                    default          -> { System.out.println("  Extra não reconhecido."); continue; }
                }

                System.out.print("  Adicionar mais um extra? (s/n): ");
                adicionandoExtra = sc.nextLine().trim().equalsIgnoreCase("s");
            }

            System.out.printf("%n  Subtotal do item: R$ %.2f%n", qtd * sorvete.getPreco());
            facade.adicionarItem(pedido, new ItemPedido(qtd, sorvete));

            System.out.print("\nAdicionar mais um item ao pedido? (s/n): ");
            adicionando = sc.nextLine().trim().equalsIgnoreCase("s");
        }

        System.out.println("\n── O que deseja fazer? ─────────────────────");
        System.out.println("  1. Iniciar Preparo");
        System.out.println("  2. Cancelar Pedido");
        System.out.print("Escolha: ");

        if (lerInt() == 2) {
            facade.cancelarPedido(pedido);
            return;
        }

        facade.iniciarPreparo(pedido);

        System.out.println("\n── Forma de Pagamento ──────────────────────");
        System.out.printf("  Total a pagar: R$ %.2f%n", pedido.calcularTotal());
        System.out.println("  1. PIX");
        System.out.println("  2. Cartão");
        System.out.println("  3. Dinheiro");
        System.out.print("Escolha: ");

        switch (lerInt()) {
            case 1  -> pedido.setPagamento(new PagamentoPix());
            case 2  -> pedido.setPagamento(new PagamentoCartao());
            case 3  -> pedido.setPagamento(new PagamentoDinheiro());
            default -> { System.out.println("Opção inválida, usando PIX.");
                pedido.setPagamento(new PagamentoPix()); }
        }

        try {
            pedido.pagar(pedido.calcularTotal());
        } catch (RuntimeException e) {
            System.out.println("\n⚠  Erro: " + e.getMessage());
            System.out.println("   Pedido cancelado por falta de estoque.");
            facade.cancelarPedido(pedido);
            return;
        }

        facade.finalizarPedido(pedido);
    }

    private static void listarPedidos(SorveteriaFacade facade) {
        var lista = facade.getPedidos();
        if (lista.isEmpty()) {
            System.out.println("\nNenhum pedido registrado nesta sessão.");
            return;
        }
        System.out.println("\n====== PEDIDOS DA SESSÃO ======");
        lista.forEach(p ->
                System.out.printf("  Pedido #%d | estado: %s | total: R$ %.2f%n",
                        p.getId(),
                        p.getEstado().getClass().getSimpleName(),
                        p.calcularTotal())
        );
    }

    private static int lerInt() {
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private static double lerDouble() {
        try { return Double.parseDouble(sc.nextLine().trim().replace(",", ".")); }
        catch (NumberFormatException e) { return 0.0; }
    }
}