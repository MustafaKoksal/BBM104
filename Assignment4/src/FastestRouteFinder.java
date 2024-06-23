import java.util.*;

public class FastestRouteFinder {
    private final String outputPath;
    private final PriorityQueue<Edge> edges = new PriorityQueue<>();
    private final Map<String, Integer> shortestDistances = new HashMap<>();
    private final Map<String, Edge> shortestRoads = new HashMap<>();
    private final Set<String> visited = new HashSet<>();
    private final List<Edge> roads;
    private int totalDistance;

    public FastestRouteFinder(List<Edge> roads, String outputPath) {
        this.roads = roads;
        this.outputPath = outputPath;
    }

    /**
     * Finds fastest route from starting point to ending point with given roads according to their lengths.
     * If their lengths are equal, finds according to their road IDs.
     *
     * @param startingPoint Starting point of the route
     */
    public void findFastestRoute(String startingPoint) {
        // Initialize edges with starting point (road ID determined as -1 to avoid confusion with other edges)
        edges.add(new Edge(0, -1, startingPoint, startingPoint));
        shortestDistances.put(startingPoint, 0);

        while (!edges.isEmpty()) {
            // Take the shortest road from the sorted list of edges
            Edge shortestRoad = edges.poll();
            String targetPoint = shortestRoad.getSecondPoint();

            // If the target city is already visited, continue from next city
            if (visited.contains(targetPoint)) {
                continue;
            }

            visited.add(targetPoint);
            shortestRoads.put(targetPoint, shortestRoad);

            for (Edge road : roads) {
                String firstPoint = road.getFirstPoint();
                String secondPoint = road.getSecondPoint();
                int newDistance = shortestRoad.getDistance() + road.getDistance();

                // Check if the first endpoint of the current road matches the target city and there is no shorter distance than the current new distance
                // and there is no existing shorter distance recorded for the second endpoint of the road
                if (firstPoint.equals(targetPoint) && (!shortestDistances.containsKey(secondPoint) || newDistance < shortestDistances.get(secondPoint))) {
                    shortestDistances.put(secondPoint, newDistance);
                    edges.add(new Edge(newDistance, road.getRoadID(), firstPoint, secondPoint));
                }

                // Apply the same operations to the second endpoint of the current road because roads have two lanes
                if (secondPoint.equals(targetPoint) && (!shortestDistances.containsKey(firstPoint) || newDistance < shortestDistances.get(firstPoint))) {
                    shortestDistances.put(firstPoint, newDistance);
                    edges.add(new Edge(newDistance, road.getRoadID(), secondPoint, firstPoint));
                }
            }
        }
    }

    /**
     * Prints path of the found fastest route from starting point to ending point.
     *
     * @param startingPoint Starting point of the route
     * @param endPoint Ending point of the route
     * @param inputLines A list of strings which taken from input file
     * @param isBCM If true, adds an extra message to the print message of the fastest route
     */
    public void printPath(String startingPoint, String endPoint, String[] inputLines, boolean isBCM) {
        List<Edge> pathEdges = new LinkedList<>();
        String currentPoint = endPoint;
        boolean isEndPoint = true;
        totalDistance = 0;

        // Trace back the path from the endpoint to the starting point
        while (!currentPoint.equals(startingPoint)) {
            Edge edge = shortestRoads.get(currentPoint);
            pathEdges.add(0, edge);
            currentPoint = edge.getFirstPoint();

            // The end edge has total distance of the fastest route because it traces back
            if (isEndPoint) {
                totalDistance = edge.getDistance();
                isEndPoint = false;
            }
        }

        // If it is barely connected map, add an extra message
        if (isBCM) {
            FileOutput.writeToFile(outputPath, "Fastest Route from " +  startingPoint + " to " + endPoint + " on Barely Connected Map ("+ totalDistance +" KM):", true, true);
        } else {
            FileOutput.writeToFile(outputPath, "Fastest Route from " +  startingPoint + " to " + endPoint + " ("+ totalDistance +" KM):", true, true);
        }

        int lineIndex = 0;

        // Print each road that found in the fastest route
        for (Edge edge : pathEdges) {
            for (String line : inputLines) {
                if (lineIndex == 0) {
                    lineIndex++;
                    continue;
                }

                String[] parts = line.split("\t");
                String currentRoadID = parts[3];

                // Print the whole line instead of taking each information of road
                if (Integer.parseInt(currentRoadID) == edge.getRoadID()) {
                    FileOutput.writeToFile(outputPath, line, true, true);
                }
                lineIndex++;
            }
            lineIndex = 0;
        }
    }

    public int getTotalDistance() {
        return totalDistance;
    }
}
