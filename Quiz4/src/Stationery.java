public class Stationery extends Item{
    private final String kind;

    public Stationery(String name, String barcode, double price, String kind) {
        super(name, barcode, price);
        this.kind = kind;
    }

    /**
     * @return properties of the stationery
     */
    @Override
    public String toString() {
        return "Kind of the " + getName() + " is " + kind +
                ". Its barcode is " + getBarcode() + " and its price is " + getPrice();
    }
}
