# Indexing Internals — SQL & NoSQL (Deep Dive)

> **Goal:** Full technical deep-dive into how indexes work inside SQL and NoSQL systems. Includes internals, data structures, read/write paths, trade-offs, diagrams, and practical advice for system-design and interviews.

---

## Table of Contents

1. Executive summary
2. Why indexes exist — benefits and costs
3. Fundamental building blocks (pages, nodes, buffer pool, WAL)
4. B‑Tree / B+‑Tree internals (detailed)
5. Hash indexes (how they work)
6. Bitmap indexes
7. Inverted indexes (search engines & full‑text)
8. LSM‑tree (log‑structured merge tree) indexes — used by many NoSQL systems
9. DB‑specific implementations & notes (Postgres, InnoDB, MongoDB, Cassandra, Elasticsearch, Redis)
10. Query planning & index selection (statistics, cardinality, covering indexes)
11. Concurrency, transactions, and MVCC interactions with indexes
12. Maintenance, fragmentation, compaction, reindexing
13. Trade‑offs & heuristics: when to pick what index
14. Performance metrics: read/write amplification, storage, latency
15. Practical interview soundbites and example SQL/commands
16. Summary and further reading suggestions

---

## 1 — Executive summary

* **Indexes** are auxiliary data structures that accelerate data retrieval at the cost of extra storage and extra work on writes.
* Most OLTP relational DBs use **B+‑trees** for range queries and ordered lookups. Many NoSQL systems use **LSM‑trees** (SSTable + memtable) for write‑heavy workloads.
* **Inverted indexes** power full‑text search systems (Elasticsearch, Lucene) and are structured very differently — they map terms → posting lists (document IDs + metadata).
* **Index choice is a trade‑off**: read latency vs write throughput vs storage overhead vs complexity for distributed consistency.

---

## 2 — Why indexes exist — benefits and costs

**Benefits**

* Faster lookups (point and range queries)
* Support for sorted order, ORDER BY, GROUP BY, DISTINCT, and join optimization
* Can enable **index‑only plans** (no need to visit base table if index contains all required columns)

**Costs**

* Extra storage (index data lives on disk and in caches)
* Extra work for writes — every insert/update/delete that affects indexed columns must update indexes
* Complexity: design choices (composite order, uniqueness, partial/functional) matter
* Maintenance: reindexing, statistics updates

---

## 3 — Fundamental building blocks (common across DBs)

* **Disk pages / blocks:** Fundamental unit read/written to disk (typically 4KB–16KB). Index nodes are stored in pages.
* **Buffer pool / cache:** In‑memory cache of pages (Postgres shared\_buffers, InnoDB buffer pool). Reduces disk IO.
* **Write‑Ahead Log (WAL):** Ensures durability; index updates generally logged so they can be replayed.
* **Lock manager / MVCC:** Controls concurrency and isolation. Index updates interact closely with transaction visibility.

---

## 4 — B‑Tree / B+‑Tree internals (detailed)

**Why B+‑trees?**

* B+‑trees are optimized for reads/writes on disk: high branching factor → low tree height → few disk seeks.
* Ordered structure supports range scans and ascending/descending iteration.

**Structure**

* Internal nodes: keys and child pointers (only guide navigation)
* Leaf nodes: actual keys and pointers to records (or record storage in clustered index)
* All data entries in leaves (B+‑tree) → good for range scans (sequential leaf chaining).

**Terms**

* `order` (m): max children per internal node.
* `fill factor`: how full pages are allowed to be; impacts page splits and fragmentation.

**Typical operations**

* **Search:** Start at root → compare key → follow child pointer → repeat → find leaf → locate record or pointer.
* **Insert:** Find leaf → if leaf has room, insert sorted. If leaf is full, **split** into two leaves, push median up → may cascade splits up to root.
* **Delete:** Remove key → if underflow, may **borrow** from sibling or **merge** sibling → propagate up.

**Disk / memory layout**

* Nodes stored in pages on disk; a node might occupy one or more pages depending on size.
* DB keeps hot pages in buffer pool; writes to a page mark it dirty; flush by page replacer.

**B+‑tree diagram (ASCII)**

```
            [ 50 ]
           /      \
   [10,20,30]    [60,70,80]
   /   |   \      /   |   \
 L1   L2   L3   L4   L5   L6
```

* Internal node has split key 50; leaves L1..L6 store actual keys and are doubly/ singly linked for fast range scans.

**Clustered vs non‑clustered**

* **Clustered index:** table rows physically ordered by index (InnoDB primary key). Leaf nodes contain the full row.
* **Non‑clustered index:** leaf contains pointers (rowid, primary key) to find the row in the clustered store (bookmark lookup).

**Index‑only scan**

* If the index holds all columns required by the query (covering index), DB may avoid visiting base table pages → faster.

**Index maintenance cost on writes**

* Inserts → find leaf → insert or split → write pages
* Updates to indexed column → may need delete + insert in index
* Deletes → remove entry, possibly merge

**Page splits & fragmentation**

* Frequent random inserts can cause many splits → more pages → more I/O and larger index size. Fill‑factor config can mitigate.

---

## 5 — Hash indexes

**Concept**

* Map key → bucket via hash(key).
* Each bucket contains entries; for collisions use chaining or open addressing.

**Properties**

* Extremely fast for equality lookups (O(1) expected).
* Not suitable for range queries (no ordering).

**Variants**

* **Static hash**: fixed number of buckets — resizing is costly.
* **Extendible or linear hashing**: dynamic grow/shrink without global rehash.

**Use cases**

* Primary use: equality lookups like `WHERE pk = X` when ordering isn't needed.

**Diagram (simple)**

```
Hash(key) -> bucket 3 -> [(keyA, ptr), (keyB, ptr)]
```

**Note:** Many RDBMS avoid general hash indexes because B+‑trees also serve equality efficiently and support range scans.

---

## 6 — Bitmap indexes

**What:** Maintain a bitmap (bitset) per distinct value of a column. For a table with N rows, each distinct value gets an N‑bit vector.

**Benefits**

* Extremely compact for low‑cardinality columns (e.g., gender, boolean flags).
* Very fast logical operations: AND, OR, XOR across bitmaps to combine multiple predicates.

**Costs**

* Not good when cardinality is high (many distinct values → lots of bitmaps).
* Updates can be costly because bitmaps are large; typically used in read‑heavy OLAP scenarios.

**Use case**

* Data warehouses, BI queries with many AND/OR filters across low‑card columns.

---

## 7 — Inverted indexes (search engines & full text)

**Core idea**

* Index terms (tokens) → posting list of documents (docID, positions, frequencies).
* Extremely efficient for `term` queries, phrase queries, proximity queries, full text relevance scoring.

**Components**

* **Analyzer / tokenizer:** breaks a document into terms, normalizes (lowercase, stem), may remove stop words.
* **Term dictionary**: sorted list of unique terms; often implemented as a compact trie (FST) in Lucene.
* **Posting lists**: for each term, ordered list of postings `(docID, termFreq, [positions...])`.
* **Segment files**: inverted index is stored in immutable segments on disk; merges compact segments.

**Query processing**

* For multi‑term queries: fetch posting lists for each term → intersect/union using skip lists or galloping search → compute scores.

**Compression**

* Posting lists store docIDs as deltas (docID differences) + variable‑byte or gamma encoding. This reduces I/O.

**Diagram**

```
Term: "cat"  -> postings: [doc1:pos(3,10), doc3:pos(7), doc10:pos(2,8,20)]
Term: "dog"  -> postings: [doc2:pos(1), doc3:pos(2), doc5:pos(9)]

Query: "cat AND dog" -> intersect postings -> doc3
```

**Advanced features**

* **Doc values / columnar doc store** for sorting/aggregations (defer to disk column format)
* **FSTs** for prefix lookups and memory efficient dictionaries
* **Field norms** and term statistics for relevance scoring (TF‑IDF, BM25)

---

## 8 — LSM‑tree (Log‑Structured Merge Tree) internals — used by Cassandra, RocksDB, LevelDB, HBase

**High level**

* Writes are appended to an in‑memory structure (memtable) and to WAL for durability.
* Memtable flushed to disk as immutable **SSTable** (sorted string table).
* SSTables are periodically **compacted** (merged) to reduce duplication and tombstones.

**Why LSM?**

* Excellent write throughput: appends are cheap; no random writes to disk pages.
* Trade‑offs: read amplification (must check multiple SSTables), write amplification (compaction cost), storage overhead for older versions.

**Write path**

1. Client issues write
2. Append to WAL (durability)
3. Insert into memtable (sorted in memory, often a skip list or tree)
4. When memtable full -> flush to disk as SSTable (immutable)

**Read path**

* Check memtable -> bloom filter -> partition index/summary -> if not found, check SSTables (newest → oldest), using partition index and block index to seek.

**SSTable layout (typical)**

* Data blocks (compressed) containing sorted key/value pairs
* Block index (sparse) mapping key ranges to blocks
* Partition index for wide partitioned rows (Cassandra)
* Bloom filter per SSTable to quickly exclude files

**Compaction**

* Merge several SSTables into one: combine keys, drop deleted keys (tombstones) past gc grace seconds.
* Compaction reduces read amplification and reclaim space but causes write amplification (multiple writes of same data).

**Diagram (write + read)**

```
Write: client -> WAL -> memtable -> (flush) -> SSTable1
                                  -> SSTable2 -> compact -> SSTable3

Read: check memtable -> if not found check SSTable1 (bloom filter -> index -> block -> key)
                                              -> SSTable2 -> ...
```

**Optimizations used**

* **Bloom filters**: probabilistic test to avoid disk reads for absent keys
* **Sparse block index**: reduce memory use by indexing block minima
* **Cache most recent SSTable blocks**
* **Levelled compaction** vs size‑tiered compaction strategies

---

## 9 — DB‑specific implementations & notes

### PostgreSQL

* **Default index type:** B‑Tree. Also supports GIN (inverted, for arrays/fulltext), GiST (generalized search tree for spatial), BRIN (Block Range Index, very lightweight, good for naturally ordered large tables).
* **MVCC & visibility:** Rows have XID visibility; index entries may point to heap TIDs; vacuum removes dead tuples; index‑only scan requires visibility map to be set.
* **Index storage:** index pages in standard page sizes; separate index relations in catalog.

**Important Postgres features**

* **GIN**: inverted index good for `jsonb`, arrays, full text; postings point to lists of item pointers.
* **BRIN**: tiny indexes storing min/max per block range → extremely small and helpful for append‑only timestamped data.

### MySQL (InnoDB)

* **Clustered primary key as B+‑tree**: primary key B+‑tree stores actual row data in leaves. Secondary indexes store primary key values as pointers (so secondary lookup → primary key lookup).
* **Adaptive hash index:** InnoDB may build an in‑memory hash index for frequently accessed pages.
* **Insert behavior:** random inserts cause page splits like any B+‑tree; PK selection influences clustering locality.

**Implication:** Choosing a good primary key (sequential, avoid hot spot random UUIDs) helps reduce page splits and fragmentation.

### MongoDB

* Uses B‑tree variants for indexes. Supports single field, compound, multikey (arrays), hashed and text indexes.
* **Multikey indexes:** array field results in multiple index entries for a single document.
* **Sharding:** hashed or range‑based sharding; hashed distributes writes across shards evenly but loses range locality.

### Cassandra (wide‑column, LSM)

* **Primary organization:** partition key decides which node stores the partition; clustering columns determine order inside a partition.
* **SSTable** layout with partition index and partition summary; bloom filters used.
* **Secondary indexes** exist but are limited; wide usage leads to performance problems. Recommended pattern: model queries into primary key (query‑driven modeling).

### Elasticsearch / Lucene

* **Segmented inverted index** model. Each segment is immutable with postings lists and term dictionaries (FST). Queries intersect posting lists, and segments are merged.
* **Doc values**: columnar on‑disk structures for sorting/aggregations.
* **Scoring:** uses term statistics (TF, DF) and normalization (BM25) to rank results.

### Redis

* In‑memory. Indexing concepts vary by data type:

  * `HASH`, `SET`, `ZSET` (sorted set uses skip list + hash table pair)
  * For sorted indexing of scores, skip lists provide O(log n) operations.
* No disk‑based B+trees; persistence via RDB/AOF is separate.

---

## 10 — Query planning & index selection (statistics, histograms, cardinality)

* **Statistics & histograms:** DB collects column-level stats (num distinct values, nulls, most common values, histograms) to estimate selectivity. Good stats → better index use.
* **Selectivity:** fraction of rows matching predicate. High selectivity = good candidate for index.
* **Composite indexes & left‑prefix rule:** For index on (a,b,c), queries can use index for searches that specify `a`, `a,b`, or `a,b,c` (left prefix); not `b` alone unless index intersection is used.
* **Index intersection:** Some engines can combine multiple single‑column indexes by intersecting row sets.
* **Cost model:** planner estimates cost of full table scan vs index seek + lookup (bookmark lookup). If many rows match, scanning is cheaper.

**Example:** `EXPLAIN` output will show index scan vs seq scan and cost estimates.

---

## 11 — Concurrency, transactions, MVCC interactions with indexes

* **Locking:** Some DBs take locks on index pages during modifications; others rely on MVCC to avoid heavy locking.
* **Phantom reads & gap locks:** MySQL InnoDB uses next‑key locks to prevent phantoms under REPEATABLE READ.
* **Visibility & index‐only scans:** To avoid reading base table for visibility checks, Postgres uses "visibility map". If tuple visibility is unknown, index scan must visit heap.
* **Index updates inside transactions:** index entries are added/marked visible only after commit; rollbacks remove entries. This must be synchronized with WAL.

---

## 12 — Maintenance, fragmentation, compaction, reindexing

* **RDBMS:** `REINDEX`, `VACUUM` (Postgres) reclaim space, update stats.
* **LSM systems:** compaction merges SSTables and cleans up tombstones.
* **Fill factor** parameter (e.g., in Postgres and SQL Server) controls how full pages are at creation; leaving free space reduces future splits for append-heavy workloads.
* **Monitoring:** check index bloat, index size, page split rates, read/write latency, and planner decisions.

---

## 13 — Trade‑offs & heuristics: when to pick what index

* **B+‑Tree**: pick when you need range scans, ordered results, prefix searches. Good in OLTP reads.
* **LSM**: pick for high write throughput (ingest), append logs, time series where writes dominate. Accept read amplification.
* **Inverted index**: pick for text search, tag/keyword queries, array containment queries (GIN in Postgres).
* **Bitmap**: pick for analytic queries over low‑cardinality attributes.
* **Hash**: pick for pure equality lookups where range queries are never needed.

**Heuristics**

* Avoid many indexes on write‑heavy tables.
* Favor composite indexes that match query patterns (order matters).
* Consider partial/filtered indexes to cover a subset of rows (reduce index size).
* Use covering indexes for hot read paths.

---

## 14 — Performance metrics: read/write amplification, storage, latency

* **Read amplification:** how many disk reads are needed to satisfy a read (LSM can check multiple SSTables; B+‑tree typically fewer but may require heap lookup).
* **Write amplification:** amount of extra writes required to persist one logical write (LSM compaction causes multiple physical writes).
* **Storage overhead:** index size relative to base table; compressed indexes vs uncompressed.
* **Latency:** small index height and cached root/upper nodes reduce lookup latency.

---

## 15 — Practical interview soundbites & example commands

**Soundbite:**

> "B+‑trees minimize disk seeks by keeping a high fan‑out, so tree height is small. LSM‑trees optimize for writes by converting random writes into sequential writes, using memtables and SSTables, but trade off read‑amplification and compaction overhead."

**SQL examples**

```sql
-- Create simple index
CREATE INDEX idx_user_email ON users (email);

-- Composite index (order matters)
CREATE INDEX idx_orders_userid_created ON orders (user_id, created_at DESC);

-- Partial index (Postgres)
CREATE INDEX idx_active_users ON users (last_login) WHERE active = true;

-- Functional index (Postgres)
CREATE INDEX idx_lower_email ON users ((lower(email)));
```

**MongoDB**

```javascript
db.users.createIndex({ email: 1 }, { unique: true })
// multikey for arrays
db.docs.createIndex({ tags: 1 })
```

**Inspecting an index**

* Postgres: `EXPLAIN (ANALYZE, BUFFERS)` to see index usage and buffer hits.
* MySQL: `EXPLAIN` shows `Using index` or `Using where; Using index` (index only).

---

## 16 — Advanced topics (brief notes)

* **FSTs (Finite State Transducers)**: used by Lucene for compact term dictionaries and prefix/prefix‑scoring.
* **Skip lists**: used in some in‑memory indexes (Redis ZSET uses skip list and hash combo).
* **Adaptive indexing / index-only storage engines**: some databases evolve indexes dynamically based on query workload.

---

## 17 — Diagrams (summary)

### A) B+‑tree (leaf linked list)

```
          [ 50 ]
         /      \
    [10,20,30]  [60,70,80]
    /  |   \     /  |   \
  L1  L2   L3  L4  L5   L6

Leaf L2 holds keys: 11,12,14,17 | pointer -> rows
Leaf nodes are linked: L1 -> L2 -> L3 ...  -> L6
```

### B) LSM write/read & compaction

```
Write path:
Client -> WAL (append) -> memtable (in-memory sorted) -> flush -> SSTable-A (immutable)

Read path:
Client -> memtable -> SSTable-A (bloom filter -> partition index -> block index -> data block)

Compaction:
SSTable-A + SSTable-B -> compact -> SSTable-C (drop tombstones, merge keys)
```

### C) Inverted index (posting lists)

```
Document 1: "the cat sat"
Document 2: "the dog sat"

Inverted index:
term: "the" -> [doc1,pos1; doc2,pos1]
term: "cat" -> [doc1,pos2]
term: "dog" -> [doc2,pos2]
term: "sat" -> [doc1,pos3; doc2,pos3]
```

---

## Closing notes — what to memorize for interviews

* Be able to draw a **B+‑tree** and explain insertion/split.
* Be able to explain **LSM write + read path, compaction, bloom filters** for read avoidance.
* Know **inverted index** basics and why it’s ideal for full‑text search.
* Be ready to discuss **CAP tradeoffs** if index choices involve distributed replicas.
* Give concrete examples: e.g., "Use LSM (Cassandra/Dynamo style) for tweet ingestion because of high write throughput; use B+‑tree for user profile lookups with transactions."

