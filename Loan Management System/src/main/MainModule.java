package main;

import dao.LoanRepositoryImpl;
import entity.Loan;
import entity.Customer;
import exception.LoanNotFoundException;

import java.util.List;
import java.util.Scanner;

public class MainModule {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize the LoanRepositoryImpl
        LoanRepositoryImpl loanRepo = new LoanRepositoryImpl();

        while (true) {
            System.out.println("\n=== Loan Management System ===");
            System.out.println("1. Apply for Loan");
            System.out.println("2. Get Loan by ID");
            System.out.println("3. View All Loans");
            System.out.println("4. Calculate EMI");
            System.out.println("5. Calculate Interest");
            System.out.println("6. Check Loan Status");
            System.out.println("7. Make Repayment");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.println("\n--- Apply for Loan ---");
                    Loan loan = new Loan();

                    System.out.print("Enter Loan ID: ");
                    loan.setLoanId(scanner.nextInt());
                    scanner.nextLine();

                    System.out.print("Enter Customer ID: ");
                    int customerId = scanner.nextInt();
                    scanner.nextLine();

                    Customer customer = new Customer();
                    customer.setCustomerId(customerId);
                    loan.setCustomer(customer);

                    System.out.print("Enter Principal Amount: ");
                    loan.setPrincipalAmount(scanner.nextDouble());

                    System.out.print("Enter Interest Rate: ");
                    loan.setInterestRate(scanner.nextDouble());

                    System.out.print("Enter Loan Term (in months): ");
                    loan.setLoanTerm(scanner.nextInt());
                    scanner.nextLine();

                    System.out.print("Enter Loan Type: ");
                    loan.setLoanType(scanner.nextLine());

                    try {
                        boolean result = loanRepo.applyLoan(loan);
                        if (result) {
                            System.out.println("Loan application successful.");
                        } else {
                            System.out.println("Loan application failed.");
                        }
                    } catch (Exception e) {
                        System.out.println("Error applying for loan: " + e.getMessage());
                    }
                    break;

                case 2:
                    System.out.print("Enter Loan ID to retrieve: ");
                    int loanId2 = scanner.nextInt();
                    try {
                        Loan loan2 = loanRepo.getLoanById(loanId2);
                        System.out.println("Loan Details: ");
                        System.out.println("Loan ID: " + loan2.getLoanId());
                        System.out.println("Customer ID: " + loan2.getCustomer().getCustomerId());
                        System.out.println("Principal Amount: " + loan2.getPrincipalAmount());
                        System.out.println("Interest Rate: " + loan2.getInterestRate());
                        System.out.println("Loan Term: " + loan2.getLoanTerm());
                        System.out.println("Loan Type: " + loan2.getLoanType());
                        System.out.println("Loan Status: " + loan2.getLoanStatus());
                    } catch (LoanNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 3:
                    try {
                        List<Loan> loans = loanRepo.getAllLoan();
                        System.out.println("List of All Loans: ");
                        for (Loan l : loans) {
                            System.out.println("\n--- Loan Details ---");
                            System.out.println("Loan ID: " + l.getLoanId());
                            System.out.println("Customer ID: " + l.getCustomer().getCustomerId());
                            System.out.println("Customer Name: " + l.getCustomer().getName());
                            System.out.println("Customer Email: " + l.getCustomer().getEmail());
                            System.out.println("Customer Phone: " + l.getCustomer().getPhone());
                            System.out.println("Customer Address: " + l.getCustomer().getAddress());
                            System.out.println("Customer Credit Score: " + l.getCustomer().getCreditScore());
                            System.out.println("Principal Amount: " + l.getPrincipalAmount());
                            System.out.println("Interest Rate: " + l.getInterestRate() + "%");
                            System.out.println("Loan Term (in months): " + l.getLoanTerm());
                            System.out.println("Loan Type: " + l.getLoanType());
                            System.out.println("Loan Status: " + l.getLoanStatus());
                        }
                    } catch (Exception e) {
                        System.out.println("Error fetching loans: " + e.getMessage());
                    }
                    break;

                case 4:
                    System.out.print("Enter Loan ID to calculate EMI: ");
                    int loanId4 = scanner.nextInt();
                    try {
                        double emi = loanRepo.calculateEMI(loanId4);
                        System.out.println("EMI for Loan ID " + loanId4 + ": " + emi);
                    } catch (LoanNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 5:
                    System.out.print("Enter Loan ID to calculate Interest: ");
                    int loanId5 = scanner.nextInt();
                    try {
                        double interest = loanRepo.calculateInterest(loanId5);
                        System.out.println("Interest for Loan ID " + loanId5 + ": " + interest);
                    } catch (LoanNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 6:
                    System.out.print("Enter Loan ID to check Status: ");
                    int loanId6 = scanner.nextInt();
                    try {
                        String status = loanRepo.loanStatus(loanId6);
                        System.out.println("Loan Status for Loan ID " + loanId6 + ": " + status);
                    } catch (LoanNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 7:
                    System.out.print("Enter Loan ID to make repayment: ");
                    int loanId7 = scanner.nextInt();
                    System.out.print("Enter Amount to repay: ");
                    double amount = scanner.nextDouble();
                    try {
                        String result = loanRepo.loanRepayment(loanId7, amount);
                        System.out.println(result);
                    } catch (LoanNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 8:
                    System.out.println("Exiting the system.");
                    return;

                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
}
