import java.util.ArrayList;
import java.util.HashMap;

public class ClassroomDirector {
    private ArrayList<Classroom> classrooms = new ArrayList<>();
    private HashMap<String, Classroom> decorations = new HashMap<>();

    public void construct(String[] itemLines, int decorationNumber, boolean isWalls) {
        int lineIndex = 0;

        while (lineIndex != itemLines.length) {
            // Get items to be implemented
            String[] parts = itemLines[lineIndex].split("\t");

            if ((parts[0].equals("CLASSROOM"))) {
                String nameOfClass = parts[1];
                String shape = parts[2];
                double width = Double.parseDouble(parts[3]);
                double length = Double.parseDouble(parts[4]);
                double height = Double.parseDouble(parts[5]);

                ClassroomBuilder builder = new ClassroomBuilder();
                // Build classroom with given items
                builder.reset();
                builder.setName(nameOfClass);
                builder.setShape(shape);
                builder.setWallsArea(width, length, height);
                builder.setFloorArea(width, length);

                // Get built classroom
                Classroom newClassroom = builder.getClassroom();

                classrooms.add(newClassroom);
            }

            if (parts[0].equals("DECORATION")) {
                ClassroomBuilder builder = new ClassroomBuilder();
                String nameOfDesign = parts[1];
                String type = parts[2];

                if (type.equalsIgnoreCase("paint") || type.equalsIgnoreCase("wallpaper")) {
                    double pricePerSquareMeter = Double.parseDouble(parts[3]);

                    // Design the classroom with paint or wallpaper
                    builder.setNameOfDesign(nameOfDesign);
                    builder.setPrice(pricePerSquareMeter, classrooms, decorationNumber - 1, isWalls);
                    builder.setType(type);

                    // Get designed classroom **POLYMORPHISM
                    Classroom decoration = builder.getClassroomDesign();

                    decorations.put(nameOfDesign, decoration);
                }

                if (type.equalsIgnoreCase("tile")) {
                    double pricePerTile = Double.parseDouble(parts[3]);
                    double areaPerTile = Double.parseDouble(parts[4]);

                    // Design the classroom with tiles
                    builder.setNumberOfTiles(areaPerTile, classrooms, decorationNumber - 1, isWalls);
                    builder.setNameOfDesign(nameOfDesign);
                    builder.setTilePrice(pricePerTile, areaPerTile, classrooms, decorationNumber - 1, isWalls);
                    builder.setType(type);

                    // Get designed classroom **POLYMORPHISM
                    Classroom decoration = builder.getClassroomDesign();

                    decorations.put(nameOfDesign, decoration);
                }
            }
            lineIndex++;
        }
    }

    public ArrayList<Classroom> getClassrooms() {
        return classrooms;
    }

    public HashMap<String, Classroom> getDecorations() {
        return decorations;
    }
}