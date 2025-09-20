# Transaction Isolation Levels

Transactions in databases ensure **consistency** and **correctness** when multiple users or processes access the database at the same time.  
To manage **concurrency**, databases provide **Isolation Levels**.

---

## 1. Why Isolation Levels?

Without isolation, concurrent transactions can cause problems like:

- **Dirty Read** → Reading uncommitted data from another transaction.  
- **Non-Repeatable Read** → Same query returns different results within the same transaction because another transaction updated the data.  
- **Phantom Read** → New rows appear/disappear when running the same query again due to another transaction’s insert/delete.

Isolation levels control how much of this is allowed vs blocked.

---

## 2. ANSI SQL Isolation Levels

### 2.1. **Read Uncommitted**
- **What it allows**: Dirty reads, non-repeatable reads, phantom reads.  
- **Explanation**: Transactions can read uncommitted (in-progress) changes from others.  
- **Use Case**: Rarely used. Only when performance matters more than accuracy.  

Example:  
1. 
- Transaction A updates `balance = 500 → 1000` but hasn’t committed.  
- Transaction B reads balance as `1000`.  
- If A rolls back, B has read **invalid data**.


2. 
- T1: `UPDATE accounts SET balance = balance - 500 WHERE id = 1;` (not yet committed)  
- T2: `SELECT balance FROM accounts WHERE id = 1;` → sees the reduced balance (even though T1 may rollback).  
- If T1 rolls back, T2 has worked with wrong information.  

👉 Dangerous in banking: you may think money is withdrawn when it actually wasn’t.

---

### 2.2. **Read Committed** (Default in most RDBMS like Oracle, SQL Server)
- **What it prevents**: Dirty reads.  
- **Still possible**: Non-repeatable reads, phantom reads.  
- **Explanation**: A transaction only sees committed data. But repeated reads may return different results.  

Example:  
1.
- Transaction A commits after updating salary.  
- Transaction B will only see the change **after commit**.  
- But if B reads twice, the value may differ between reads.
2. #### Example:
- T1: `UPDATE accounts SET balance = balance - 500 WHERE id = 1;` (not yet committed)  
- T2: `SELECT balance FROM accounts WHERE id = 1;` → cannot see uncommitted value, only old committed balance.  
- But if T1 commits and T2 queries again → it will now see the updated balance.  

👉 Prevents dirty reads, but the same query in T2 can give **different results** during one transaction → **non-repeatable read**.


---

### 2.3. **Repeatable Read** (Default in MySQL InnoDB)
- **What it prevents**: Dirty reads, non-repeatable reads.  
- **Still possible**: Phantom reads.  
- **Explanation**: Same row read multiple times in a transaction will always return the same value, even if another transaction updates it.  
- **Use Case**: Banking, e-commerce orders.  

#### Example: 
1. 
- Transaction A reads a row (salary = 5000).  
- Transaction B updates salary to 6000 and commits.  
- Transaction A re-reads → still sees 5000 (until it finishes).

2. 
- T1: `SELECT salary FROM employees WHERE id = 10;` → gets 5000  
- T2: `UPDATE employees SET salary = 6000 WHERE id = 10; COMMIT;`  
- T1: Repeats same query → still sees 5000 (until T1 completes).  
- ✅ Non-repeatable read prevented.  

But:  
- T1: `SELECT * FROM employees WHERE salary > 5000;` → gets 3 rows  
- T2: `INSERT INTO employees (id, name, salary) VALUES (50, 'Akshay', 7000); COMMIT;`  
- T1: Runs query again → now sees 4 rows (new phantom row appeared).  
- ❌ Phantom read still possible.

---

### 2.4. **Serializable** (Strictest level)
- **What it prevents**: Dirty reads, non-repeatable reads, phantom reads.  
- **Explanation**: Transactions are executed sequentially, as if one after another. No concurrency anomalies.  
- **Downside**: Very slow, less concurrency.  
- **Use Case**: Critical systems requiring maximum correctness.  

#### Example:
- T1: `SELECT SUM(balance) FROM accounts WHERE branch_id = 5;`  
- T2: `INSERT INTO accounts (id, branch_id, balance) VALUES (99, 5, 2000);`  

At lower isolation, T1 could run twice and see different results due to T2’s insert.  
But under **Serializable**, T2 must **wait** until T1 finishes completely.  

👉 Guarantees correctness but slows throughput.

## 3. Summary Table

| Isolation Level   | Dirty Read | Non-Repeatable Read | Phantom Read |
|-------------------|------------|---------------------|--------------|
| Read Uncommitted  | ✅ Allowed | ✅ Allowed          | ✅ Allowed   |
| Read Committed    | ❌ Prevented | ✅ Allowed        | ✅ Allowed   |
| Repeatable Read   | ❌ Prevented | ❌ Prevented      | ✅ Allowed   |
| Serializable      | ❌ Prevented | ❌ Prevented      | ❌ Prevented |

✅ = Can happen  
❌ = Prevented

---

## 4. Real-World Use Cases

- **Read Uncommitted** → Analytics on large logs (speed > accuracy).  
- **Read Committed** → General apps like CRMs, e-commerce browsing.  
- **Repeatable Read** → Banking transfers, e-commerce checkout.  
- **Serializable** → Stock trading, core banking ledgers.

---

## 5. Analogy

- **Read Uncommitted** → Gossiping about something before it’s confirmed.  
- **Read Committed** → Only talk about confirmed news.  
- **Repeatable Read** → Stick to one version of the news during your conversation.  
- **Serializable** → Only one person speaks at a time, everyone else waits.

---

## 6. Notes (Interview Insights)

- PostgreSQL defaults to **Read Committed**.  
- MySQL InnoDB defaults to **Repeatable Read**.  
- Serializable is rarely used due to performance costs.  
- Isolation level choice is a trade-off between **performance** and **correctness**.

---
