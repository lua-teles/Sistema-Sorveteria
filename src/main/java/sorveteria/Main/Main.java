package sorveteria.Main;

import sorveteria.banco.DataBaseConnectionSingleton;
import sorveteria.banco.EstoqueManagerSingleton;
import sorveteria.banco.PedidoDAO;
import sorveteria.composite.ExtraLeaf;
import sorveteria.composite.ProdutoComposite;
import sorveteria.facade.SorveteriaFacade;
import sorveteria.model.BaseSorvete;
import sorveteria.model.ItemPedido;
import sorveteria.model.Pedido;
import sorveteria.model.SaborSorvete;
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
        System.out.println("\nAté logo!");
    }


    private static void fluxoPedido(SorveteriaFacade facade) {

        Pedido pedido = facade.criarPedido();

        boolean adicionando = true;
        while (adicionando) {
            System.out.println("\n── Monte seu Sorvete ───────────────────────");


            System.out.println("\n  1. Escolha a Base:");
            BaseSorvete.listarBases();
            System.out.print("  Número da base: ");
            int numBase = lerInt();
            String base = BaseSorvete.fromNumero(numBase);

            if (base == null) {
                System.out.println("  Base inválida! Tente novamente.");
                continue;
            }
            double precoBase = BaseSorvete.getPrecoBase(base);


            System.out.println("\n  2. Escolha o Sabor:");
            SaborSorvete.listarSabores();
            System.out.print("  Número do sabor: ");
            int numSabor = lerInt();
            String sabor = SaborSorvete.fromNumero(numSabor);

            if (sabor == null) {
                System.out.println("  Sabor inválido! Tente novamente.");
                continue;
            }
            double precoSabor = SaborSorvete.getPrecoBase(sabor);

            double precoTotal = precoBase + precoSabor;

            System.out.print("\n  Quantidade: ");
            int qtd = lerInt();
            if (qtd <= 0) {
                System.out.println("  Quantidade inválida.");
                continue;
            }

            ProdutoComposite sorvete = new ProdutoComposite("Sorvete", base, sabor, precoTotal);


            System.out.println("\n  3. Adicionais (0 para finalizar):");
            SaborSorvete.listarExtras();

            boolean adicionandoExtra = true;
            while (adicionandoExtra) {
                System.out.print("  Número do adicional (0 = nenhum/finalizar): ");
                int numExtra = lerInt();

                if (numExtra == 0) {
                    adicionandoExtra = false;
                    continue;
                }

                String extraNome  = SaborSorvete.extraNomeFromNumero(numExtra);
                double extraPreco = SaborSorvete.extraPrecoFromNumero(numExtra);

                if (extraNome == null) {
                    System.out.println("  Adicional inválido.");
                    continue;
                }

                sorvete.addComponente(new ExtraLeaf(extraNome, extraPreco));
                System.out.printf("    ✓ %s adicionado! + R$ %.2f%n", extraNome, extraPreco);
            }


            System.out.print("\n  4. Observações (Enter para pular): ");
            String obs = sc.nextLine().trim();


            System.out.println("\n  ─── RESUMO DO ITEM ───────────────────────");
            System.out.println("  Base   : " + base  + " (R$ " + String.format("%.2f", precoBase)  + ")");
            System.out.println("  Sabor  : " + sabor + " (R$ " + String.format("%.2f", precoSabor) + ")");
            if (!sorvete.getComponentes().isEmpty()) {
                System.out.println("  Adicionais:");
                sorvete.getComponentes().forEach(c ->
                        System.out.printf("    - %-16s R$ %.2f%n", c.getNome(), c.getPreco()));
            } else {
                System.out.println("  Adicionais: Nenhum");
            }
            if (!obs.isBlank()) System.out.println("  Observação: " + obs);
            System.out.printf("  Preço unitário : R$ %.2f%n", sorvete.getPreco());
            System.out.printf("  Subtotal (%dx) : R$ %.2f%n", qtd, qtd * sorvete.getPreco());
            System.out.println("  ─────────────────────────────────────────");

            facade.adicionarItem(pedido, new ItemPedido(qtd, sorvete));
            facade.adicionarObservacao(pedido, obs);

            System.out.print("\nAdicionar mais um item ao pedido? (s/n): ");
            adicionando = sc.nextLine().trim().equalsIgnoreCase("s");
        }


        System.out.println("\n======================================");
        System.out.println("         RESUMO DO PEDIDO");
        System.out.println("======================================");
        for (ItemPedido item : pedido.getItens()) {
            ProdutoComposite s = (ProdutoComposite) item.getItem();
            System.out.printf("  %dx %-10s | Base: %-10s | R$ %.2f%n",
                    item.getQuantidade(), s.getSabor(), s.getBase(),
                    item.calcularSubtotal());
        }
        System.out.println("--------------------------------------");
        System.out.printf("  TOTAL: R$ %.2f%n", pedido.calcularTotal());
        System.out.println("======================================\n");


        System.out.println("── Forma de Pagamento ──────────────────────────");
        System.out.printf("  Total a pagar: R$ %.2f%n", pedido.calcularTotal());
        System.out.println("  1. PIX");
        System.out.println("  2. Cartão");
        System.out.println("  3. Dinheiro");
        System.out.print("Escolha: ");

        switch (lerInt()) {
            case 1  -> pedido.setPagamento(new PagamentoPix());
            case 2  -> pedido.setPagamento(new PagamentoCartao());
            case 3  -> pedido.setPagamento(new PagamentoDinheiro());
            default -> { System.out.println("Opção inválida, usando PIX."); pedido.setPagamento(new PagamentoPix()); }
        }

        try {
            pedido.pagar(pedido.calcularTotal());
            System.out.println("\nPagamento realizado com sucesso!");
        } catch (RuntimeException e) {
            System.out.println("\nErro: " + e.getMessage());
            System.out.println("Pedido cancelado por falta de estoque.");
            facade.cancelarPedido(pedido);
            return;
        }
        
        System.out.println("\nPedido pago! Iniciando o preparo...");
        facade.iniciarPreparo(pedido);

        System.out.println("\n[GERENTE] Pedido #" + pedido.getId() + " em preparo.");
        System.out.print("          Digite 'pronto' para finalizar o pedido: ");
        while (!sc.nextLine().trim().equalsIgnoreCase("pronto")) {
            System.out.print("          Digite 'pronto' para finalizar o pedido: ");
        }

        facade.finalizarPedido(pedido);
        System.out.println("Pedido finalizado! Obrigado pela compra!");
    }

    private static void listarPedidos(SorveteriaFacade facade) {
        var lista = facade.getPedidos();
        if (lista.isEmpty()) {
            System.out.println("\nNenhum pedido registrado nesta sessão.");
            return;
        }
        System.out.println("\n====== PEDIDOS DA SESSÃO ======");
        lista.forEach(p -> System.out.printf("  Pedido #%d | estado: %s | total: R$ %.2f%n",
                p.getId(), p.getEstado().getClass().getSimpleName(), p.calcularTotal()));
    }

    private static int lerInt() {
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }
}