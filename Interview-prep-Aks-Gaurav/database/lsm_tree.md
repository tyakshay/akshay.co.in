# 📘 Log-Structured Merge Tree (LSM Tree)

## 🔹 What is an LSM Tree?

**LSM (Log-Structured Merge) Tree** is a data structure used for **high-write throughput storage systems**.

* Instead of updating data directly on disk (which is expensive), it writes changes sequentially in **log files** (append-only).
* Periodically, these logs are merged into larger sorted structures.
* Reads require looking at multiple levels (recent logs + merged files).

Think of it as:
👉 "Write fast in memory, flush later to disk in sorted batches."

---

## 🔹 How does it work?

1. **MemTable (in-memory structure)**

   * Incoming writes go to an in-memory balanced tree (often a **skip list** or **red-black tree**).
   * Also appended to a **Write-Ahead Log (WAL)** for durability.

2. **Flush to SSTables (disk)**

   * When MemTable is full → flush to disk as a **Sorted String Table (SSTable)**.
   * SSTables are immutable, sorted files.

3. **Compaction (merge process)**

   * Over time, multiple SSTables exist.
   * Background process **merges** them, removing duplicates/old values, keeping disk efficient.

4. **Reads**

   * Look first in MemTable, then WAL, then search across SSTables (with help of **Bloom filters** to skip irrelevant files).

---

## 🔹 Why LSM Tree?

👉 Because random writes on disk are **slow**.
👉 Sequential writes (append-only) are **fast**.
👉 LSM trades **read complexity** for **write speed**.

---

## 🔹 Use Cases

* **Write-heavy systems** where throughput matters more than single-read latency. Examples:

  1. **NoSQL databases**: Cassandra, HBase, LevelDB, RocksDB
  2. **Time-series databases**: InfluxDB, Timescale (partially)
  3. **Search engines**: Elasticsearch, Lucene
  4. **Message brokers**: Kafka uses log-structured storage (similar concept).

---

## 🔹 Advantages

* Very high **write throughput**.
* Efficient disk usage (due to sequential writes).
* Compaction keeps storage optimized.

---

## 🔹 Trade-offs

* Reads are **slower** (may need to check multiple levels).
* Background compaction consumes CPU & I/O.
* Higher **read amplification** compared to B-Trees.

---

✅ **Interview line:**
*"An LSM Tree is a log-structured data structure optimized for write-heavy workloads. It batches writes in memory, flushes them to disk in sorted files (SSTables), and periodically merges them. It's heavily used in modern NoSQL databases like Cassandra and RocksDB because it provides high write throughput with reasonable read performance, often improved using Bloom filters."*

------



# Point to Point revision

## 1. Introduction
- **LSM Tree** is a data structure designed for **high write throughput**.  
- It avoids expensive random disk writes by writing sequentially to disk.  
- Widely used in **databases, search engines, and key-value stores**.  

---

## 2. Core Idea
- Instead of updating data in place, LSM trees **append writes** to disk.  
- Data is written to memory first, then flushed to disk in sorted form.  
- Background **compaction** merges old and new data to keep storage efficient.  

---

## 3. Architecture

### 3.1 Components
1. **MemTable** (in-memory store)  
   - Stores new writes in memory (often a skip list or red-black tree).  
   - Also logged in a **Write-Ahead Log (WAL)** for durability.  

2. **SSTables** (Sorted String Tables)  
   - Immutable, sorted files on disk.  
   - Generated when MemTable is flushed.  

3. **Compaction Process**  
   - Periodically merges multiple SSTables.  
   - Removes obsolete values and deletes.  
   - Maintains efficiency of reads and disk space.  

---

## 4. Operations

### 4.1 Write Path
1. Append data to **WAL** (for crash recovery).  
2. Insert into **MemTable**.  
3. When MemTable is full → flush to disk as an **SSTable**.  

👉 **Writes are sequential → very fast.**

---

### 4.2 Read Path
1. Search in **MemTable**.  
2. If not found, check recent SSTables.  
3. Use **Bloom filters** to skip irrelevant SSTables.  
4. Merge results if multiple versions exist.  

👉 **Reads may be slower (multiple lookups).**

---

### 4.3 Compaction
- Background merge of SSTables to:  
  - Remove duplicates and tombstones (deletes).  
  - Keep fewer SSTables → reduces read amplification.  
- Types:  
  - **Minor compaction**: MemTable → SSTable.  
  - **Major compaction**: Merge many SSTables together.  

---

## 5. Advantages
- Excellent **write performance** (sequential I/O).  
- Efficient use of disk bandwidth.  
- Scales well for large datasets.  
- Works well with modern SSDs.  

---

## 6. Disadvantages
- **Read amplification**: Must check multiple SSTables.  
- **Compaction overhead**: Expensive in CPU & I/O.  
- **Write amplification**: Data written multiple times (MemTable → SSTable → compaction).  

---

## 7. Optimizations
- **Bloom Filters** → Avoid unnecessary SSTable lookups.  
- **Index structures** in SSTables for faster key search.  
- **Tiered or Leveled compaction strategies** to balance performance.  
- **Block caching** for frequently accessed data.  

---

## 8. Use Cases
- **NoSQL databases**:  
  - Cassandra  
  - HBase  
  - RocksDB / LevelDB  

- **Search engines**:  
  - Apache Lucene  
  - Elasticsearch  

- **Time-series DBs**:  
  - InfluxDB  
  - Timescale (hybrid approach)  

- **Messaging systems**:  
  - Kafka (log-structured storage, similar principles).  

---

## 9. LSM Tree vs B-Tree

| Aspect              | LSM Tree                              | B-Tree                          |
|---------------------|---------------------------------------|---------------------------------|
| Writes              | Fast (sequential)                     | Slower (random I/O)             |
| Reads               | Slower (check multiple SSTables)       | Faster (direct lookup)          |
| Space Efficiency    | Higher (compaction)                   | Lower (fragmentation possible)  |
| Best Use Case       | Write-heavy workloads                 | Read-heavy workloads            |
| Example Databases   | Cassandra, RocksDB                    | MySQL, PostgreSQL               |

---

## 10. ASCII Diagram

### 10.1 Write Flow

```
                +------------------+
   Write -----> |  Write Ahead Log |  (Durability: crash recovery)
                +------------------+
                          |
                          v
                +------------------+
                |     MemTable     |  (In-memory, sorted)
                +------------------+
                          |
            (Flush when full) ↓
                +------------------+
                |     SSTable      |  (Immutable, sorted file on disk)
                +------------------+
                          |
         -----------------|-------------------
        |                 |                  |
        v                 v                  v
  +-----------+     +-----------+      +-----------+
  |  SSTable1 |     |  SSTable2 | ...  |  SSTableN |
  +-----------+     +-----------+      +-----------+
        \________________________________________/
                          |
                          v
                +------------------+
                |    Compaction    |  (Merge, remove old versions, deletes)
                +------------------+
                          |
                          v
                +------------------+
                | Optimized Levels |
                +------------------+
```

### 10.2 Read Flow

```
   Read Query
       |
       v
+------------------+
|     MemTable     |  (Check memory first)
+------------------+
       |
       v
+------------------+    +------------------+
| Bloom Filter #1  | -> |   SSTable #1     |
+------------------+    +------------------+
       |
       v
+------------------+    +------------------+
| Bloom Filter #2  | -> |   SSTable #2     |
+------------------+    +------------------+
       .
       .
       .
```

👉 If Bloom filter says **“Not Present”** → skip SSTable.  
👉 If Bloom filter says **“Maybe Present”** → check SSTable index and fetch.  

---

## 11. Interview Q&A

### Q1. Why do we need an LSM Tree when we already have B-Trees?  
- B-Trees do in-place updates → **random writes** → bad for disks.  
- LSM Trees convert random writes into **sequential writes** → much faster on HDDs and SSDs.  
- For **write-heavy workloads** (like logs, time-series data, messaging systems), LSM trees massively outperform B-Trees.  

### Q2. What is the biggest drawback of LSM Trees?  
- **Read Amplification**: Reads may need to search across multiple SSTables.  
- **Compaction Cost**: Background merges consume CPU and I/O.  
- **Write Amplification**: Data written multiple times (MemTable → SSTable → Compaction).  

### Q3. How do Bloom filters help in LSM Trees?  
- They quickly tell if a key is **definitely not** in an SSTable.  
- Prevents wasted disk lookups.  
- Trade-off: possible **false positives** but no **false negatives**.  

### Q4. How does compaction work and why is it important?  
- Compaction merges SSTables to:  
  - Remove old versions and deletes.  
  - Reduce number of SSTables to search.  
- Without compaction, reads become too slow.  
- Trade-off: compaction consumes I/O and CPU → tuning is critical.  

### Q5. When should you prefer LSM Trees over B-Trees?  
- **Write-heavy workloads** → logs, metrics, time-series, event stores.  
- When **sequential writes** are more important than low-latency reads.  
- Examples: Cassandra, RocksDB, Kafka, Elasticsearch.  

### Q6. What are common optimizations in LSM Trees?  
- **Leveled vs Tiered compaction strategies** (to balance read vs write cost).  
- **Bloom filters** to cut down read amplification.  
- **Block cache** to speed up frequently read data.  
- **Partitioning/sharding** to distribute load.  

### Q7. Can you explain Write Amplification in LSM Trees?  
- Data is written multiple times:  
  1. WAL  
  2. MemTable  
  3. Flushed SSTable  
  4. Rewritten during compaction  
- Write amplification is the cost of these repeated writes.  
- It’s a major trade-off for getting high write throughput.  

### Q8. How does Cassandra (or RocksDB) use LSM Trees in practice?  
- **Cassandra**: Each SSTable has its own Bloom filter and index; compaction strategy can be configured (SizeTiered, Leveled).  
- **RocksDB**: Provides tunable LSM with multiple levels and block cache for optimizing reads.  

---

## 12. Quick Summary for Interview
- **Strength:** Write performance (sequential I/O).  
- **Weakness:** Read amplification & compaction overhead.  
- **Fixes:** Bloom filters, caching, compaction strategies.  
- **Use-case:** Write-heavy, append-only workloads (Cassandra, RocksDB, Kafka, Elasticsearch).  
