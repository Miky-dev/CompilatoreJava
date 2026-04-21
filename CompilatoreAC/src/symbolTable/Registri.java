package symbolTable;

public class Registri {
    // Partiamo dalla lettera 'a'
    private static char currentRegister = 'a';

    // Utile per resettare i registri se lanciamo il compilatore più volte
    public static void init() {
        currentRegister = 'a';
    }

    // Restituisce la lettera corrente e passa alla successiva
    public static char newRegister() {
        if (currentRegister > 'z') {
            // Se andiamo oltre la 'z', finiamo i registri!
            // Restituiamo un carattere speciale che il nostro Visitor riconoscerà come errore.
            return '\0'; 
        }
        char reg = currentRegister;
        currentRegister++;
        return reg;
    }
}