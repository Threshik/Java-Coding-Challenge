use LoanManagement;

CREATE TABLE Customer (
    customerId INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    address VARCHAR(255),
    creditScore INT
);

CREATE TABLE Loan (
    loanId INT PRIMARY KEY AUTO_INCREMENT,
    customerId INT,
    principalAmount DECIMAL(10, 2),
    interestRate DECIMAL(5, 2),
    loanTerm INT,
    loanType VARCHAR(50),
    loanStatus VARCHAR(20),
    FOREIGN KEY (customerId) REFERENCES Customer(customerId)
);

CREATE TABLE HomeLoan (
    loanId INT PRIMARY KEY,
    propertyAddress VARCHAR(255),
    propertyValue INT,
    FOREIGN KEY (loanId) REFERENCES Loan(loanId)
);

CREATE TABLE CarLoan (
    loanId INT PRIMARY KEY,
    carModel VARCHAR(100),
    carValue INT,
    FOREIGN KEY (loanId) REFERENCES Loan(loanId)
);


INSERT INTO Customer (name, email, phone, address, creditScore)
VALUES 
('Alice Johnson', 'alice@example.com', '1234567890', 'New York', 720),
('Bob Smith', 'bob@example.com', '0987654321', 'California', 610);


INSERT INTO Loan (customerId, principalAmount, interestRate, loanTerm, loanType, loanStatus)
VALUES
(1, 500000, 7.5, 60, 'HomeLoan', 'Pending'),
(2, 300000, 9.0, 36, 'CarLoan', 'Pending');


INSERT INTO HomeLoan (loanId, propertyAddress, propertyValue)
VALUES
(1, '123 Maple Street', 600000);


INSERT INTO CarLoan (loanId, carModel, carValue)
VALUES
(2, 'Honda City', 350000);


