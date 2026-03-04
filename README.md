# Lab 7 – Advanced Data Structures and File Processing
**CS 302**

## Overview
This lab focuses on implementing two graph algorithms that both run in **linear time** — O(|V| + |E|). The key challenge is achieving this efficiency constraint rather than simply getting correct results.

---

## Problem 1 – Sorting Adjacency Lists

### Problem
Given an undirected graph represented as an adjacency list, sort every neighbor list in ascending order — all in **O(|V| + |E|)** total time.

### Why Naive Sorting Fails
Sorting each adjacency list individually (even with counting sort) costs **O(|V|² + |E|)**, which is too slow. A new count array of size n per vertex alone costs O(|V|²) just for initialization.

### Approach – Global Counting Sort
The trick is to reuse a **single global count array** of size n across all adjacency lists:

1. For each vertex `v`, fill the count array using `edges[v]`
2. Walk the count array from `0` to `n-1`, writing back into `edges[v]` in sorted order
3. Reset only the slots that were used (not the whole array)

Since vertex IDs are integers in range `[0, n-1]`, counting sort applies naturally — no comparisons needed.

**Total cost:**
- O(|V|) — one global count array
- O(|E|) — filling counts across all vertices
- O(|E|) — rebuilding lists
- O(|E|) — resetting used slots

= **O(|V| + |E|)** ✓

### Key Insight
Resetting only the slots you **actually used** (rather than the whole array each time) is what keeps the total work linear.

---

## Problem 2 – Shortest Paths on a Weighted DAG

### Problem
Given a weighted **directed acyclic graph (DAG)** and a source vertex `s`, find the shortest distance from `s` to all other vertices in **O(|V| + |E|)** time. Edge weights can be negative.

### Why Dijkstra's Fails
Dijkstra's algorithm assumes **non-negative edge weights**. With negative weights it can finalize a vertex too early and miss shorter paths discovered later.

### Why Bellman-Ford is Too Slow
Bellman-Ford runs in **O(|V| × |E|)**, far worse than the required linear time.

### Approach – Topological Order + Relaxation
A DAG has no cycles, which means vertices can be processed in **topological order** — an ordering where every vertex is processed before any vertex that depends on it. This guarantees that once a vertex is processed, its shortest distance is finalized and never needs to be revisited.

**Steps:**
1. Run `dfs(s)` and extract the **post-order** array — this gives topological order
2. Initialize distances: `distances[s] = 0`, all others = `Integer.MAX_VALUE`
3. Walk through vertices in topological order, calling `relax(v, i, distances)` for each neighbor

Each edge is relaxed exactly **once** → **O(|V| + |E|)** ✓

### Key Insight
The DAG property eliminates the need to revisit vertices, making a single pass in topological order sufficient — even with negative weights.

---

## Implementation Notes
- All logic is implemented inside `Problem1` and `Problem2` in `Lab7.java`
- Uses helper functions `dfs`, `bfs`, and `relax` from `Graph.java`
- No additional helper functions were added outside the required methods

---

## Files
| File | Description |
|------|-------------|
| `Lab7.java` | Main implementation file with Problem1 and Problem2 |
| `Graph.java` | Provided graph class with `relax`, `dfs`, and `bfs` utilities |
