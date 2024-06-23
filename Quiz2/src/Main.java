import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        String[] itemLines = FileInput.readFile(args[0], true, true);
        String[] decorationLines = FileInput.readFile(args[1], true, true);
        FileOutput.writeToFile(args[2], "", false, false);

        sortDecorationLines(decorationLines);

        printClassrooms(itemLines, decorationLines, args[2]);
    }

    // Print all designed classrooms
    private static void printClassrooms(String[] itemLines, String[] decorationLines, String outputFilePath) {
        ClassroomDirector director = new ClassroomDirector();
        ArrayList<Classroom> classrooms;
        HashMap<String, Classroom> decorations;

        double areaOfWalls;
        double areaOfFloor;
        String typeOfWall;
        String typeOfFloor;
        double priceOfWalls;
        double priceOfFloor;
        double totalPrice;
        double allClassroomsPrice = 0;
        String classroomName;
        int numberOfTilesOfWalls;
        int numberOfTilesOfFloor;

        for (int i = 0; i < decorationLines.length; i++) {
            // Get designs to be implemented
            String[] parts = decorationLines[i].split("\t");

            totalPrice = 0;
            numberOfTilesOfWalls = 0;
            numberOfTilesOfFloor = 0;

            int classroomIndex = Integer.parseInt(parts[0].substring(1));

            // Construct the walls of classroom
            director.construct(itemLines, classroomIndex, true);

            // Get classroom and design with built walls
            classrooms = director.getClassrooms();
            decorations = director.getDecorations();

            // Get & calculate required information
            areaOfWalls = Math.ceil(classrooms.get(i).getWallsArea());
            typeOfWall = decorations.get(parts[1]).getType();
            priceOfWalls = decorations.get(parts[1]).getPrice();

            if (typeOfWall.equalsIgnoreCase("tile")) {
                numberOfTilesOfWalls = decorations.get(parts[1]).getNumberOfTiles();
            }

            totalPrice += priceOfWalls;

            // Construct the floor of classroom
            director.construct(itemLines, classroomIndex, false);

            // Get classroom and design with built floor
            classrooms = director.getClassrooms();
            decorations = director.getDecorations();

            // Get & calculate required information
            classroomName = classrooms.get(i).getName();
            areaOfFloor = Math.ceil(classrooms.get(i).getFloorArea());
            typeOfFloor = decorations.get(parts[2]).getType();
            priceOfFloor = decorations.get(parts[2]).getPrice();

            if (typeOfFloor.equalsIgnoreCase("tile")) {
                numberOfTilesOfFloor = decorations.get(parts[2]).getNumberOfTiles();
            }

            totalPrice += priceOfFloor;
            allClassroomsPrice += totalPrice;

            boolean isTileWall = typeOfWall.equalsIgnoreCase("tile");
            boolean isTileFloor = typeOfFloor.equalsIgnoreCase("tile");

            // Print information of classroom and design
            String output = "Classroom " + classroomName + " used " +
                    ( isTileWall ? numberOfTilesOfWalls : (int) areaOfWalls) +
                    ( isTileWall ? " Tiles for" : "m2 of " + typeOfWall + " for") + " walls and used " +
                    ( isTileFloor ? numberOfTilesOfFloor : (int) areaOfFloor) +
                    ( isTileFloor ? " Tiles for" : "m2 of " + typeOfFloor + " for") + " flooring," +
                    " these costed " + (int) Math.ceil(totalPrice) + "TL.";

            FileOutput.writeToFile(outputFilePath, output, true, true);

        }
        String output = "Total price is: " + (int) Math.ceil(allClassroomsPrice) + "TL.";
        FileOutput.writeToFile(outputFilePath, output, true, false);
    }

    // Sort decoration lines according to classroom names
    private static void sortDecorationLines(String[] decorationLines) {
        Arrays.sort(decorationLines, (line1, line2) -> {
            int classNumber1 = Integer.parseInt(line1.split("\t")[0].substring(1));
            int classNumber2 = Integer.parseInt(line2.split("\t")[0].substring(1));
            return Integer.compare(classNumber1, classNumber2);
        });
    }
}