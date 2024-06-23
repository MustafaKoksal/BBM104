import java.util.ArrayList;

public class ClassroomBuilder implements Builder {
    private Classroom classroom;
    private ClassroomDesign classroomDesign;

    public ClassroomBuilder() {
        this.reset();
    }

    // Initialize classroom and its design
    public void reset() {
        this.classroom = new Classroom();
        this.classroomDesign = new ClassroomDesign();
    }

    // Calculate area of the walls with given dimensions
    public void setWallsArea(double width, double length, double height) {
        if (classroom.getShape().equalsIgnoreCase("rectangle")) {
            this.classroom.setWallsArea(2 * (width + length) * height);
        } else if (classroom.getShape().equalsIgnoreCase("circle")) {
            this.classroom.setWallsArea(Math.PI * length * height);
        }
    }

    // Calculate area of the floor with given dimensions
    public void setFloorArea(double width, double length) {
        if (classroom.getShape().equalsIgnoreCase("rectangle")) {
            this.classroom.setFloorArea(width * length);
        } else if (classroom.getShape().equalsIgnoreCase("circle")) {
            this.classroom.setFloorArea(Math.PI * length * length / 4);
        }
    }

    // Calculate price of the built decorations
    public void setPrice(double pricePerSquareMeter, ArrayList<Classroom> classrooms, int index, boolean isWalls) {
        if (isWalls) {
            classroomDesign.setPrice( (pricePerSquareMeter * classrooms.get(index).getWallsArea()));
        } else {
            classroomDesign.setPrice( (pricePerSquareMeter * classrooms.get(index).getFloorArea()));
        }
    }

    // Calculate price of the tiles
    public void setTilePrice(double price, double areaOfTile, ArrayList<Classroom> classrooms, int index, boolean isWalls) {
        if (isWalls) {
            classroomDesign.setPrice( (price * Math.ceil(classrooms.get(index).getWallsArea() / areaOfTile)));
        } else {
            classroomDesign.setPrice( (price * Math.ceil(classrooms.get(index).getFloorArea() / areaOfTile)));
        }
    }

    // Calculate number of tiles with area of the surface
    public void setNumberOfTiles(double areaOfTile ,ArrayList<Classroom> classrooms, int index, boolean isWalls) {
        if (isWalls) {
            classroomDesign.setNumberOfTiles((int) Math.ceil(classrooms.get(index).getWallsArea() / areaOfTile));
        } else {
            classroomDesign.setNumberOfTiles((int) Math.ceil(classrooms.get(index).getFloorArea() / areaOfTile));
        }
    }

    public void setShape(String shape) {
        classroom.setShape(shape);
    }

    public void setName(String nameOfClass) {
        classroom.setName(nameOfClass);
    }

    public void setNameOfDesign(String nameOfDesign) {
        classroomDesign.setName(nameOfDesign);
    }

    public void setType(String type) {
        classroomDesign.setType(type);
    }

    // Get built classroom
    public Classroom getClassroom() {
        Classroom newClassroom = classroom;
        reset();
        return newClassroom;
    }

    // Get built classroom with designs
    public ClassroomDesign getClassroomDesign() {
        ClassroomDesign newClassroom = classroomDesign;
        reset();
        return newClassroom;
    }
}
