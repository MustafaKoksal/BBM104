public class Edge implements Comparable<Edge>{
    private final int distance;
    private final int roadID;
    private final String firstPoint;
    private final String secondPoint;

    public Edge(int distance, int roadID, String firstPoint, String secondPoint) {
        this.distance = distance;
        this.roadID = roadID;
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
    }

    /**
     * Compares current road and given road according to their lengths and road IDs.
     *
     * @param road the object to be compared.
     * @return A positive number if the current road is greater, a negative number if the current road is smaller and 0 if they are equal.
     */
    @Override
    public int compareTo(Edge road) {
        // If lengths are equal, compare according to road IDs
        if (distance == road.getDistance()) {
            return Integer.compare(roadID ,road.getRoadID());
        }

        return Integer.compare(distance, road.getDistance());
    }

    public int getDistance() {
        return distance;
    }

    public int getRoadID() {
        return roadID;
    }

    public String getFirstPoint() {
        return firstPoint;
    }

    public String getSecondPoint() {
        return secondPoint;
    }
}
