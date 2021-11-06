import java.util.HashMap;

public class SymbolTable {
    private static HashMap<String, Integer> hashMap = new HashMap<>();

    public static void addEntry(String symbol, int address) {
        hashMap.put(symbol, address);
    }

    public static boolean contains(String symbol) {
        return hashMap.containsKey(symbol);
    }

    public static int getAddress(String symbol) {
        return hashMap.get(symbol);
    }
}
