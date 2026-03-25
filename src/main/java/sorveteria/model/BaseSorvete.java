package sorveteria.model;

public class BaseSorvete {

    public static double getPrecoBase(String base) {
        return switch (base.toLowerCase()) {
            case "casquinha" -> 2.00;
            case "copo p"    -> 0.00; // preço já embutido no sabor
            case "copo g"    -> 3.00;
            case "milkshake" -> 5.00;
            default          -> 0.00;
        };
    }

    public static boolean isValid(String base) {
        return switch (base.toLowerCase()) {
            case "casquinha", "copo p", "copo g", "milkshake" -> true;
            default -> false;
        };
    }

    public static void listarBases() {
        System.out.println("  Bases disponíveis:");
        System.out.println("    1. Casquinha  + R$ 2,00");
        System.out.println("    2. Copo P     + R$ 0,00");
        System.out.println("    3. Copo G     + R$ 3,00");
        System.out.println("    4. Milkshake  + R$ 5,00");
    }

    public static String fromNumero(int n) {
        return switch (n) {
            case 1 -> "Casquinha";
            case 2 -> "Copo P";
            case 3 -> "Copo G";
            case 4 -> "Milkshake";
            default -> null;
        };
    }
}