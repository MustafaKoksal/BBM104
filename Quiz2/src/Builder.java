import java.util.ArrayList;

public interface Builder {
    void setWallsArea(double width, double length, double height);
    void setFloorArea(double width, double length);
    void setPrice(double pricePerSquareMeter, ArrayList<Classroom> classrooms, int index, boolean isWalls);
    void setTilePrice(double price, double areaOfTile, ArrayList<Classroom> classrooms, int index, boolean isWalls);
    void setNumberOfTiles(double areaOfTile, ArrayList<Classroom> classrooms, int index, boolean isWalls);
    void setShape(String shape);
    void setName(String nameOfClass);
    void setNameOfDesign(String nameOfDesign);
    void setType(String type);
}
