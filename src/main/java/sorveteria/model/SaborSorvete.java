package sorveteria.model;

public class SaborSorvete {

    public static double getPrecoBase(String sabor) {
        return switch (sabor.toLowerCase()) {
            case "chocolate" -> 8.00;
            case "morango"   -> 8.00;
            case "baunilha"  -> 7.00;
            case "creme"     -> 7.00;
            default          -> 0.00;
        };
    }

    public static boolean isValid(String sabor) {
        return switch (sabor.toLowerCase()) {
            case "chocolate", "morango", "baunilha", "creme" -> true;
            default -> false;
        };
    }

    public static void listarSabores() {
        System.out.println("  Sabores disponíveis:");
        System.out.println("    1. Chocolate  R$ 8,00");
        System.out.println("    2. Morango    R$ 8,00");
        System.out.println("    3. Baunilha   R$ 7,00");
        System.out.println("    4. Creme      R$ 7,00");
    }

    public static String fromNumero(int n) {
        return switch (n) {
            case 1 -> "chocolate";
            case 2 -> "morango";
            case 3 -> "baunilha";
            case 4 -> "creme";
            default -> null;
        };
    }

    // listarExtras e extraNomeFromNumero / extraPrecoFromNumero sem alteração
    public static void listarExtras() {
        System.out.println("  Adicionais disponíveis:");
        System.out.println("    1. Calda Chocolate  + R$ 1,50");
        System.out.println("    2. Calda Morango    + R$ 1,50");
        System.out.println("    3. Granulado        + R$ 0,75");
        System.out.println("    4. Frutas           + R$ 2,00");
        System.out.println("    5. Kit-Kat          + R$ 2,50");
        System.out.println("    6. Paçoca           + R$ 1,75");
    }

    public static String extraNomeFromNumero(int n) {
        return switch (n) {
            case 1 -> "Calda Chocolate";
            case 2 -> "Calda Morango";
            case 3 -> "Granulado";
            case 4 -> "Frutas";
            case 5 -> "Kit-Kat";
            case 6 -> "Paçoca";
            default -> null;
        };
    }

    public static double extraPrecoFromNumero(int n) {
        return switch (n) {
            case 1, 2 -> 1.50;
            case 3    -> 0.75;
            case 4    -> 2.00;
            case 5    -> 2.50;
            case 6    -> 1.75;
            default   -> 0.00;
        };
    }
}