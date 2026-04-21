package symbolTable;

import java.util.HashMap;
import ast.LangType;

public class SymbolTable {

    // La classe interna per gli attributi (Slide 3)
    public static class Attributes {
        private LangType tipo;

        public Attributes(LangType tipo) {
            this.tipo = tipo;
        }

        public LangType getTipo() {
            return tipo;
        }
    }

    // La tabella statica per associare ID -> Attributi
    private static HashMap<String, Attributes> table;

    // Metodo di inizializzazione
    public static void init() {
        table = new HashMap<>();
    }

    // Inserisce una nuova variabile. Ritorna false se l'ID esiste già.
    public static boolean enter(String id, Attributes entry) {
        if (table.containsKey(id)) {
            return false; // Errore: Variabile già dichiarata
        }
        table.put(id, entry);
        return true;
    }

    // Cerca una variabile. Ritorna null se non esiste.
    public static Attributes lookup(String id) {
        return table.get(id);
    }

    public static int size() {
        return table.size();
    }

    public static String toStr() {
        StringBuilder sb = new StringBuilder("Symbol Table:\n");
        for (String id : table.keySet()) {
            sb.append(id).append(" -> ").append(table.get(id).getTipo()).append("\n");
        }
        return sb.toString();
    }
}