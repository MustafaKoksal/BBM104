public class Book extends Item{
    private final String author;
    public Book(String name, String author, String barcode, double price) {
        super(name, barcode, price);
        this.author = author;
    }

    /**
     * @return properties of the book
     */
    @Override
    public String toString() {
        return "Author of the " + getName() + " is " + author +
                ". Its barcode is " + getBarcode() + " and its price is " + getPrice();
    }
}
