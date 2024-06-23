public class StandardVoyage extends Voyage {
    private int refundCut;

    public StandardVoyage(int voyageID, int totalSeats, String typeOfBus, String fromTo, String toWhere, double pricePerSeat, int refundCut, int numberOfRows) {
        super(voyageID, totalSeats, typeOfBus, fromTo, toWhere, pricePerSeat, numberOfRows, 0, 0);
        this.refundCut = refundCut;
    }

    @Override
    public double calculateRefundedMoney(int seatNumber) {
        return getPricePerSeat() - getPricePerSeat() * refundCut / 100;
    }

    // Calculate total seats as 2+2
    @Override
    public int calculateTotalSeat(int numberOfRows) {
        return numberOfRows * 4;
    }

    // Calculate regular seats as 2+2
    @Override
    public int calculateRegularSeats(int numberOfRows) {
        return numberOfRows * 4;
    }
}
