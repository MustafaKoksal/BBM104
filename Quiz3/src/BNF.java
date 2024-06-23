import java.util.Map;
import java.util.HashMap;
public class BNF {
    /**
     * Main method to initiate BNF conversion.
     *
     * @param args Command line arguments. args[0] is the input file containing terminals and non-terminals,
     *             args[1] is the output file.
     */
    public static void main(String[] args) {
        FileOutput.writeToFile(args[1], "", false, false); // If the output file is exist, wipe it
        convertToBNF(args[1] ,initializeNonTerminals(FileInput.readFile(args[0], true, true)));
    }

    /**
     * Initialize non-terminals from input lines.
     *
     * @param lines Array of input lines containing non-terminals.
     * @return HashMap containing non-terminals mapped to their corresponding terminals.
     */
    private static Map<String, String> initializeNonTerminals(String[] lines) {
        Map<String, String> items = new HashMap<>(); // I used HashMap to map non-terminals to terminals

        // Read all input lines and convert them to corresponding terminals from non-terminals
        for (String line : lines) {
            String[] parts = line.split("->");
            String nonTerminal = parts[0]; String terminal = parts[1];
            items.put(nonTerminal, terminal);
        }

        return items;
    }

    /**
     * Check if a string contains an uppercase character.
     *
     * @param stringToCheck String to check for uppercase characters.
     * @return True if the string contains an uppercase character, false otherwise.
     */
    private static boolean isThereAnUppercase(String stringToCheck) {
        for (int i = 0; i < stringToCheck.length(); i++) {
            // Take current char from string
            char currentChar = stringToCheck.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Convert productions to Backus-Naur Form recursively.
     *
     * @param outputFile The name of the output file to write the BNF.
     * @param items HashMap containing non-terminals mapped to their corresponding terminals.
     * @return String representing the Backus-Naur Form of the productions.
     */
    private static String convertToBNF(String outputFile ,Map<String, String> items) {
        String terminalsOfS = "(" + items.get("S") + ")";

        // Convert non-terminals to corresponding terminals
        for (int i = 0; i < terminalsOfS.length(); i++) {
            char currentChar = terminalsOfS.charAt(i);
            if (Character.isUpperCase(currentChar)) {
                String replacement = "(" + items.get(Character.toString(currentChar)) + ")";
                terminalsOfS = terminalsOfS.replace(Character.toString(currentChar), replacement);
            }
        }

        // Update the given start("S")
        items.put("S", terminalsOfS);

        // If there is a non-terminal, then convert them recursively
        if (isThereAnUppercase(terminalsOfS)) {
            return convertToBNF(outputFile ,items);
        } else {
            FileOutput.writeToFile(outputFile, terminalsOfS, true, false);
            return terminalsOfS;
        }
    }
}