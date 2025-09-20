## Comprehensive Revision Summary of Databases and Storage

### I. Introduction to Databases and Management Systems

#### A. Database Definition
A **database** is an organized collection of structured information, or data, typically stored electronically in a computer system. The combination of the data, the Database Management System (DBMS), and associated applications is often referred to as a database system, or simply a database.

#### B. Database Management System (DBMS)
A DBMS is the comprehensive software program that typically controls a database. It acts as an interface between the database and its end-users or programs.
**Functions of a DBMS:**
*   Allows users to retrieve, update, and manage how information is organized and optimized.
*   Facilitates oversight and control of databases.
*   Enables administrative operations such as performance monitoring, tuning, and backup and recovery.

#### C. Common Database Components
Databases share several common components:
*   **Schema:** Defines the shape of a data structure and specifies what kinds of data can go where. Schemas can be strictly enforced across the entire database, loosely enforced on parts, or may not exist at all.
*   **Table:** Contains various columns, similar to a spreadsheet. A table can have as few as two columns or upwards of a hundred or more.
*   **Column:** Contains a set of data values of a particular type (text, numbers, enums, timestamps, etc.), with one value for each row of the database.
*   **Row:** Records data in a table. Tables can have thousands or millions of rows.

### II. Types of Databases

Databases are generally categorized into two main types:

1.  **SQL (Relational):** Based on the relational model.
2.  **NoSQL:** Includes several sub-types:
    *   Document stores
    *   Key-value stores
    *   Graph databases
    *   Time series databases
    *   Wide column stores
    *   Multi-model databases

### III. Challenges Faced While Running Databases at Scale

Running databases at scale presents common difficulties:
1.  **Absorbing significant increases in data volume** (e.g., from sensors and connected machines).
2.  **Ensuring data security** while maintaining easy accessibility for users.
3.  **Keeping up with demand** for real-time access to support timely decision-making.
4.  **Managing and maintaining the database and infrastructure**, which often requires hiring additional talent as databases and data volumes grow.
5.  **Removing limits on scalability**, particularly the difficulty in predicting necessary capacity for on-premises databases.
6.  **Ensuring data residency, data sovereignty, or latency requirements**, which sometimes necessitates running on-premises systems pre-configured and pre-optimized for the database.

### IV. Relational Database Management Systems (RDBMS) and ACID

#### A. Relational Database Structure (SQL)
A relational database (like SQL) is a collection of data items organized in tables.
*   It consists of data items with pre-defined relationships between them.
*   Information is organized as a set of tables with columns and rows.
*   Tables hold information about the objects represented in the database.
*   Columns hold specific kinds of data, and fields store the actual attribute value.
*   Rows represent a collection of related values of one object or entity.
*   Each row can be marked with a **primary key** (a unique identifier), and rows across multiple tables can be related using **foreign keys**.
*   This data can be accessed in many ways without re-organizing the tables themselves.

#### B. ACID Consistency Model
SQL databases usually follow the **ACID** consistency model. ACID is a set of properties for relational database transactions, ensuring data integrity.

*   **Atomicity:** Each transaction is "all or nothing"—all operations succeed, or every operation is rolled back.
*   **Consistency:** Any transaction will bring the database from one valid state to another; upon completion, the database is structurally sound.
*   **Isolation:** Executing transactions concurrently yields the same results as if the transactions were executed serially, meaning transactions do not contend with one another.
*   **Durability:** Once a transaction has been committed (writes are on disk), it will remain in the system even if a system failure occurs.

### V. Scaling Techniques for Relational Databases

There are many techniques used to scale a relational database, including replication, federation, sharding, denormalization, and SQL tuning.

#### A. Replication
**Replication** is the process of sharing information to ensure consistency between redundant databases, improving reliability, fault-tolerance, and accessibility.

##### 1. Master-Slave Replication
The master handles reads and writes and replicates writes to one or more slaves, which only serve reads. Slaves can replicate to additional slaves in a tree structure. If the master fails, the system can operate in read-only mode until a slave is promoted or a new master is provisioned.
*   **Advantages:** Backups have relatively no impact on the master; applications can read from slaves without impacting the master; slaves can be taken offline and synced back without downtime.
*   **Disadvantages:** Adds more hardware and complexity; downtime and possible data loss when a master fails; all writes must go to the master; increased replication lag with more read slaves.

##### 2. Master-Master Replication
Both masters serve reads/writes and coordinate with each other. If one master fails, the system continues to operate with both reads and writes.
*   **Advantages:** Applications can read from both masters; distributes write load across both nodes; simple, automatic, and quick failover.
*   **Disadvantages:** More complex to configure and deploy than master-slave; either loosely consistent or features increased write latency due to synchronization; conflict resolution becomes an issue as more write nodes are added and latency increases.

##### 3. Synchronous vs. Asynchronous Replication
*   **Synchronous Replication:** Data is written to primary storage and the replica **simultaneously**. The primary copy and replica should always remain synchronized.
*   **Asynchronous Replication:** Data is copied to the replica **after** it is already written to the primary storage. Replication may occur near-real-time or on a scheduled basis, making it more cost-effective.

#### B. Federation (Functional Partitioning)
Federation splits databases based on function. For example, a monolithic database could be split into separate databases for forums, users, and products.
*   **Advantages:** Less read and write traffic to each database, reducing replication lag; smaller databases mean more data can fit in memory, resulting in more cache hits due to improved cache locality; increased throughput as writes can occur in parallel without a single central master serializing them.
*   **Disadvantages:** Ineffective if the schema requires huge functions or tables; requires updating application logic to determine which database to read/write; joining data from two databases is more complex using a server link; adds more hardware and complexity.

#### C. Sharding
Sharding distributes data across different databases so that each database manages only a subset of the data. As the amount of data (e.g., users) increases, more shards are added. Common sharding methods include using the user's last name initial or geographic location.
*   **Advantages:** Less read and write traffic; reduced replication and increased cache hits; index size is reduced, generally improving performance with faster queries; increased throughput as writes can occur in parallel; other shards remain operational if one goes down (though replication is needed to prevent data loss).
*   **Disadvantages:** Requires updating application logic to handle shards, potentially resulting in complex SQL queries; data distribution can become lopsided (e.g., power users on one shard); rebalancing adds complexity (consistent hashing can help reduce transferred data); joining data from multiple shards is more complex; adds more hardware and complexity.

#### D. SQL Tuning
SQL tuning involves benchmarking and profiling to simulate and uncover bottlenecks.

**1. Benchmarking and Profiling:**
*   **Benchmark:** Simulate high-load situations (e.g., with tools like `ab`).
*   **Profile:** Enable tools like the slow query log to track performance issues.

**2. Tighten up the Schema:**
*   Use `CHAR` instead of `VARCHAR` for fixed-length fields for fast, random access.
*   Use `TEXT` for large blocks of text (e.g., blog posts); this stores a pointer on disk to locate the text block.
*   Use `INT` for numbers up to 2^32.
*   Use `DECIMAL` for currency to avoid floating-point representation errors.
*   Avoid storing large `BLOBS`; store the location of the object instead.
*   Set the `NOT NULL` constraint where applicable to improve search performance.

**3. Use Good Indices:**
*   Columns used for `SELECT`, `GROUP BY`, `ORDER BY`, or `JOIN` operations can be faster with indices.
*   Indices are usually represented as self-balancing B-trees, allowing searches, sequential access, insertions, and deletions in logarithmic time.
*   **Trade-offs:** Placing an index requires more space (keeping data in memory); writes are slower since the index must also be updated; when loading large data, it might be faster to disable indices, load data, then rebuild them.

**4. Other Tuning Methods:**
*   **Avoid expensive joins:** Denormalize where performance demands it.
*   **Partition tables:** Break up a table by putting hot spots in a separate table to help keep it in memory.
*   **Tune the query cache:** In some cases, the query cache can cause performance issues.

### VI. Indexing

**Indexes** are used to improve the speed of data retrieval operations on the data store. They work by allowing data to be located quickly without examining every row in a table.

**Trade-offs of Indexes:** Increased storage overhead and slower writes (as the index must be updated) are traded for faster reads.
*   An index is a data structure, like a table of contents, pointing to the location where actual data lives.
*   Creating an index on a column stores that column and a pointer to the whole row in the index.
*   Indexes can be used to create different views of the same data, allowing for different filters or sorting without creating multiple additional copies of the data.

#### A. Dense Index
In a **dense index**, an index record is created for **every row** of the table.
*   Records can be located directly because each index record holds the search key value and the pointer to the actual record.
*   **Trade-offs:** Requires more maintenance than sparse indexes at write-time (on inserts, updates, and deletes); requires more memory.
*   **Benefits:** Values can be quickly found with just a binary search; does not impose any ordering requirements on the data.

#### B. Sparse Index
In a **sparse index**, index records are created only for **some** of the records.
*   **Benefits:** Requires less maintenance than dense indexes at write-time; inserts, updates, and deletes are faster; uses less memory.
*   **Trade-offs:** Finding data is slower, as a scan across the page typically follows the binary search; optional when working with ordered data.

### VII. Normalization and Denormalization

#### A. Key Terms
*   **Primary key:** Column(s) used to uniquely identify every row in a table.
*   **Composite key:** A primary key made up of multiple columns.
*   **Super key:** Set of all keys that can uniquely identify all rows in a table.
*   **Candidate key:** Attributes that uniquely identify rows in a table.
*   **Foreign key:** A reference to a primary key of another table.
*   **Alternate key:** Keys that are not primary keys.
*   **Surrogate key:** A system-generated value that uniquely identifies each entry when no other column could hold primary key properties.
*   **Partial dependency:** Occurs when the primary key determines some other attributes.
*   **Functional dependency:** A relationship between two attributes, typically the primary key and a non-key attribute.
*   **Transitive functional dependency:** Occurs when some non-key attribute determines some other attribute.

#### B. Database Anomalies
A database anomaly is a flaw in the database due to incorrect planning or storing everything in a flat database. Normalization addresses these.
1.  **Insertion anomaly:** Inability to insert certain attributes without the presence of other attributes.
2.  **Update anomaly:** Occurs due to data redundancy and partial update, where a correct update requires additional actions (addition, deletion, or both).
3.  **Deletion anomaly:** Deletion of some data requires the deletion of other, unrelated data.

#### C. Normalization
**Normalization** is the process of organizing data by creating tables and establishing relationships between them according to rules designed to protect data and make the database more flexible by eliminating redundancy and inconsistent dependency.

*   **Goal:** Eliminate redundant data and ensure data consistency.
*   A fully normalized database allows its structure to be extended to accommodate new data types without significantly changing the existing structure.

**Normal Forms (Guidelines to ensure normalization):**
1.  **First Normal Form (1NF):** No repeating groups are permitted; identify each set of related data with a primary key; related data should have a separate table; mixing data types in the same column is not permitted.
2.  **Second Normal Form (2NF):** Satisfies 1NF; should not have any partial dependency.
3.  **Third Normal Form (3NF):** Satisfies 2NF; transitive functional dependencies are not permitted.
    *   In a relational database, meeting 3NF is often considered "normalized". Most 3NF relations are free of insertion, update, and deletion anomalies.
4.  **Boyce-Codd Normal Form (BCNF, or 3.5NF):** A slightly stronger version of 3NF. Satisfies 3NF; for every functional dependency X → Y, X must be the super key.

**Normalization Trade-offs:**
*   **Advantages:** Reduces data redundancy; better data design; increases data consistency; enforces referential integrity.
*   **Disadvantages:** Complex data design; slower performance; maintenance overhead; requires more joins.

#### D. Denormalization
**Denormalization** is a database optimization technique where redundant data is added to one or more tables.
*   **Purpose:** To avoid costly joins in a relational database.
*   It attempts to improve read performance at the expense of some write performance.
*   It can circumvent the need for complex joins when data is distributed using federation or sharding.

**Denormalization Trade-offs:**
*   **Advantages:** Retrieving data is faster; writing queries is easier; reduction in the number of tables; convenient to manage.
*   **Disadvantages:** Expensive inserts and updates; increases complexity of database design; increases data redundancy; increases the chances of data inconsistency.

### VIII. ACID and BASE Consistency Models (Trade-offs)

There is no single "right answer" to whether an application needs ACID or BASE; the choice depends on application requirements.

#### A. ACID (Relational Databases)
ACID properties are used for maintaining data integrity during transaction processing.
*   A fully ACID database is the perfect fit for use cases where **data reliability and consistency are essential**.

#### B. BASE (NoSQL Databases)
In the NoSQL world, databases often loosen requirements for immediate consistency, data freshness, and accuracy to gain benefits like **scale and resilience**. BASE properties are much looser than ACID guarantees.

*   **Basic Availability (BA):** The database appears to work most of the time.
*   **Soft-state (S):** Stores do not necessarily have to be write-consistent, and different replicas do not have to be mutually consistent all the time. The state of the system may change over time even without new input.
*   **Eventual Consistency (E):** The data might not be immediately consistent but will eventually become consistent, provided the system receives no new input during that period. Reads are still possible, though they may not give the correct response due to inconsistency.

#### C. BASE Trade-offs
*   BASE typically chooses **availability over consistency** (in comparison with the CAP Theorem).
*   This approach changes database design, moving logic out of the database and making the database more independent, focusing solely on storing data.
*   Developers choosing a BASE store must be more knowledgeable and rigorous about consistent data. Planning around BASE limitations can be a major disadvantage compared to the simplicity of ACID transactions.

### IX. NoSQL Database Types and Use Cases

NoSQL is a collection of data items represented in various non-relational stores. Data is denormalized, and joins are generally handled in the application code.

#### A. Reasons to Choose NoSQL
1.  Semi-structured data.
2.  Dynamic or flexible schema.
3.  Non-relational data.
4.  No need for complex joins.
5.  Need to store many TB or PB of data.
6.  Very data-intensive workload.
7.  Very high throughput for IOPS.

#### B. Sample Data Well-Suited for NoSQL
*   Rapid ingest of clickstream and log data.
*   Leader-board or scoring data.
*   Temporary data, such as a shopping cart.
*   Frequently accessed ('hot') tables.
*   Metadata/lookup tables.

#### C. Specific NoSQL Types

**1. Key-value store**
*   **Abstraction:** Hash table.
*   Generally allows for O(1) reads and writes and is often backed by memory or SSD.
*   Can maintain keys in lexicographic order, allowing efficient retrieval of key ranges.
*   Provides high performance and is used for simple data models or rapidly-changing data, such as an in-memory cache layer.
*   Often serves as the basis for more complex systems like document stores.
*   Complexity shifts to the application layer if additional operations are needed due to the limited set of operations offered.

**2. Document store**
*   **Abstraction:** Key-value store where documents (XML, JSON, binary, etc.) are stored as values.
*   A document stores all information for a given object.
*   Provides APIs or a query language to query based on the internal structure of the document.
*   Documents are organized by collections, tags, metadata, or directories.
*   Provides high flexibility and is often used for working with occasionally changing data.

**3. Wide column store**
*   **Abstraction:** Nested map (ColumnFamily <RowKey, Columns <ColKey, Value, Timestamp>>).
*   The basic unit is a column (name/value pair), which can be grouped into column families (like a SQL table).
*   Each column value contains a timestamp for versioning and conflict resolution.
*   Keys are often maintained in lexicographic order for efficient retrieval of key ranges.
*   Offers **high availability and high scalability** and is often used for very large datasets.

**4. Graph database**
*   **Abstraction:** Graph.
*   Each node is a record, and each arc is a relationship between two nodes.
*   Optimized to represent complex relationships with many foreign keys or many-to-many relationships.
*   Offers high performance for data models with complex relationships, such as a social network.
*   They are relatively new, and it might be difficult to find development tools and resources.

### X. Caching

#### A. Fundamentals
A cache's primary purpose is to **increase data retrieval performance** by reducing the need to access the underlying slower storage layer. Caching trades off capacity for speed, storing a subset of data transiently (unlike databases, whose data is usually complete and durable).

*   Caches leverage the principle of **locality of reference** ("recently requested data is likely to be requested again").
*   Caches store data in a **hierarchy of levels** (L1, L2, L3, etc.).
*   Cache operations (reads or writes) are done one block at a time.

**1. Cache Hit and Miss:**
*   **Cache Hit:** Content is successfully served from the cache.
    *   **Hot Cache:** Data read from L1 (fastest possible rate).
    *   **Warm Cache:** Data found in L2 or L3 (faster than cold).
    *   **Cold Cache:** Data found lower in the memory hierarchy (slowest successful read).
*   **Cache Miss:** Data is searched for but not found in memory. The content is then transferred and written into the cache.

**2. Benefits of Caching:**
*   Improves page load times and reduces load on servers and databases.
*   Helps absorb uneven loads and spikes in traffic by sitting in front of a database.
*   Improves performance, reduces latency, reduces network cost, and increases read throughput.

**3. When Not to Use Caching:**
*   When accessing the cache takes as long as accessing the primary data store.
*   When requests have low repetition (high randomness), as performance relies on repeated access patterns.
*   When data changes frequently, causing the cached version to get out of sync.
*   **Important Note:** A cache should **not** be used as permanent data storage; they are usually implemented in volatile, transient memory.

#### B. Cache Locations and Levels
Caches can be located on the client side, server-side, or in a distinct cache layer.

| Cache Location | Description |
| :--- | :--- |
| **Client Caching** | OS or browser cache. |
| **CDN Caching** | Content Delivery Networks are a type of cache. |
| **Web Server Caching** | Reverse proxies (like Varnish) or web servers serving static/dynamic content directly, avoiding application servers. |
| **Database Caching** | Default configuration caching within the database, which can be tweaked for specific usage patterns. |
| **Application Caching** | In-memory caches (Memcached, Redis) between the application and data storage. Data is held in faster RAM. |

**Levels of Data Caching:**
*   Row level
*   Query-level
*   Fully-formed serializable objects
*   Fully-rendered HTML

**Best Practice:** Generally avoid file-based caching, as it complicates cloning and auto-scaling.

#### C. Cache Update Strategies

**1. Cache-Aside (Lazy Loading)**
The application manages the cache and storage; the cache does not interact with storage directly. Only requested data is cached.
*   **Process (Cache Miss):** Application looks for entry in cache (miss) → Loads entry from database → Adds entry to cache → Returns entry.
*   **Pro:** Subsequent reads are fast. Avoids filling the cache with unrequested data.
*   **Disadvantages:** Each cache miss requires three trips, causing noticeable delay; data can become stale if updated in the database (mitigated by setting a Time-to-Live (TTL) or using write-through); node failure means replacement by an empty node, increasing latency.

**2. Write-Through Cache**
The application uses the cache as the main data store, and the cache is responsible for reading and writing to the database synchronously.
*   **Process:** Application adds/updates entry in cache → Cache synchronously writes entry to data store → Return. Data in the cache and database are updated simultaneously.
*   **Pro:** Subsequent reads of just written data are fast; data in the cache is not stale; complete data consistency between cache and storage.
*   **Disadvantages:** Slow overall operation due to synchronous write; new nodes created upon failure/scaling will not cache entries until they are updated in the database (cache-aside in conjunction with write-through can mitigate this); higher latency for write operations.

**3. Write-Behind (Write-Back)**
The application updates the entry in the cache, and the entry is **asynchronously** written to the data store.
*   **Pro:** Improves write performance.
*   **Disadvantages:** Potential for data loss if the cache fails before contents hit the data store; more complex to implement than cache-aside or write-through.

**4. Refresh-Ahead**
The cache is configured to automatically refresh a recently accessed entry prior to its expiration.
*   **Pro:** Can reduce latency if the cache accurately predicts which items will be needed.
*   **Disadvantages:** Inaccurate prediction of needed items can result in reduced performance.

#### D. Eviction Policies (To manage limited RAM capacity)
*   **First In First Out (FIFO):** Evicts the first block accessed, regardless of access frequency.
*   **Last In First Out (LIFO):** Evicts the most recently accessed block first, regardless of access frequency.
*   **Least Recently Used (LRU):** Discards the least recently used items first.
*   **Most Recently Used (MRU):** Discards the most recently used items first.
*   **Least Frequently Used (LFU):** Counts access frequency and discards those used least often.
*   **Random Replacement (RR):** Randomly selects an item to discard.

#### E. Distributed and Global Cache
*   **Distributed Cache:** Pools the RAM of multiple networked computers into a single in-memory data store. This allows the cache to grow beyond the memory limits of a single machine.
*   **Global Cache:** A single shared cache used by all application nodes. If requested data is missing, the cache finds it from the underlying data store.

### XI. Storage Types

**Storage** is a mechanism that enables a system to retain data, either temporarily or permanently.

#### A. RAID (Redundant Array of Independent Disks)
RAID is a method of storing the same data on multiple hard disks or SSDs to protect data in case of drive failure.

| RAID Level | Description | Key Feature(s) | Minimum Disks | Fault Tolerance | Capacity Utilization |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **RAID 0** | Striping | Data split evenly across drives. | 2 | None | 100% |
| **RAID 1** | Mirroring | At least two drives contain an exact copy. | 2 | Single-drive failure | 50% |
| **RAID 5** | Striping with Parity | Data striped across multiple drives with parity distributed across drives. | 3 | Single-drive failure | 67%-94% |
| **RAID 6** | Striping with Double Parity | Parity data written to two drives. | 4 | Two-drive failure | 50%-80% |
| **RAID 10** | Striping and Mirroring | Combines RAID 0 and RAID 1. Provides security by mirroring data while using striping to speed up transfers. | 4 | Up to one disk failure in each sub-array | 50% |

#### B. Volumes
A **volume** is a fixed amount of storage on a disk or tape. A single disk can contain multiple volumes, or a volume can span more than one disk.

#### C. Common Storage Types

**1. File Storage**
*   Stores data as files and presents it to users as a hierarchical directory structure.
*   **Advantage:** User-friendly solution to store and retrieve files.
*   Locating a file requires the complete path.

**2. Block Storage**
*   Divides data into blocks (chunks) and stores them as separate pieces.
*   Each block receives a unique identifier.
*   Decouples data from user environments, allowing data spread across multiple environments.
*   When requested, the storage system reassembles the data blocks.
*   Example: Amazon EBS.

**3. Object Storage**
*   Breaks data files up into pieces called **objects**.
*   Stores objects in a single repository that can be spread across multiple networked systems.
*   Example: Amazon S3.

#### D. Specialized Storage

**1. NAS (Network Attached Storage)**
*   A storage device connected to a network that allows storage and retrieval of data from a central location for authorized network users.
*   Flexible; storage can be added as needed.
*   Faster, less expensive, and provides the benefits of a public cloud on-site with complete control.

**2. HDFS (Hadoop Distributed File System)**
*   A distributed file system designed to run on commodity hardware.
*   Highly fault-tolerant and suitable for applications with large datasets.
*   Provides high throughput access to application data.
*   Stores each file as a sequence of blocks (all blocks except the last are the same size), which are replicated for fault tolerance.