public class PremiumVoyage extends Voyage {
    private int refundCut;
    private int premiumFee;

    public PremiumVoyage(int voyageID, int totalSeats, String typeOfBus, String fromTo, String toWhere,
                         double pricePerSeat, int refundCut, int premiumFee, int numberOfRows) {
        super(voyageID, totalSeats, typeOfBus, fromTo, toWhere, pricePerSeat, numberOfRows, 0, 0);
        this.refundCut = refundCut;
        this.premiumFee = premiumFee;
    }

    @Override
    public double calculateRefundedMoney(int seatNumber) {
        return calculatePricePerSeat(seatNumber) - calculatePricePerSeat(seatNumber) * refundCut / 100;

    }

    @Override
    public double calculatePricePerSeat(int seatNumber) {
        // If seat is premium, calculate price with additional premium fee
        if (seatNumber % 3 == 1) {
            return getPricePerSeat() + getPricePerSeat() * premiumFee / 100;
        } else {
            return getPricePerSeat();
        }
    }

    // Calculate total seats as 1+2
    @Override
    public int calculateTotalSeat(int numberOfRows) {
        return numberOfRows * 3;
    }
}
