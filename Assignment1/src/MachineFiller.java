import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MachineFiller {
    private final String[] lines;
    private final List<String> products = new ArrayList<>();
    private final List<Double> valueOfProtein = new ArrayList<>();
    private final List<Double> valueOfCarbohydrate = new ArrayList<>();
    private final List<Double> valueOfFat = new ArrayList<>();
    private final String[] machine = new String[24];
    private final List<Integer> money = new ArrayList<>();
    private final List<Integer> moneyList = new ArrayList<>();
    private final List<Double> valOfCarbList = new ArrayList<>();
    private final List<Double> valOfProtList = new ArrayList<>();
    private final List<Double> valOfFatList = new ArrayList<>();
    private final List<String> productsList = new ArrayList<>();
    private final List<Double> valOfCalorieList = new ArrayList<>();
    private final String outputFilePath;

    // Get lines from input files
    public MachineFiller(String[] lines, String outputFilePath) {
        this.lines = lines;
        this.outputFilePath = outputFilePath;
    }

    public List<Double> calculateCalorie() {
        double calorie;
        List<Double> calories = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
            calorie = 4 * valueOfProtein.get(i) + 4 * valueOfCarbohydrate.get(i) + 9 * valueOfFat.get(i);
            calories.add((double) Math.round(calorie));
        }

        return calories;
    }

    // Fill the empty machine with given products
    public void fill(boolean printMessage) {
        String empty = "";
        int currentIndex = 0;
        int numberOfProducts = products.size();
        boolean break1OuterLoop;
        int amount = 1;
        int filledSlots = 0;

        // Create an empty machine with 24 slots
        for (int slot = 0; slot < 24; slot++) {
                machine[slot] = empty;
        }

        while (currentIndex < numberOfProducts) {
            String currentProduct = products.get(currentIndex);
            break1OuterLoop = false;

            for (int slot = 0; slot < 24; slot++) {
                    String[] parts = machine[slot].split("\\(");

                    // Increase amount in the slot if products are the same and less than 10
                    if (Objects.equals(parts[0], currentProduct)) {
                        String[] nums = parts[1].split(", ");
                        int currentAmount = Integer.parseInt(nums[1].substring(0, nums[1].length() - 1));

                        if (currentAmount < 10) {
                            currentAmount++;

                            if (currentAmount == 10) {
                                filledSlots++;
                            }

                            machine[slot] = String.format("%s(%s, %d)", parts[0], nums[0], currentAmount);

                            // If all the slots are filled, stop filling
                            if (filledSlots == 24 && printMessage) {
                                FileOutput.writeToFile(outputFilePath,"INFO: There is no available place to put " + products.get(currentIndex + 1), true, true);
                                FileOutput.writeToFile(outputFilePath,"INFO: The machine is full!", true, true);
                                break1OuterLoop = true;

                                break;
                            }
                            break;
                        }

                    // If the slot is empty, place the product
                    } else if (Objects.equals(machine[slot], empty)) {
                        machine[slot] = String.format("%s(%.0f, %d)", currentProduct, calculateCalorie().get(currentIndex), amount);

                        // Make lists about filled products
                        productsList.add(products.get(currentIndex));
                        moneyList.add(money.get(currentIndex));
                        valOfCarbList.add(valueOfCarbohydrate.get(currentIndex));
                        valOfProtList.add(valueOfProtein.get(currentIndex));
                        valOfFatList.add(valueOfFat.get(currentIndex));
                        valOfCalorieList.add(calculateCalorie().get(currentIndex));

                        break;
                    }

                    // If it is in the last slot, and we still haven't placed the product, print info
                    if (slot == 23 && printMessage) {
                        FileOutput.writeToFile(outputFilePath,"INFO: There is no available place to put " + currentProduct, true, true);
                    }

            }

            if (break1OuterLoop) {
                break;
            }

            currentIndex++;
        }
    }

    // Take product information
    public void processFirstInput() {
        for (String line : lines) {
            String[] parts = line.split("\t");
            String[] nutritionalValues = parts[2].split(" ");

            // Make lists about product's information
            products.add(parts[0]);
            money.add(Integer.parseInt(parts[1]));
            valueOfProtein.add(Double.parseDouble(nutritionalValues[0]));
            valueOfCarbohydrate.add(Double.parseDouble(nutritionalValues[1]));
            valueOfFat.add(Double.parseDouble(nutritionalValues[2]));
        }

    }

    // Update the machine after purchases
    public void updateMachine(List<String> purchasedProducts, List<Integer> purchasedSlotNumbers) {
        // Update machine according to their names
        for (String product : purchasedProducts) {
            for (int i = 0; i < machine.length; i++) {
                String[] parts = machine[i].split("\\(");
                String productName = parts[0];

                if (productName.equals(product)) {
                    if (updateSlots(i, parts, productName)) break;
                }
            }
        }

        // Update machine according to slot numbers
        for (int number : purchasedSlotNumbers) {
            for (int i = 0; i < machine.length; i++) {
                if (!Objects.equals(machine[i], "")) {
                    String[] parts = machine[i].split("\\(");
                    String productName = parts[0];

                    if (number == i) {
                        if (updateSlots(i, parts, productName)) break;
                    }
                }
            }
        }
    }

    // Update slots after purchases
    private boolean updateSlots(int i, String[] parts, String productName) {
        String[] nums = parts[1].split(", ");
        int currentAmount = Integer.parseInt(nums[1].substring(0, nums[1].length() - 1));

        if (currentAmount > 0 && !Objects.equals(machine[i], "")) {
            currentAmount--;

            if (currentAmount == 0) {
                machine[i] = "";
            } else {
                machine[i] = String.format("%s(%s, %d)", productName, nums[0], currentAmount);
            }

            return true;
        }
        return false;
    }

    public void printMachine(String[] machine) {
        FileOutput.writeToFile(outputFilePath,"-----Gym Meal Machine-----", true, true);

        for (int row = 0; row < 24; row++) {
                if (Objects.equals(machine[row], "")) {
                    machine[row] = "___(0, 0)";
                }
                if (row % 4 == 3) {
                    FileOutput.writeToFile(outputFilePath,machine[row] + "___\n", true, false);
                } else {
                    FileOutput.writeToFile(outputFilePath,machine[row] + "___", true, false);
                }
        }

        FileOutput.writeToFile(outputFilePath,"----------", true, true);
    }

    public List<Double> getValOfCalorieList() {
        return valOfCalorieList;
    }

    public List<String> getProductsList() {
        return productsList;
    }

    public List<Double> getValueOfProtein() {
        return valOfProtList;
    }

    public List<Double> getValueOfCarbohydrate() {
        return valOfCarbList;
    }

    public List<Double> getValueOfFat() {
        return valOfFatList;
    }

    public List<Integer> getMoneyList() {
        return moneyList;
    }

    public String[] getMachine() { return machine; }
}