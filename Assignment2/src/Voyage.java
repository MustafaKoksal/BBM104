public class Voyage {
    private int voyageID;
    private int totalSeats;
    private String[] seats;
    private String typeOfBus;
    private String fromTo;
    private String toWhere;
    private double pricePerSeat;
    private double revenue;

    public Voyage(int voyageID, int totalSeats, String typeOfBus, String fromTo, String toWhere, double pricePerSeat, int numberOfRows, int refundCut, int premiumFee) {
        this.voyageID = voyageID;
        this.totalSeats = totalSeats;
        this.typeOfBus = typeOfBus;
        this.fromTo = fromTo;
        this.toWhere = toWhere;
        this.pricePerSeat = pricePerSeat;

    }

    // Make all bus seats empty
    public void initializeSeats() {
        seats = new String[totalSeats];
        for (int i = 0; i < totalSeats; i++) {
            seats[i] = "*";
        }
    }

    // Price per seat will calculate with polymorphism in other type of buses
    public double calculatePricePerSeat(int seatNumber) {
        return pricePerSeat;
    }

    // Refunded money will calculate with polymorphism in other type of buses
    public double calculateRefundedMoney(int seatNumber) {
        return pricePerSeat;
    }

    public int calculateTotalSeat(int numberOfRows) {
        return numberOfRows * 2;
    }

    public int calculateRegularSeats(int numberOfRows) {
        return numberOfRows * 2;
    }

    public int getVoyageID() {
        return voyageID;
    }

    public String[] getSeats() {
        return seats;
    }

    public String getTypeOfBus() {
        return typeOfBus;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public double getPricePerSeat() {
        return pricePerSeat;
    }

    public double getRevenue() { return revenue; }

    public String getFromTo() { return fromTo; }

    public String getToWhere() { return toWhere; }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public void setSeats(String[] seats) {
        this.seats = seats;
    }

    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
}
