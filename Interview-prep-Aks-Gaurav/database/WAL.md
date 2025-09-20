# Write-Ahead Log (WAL) - Notes

---

## 📌 What is WAL?
A **Write-Ahead Log (WAL)** is a sequential log file where every change (insert, update, delete) is written **before** applying it to the main database/storage.

- Think of it as a **diary**: first note down what you will do, then actually do it.
- Used for durability and crash recovery.

---

## 📌 Why use WAL?
1. **Durability** – No data loss even if the system crashes.
2. **Atomicity** – Incomplete transactions can be rolled back.
3. **Performance** – Sequential disk writes are faster than random writes.

---

## 📌 How WAL Works (Step by Step)
1. A write request comes in.
2. Database appends the operation to WAL (append-only).
3. WAL is **fsynced** (flushed) to disk.
4. Client gets success response.
5. Later, data is applied to actual tables / indexes.

👉 On crash: WAL is replayed to restore consistency.

---

## 📌 Why WAL Survives Crashes

### 1️⃣ Sequential Writes
- WAL is append-only → much safer than random writes.
- At worst, only the last partial record is corrupted.

### 2️⃣ Fsync (Flush to Disk)
- DB ensures WAL is flushed (`fsync`) before confirming success.
- Guarantees persistence even if system crashes.

### 3️⃣ Log Record Structure
- Each record has a header (length + checksum) + payload.
- On recovery: invalid/partial records are discarded.

### 4️⃣ Recovery Process
- Read WAL sequentially at startup.
- Stop at first invalid record.
- Replay all valid ones.

### 5️⃣ Real-World Safeguards
- **PostgreSQL**: Preallocated WAL segments, checksums for validation.
- **Cassandra/RocksDB**: CRC checksums, corrupted tails ignored.
- **MySQL InnoDB**: Redo log + double-write buffer to protect pages.

### 6️⃣ Edge Case: Power Loss
- Enterprise SSDs have battery-backed cache.
- Otherwise, fsync guarantees durability.

---
## WAL Workflow 
![workflow](image\wal.webp)

## 📌 Where is WAL Used?

- **PostgreSQL** → WAL for transactions & crash recovery.
- **Cassandra / RocksDB / LevelDB** → WAL before MemTable flush.
- **HDFS (NameNode)** → Edit log (WAL) for filesystem metadata.
- **Kafka** → Topic partitions are basically WALs.
- **MySQL (InnoDB)** → Redo log = WAL.

---

## 📌 Analogy
Running a restaurant:  
- Before cooking/serving food, you **write orders in a notebook (WAL)**.  
- If the kitchen burns (crash), you still know the orders.  
- Later you cook and serve (apply to DB).

---

## 📌 Benefits of WAL
- Fast sequential writes.
- Crash recovery.
- Ensures **A + D** in ACID.

---

## 📌 Downsides of WAL
- Log grows indefinitely → needs checkpoints/truncation.
- Write amplification (data written to WAL + actual DB).

---

# 🔹 WAL Interview Q&A

### ❓ Q1. Why use WAL instead of directly writing to DB files?
**Answer:** Direct writes are random & slow, WAL turns them into fast sequential writes. Also ensures crash recovery.

---

### ❓ Q2. How does WAL remain safe during crashes?
**Answer:**
- Append-only sequential writes.
- Fsync ensures durability.
- Checksums detect corruption.
- On recovery, replay only valid entries.

---

### ❓ Q3. What happens if WAL itself gets corrupted?
**Answer:** Only the last partial entry is discarded (due to checksums). Integrity of rest of DB is not affected.

---

### ❓ Q4. Where do you see WAL in real systems?
**Answer:** PostgreSQL, MySQL InnoDB, Cassandra, RocksDB, Kafka, HDFS.

---

### ❓ Q5. What is the trade-off of WAL?
**Answer:**
- Pros: Fast writes, durability, recovery.
- Cons: Extra storage, write amplification, log management overhead.

---

# 🔹 Quick Summary
- WAL = Write changes to log first → then apply to DB.
- Guarantees durability, sequential I/O, crash recovery.
- Used in most modern DBs and distributed systems.
