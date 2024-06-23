import java.util.*;

public class BarelyConnectedMapFinder {
    private final String outputPath;
    private final List<Edge> roads;
    private final List<Edge> barelyConnectedMap = new ArrayList<>();
    private double constructionMaterial;

    public BarelyConnectedMapFinder(List<Edge> roads, String outputPath) {
        this.roads = roads;
        this.outputPath = outputPath;
    }

    /**
     * Sorts the list of cities alphabetically.
     *
     * @return List of sorted cities
     */
    private List<String> sortPoints() {
        List<String> sortedPoints = new ArrayList<>();

        // Add all cities to the list
        for (Edge road : roads) {
            if (!sortedPoints.contains(road.getFirstPoint())) {
                sortedPoints.add(road.getFirstPoint());
            }

            if (!sortedPoints.contains(road.getSecondPoint())) {
                sortedPoints.add(road.getSecondPoint());
            }
        }

        Collections.sort(sortedPoints);

        return sortedPoints;
    }

    /**
     * Creates a barely connected map based on the length of the roads without any cycle,
     * not creating a path different from the original map.
     */
    public void findBarelyConnectedMap() {
        List<String> sortedPoints = sortPoints();
        PriorityQueue<Edge> edges = new PriorityQueue<>(roads); // Used PriorityQueue to always keep roads in order

        // Union-Find data structure to maintain knowledge of the connected components of cities
        Map<String, String> parent = new HashMap<>();
        Map<String, Integer> rank = new HashMap<>();

        // Initialize each point's parent as itself and its rank as 0 for union-find operations
        for (String point : sortedPoints) {
            parent.put(point, point);
            rank.put(point, 0);
        }

        while (!edges.isEmpty() && barelyConnectedMap.size() < sortedPoints.size() - 1) {
            // Take the shortest road from the sorted list of edges
            Edge shortestRoad = edges.poll();
            String firstPoint = shortestRoad.getFirstPoint();
            String secondPoint = shortestRoad.getSecondPoint();

            // Prevent cycles on the map
            if (find(parent, firstPoint).equals(find(parent, secondPoint))) {
                continue;
            }

            union(parent, rank, firstPoint, secondPoint);
            barelyConnectedMap.add(shortestRoad);
        }
    }

    /**
     * Finds the root of the given point.
     *
     * @param parent The map representing the parent relationship between points
     * @param point  The point whose root is being found
     * @return The root of the given point
     */
    private String find(Map<String, String> parent, String point) {
        if (!parent.get(point).equals(point)) {
            parent.put(point, find(parent, parent.get(point))); // Path compression
        }

        return parent.get(point);
    }

    /**
     * Combines two sets by joining them based on their ranks.
     *
     * @param parent The map representing the parent relationship between points
     * @param rank The map contains the ranks of points
     * @param firstPoint The first point to be united
     * @param secondPoint The second point to be united
     */
    private void union(Map<String, String> parent, Map<String, Integer> rank, String firstPoint, String secondPoint) {
        String firstRoot = find(parent, firstPoint);
        String secondRoot = find(parent, secondPoint);

        if (!firstRoot.equals(secondRoot)) {
            int rank1 = rank.get(firstRoot);
            int rank2 = rank.get(secondRoot);

            if (rank1 > rank2) {
                parent.put(secondRoot, firstRoot);
            } else if (rank1 < rank2) {
                parent.put(firstRoot, secondRoot);
            } else { // If the ranks are equal, choose either root to become the new parent
                parent.put(secondRoot, firstRoot);
                // Increment the rank of the chosen root
                rank.put(firstRoot, rank1 + 1);
            }
        }
    }

    /**
     * Prints path of the found barely connected map from starting point to ending point.
     */
    public void printBarelyConnectedMap() {
        FileOutput.writeToFile(outputPath, "Roads of Barely Connected Map is:", true, true);

        for (Edge road : barelyConnectedMap) {
            FileOutput.writeToFile(outputPath, road.getFirstPoint() + "\t" + road.getSecondPoint() + "\t" +
                    road.getDistance() + "\t" + road.getRoadID(), true, true);
            // Calculate the total used construction material on the barely connected map, while printing paths
            constructionMaterial += road.getDistance();
        }
    }

    public double getConstructionMaterial() {
        return constructionMaterial;
    }

    public List<Edge> getBarelyConnectedMap() {
        return barelyConnectedMap;
    }
}
