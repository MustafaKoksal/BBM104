public class Main {
    public static void main(String[] args) {
        String[] productLines = FileInput.readFile(args[0], false, false);
        String[] purchaseLines = FileInput.readFile(args[1], false, false);
        FileOutput.writeToFile(args[2], "", false, false);

        String outputFilePath = args[2];

        MachineFiller machineFiller = new MachineFiller(productLines, outputFilePath);
        PurchaseProduct purchaseProduct = new PurchaseProduct(productLines, purchaseLines, outputFilePath);

        machineFiller.processFirstInput();
        machineFiller.fill(true);
        machineFiller.printMachine(machineFiller.getMachine());

        purchaseProduct.processSecondInput();
        purchaseProduct.purchase();

        machineFiller.printMachine(purchaseProduct.getMachine());
    }
}