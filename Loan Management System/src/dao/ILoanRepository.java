package dao;
import entity.Loan;

import exception.*;

import java.util.List;


public interface ILoanRepository {

    boolean applyLoan(Loan loan) throws Exception;

    double calculateInterest(int loanId) throws LoanNotFoundException;
    double calculateInterest(double principal, double rate, int term);

    String loanStatus(int loanId) throws LoanNotFoundException;

    double calculateEMI(int loanId) throws LoanNotFoundException;
    double calculateEMI(double principal, double rate, int term);

    String loanRepayment(int loanId, double amount) throws LoanNotFoundException;

    List<Loan> getAllLoan() throws Exception;

    Loan getLoanById(int loanId) throws LoanNotFoundException;
}
