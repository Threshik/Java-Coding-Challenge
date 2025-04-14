package dao;

import entity.*;
import exception.LoanNotFoundException;
import util.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoanRepositoryImpl implements ILoanRepository {
    private Connection connection;
    public LoanRepositoryImpl() {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public boolean applyLoan(Loan loan) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to apply for this loan? (Yes/No)");
        String confirm = scanner.nextLine();

        if (!confirm.equalsIgnoreCase("Yes")) {
            System.out.println("Loan application cancelled.");
            return false;
        }

        String sql = "INSERT INTO loan (loanId, customerId, principalAmount, interestRate, loanTerm, loanType, loanStatus) VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, loan.getLoanId());
        ps.setInt(2, loan.getCustomer().getCustomerId());
        ps.setDouble(3, loan.getPrincipalAmount());
        ps.setDouble(4, loan.getInterestRate());
        ps.setInt(5, loan.getLoanTerm());
        ps.setString(6, loan.getLoanType());
        ps.setString(7, "Pending");

        int rowsInserted = ps.executeUpdate();
        return rowsInserted > 0;
    }

    @Override
    public double calculateInterest(int loanId) throws LoanNotFoundException {
        try {
            String sql = "SELECT principalAmount, interestRate, loanTerm FROM loan WHERE loanId = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, loanId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double principal = rs.getDouble("principalAmount");
                double rate = rs.getDouble("interestRate");
                int term = rs.getInt("loanTerm");

                return calculateInterest(principal, rate, term);
            } else {
                throw new LoanNotFoundException("Loan with ID " + loanId + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double calculateInterest(double principal, double rate, int term) {
        return (principal * rate * term) / 12;
    }

    @Override
    public String loanStatus(int loanId) throws LoanNotFoundException {
        try {
            String sql = "SELECT creditScore FROM customer WHERE customerId = (SELECT customerId FROM loan WHERE loanId = ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, loanId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int creditScore = rs.getInt("creditScore");  // Corrected field name
                String status = creditScore > 650 ? "Approved" : "Rejected";

                // Update the loan status
                String updateStatusSql = "UPDATE loan SET loanStatus = ? WHERE loanId = ?";
                PreparedStatement updatePs = connection.prepareStatement(updateStatusSql);
                updatePs.setString(1, status);
                updatePs.setInt(2, loanId);
                updatePs.executeUpdate();

                return status;
            } else {
                throw new LoanNotFoundException("Loan with ID " + loanId + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double calculateEMI(int loanId) throws LoanNotFoundException {
        try {
            String sql = "SELECT principalAmount, interestRate, loanTerm FROM loan WHERE loanId = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, loanId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double principal = rs.getDouble("principalAmount");
                double rate = rs.getDouble("interestRate") / 12 / 100;  // Monthly rate
                int term = rs.getInt("loanTerm");

                // EMI calculation formula
                return (principal * rate * Math.pow(1 + rate, term)) / (Math.pow(1 + rate, term) - 1);
            } else {
                throw new LoanNotFoundException("Loan with ID " + loanId + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double calculateEMI(double principal, double rate, int term) {
        rate = rate / 12 / 100;  // Monthly rate
        return (principal * rate * Math.pow(1 + rate, term)) / (Math.pow(1 + rate, term) - 1);
    }

    @Override
    public String loanRepayment(int loanId, double amount) throws LoanNotFoundException {
        try {
            String sql = "SELECT loanTerm FROM loan WHERE loanId = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, loanId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int term = rs.getInt("loanTerm");

                double emi = calculateEMI(loanId);

                if (amount < emi) {
                    return "Amount is less than EMI. Repayment rejected.";
                }

                // Calculate number of EMIs that can be paid
                int noOfEMIsPaid = (int)(amount / emi);
                term -= noOfEMIsPaid;  // Update loan term after repayment

                // Update the loan term
                String updateTermSql = "UPDATE loan SET loanTerm = ? WHERE loanId = ?";
                PreparedStatement updatePs = connection.prepareStatement(updateTermSql);
                updatePs.setInt(1, term);
                updatePs.setInt(2, loanId);
                updatePs.executeUpdate();

                return "Repayment successful. " + noOfEMIsPaid + " EMI(s) paid.";
            } else {
                throw new LoanNotFoundException("Loan with ID " + loanId + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Loan> getAllLoan() throws Exception {
        try {
            String sql = "SELECT * FROM loan";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            List<Loan> loans = new ArrayList<>();
            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("loanId"));

                int customerId = rs.getInt("customerId");
                Customer customer = getCustomerById(customerId);  // Fetch customer details

                loan.setCustomer(customer); // Set the customer object

                loan.setPrincipalAmount(rs.getDouble("principalAmount"));
                loan.setInterestRate(rs.getDouble("interestRate"));
                loan.setLoanTerm(rs.getInt("loanTerm"));
                loan.setLoanType(rs.getString("loanType"));
                loan.setLoanStatus(rs.getString("loanStatus"));

                loans.add(loan);
            }

            return loans;
        } catch (SQLException e) {
            throw new Exception("Error retrieving loan data.", e);
        }
    }

    @Override
    public Loan getLoanById(int loanId) throws LoanNotFoundException {
        try {
            String sql = "SELECT * FROM loan WHERE loanId = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, loanId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("loanId"));

                // Fetch customerId from the loan record
                int customerId = rs.getInt("customerId");

                // Fetch Customer object from the database using customerId
                Customer customer = getCustomerById(customerId);

                // Set the Customer object in the Loan object
                loan.setCustomer(customer);

                loan.setPrincipalAmount(rs.getDouble("principalAmount"));
                loan.setInterestRate(rs.getDouble("interestRate"));
                loan.setLoanTerm(rs.getInt("loanTerm"));
                loan.setLoanType(rs.getString("loanType"));
                loan.setLoanStatus(rs.getString("loanStatus"));

                return loan;
            } else {
                throw new LoanNotFoundException("Loan with ID " + loanId + " not found.");
            }
        } catch (SQLException e) {
            throw new LoanNotFoundException("Loan with ID " + loanId + " not found." + e);
        }
    }

    // Helper method to fetch customer details by ID
    private Customer getCustomerById(int customerId) throws SQLException {
        String sql = "SELECT * FROM customer WHERE customerId = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, customerId);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Customer customer = new Customer();
            customer.setCustomerId(rs.getInt("customerId"));
            customer.setName(rs.getString("name"));
            customer.setEmail(rs.getString("email"));
            customer.setPhone(rs.getString("phone"));
            customer.setAddress(rs.getString("address"));
            customer.setCreditScore(rs.getInt("creditScore"));
            return customer;
        } else {
            throw new SQLException("Customer not found");
        }
    }
}
