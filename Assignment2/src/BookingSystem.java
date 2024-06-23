import java.io.FilePermission;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BookingSystem {
    private static List<Voyage> voyages = new ArrayList<>();

    public static void main(String[] args) {
        if (isThereErrorInFiles(args)) {
            return;
        }

        FileOutput.writeToFile(args[1], "", false, false); // Initialize empty output file

        bookingSystem(FileInput.readFile(args[0], true, true), args[1]);
    }

    public static void bookingSystem(String[] commandLines, String outputFilePath) {
        int lineIndex = 1;
        boolean isItLastLine = false;

        // Check if commandLines is empty
        if (commandLines.length == 0 || commandLines == null) {
            FileOutput.writeToFile(outputFilePath, "----------------", true, true);
            FileOutput.writeToFile(outputFilePath, "No Voyages Available!", true, true);
            FileOutput.writeToFile(outputFilePath, "----------------", true, false);
        }

        // Start to read all input lines
        for (String line : commandLines) {
            if (lineIndex == commandLines.length) {
                isItLastLine = true;
            }

            // Perform the process according to given command
            processCommand(line, outputFilePath, isItLastLine);

            String[] parts = line.split("\t");
            String command = parts[0];

            // Print Z_REPORT in the last line, even if command is not Z_REPORT
            if (lineIndex == commandLines.length && !command.equals("Z_REPORT")) {
                FileOutput.writeToFile(outputFilePath, "Z Report:", true, true);

                if (voyages.isEmpty()) {
                    FileOutput.writeToFile(outputFilePath, "----------------", true, true);
                    FileOutput.writeToFile(outputFilePath, "No Voyages Available!", true, true);
                    FileOutput.writeToFile(outputFilePath, "----------------", true, false);
                } else {
                    // Sort voyages by increasing voyage ID's
                    voyages.sort(Comparator.comparing(Voyage::getVoyageID));

                    for (Voyage voyage : voyages) {
                        printVoyageInfo(outputFilePath, voyage);
                        FileOutput.writeToFile(outputFilePath, "----------------", true, false);
                    }
                }
            }

            lineIndex++;
        }
    }

    private static boolean isThereErrorInFiles(String[] args) {
        // If the user tries to enter too many args, give error
        if (args.length > 2) {
            System.err.println("ERROR: This program works exactly with two command line arguments, the first one is the path to the input file whereas the second one is the path to the output file. Sample usage can be as follows: \"java8 BookingSystem input.txt output.txt\". Program is going to terminate!");
            return true;
        }

        try {
            // Check if input file exists or has permission
            Files.readAllLines(Paths.get(args[0]));
        } catch (IOException e) {
            System.err.println("ERROR: This program cannot read from the \"" + args[0] + " \", either this program does not have read permission to read that file or file does not exist. Program is going to terminate!");
            return true;
        }

        // Check if output file is accessible
        if (Files.exists(Paths.get(args[1])) && !Files.isWritable(Paths.get(args[1]))) {
            System.err.println("ERROR: This program cannot write to the \"" + args[1] + "\", please check the permissions to write that directory. Program is going to terminate!");
            return true;
        }

        return false;
    }

    private static void printVoyageInfo(String outputFilePath, Voyage voyage) {
        FileOutput.writeToFile(outputFilePath, "----------------", true, true);
        FileOutput.writeToFile(outputFilePath, "Voyage " + voyage.getVoyageID(), true, true);
        FileOutput.writeToFile(outputFilePath, voyage.getFromTo() + "-" + voyage.getToWhere(), true, true);
        displaySeats(voyage.getSeats(), voyage.getTypeOfBus(), voyage.getTotalSeats(), outputFilePath);
        FileOutput.writeToFile(outputFilePath, String.format("Revenue: %.2f", voyage.getRevenue()), true, true);
    }

    private static void processCommand(String line, String outputFilePath, boolean isItLastLine) {
        String[] parts = line.split("\t");
        String command = parts[0];

        FileOutput.writeToFile(outputFilePath, "COMMAND: " + line, true, true);

        switch (command) {
            case "INIT_VOYAGE":
                initVoyage(parts, outputFilePath);
                break;
            case "PRINT_VOYAGE":
                printVoyage(parts, outputFilePath);
                break;
            case "SELL_TICKET":
                sellTicket(parts, outputFilePath);
                break;
            case "REFUND_TICKET":
                refundTicket(parts, outputFilePath);
                break;
            case "CANCEL_VOYAGE":
                cancelVoyage(parts, outputFilePath);
                break;
            case "Z_REPORT":
                generateZReport(parts, outputFilePath, isItLastLine);
                break;
            default:
                FileOutput.writeToFile(outputFilePath, "ERROR: There is no command namely " + command + "!", true, true);
                break;
        }
    }

    // Check input file if it is empty, give error
    public static Voyage findVoyage(int voyageID) {
        for (Voyage voyage : voyages) {
            if (voyage.getVoyageID() == voyageID) {
                return voyage;
            }
        }
        return null;
    }

    public static void displaySeats(String[] seats, String typeOfBus, int totalSeats, String outputFilePath) {
        // If type of bus is minibus, display seats as 2
        if (typeOfBus.equals("Minibus")) {
            for (int i = 0; i <= totalSeats - 2; i+=2) {
                FileOutput.writeToFile(outputFilePath, String.format("%s %s", seats[i], seats[1 + i]), true, true);
            }
        }

        // If type of bus is standard, display seats as 2+2
        if (typeOfBus.equals("Standard")) {
            for (int i = 0; i <= totalSeats - 4; i+=4) {
                FileOutput.writeToFile(outputFilePath, String.format("%s %s | %s %s", seats[i], seats[1 + i], seats[2 + i], seats[3 + i]), true, true);
            }
        }

        // If type of bus is premium, display seats as 1+2
        if (typeOfBus.equals("Premium")) {
            for (int i = 0; i <= totalSeats - 3; i+=3) {
                FileOutput.writeToFile(outputFilePath, String.format("%s | %s %s", seats[i], seats[1 + i], seats[2 + i]), true, true);
            }
        }
    }

    // Initialize voyage with given information
    private static void initVoyage(String[] parts, String outputFilePath) {
        String command = parts[0]; String typeOfBus; int voyageID = 0; int numberOfRows = 0; int premiumFee;
        double pricePerSeat = 0; boolean isThereError = false; String from_to; double premiumPrice;
        String to_Where; int totalSeat; int refundCut;

        // If there is less or more information than needed, give error
        if (parts.length < 7 || parts.length > 9) {
            FileOutput.writeToFile(outputFilePath, "ERROR: Erroneous usage of \"" + command + "\" command!", true, true);
            return;
        }

        typeOfBus = parts[1];

        // Check if ID's type given correctly
        try {
            voyageID = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            FileOutput.writeToFile(outputFilePath, "ERROR: "+ parts[2] + " is not a positive integer, ID of a voyage must be a positive integer!", true, true);
            return;
        }

        // Check if row's type given correctly
        try {
            numberOfRows = Integer.parseInt(parts[5]);
        } catch (NumberFormatException e) {
            FileOutput.writeToFile(outputFilePath, "ERROR: " + parts[5] + " is not a positive integer, number of seat rows of a voyage must be a positive integer!", true, true);
            return;
        }

        // Check if price's type given correctly
        try {
            pricePerSeat = Double.parseDouble(parts[6]);
        } catch (NumberFormatException e) {
            FileOutput.writeToFile(outputFilePath, "ERROR: " + parts[6] + " is not a positive number, price must be a positive number!", true, true);
            return;
        }

        // Check is there a voyage with given ID
        for (Voyage voyage : voyages) {
            if (voyage.getVoyageID() == voyageID) {
                FileOutput.writeToFile(outputFilePath, "ERROR: There is already a voyage with ID of " + voyageID + "!", true, true);
                isThereError = true;
                break;
            }
        }

        if (isThereError) {
            return;
        }

        if (voyageID <= 0) {
            FileOutput.writeToFile(outputFilePath, "ERROR: "+ voyageID + " is not a positive integer, ID of a voyage must be a positive integer!", true, true);
            return;
        } else if (numberOfRows <= 0) {
            FileOutput.writeToFile(outputFilePath, "ERROR: " + numberOfRows + " is not a positive integer, number of seat rows of a voyage must be a positive integer!", true, true);
            return;
        } else if (pricePerSeat <= 0) {
            FileOutput.writeToFile(outputFilePath, "ERROR: " + (int) pricePerSeat + " is not a positive number, price must be a positive number!", true, true);
            return;
        }

        from_to = parts[3];
        to_Where = parts[4];

        // Apply specific implementation according to type of bus
        switch (typeOfBus) {
            case "Minibus": {
                Voyage voyage = new Voyage(voyageID, 0, typeOfBus, from_to, to_Where, pricePerSeat, numberOfRows, 0, 0);

                // Assign given information to the voyage and initialize
                totalSeat = voyage.calculateTotalSeat(numberOfRows);
                voyage.setTotalSeats(totalSeat);
                voyage.initializeSeats();
                voyages.add(voyage);

                FileOutput.writeToFile(outputFilePath, String.format("Voyage %d was initialized as a minibus (2) voyage from %s to %s with %.2f TL priced %d" +
                        " regular seats. Note that minibus tickets are not refundable.", voyageID, from_to, to_Where, pricePerSeat, totalSeat), true, true);
                break;
            }
            case "Standard": {
                // Check if refund cut's type given correctly
                try {
                    refundCut = Integer.parseInt(parts[7]);
                } catch (NumberFormatException e) {
                    FileOutput.writeToFile(outputFilePath, "ERROR: " + parts[7] + " is not an integer that is in range of [0, 100], refund cut must be an integer that is in range of [0, 100]!", true, true);
                    return;
                }

                // Check if the refund cut is within the range of [0,100]
                if (refundCut < 0 || refundCut > 100) {
                    FileOutput.writeToFile(outputFilePath, "ERROR: " + refundCut + " is not an integer that is in range of [0, 100], refund cut must be an integer that is in range of [0, 100]!", true, true);
                    return;
                }

                Voyage voyage = new StandardVoyage(voyageID, 0, typeOfBus, from_to, to_Where, pricePerSeat, refundCut, numberOfRows);

                // Assign given information to the voyage and initialize
                totalSeat = voyage.calculateTotalSeat(numberOfRows);
                voyage.setTotalSeats(totalSeat);
                voyage.initializeSeats();
                voyages.add(voyage);

                FileOutput.writeToFile(outputFilePath, String.format("Voyage %d was initialized as a standard (2+2) voyage from %s to %s with %.2f TL priced %d" +
                        " regular seats. Note that refunds will be %s less than the paid amount.", voyageID, from_to, to_Where, pricePerSeat, totalSeat, refundCut + "%"), true, true);
                break;
            }
            case "Premium": {
                // Check if refund cut's type is given correctly
                try {
                    refundCut = Integer.parseInt(parts[7]);
                } catch (NumberFormatException e) {
                    FileOutput.writeToFile(outputFilePath, "ERROR: " + parts[7] + " is not an integer that is in range of [0, 100], refund cut must be an integer that is in range of [0, 100]!", true, true);
                    return;
                }

                // Check if premium fee's type is given correctly
                try {
                    premiumFee = Integer.parseInt(parts[8]);
                } catch (NumberFormatException e) {
                    FileOutput.writeToFile(outputFilePath, "ERROR: " + parts[8] + " is not a non-negative integer, premium fee must be a non-negative integer!", true, true);
                    return;
                }

                // Check if the refund cut is within the range of [0,100]
                if (refundCut < 0 || refundCut > 100) {
                    FileOutput.writeToFile(outputFilePath, "ERROR: " + refundCut + " is not an integer that is in range of [0, 100], refund cut must be an integer that is in range of [0, 100]!", true, true);
                    return;
                } else if (premiumFee < 0) {
                    FileOutput.writeToFile(outputFilePath, "ERROR: " + premiumFee + " is not a non-negative integer, premium fee must be a non-negative integer!", true, true);
                    return;
                }

                premiumPrice = pricePerSeat + (pricePerSeat * premiumFee) / 100;

                Voyage voyage = new PremiumVoyage(voyageID, 0, typeOfBus, from_to, to_Where, pricePerSeat, refundCut, premiumFee, numberOfRows);

                int regularSeats = voyage.calculateRegularSeats(numberOfRows);
                int premiumSeats = voyage.calculateTotalSeat(numberOfRows) - regularSeats;

                // Assign given information to the voyage and initialize
                voyage.setTotalSeats(regularSeats + premiumSeats);
                voyage.initializeSeats();
                voyages.add(voyage);

                FileOutput.writeToFile(outputFilePath, String.format("Voyage %d was initialized as a premium (1+2) voyage from %s to %s with %.2f TL priced %d" +
                        " regular seats and %.2f TL priced %d premium seats. Note that refunds will be %s less than the paid amount.", voyageID, from_to, to_Where, pricePerSeat, regularSeats, premiumPrice, premiumSeats, refundCut + "%"), true, true);
                break;
            }
            default:
                FileOutput.writeToFile(outputFilePath, "ERROR: Erroneous usage of \"" + command + "\" command!", true, true);
                break;
        }
    }

    // Print the voyage with given ID
    private static void printVoyage(String[] parts, String outputFilePath) {
        String command = parts[0];
        int selectedID;

        // If there is less or more information than needed, give error
        if (parts.length != 2) {
            FileOutput.writeToFile(outputFilePath, "ERROR: Erroneous usage of \"" + command + "\" command!", true, true);
            return;
        }

        // Check if ID's type given correctly
        try {
            selectedID = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            FileOutput.writeToFile(outputFilePath, "ERROR: " + parts[1] + " is not a positive integer, ID of a voyage must be a positive integer!", true, true);
            return;
        }

        if (selectedID <= 0) {
            FileOutput.writeToFile(outputFilePath, "ERROR: " + selectedID + " is not a positive integer, ID of a voyage must be a positive integer!", true, true);
            return;
        }

        if (findVoyage(selectedID) == null) {
            FileOutput.writeToFile(outputFilePath, "ERROR: There is no voyage with ID of " + selectedID + "!", true, true);
            return;
        }

        // Print information about the voyage
        for (Voyage voyage : voyages) {
            if (voyage.getVoyageID() == selectedID) {
                FileOutput.writeToFile(outputFilePath, "Voyage " + voyage.getVoyageID(), true, true);
                FileOutput.writeToFile(outputFilePath, voyage.getFromTo() + "-" + voyage.getToWhere(), true, true);
                displaySeats(voyage.getSeats(), voyage.getTypeOfBus(), voyage.getTotalSeats(), outputFilePath);
                FileOutput.writeToFile(outputFilePath, String.format("Revenue: %.2f", voyage.getRevenue()), true, true);
            }
        }
    }

    // Sell desired seat(s) with own special(regular or premium) prices
    private static void sellTicket(String[] parts, String outputFilePath) {
        String command = parts[0]; boolean isSeatAlreadySold = false; boolean isVoyageFound = false;
        double previousRevenue; boolean isSeatFound = true; boolean isErroneous = false; int voyageIDOfTheSoldSeat = 0;

        // If there is less or more information than needed, give error
        if (parts.length != 3) {
            FileOutput.writeToFile(outputFilePath, "ERROR: Erroneous usage of \"" + command + "\" command!", true, true);
            return;
        }

        // Check if ID's type given correctly
        try {
            voyageIDOfTheSoldSeat = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            FileOutput.writeToFile(outputFilePath, "ERROR: " + parts[1] + " is not a positive integer, ID of a voyage must be a positive integer!", true, true);
            return;
        }

        String[] soldSeatNumbers = parts[2].split("_");

        double earnedMoney = 0;

        for (Voyage voyage : voyages) {
            isSeatAlreadySold = false;

            // Find the desired voyage
            if (voyageIDOfTheSoldSeat == voyage.getVoyageID()) {
                isVoyageFound = true;
                String[] seats = voyage.getSeats();
                previousRevenue = voyage.getRevenue();
                String[] previousSeats = new String[seats.length];
                // If an error occurs, use deep copy to return the previous seats
                System.arraycopy(seats, 0, previousSeats, 0, seats.length);

                for (String seatNumberStr : soldSeatNumbers) {
                    int seatNumber;

                    // Check if seat number's type given correctly
                    try {
                        seatNumber = Integer.parseInt(seatNumberStr);
                    } catch (NumberFormatException e) {
                        FileOutput.writeToFile(outputFilePath, "ERROR: " + seatNumberStr + " is not a positive integer, seat number must be a positive integer!", true, true);
                        isErroneous = true;
                        break;
                    }

                    if (seatNumber <= 0) {
                        FileOutput.writeToFile(outputFilePath, "ERROR: " + seatNumber + " is not a positive integer, seat number must be a positive integer!", true, true);
                        isSeatFound = false;
                        break;
                    }

                    // If desired seat is full or not exist, give error
                    try {
                        if (seats[seatNumber - 1].equals("X")) {
                            FileOutput.writeToFile(outputFilePath, "ERROR: One or more seats already sold!", true, true);
                            isSeatAlreadySold = true;

                            voyage.setRevenue(previousRevenue);
                            voyage.setSeats(previousSeats);
                            break;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        FileOutput.writeToFile(outputFilePath, "ERROR: There is no such a seat!", true, true);
                        isSeatFound = false;
                        break;
                    }

                    // Make the sold seat full, if it is existed
                    try {
                        seats[seatNumber - 1] = "X";
                    } catch (IndexOutOfBoundsException e) {
                        FileOutput.writeToFile(outputFilePath, "ERROR: There is no such a seat!", true, true);
                        isSeatFound = false;
                        break;
                    }

                    // Update revenue of the voyage
                    earnedMoney += voyage.calculatePricePerSeat(seatNumber);
                    voyage.setRevenue(voyage.getRevenue() + voyage.calculatePricePerSeat(seatNumber));
                }
            }
        }

        if (!isVoyageFound) {
            FileOutput.writeToFile(outputFilePath, "ERROR: There is no voyage with ID of " + voyageIDOfTheSoldSeat + "!", true, true);
            return;
        }

        if (!isSeatFound || isSeatAlreadySold || isErroneous) {
            return;
        }

        FileOutput.writeToFile(outputFilePath, String.format("Seat %s of the Voyage %d from %s to %s was successfully sold for %.2f TL.",
                String.join("-", soldSeatNumbers), voyageIDOfTheSoldSeat,
                findVoyage(voyageIDOfTheSoldSeat).getFromTo(), findVoyage(voyageIDOfTheSoldSeat).getToWhere(),
                earnedMoney), true, true);
    }

    // Refund desired seat(s) with its own special refund cut
    private static void refundTicket(String[] parts, String outputFilePath) {
        String command = parts[0]; boolean isVoyageFound = false; boolean isEmptySeat = false;
        double refundedMoney = 0; double previousRevenue; boolean isSeatFound = true; int voyageIDOfRefundedTicket;

        // If there is more or less information than needed, give error
        if (parts.length != 3) {
            FileOutput.writeToFile(outputFilePath, "ERROR: Erroneous usage of \"" + command + "\" command!", true, true);
            return;
        }

        // Check if the voyage's ID given correctly
        try {
            voyageIDOfRefundedTicket = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            FileOutput.writeToFile(outputFilePath, "ERROR: " + parts[1] + " is not a positive integer, ID of a voyage must be a positive integer!", true, true);
            return;
        }

        Voyage voyage = findVoyage(voyageIDOfRefundedTicket);

        if (voyage == null) {
            FileOutput.writeToFile(outputFilePath, "ERROR: There is no voyage with ID of " + voyageIDOfRefundedTicket + "!", true, true);
            return;
        }

        // Use refund procedures if type of voyage is not minibus
        if (voyage.getTypeOfBus().equals("Minibus")) {
            FileOutput.writeToFile(outputFilePath, "ERROR: Minibus tickets are not refundable!", true, true);
        } else {
            String[] refundedSeats = parts[2].split("_");

            // Find desired voyage
            if (voyageIDOfRefundedTicket == voyage.getVoyageID()) {
                String[] seats = voyage.getSeats();
                isVoyageFound = true;
                refundedMoney = 0;
                previousRevenue = voyage.getRevenue();
                String[] previousSeats = new String[seats.length];
                // If an error occurs, use deep copy to return the previous seats
                System.arraycopy(seats, 0, previousSeats, 0, seats.length);

                for (String seatNumberStr : refundedSeats) {
                    int seatNumber = Integer.parseInt(seatNumberStr);

                    if (seatNumber <= 0) {
                        FileOutput.writeToFile(outputFilePath, "ERROR: " + seatNumber + " is not a positive integer, seat number must be a positive integer!", true, true);
                        isSeatFound = false;
                        break;
                    }

                    // If the seat to be refunded is already empty or not exist, give error
                    try {
                        if (seats[seatNumber - 1].equals("*")) {
                            FileOutput.writeToFile(outputFilePath, "ERROR: One or more seats are already empty!", true, true);
                            voyage.setRevenue(previousRevenue);
                            voyage.setSeats(previousSeats);
                            isEmptySeat = true;
                            break;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        FileOutput.writeToFile(outputFilePath, "ERROR: There is no such a seat!", true, true);
                        isSeatFound = false;
                        break;
                    }

                    // Make the refunded seat empty, if it is existed
                    try {
                        seats[seatNumber - 1] = "*";
                    } catch (IndexOutOfBoundsException e) {
                        FileOutput.writeToFile(outputFilePath, "ERROR: There is no such a seat!", true, true);
                        isSeatFound = false;
                        break;
                    }

                    // Update revenue of the voyage
                    voyage.setRevenue(voyage.getRevenue() - voyage.calculateRefundedMoney(seatNumber));
                    refundedMoney += voyage.calculateRefundedMoney(seatNumber);
                }
            }

            if (!isVoyageFound) {
                FileOutput.writeToFile(outputFilePath, "ERROR: There is no voyage with ID of " + voyageIDOfRefundedTicket + "!", true, true);
                return;
            }

            if (!isSeatFound || isEmptySeat) {
                return;
            }

            FileOutput.writeToFile(outputFilePath, String.format("Seat %s of the Voyage %d from %s to %s was successfully refunded for %.2f TL.",
                    String.join("-", refundedSeats), voyageIDOfRefundedTicket,
                    findVoyage(voyageIDOfRefundedTicket).getFromTo(), findVoyage(voyageIDOfRefundedTicket).getToWhere(),
                    refundedMoney) , true, true);
        }
    }

    private static void cancelVoyage(String[] parts, String outputFilePath) {
        String command = parts[0]; int IDOfRemovedVoyage = 0;

        // If there is less or more information than needed, give error
        if (parts.length != 2) {
            FileOutput.writeToFile(outputFilePath, "ERROR: Erroneous usage of \"" + command + "\" command!", true, true);
            return;
        }

        // Check if ID's type given correctly
        try {
            IDOfRemovedVoyage = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            FileOutput.writeToFile(outputFilePath, "ERROR: " + parts[1] + " is not a positive integer, ID of a voyage must be a positive integer!", true, true);
            return;
        }

        if (IDOfRemovedVoyage <= 0) {
            FileOutput.writeToFile(outputFilePath, "ERROR: " + IDOfRemovedVoyage + " is not a positive integer, ID of a voyage must be a positive integer!", true, true);
            return;
        }

        Voyage voyage = findVoyage(IDOfRemovedVoyage);

        if (voyage == null) {
            FileOutput.writeToFile(outputFilePath, "ERROR: There is no voyage with ID of " + IDOfRemovedVoyage + "!", true, true);
            return;
        }

        FileOutput.writeToFile(outputFilePath, "Voyage " + IDOfRemovedVoyage + " was successfully cancelled!", true, true);
        FileOutput.writeToFile(outputFilePath, "Voyage details can be found below:", true, true);

        String[] seats = voyage.getSeats();
        int seatNumber = 1;

        // Refund return fees to passengers without refund cut
        for (String seat : seats) {
            if (seat.equals("X")) {
                voyage.setRevenue(voyage.getRevenue() - voyage.calculatePricePerSeat(seatNumber));
            }
            seatNumber++;
        }

        // Print information about the voyage
        if (voyage.getVoyageID() == IDOfRemovedVoyage) {
            FileOutput.writeToFile(outputFilePath, "Voyage " + voyage.getVoyageID(), true, true);
            FileOutput.writeToFile(outputFilePath, voyage.getFromTo() + "-" + voyage.getToWhere(), true, true);
            displaySeats(voyage.getSeats(), voyage.getTypeOfBus(), voyage.getTotalSeats(), outputFilePath);
            FileOutput.writeToFile(outputFilePath, String.format("Revenue: %.2f", voyage.getRevenue()), true, true);
        }

        voyages.remove(voyage);
    }

    // Print information and seats of all voyages
    private static void generateZReport(String[] parts, String outputFilePath, boolean isItLastLine) {
        String command = parts[0];

        // If given information is more or less than needed, give error
        if (parts.length != 1) {
            FileOutput.writeToFile(outputFilePath, "ERROR: Erroneous usage of \"" + command + "\" command!", true, true);
            return;
        }

        FileOutput.writeToFile(outputFilePath, "Z Report:", true, true);

        if (voyages.isEmpty()) {
            FileOutput.writeToFile(outputFilePath, "----------------", true, true);
            FileOutput.writeToFile(outputFilePath, "No Voyages Available!", true, true);
        } else {
            // Sort voyages by increasing voyage ID's
            voyages.sort(Comparator.comparing(Voyage::getVoyageID));

            for (Voyage voyage : voyages) {
                printVoyageInfo(outputFilePath, voyage);
            }
        }

        FileOutput.writeToFile(outputFilePath, "----------------", true, !isItLastLine);
    }
}