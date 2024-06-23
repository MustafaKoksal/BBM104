public class Toy extends Item{
    private final String color;

    public Toy(String name, String barcode, double price, String color) {
        super(name, barcode, price);
        this.color = color;
    }

    /**
     * @return properties of the toy
     */
    @Override
    public String toString() {
        return "Color of the " + getName() + " is " + color +
                ". Its barcode is " + getBarcode() + " and its price is " + getPrice();
    }
}
