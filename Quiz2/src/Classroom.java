public class Classroom {
    // Initialize the variables that must be in all classrooms
    private String shape;
    private double wallsArea;
    private String nameOfClass;
    private double floorArea;
    private double price;
    private String type;
    private int numberOfTiles;

    public String getShape() { return shape; }

    public void setShape(String shape) { this.shape = shape; }

    public double getWallsArea() { return wallsArea; }

    public void setWallsArea(double wallsArea) { this.wallsArea = wallsArea; }

    public String getName() {
        return nameOfClass;
    }

    public void setName(String nameOfClass) { this.nameOfClass = nameOfClass; }

    public double getFloorArea() { return floorArea; }

    public void setFloorArea(double floorArea) { this.floorArea = floorArea; }

    public double getPrice() { return price; }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumberOfTiles() { return numberOfTiles; }

    public void setNumberOfTiles(int numberOfTiles) { this.numberOfTiles = numberOfTiles; }
}