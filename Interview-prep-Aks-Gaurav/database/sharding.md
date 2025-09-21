# 🔹 Sharding & Partitioning — Problem → Solutions → Tradeoffs → Best Solution

---

## 1. Problem Statement

We have a database that is:

* Growing too large for a single server (**storage bottleneck**).
* Queries slowing down due to large tables & indexes (**performance bottleneck**).
* Heavy read & write load on a single DB (**scalability bottleneck**).
* Need to ensure **high availability** (single machine failure can’t bring down system).

👉 So, we must **split the data** (partition/shard) to handle **scale, performance, and reliability**.

---

## 2. Solutions

### (A) Vertical Partitioning

* Split table by columns.
* Example: User basic info in DB1, preferences/settings in DB2.

✅ Keeps tables smaller, faster queries.
❌ Doesn’t scale **storage across machines** (still on one node).

---

### (B) Horizontal Partitioning (Partitioning within a DB)

* Split rows across partitions in the **same DB instance**.
* Example: UserID 1–1M → Partition1, 1M–2M → Partition2.

✅ Queries touch smaller partitions, indexes are lighter.
❌ Still limited by one machine’s resources.

---

### (C) Sharding (Horizontal Partitioning across DB nodes)

* Distribute rows across multiple DB servers.
* Example: UserID-based sharding: Users 1–1M in Shard1, etc.

✅ Scales beyond a single machine (storage + load distribution).
❌ Introduces new challenges → query routing, joins across shards, rebalancing.

---

## 3. Sharding Strategies with Tradeoffs

### 1. Range-based Sharding

* Divide data into ranges (e.g., ID 1–1M → Shard1, 1M–2M → Shard2).

✅ Pros:

* Easy to implement.
* Range queries are efficient.

❌ Cons:

* Hotspot risk: new inserts always go to the latest shard.
* Rebalancing is costly (must move ranges).

---

### 2. Hash-based Sharding

* Compute `shard_id = hash(key) % N`.

✅ Pros:

* Uniform distribution of data.
* Reduces hotspot risk.

❌ Cons:

* Hard to do range queries.
* Adding/removing shards = rehashing lots of data.

---

### 3. Directory/Lookup Service

* Maintain a mapping table of key → shard.

✅ Pros:

* Very flexible (can move data easily).
* Can rebalance dynamically.

❌ Cons:

* Central service = single point of failure.
* Extra lookup adds latency.

---

### 4. Geo-based Sharding

* Users split by region (US users → US shard, EU users → EU shard).

✅ Pros:

* Lower latency for users.
* Data residency compliance (GDPR, etc.).

❌ Cons:

* Cross-region queries become very slow.
* Uneven data distribution (US >> EU).

---

## 4. Best Solution (Avoiding Tradeoffs)

👉 **No one-size-fits-all** — but a robust solution usually looks like this:

* **Primary Strategy = Hash-based Sharding with Consistent Hashing**

  * Spreads load evenly.
  * Handles shard addition/removal gracefully (only small % data moves).
* **Combine with Range Sharding inside each shard**

  * For better query performance on ranges.
* **Add Lookup Service (like MongoDB Config Server, Vitess, Citus)**

  * Central router ensures clients don’t need complex logic.
* **Mitigate Joins Across Shards**

  * Denormalize data.
  * Application-level joins.
  * Pre-computed aggregates.
* **Rebalancing Strategy**

  * Use consistent hashing OR logical partitions (virtual shards) so new physical nodes can steal partitions easily.

---

## 5. Real-World Best Practices

* **Cassandra/HBase/Bigtable** → consistent hashing + range partitions.
* **MongoDB** → shard key + config server for routing.
* **Vitess (used by YouTube)** → manages sharding transparently over MySQL.
* **Google Spanner** → sharding + Paxos for global transactions (solves cross-shard ACID).

---

## 6. Interview Insight (Key Defense Points)

* If interviewer says **“Hotspot risk”** → answer with **composite shard keys** or **hashing with randomness**.
* If they say **“What about rebalancing?”** → answer with **consistent hashing or virtual shards**.
* If they say **“Cross-shard transactions?”** → answer with **avoid them, or use 2PC/Spanner-like consensus**.
* If they say **“Geo-distribution?”** → answer with **geo-sharding, async replication across regions, compliance-aware routing**.

---

✅ **Final Wrap**

* **Partitioning** = logical data split (rows/columns/features).
* **Sharding** = physical distribution across DB servers.
* Each strategy solves scaling but introduces **tradeoffs**.
* **Best practice** → Hash-based sharding with consistent hashing + router + replicas, combined with denormalization to avoid cross-shard joins.

---
