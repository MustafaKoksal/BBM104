import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Inventory<T extends Item> {
    private final List<T> items;

    public Inventory() {
        items = new ArrayList<>();
    }

    public void addItem(T item) {
        items.add(item);
    }

    /**
     * Removes the item based on the provided barcode.
     *
     * @param barcode String unique barcode of the item
     * @return True if an item is to be removed is found, false otherwise
     */
    public boolean removeItem(String barcode) {
        boolean isItemFound = false;

        // Check if there is an item based on the provided barcode
        Iterator<T> iterator = items.iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (item.getBarcode().equals(barcode)) {
                iterator.remove();
                isItemFound = true;
                break;
            }
        }

        return isItemFound;
    }

    /**
     * Searches for an item in the inventory based on the provided barcode or name, depending on the search type.
     *
     * @param barcode    the barcode of the item to search for, or null if searching by name
     * @param name       the name of the item to search for, or null if searching by barcode
     * @param searchType the type of search to perform ("barcode" or "name")
     * @return the found item, or null if no item is found
     */
    public T searchItem(String barcode, String name, String searchType) {
        for (T item : items) {
            if (item.getBarcode().equals(barcode) && searchType.equals("barcode")) {
                return item;
            }

            if (item.getName().equals(name) && searchType.equals("name")) {
                return item;
            }
        }

        return null;
    }

    public List<T> getItems() {
        return items;
    }
}
