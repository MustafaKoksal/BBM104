import java.util.Arrays;
import java.util.List;

public class Main {
    private static final Inventory<Book> bookInventory = new Inventory<>();
    private static final Inventory<Toy> toyInventory = new Inventory<>();
    private static final Inventory<Stationery> stationeryInventory = new Inventory<>();

    public static void main(String[] args) {
        FileOutput.writeToFile(args[1], "", false, false); // Wipe the output file
        applyOperations(FileInput.readFile(args[0], true, true), args[1]);
    }

    /**
     * Applies specific operations for each command ("ADD", "REMOVE", "SEARCHBYBARCODE", "SEARCHBYNAME" or "DISPLAY").
     *
     * @param inputLines provided lines from input file
     * @param outputPath output file path to write
     */
    private static void applyOperations(String[] inputLines, String outputPath) {
        for (String line : inputLines) {
            String[] parts = line.split("\t");
            String command = parts[0];

            // Apply the appropriate method for each command (these methods will be explained)
            switch (command) {
                case "ADD":
                    addItem(parts);
                    break;
                case "REMOVE":
                    FileOutput.writeToFile(outputPath, "REMOVE RESULTS:", true, true);
                    removeItem(parts[1], outputPath);
                    FileOutput.writeToFile(outputPath, "------------------------------", true, true);
                    break;
                case "SEARCHBYBARCODE":
                    FileOutput.writeToFile(outputPath, "SEARCH RESULTS:", true, true);
                    searchItem(parts[1], outputPath);
                    FileOutput.writeToFile(outputPath, "------------------------------", true, true);
                    break;
                case "SEARCHBYNAME":
                    FileOutput.writeToFile(outputPath, "SEARCH RESULTS:", true, true);
                    searchItem(parts[1], outputPath);
                    FileOutput.writeToFile(outputPath, "------------------------------", true, true);
                    break;
                case "DISPLAY":
                    displayItems(outputPath);
                    break;
            }
        }
    }

    /**
     * Adds item to its own inventory.
     *
     * @param parts String parts from the current line
     */
    private static void addItem(String[] parts) {
        String type = parts[1];

        switch (type) {
            case "Book":
                bookInventory.addItem(new Book(parts[2], parts[3], parts[4], Double.parseDouble(parts[5])));
                break;
            case "Toy":
                toyInventory.addItem(new Toy(parts[2], parts[4], Double.parseDouble(parts[5]), parts[3]));
                break;
            case "Stationery":
                stationeryInventory.addItem(new Stationery(parts[2], parts[4], Double.parseDouble(parts[5]), parts[3]));
                break;
        }
    }

    /**
     * Find the desired item based on the barcode or name from inventories.
     *
     * @param barcodeOrName take barcode or name of the item, method will decide if its barcode or name
     * @param outputPath output file path to write
     */
    private static void searchItem(String barcodeOrName, String outputPath) {
        List<Inventory<? extends Item>> inventories = Arrays.asList(bookInventory, toyInventory, stationeryInventory);
        Item foundItem = null;

        // Search all the inventories to find desired item
        for (Inventory<? extends Item> inventory : inventories) {
            foundItem = inventory.searchItem(null, barcodeOrName, "name");
            if (foundItem != null) {
                FileOutput.writeToFile(outputPath, foundItem.toString(), true, true);
                break;
            }

            // If the given String is not a name, try for barcode
            foundItem = inventory.searchItem(barcodeOrName, null, "barcode");
            if (foundItem != null) {
                FileOutput.writeToFile(outputPath, foundItem.toString(), true, true);
                break;
            }
        }

        if (foundItem == null) {
            FileOutput.writeToFile(outputPath, "Item is not found.", true, true);
        }
    }

    /**
     * Find the desired item based on the barcode to remove.
     *
     * @param barcode unique barcode of the item
     * @param outputPath output file path to write
     */
    private static void removeItem(String barcode, String outputPath) {
        List<Inventory<? extends Item>> inventories = Arrays.asList(bookInventory, toyInventory, stationeryInventory);
        boolean isFound = false;

        // Check all the inventories to remove given item
        for (Inventory<? extends Item> inventory : inventories) {
            isFound = inventory.removeItem(barcode);
            if (isFound) {
                FileOutput.writeToFile(outputPath, "Item is removed.", true, true);
                break;
            }
        }

        if (!isFound) {
            FileOutput.writeToFile(outputPath, "Item is not found.", true, true);
        }
    }

    /**
     * Displays all the items from inventory in "Book", "Toy", "Stationery" order.
     *
     * @param outputPath output file path to write
     */
    private static void displayItems(String outputPath) {
        List<Inventory<? extends Item>> inventories = Arrays.asList(bookInventory, toyInventory, stationeryInventory);
        FileOutput.writeToFile(outputPath, "INVENTORY:", true, true);

        // Display all types of items
        for (Inventory<? extends Item> inventory : inventories) {
            for (Item item : inventory.getItems()) {
                FileOutput.writeToFile(outputPath, item.toString(), true, true);
            }
        }

        FileOutput.writeToFile(outputPath, "------------------------------", true, true);
    }
}