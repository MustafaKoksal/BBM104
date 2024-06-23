import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapAnalyzer {
    private static final List<Edge> roads = new ArrayList<>();
    private static String startingPoint;
    private static String endPoint;
    private static double constructionMaterialOfOM;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        FileOutput.writeToFile(args[1], "", false, false);

        createRoads(FileInput.readFile(args[0], true, true));
        printPathsAndInformation(FileInput.readFile(args[0], true, true), args[1]);
    }

    /**
     * Initializes all the roads with its end points, distance and road ID taken from the input file.
     *
     * @param inputLines A list of strings which taken from input file
     */
    private static void createRoads(String[] inputLines) {
        constructionMaterialOfOM = 0;
        boolean isFirstLine = true;

        for (String line : inputLines) {
            String[] parts = line.split("\t");
            String firstPoint = parts[0];
            String secondPoint = parts[1];

            // Take starting and ending point from first line
            if (isFirstLine) {
                startingPoint = parts[0];
                endPoint = parts[1];
                isFirstLine = false;
                continue;
            }

            int distance = Integer.parseInt(parts[2]);
            int roadID = Integer.parseInt(parts[3]);

            constructionMaterialOfOM += distance;

            roads.add(new Edge(distance, roadID, firstPoint, secondPoint));
        }
    }

    /**
     * Finds and prints the barely connected map and the fastest route of original and barely connected maps.
     *
     * @param inputLines A list of strings which taken from input file
     * @param outputPath A path to write output file
     */
    private static void printPathsAndInformation(String[] inputLines, String outputPath) {
        // Find the fastest route of original map and then print its path
        FastestRouteFinder fastestRouteOfOriginalMap = new FastestRouteFinder(roads, outputPath);
        fastestRouteOfOriginalMap.findFastestRoute(startingPoint);
        fastestRouteOfOriginalMap.printPath(startingPoint, endPoint, inputLines , false);
        double fastestRouteDistanceOfOM = fastestRouteOfOriginalMap.getTotalDistance();

        // Find the barely connected map and then print its path
        BarelyConnectedMapFinder BCM = new BarelyConnectedMapFinder(roads, outputPath);
        BCM.findBarelyConnectedMap();
        BCM.printBarelyConnectedMap();
        double constructionMaterialOfBCM = BCM.getConstructionMaterial();

        // Find the fastest route of barely connected map and then print its path
        FastestRouteFinder fastestRouteOfBCM = new FastestRouteFinder(BCM.getBarelyConnectedMap(), outputPath);
        fastestRouteOfBCM.findFastestRoute(startingPoint);
        fastestRouteOfBCM.printPath(startingPoint, endPoint, inputLines, true);
        double fastestRouteDistanceOfBCM = fastestRouteOfBCM.getTotalDistance();

        // Print analysis information about these paths
        analysis(constructionMaterialOfBCM, fastestRouteDistanceOfOM, fastestRouteDistanceOfBCM, outputPath);
    }

    /**
     * Prints an analysis of the ratios of the fastest route distances and
     * the construction metals used in both the barely connected map and the original map.
     *
     * @param constructionMaterialOfBCM Construction metal used in the barely connected map
     * @param fastestRouteDistanceOfOM Distance of fastest route in original map
     * @param fastestRouteDistanceOfBCM Distance of fastest route of barely connected map
     * @param outputPath A path to write output file
     */
    private static void analysis(double constructionMaterialOfBCM, double fastestRouteDistanceOfOM, double fastestRouteDistanceOfBCM, String outputPath) {
        FileOutput.writeToFile(outputPath, "Analysis:", true, true);

        double ratioOfConstructionMaterials = constructionMaterialOfBCM / constructionMaterialOfOM;
        FileOutput.writeToFile(outputPath, String.format("Ratio of Construction Material Usage Between" +
                " Barely Connected and Original Map: %.2f", ratioOfConstructionMaterials), true, true);

        double ratioOfFastestRouteDistances = fastestRouteDistanceOfBCM / fastestRouteDistanceOfOM;
        FileOutput.writeToFile(outputPath, String.format("Ratio of Fastest Route Between" +
                " Barely Connected and Original Map: %.2f", ratioOfFastestRouteDistances), true, false);
    }
}