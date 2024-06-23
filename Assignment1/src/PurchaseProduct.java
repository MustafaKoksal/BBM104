
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PurchaseProduct {
    private final String[] productLines;
    private final String[] lines;
    private int lineIndex;
    private int change;
    private final List<Integer> allMoney = new ArrayList<>();
    private final List<String> allChoices = new ArrayList<>();
    private final List<Integer> allValues = new ArrayList<>();
    private int totalGivenMoney = 0;
    private final List<String> purchasedItems = new ArrayList<>();
    private final String outputFilePath;
    private final List<Integer> purchasedSlotNumbers = new ArrayList<>();
    private String[] machine;
    private List<Boolean> invalidMoney = new ArrayList<>();

    // Get lines from input files
    public PurchaseProduct(String[] productLines, String[] purchaseLines, String outputFilePath) {
        this.productLines = productLines;
        this.lines = purchaseLines;
        this.outputFilePath = outputFilePath;
    }

    // Calculate total money by converting strings to integers
    private int calculateTotalMoney(String[] moneyString) {
        totalGivenMoney = 0;
        boolean isInvalidMoneyAdded = false;

        for (String money : moneyString) {
            if (!Objects.equals(money, "1") && !Objects.equals(money, "5") && !Objects.equals(money, "10") &&
                    !Objects.equals(money, "20") && !Objects.equals(money, "50") &&
                    !Objects.equals(money, "100") && !Objects.equals(money, "200")) {
                invalidMoney.add(true);
                isInvalidMoneyAdded = true;
            } else {
                totalGivenMoney += Integer.parseInt(money);
            }

        }

        if (!isInvalidMoneyAdded) {
            invalidMoney.add(false);
        }

        return totalGivenMoney;
    }

    // Make the purchase by informing the customer
    public int purchase() {
        MachineFiller GMM = new MachineFiller(productLines, outputFilePath);

        // Firstly fill the machine before purchases
        GMM.processFirstInput();
        GMM.fill(false);

        // Make required lists according to products
        List<Integer> productPrices = GMM.getMoneyList();
        List<Double> valuesOfCarb = GMM.getValueOfCarbohydrate();
        List<Double> valuesOfProtein = GMM.getValueOfProtein();
        List<Double> valuesOfFat = GMM.getValueOfFat();
        List<String> productsList = GMM.getProductsList();
        List<Double> calorieList = GMM.getValOfCalorieList();

        boolean isProductFound;

        while (lineIndex < lines.length) {
            isProductFound = false;

            // Always update the machine after purchases
            GMM.updateMachine(purchasedItems, purchasedSlotNumbers);
            machine = GMM.getMachine();

            purchasedSlotNumbers.clear();
            purchasedItems.clear();

            FileOutput.writeToFile(outputFilePath,"INPUT: " + lines[lineIndex], true, true);

            // Find the product suitable for the desired CALORIE
            if (Objects.equals(allChoices.get(lineIndex), "CALORIE")) {
                if (invalidMoney.get(lineIndex)) {
                    FileOutput.writeToFile(outputFilePath,"INFO: Invalid money, please try again.", true, true);
                }
                findProduct(productPrices, productsList, calorieList, isProductFound);

            }

            // Find the product suitable for the desired CARBOHYDRATE
            if (Objects.equals(allChoices.get(lineIndex), "CARB")) {
                if (invalidMoney.get(lineIndex)) {
                    FileOutput.writeToFile(outputFilePath,"INFO: Invalid money, please try again.", true, true);
                }
                findProduct(productPrices, productsList, valuesOfCarb, isProductFound);

            }

            // Find the product suitable for the desired PROTEIN
            if (Objects.equals(allChoices.get(lineIndex), "PROTEIN")) {
                if (invalidMoney.get(lineIndex)) {
                    FileOutput.writeToFile(outputFilePath,"INFO: Invalid money, please try again.", true, true);
                }
                findProduct(productPrices, productsList, valuesOfProtein, isProductFound);
            }

            // Find the product suitable for the desired FAT
            if (Objects.equals(allChoices.get(lineIndex), "FAT")) {
                if (invalidMoney.get(lineIndex)) {
                    FileOutput.writeToFile(outputFilePath,"INFO: Invalid money, please try again.", true, true);
                }
                findProduct(productPrices, productsList, valuesOfFat, isProductFound);
            }

            // Find the slot of the desired number
            if (Objects.equals(allChoices.get(lineIndex), "NUMBER")) {
                if (invalidMoney.get(lineIndex)) {
                    FileOutput.writeToFile(outputFilePath,"INFO: Invalid money, please try again.", true, true);
                } else {
                    // Case for inappropriate numbers
                    if (!(allValues.get(lineIndex) < 25 && allValues.get(lineIndex) >= 0)) {
                        FileOutput.writeToFile(outputFilePath,"INFO: Number cannot be accepted. Please try again with another number.", true, true);
                        FileOutput.writeToFile(outputFilePath,"RETURN: Returning your change: " + allMoney.get(lineIndex) + " TL", true, true);
                        isProductFound = true;
                    }
                    // Case for empty slots after purchases
                    else if (Objects.equals(machine[allValues.get(lineIndex)], "")) {
                        FileOutput.writeToFile(outputFilePath, "INFO: This slot is empty, your money will be returned.", true, true);
                        FileOutput.writeToFile(outputFilePath,"RETURN: Returning your change: " + allMoney.get(lineIndex) + " TL", true, true);
                        isProductFound = true;

                    }
                    // Find the product without any problems
                    else if ((allValues.get(lineIndex) < productsList.size() && allValues.get(lineIndex) >= 0)) {
                        change = allMoney.get(lineIndex) - productPrices.get(allValues.get(lineIndex));

                        if (change >= 0) {
                            FileOutput.writeToFile(outputFilePath, "PURCHASE: You have bought one " + productsList.get(allValues.get(lineIndex)), true, true);
                            purchasedSlotNumbers.add(allValues.get(lineIndex));
                            isProductFound = true;
                            change(lineIndex, allMoney, productPrices, allValues.get(lineIndex));
                        } else {
                            change(lineIndex, allMoney, productPrices, allValues.get(lineIndex));
                        }

                    }
                    // Case for empty slots after the first machine filling
                    else {
                        FileOutput.writeToFile(outputFilePath,"INFO: This slot is empty, your money will be returned.", true, true);
                        FileOutput.writeToFile(outputFilePath,"RETURN: Returning your change: " + allMoney.get(lineIndex) + " TL", true, true);
                        isProductFound = true;
                    }
                }
            }



            lineIndex++;
        }
        GMM.updateMachine(purchasedItems, purchasedSlotNumbers);
        return 0;
    }

    // Find appropriate product from Gym Meal Machine according to the customer's request
    private void findProduct(List<Integer> productPrices, List<String> productsList, List<Double> nutritionList, boolean isProductFound) {
        for (int i = 0; i < productsList.size(); i++) {
            // Find the product that may be between 5 more or less than the desired nutritional value
            if (allValues.get(lineIndex) - 5 <= nutritionList.get(i) && allValues.get(lineIndex) + 5 >= nutritionList.get(i)) {
                change = allMoney.get(lineIndex) - productPrices.get(i);

                // Check the customer whether customer has enough money or not
                if (change >= 0) {
                    FileOutput.writeToFile(outputFilePath,"PURCHASE: You have bought one " + productsList.get(i), true, true);
                    purchasedItems.add(productsList.get(i));
                }

                isProductFound = true;
                change(lineIndex, allMoney, productPrices, i);
                break;
            }
        }

        if (!isProductFound) {
            FileOutput.writeToFile(outputFilePath,"INFO: Product not found, your money will be returned.", true, true);
            FileOutput.writeToFile(outputFilePath,"RETURN: Returning your change: " + allMoney.get(lineIndex) + " TL", true, true);
        }
    }

    // Calculate change for the product then inform the customers about their change
    private void change(int lineIndex, List<Integer> allMoney, List<Integer> productPrices, int i) {
        change = allMoney.get(lineIndex) - productPrices.get(i);
        
        if (change >= 0) {
            FileOutput.writeToFile(outputFilePath,"RETURN: Returning your change: " + change + " TL", true, true);
        } else {
            change = allMoney.get(lineIndex);
            FileOutput.writeToFile(outputFilePath,"INFO: Insufficient money, try again with more money.", true, true);
            FileOutput.writeToFile(outputFilePath,"RETURN: Returning your change: " + change + " TL", true, true);
        }
    }

    // Take purchase information
    public void processSecondInput() {
        for (String line : lines) {
            String[] parts = line.split("\t");
            String[] moneyString = parts[1].split(" ");
            totalGivenMoney = calculateTotalMoney(moneyString);

            // Make lists about their requests and money
            allMoney.add(totalGivenMoney);
            allChoices.add(parts[2]);
            allValues.add(Integer.parseInt(parts[3]));
        }

    }

    public String[] getMachine() {
        return machine;
    }
}