package sorveteria.model;
import java.util.HashMap;
import java.util.Map;

public class SaborSorvete {
    private static final Map<String, Double> tabelaPrecos = new HashMap<>();

    static {
        tabelaPrecos.put("chocolate", 8.00);
        tabelaPrecos.put("morango", 8.00);
        tabelaPrecos.put("baunilha", 8.50);
        tabelaPrecos.put("frutas", 8.50);
        tabelaPrecos.put("kit-kat", 10.00);
    }

    public static double getPrecoBase(String sabor) {
        String key = sabor.toLowerCase();
        if (!tabelaPrecos.containsKey(key)) {
            throw new IllegalArgumentException("Sabor inválido: " + sabor);
        }
        return tabelaPrecos.get(key);
    }

    public static boolean isValid(String sabor) {
        return tabelaPrecos.containsKey(sabor.toLowerCase());
    }

    public static void listarSabores() {
        System.out.println("  Sabores disponíveis:");
        for (Map.Entry<String, Double> entry : tabelaPrecos.entrySet()) {
            System.out.println("    " + entry.getKey() + " - R$ " + String.format("%.2f", entry.getValue()));
        }
    }
}
