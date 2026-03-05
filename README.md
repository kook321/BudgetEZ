# BudgetEZ

This is a personal finance management web application running on localhost. The project structure is designed to be clean and lightweight, using Java as the backend (without relying on heavy frameworks like Spring), coupled with an SQLite database and web interface rendered using HTML/CSS/JavaScript (Vanilla).

---

## ✨ Features (Main Capabilities)

- **📊 Dashboard Overview:** Real-time summary of net balance, total income, and total expenses.
- **💼 Account Management:** Wallet/bank account management system. Supports adding, editing, and deleting accounts.
- **💸 Transaction Tracking:** Record 3 transaction types:
- Expense (Expenses)
- Income (Income)
- Transfer (Transfers between accounts, separate from income/expense calculations)
- **🏷️ Categories & Notes:** Categorize items (e.g., Food, Shopping, Salary) with a notes section for additional details.
- **⏳ Status System:** Supports transaction status tracking. (COMPLETED, PENDING) The system will only calculate the amount for completed transactions.
- **🔍 Sort & Filter:** You can sort transaction history by date, name, or category, and view data specifically for today, this week, this month, or this year.
- **💾 Persistent Data:** All data is securely saved to the `finance.db` database (SQLite). Data will not be lost even if the webpage is closed or the server is restarted.

---

## How to Compile & Run

**Prerequisites**

- Java Development Kit (JDK) version 11 or above
- SQLite JDBC Driver version 3.39.3.0 or above

Steps (Linux/WSL/Mac)
open terminal in project folder

**Compile**

```
javac -cp "lib/*" -d bin src/*.Java
```

**Run**

```
java -cp "bin:lib/*" AppServer
```

> windows user change : to ;

for using web-app go to public/ and click on index.html
