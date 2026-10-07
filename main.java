import java.io.*;
import java.util.*;

// 1. BASE ABSTRACT CLASS (Encapsulation)

abstract class BankAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private String accountHolderName;
    protected double balance;

    public BankAccount(String accountNumber, String accountHolderName, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialDeposit;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("[SUCCESS] Deposited ₦" + amount + ". New Balance: ₦" + balance);
        } else {
            System.out.println("[ERROR] Deposit amount must be positive.");
        }
    }

    // Abstract method to be overridden by subclasses
    public abstract boolean withdraw(double amount);

    public void displayAccountInfo() {
        System.out.println("Account Number : " + accountNumber);
        System.out.println("Holder Name    : " + accountHolderName);
        System.out.println("Current Balance: ₦" + balance);
    }
}

// 2. DERIVED CLASS (Inheritance & Polymorphism)

class SavingsAccount extends BankAccount {
    private static final long serialVersionUID = 1L;
    private static final double MINIMUM_BALANCE = 1000.00;

    public SavingsAccount(String accountNumber, String accountHolderName, double initialDeposit) {
        super(accountNumber, accountHolderName, initialDeposit);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("[ERROR] Withdrawal amount must be greater than zero.");
            return false;
        }

        if (balance - amount < MINIMUM_BALANCE) {
            System.out.println("[ERROR] Transaction rejected. Minimum balance of ₦" + MINIMUM_BALANCE + " required.");
            return false;
        }

        balance -= amount;
        System.out.println("[SUCCESS] Withdrew ₦" + amount + ". Remaining Balance: ₦" + balance);
        return true;
    }
}

// 3. MAIN DRIVER & SYSTEM CONTROLLER (File I/O & CLI)

public class Main {
    private static final String DATA_FILE = "bank_accounts.dat";
    private static Map<String, BankAccount> accounts = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadAccounts();

        while (true) {
            System.out.println("\n--- JAVA OOP BANKING MANAGEMENT SYSTEM ---");
            System.out.println("1. Create New Savings Account");
            System.out.println("2. Deposit Funds");
            System.out.println("3. Withdraw Funds");
            System.out.println("4. Check Balance / Account Info");
            System.out.println("5. List All Accounts");
            System.out.println("6. Exit");
            System.out.print("Select an option (1-6): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createAccount();
                    break;
                case "2":
                    performDeposit();
                    break;
                case "3":
                    performWithdrawal();
                    break;
                case "4":
                    checkAccountInfo();
                    break;
                case "5":
                    listAllAccounts();
                    break;
                case "6":
                    saveAccounts();
                    System.out.println("\nData saved successfully. Exiting system. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("\n[ERROR] Invalid selection. Please choose between 1 and 6.");
            }
        }
    }

    private static void createAccount() {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();

        if (accounts.containsKey(accNum)) {
            System.out.println("\n[ERROR] An account with this number already exists.");
            return;
        }

        System.out.print("Enter Account Holder Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Initial Deposit (Minimum ₦1,000): ");
        try {
            double deposit = Double.parseDouble(scanner.nextLine().trim());
            if (deposit < 1000) {
                System.out.println("\n[ERROR] Initial deposit must be at least ₦1,000.");
                return;
            }
            SavingsAccount account = new SavingsAccount(accNum, name, deposit);
            accounts.put(accNum, account);
            saveAccounts();
            System.out.println("\n[SUCCESS] Account created successfully for " + name + "!");
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Invalid numeric input for deposit amount.");
        }
    }

    private static void performDeposit() {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        BankAccount acc = accounts.get(accNum);

        if (acc == null) {
            System.out.println("\n[ERROR] Account not found.");
            return;
        }

        System.out.print("Enter Amount to Deposit: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            acc.deposit(amount);
            saveAccounts();
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Invalid amount entered.");
        }
    }

    private static void performWithdrawal() {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        BankAccount acc = accounts.get(accNum);

        if (acc == null) {
            System.out.println("\n[ERROR] Account not found.");
            return;
        }

        System.out.print("Enter Amount to Withdraw: ");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            if (acc.withdraw(amount)) {
                saveAccounts();
            }
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Invalid amount entered.");
        }
    }

    private static void checkAccountInfo() {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine().trim();
        BankAccount acc = accounts.get(accNum);

        if (acc != null) {
            System.out.println("\n=== ACCOUNT DETAILS ===");
            acc.displayAccountInfo();
        } else {
            System.out.println("\n[ERROR] Account not found.");
        }
    }

    private static void listAllAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("\n[INFO] No registered accounts found in system.");
            return;
        }

        System.out.println("\n================ ALL REGISTERED ACCOUNTS ================");
        for (BankAccount acc : accounts.values()) {
            acc.displayAccountInfo();
            System.out.println("---------------------------------------------------------");
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadAccounts() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            accounts = (Map<String, BankAccount>) ois.readObject();
        } catch (Exception e) {
            System.out.println("[WARNING] Could not load saved accounts file. Starting fresh.");
        }
    }

    private static void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(accounts);
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to save account data to file.");
        }
    }
}