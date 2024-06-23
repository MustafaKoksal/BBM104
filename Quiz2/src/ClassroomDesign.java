public class ClassroomDesign extends Classroom {
    // Initialize design variables for classroom
    private double decorationPrice;
    private int numberOfTiles;
    private String nameOfDesign;

    public void setPrice(double decorationPrice) {
        this.decorationPrice = decorationPrice;
    }

    public double getPrice() {
        return decorationPrice;
    }

    public void setNumberOfTiles(int numberOfTiles) {
        this.numberOfTiles = numberOfTiles;
    }

    public int getNumberOfTiles() { return numberOfTiles; }

    public void setName(String nameOfDesign) {
        this.nameOfDesign = nameOfDesign;
    }

    public String getName() {
        return nameOfDesign;
    }
}
