# ReviewFlow

> **From Behavioural Models to Optimized Implementation**

This repository contains two independent Java (Maven) projects side-by-side:

| Folder | Purpose |
|--------|---------|
| `Original/` | Baseline implementation
| `Optimized/` | Refactored implementation

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java (JDK) | 11 or later |
| Maven | 3.6 or later |

Verify with:
```bash
java -version
mvn -version
```

---

## Running the Original (Baseline) System

```bash
cd Original
mvn compile
mvn exec:java -Dexec.mainClass="Main"
```

### What it does
1. Prompts you (via console) to enter a submission title and content.
2. Validates format via `Validator`.
3. Saves the submission to the **H2 embedded database** (`peerreview.mv.db`).
4. Fetches reviewers from the DB, filters conflicts, checks workload.
5. Assigns each reviewer and runs evaluation (average → consensus → decision).
6. Prints the outcome: **ACCEPTED / REJECTED / REVISION**.

---

## Running the Optimized System

```bash
cd Optimized
mvn compile
mvn exec:java -Dexec.mainClass="Main"
powershell : exec:java "-Dexec.mainClass=Main"
```

The flow is functionally identical but the internals differ:
- `ReviewerManager` filters **in a single pass** instead of two separate loops.
- Scores are persisted **in bulk** (`persistScores`) instead of one-by-one.
- Decision logic lives entirely in `DecisionTable.evaluate()` – no scattered `if/else` across classes.
- `NotificationService.sendNotification()` dispatches all three outcomes through one method.

---

## Checking the Database (Original only)

The Original system starts an **H2 Web Console** automatically when you run it.

1. Run the Original system (keep the process running).
2. Open your browser and go to: **http://localhost:8082**
3. Use these connection settings:

   | Field | Value |
   |-------|-------|
   | Driver Class | `org.h2.Driver` |
   | JDBC URL | `jdbc:h2:./peerreview` |
   | User Name | `sa` |
   | Password | *(leave blank)* |

4. Click **Connect**.

Useful queries once inside:
```sql
-- See all submissions
SELECT * FROM submissions;

-- See all reviewers and their current scores / workload
SELECT * FROM reviewers;
```

> **Note:** The Optimized system uses an **in-memory mock** (`Database.java`) – no persistent file is created, so there is no web console for that project.

---

## Tweaking Reviewer Scores

### Original – via the H2 database

Reviewer scores are seeded once on first run (when the `reviewers` table is empty).  
To change them **before the first run**, edit `Original/src/main/java/Database.java`,  
inside `insertSampleReviewers()`:

```java
// Original/src/main/java/Database.java  ← line ~70
Object[][] data = {
    {"Alice", 1.5, false, 1},   // name, score, hasConflict, currentWorkload
    {"Bob",   1.2, false, 0},
    {"Carol", 1.8, false, 2},
    {"David", 1.0, false, 1}
};
```

> Scores here are on a **0–10 scale** but the sample data uses low values (1–2).  
> The accept/reject thresholds in `EvaluationManager` are `ACCEPT_THRESHOLD = 7.0` and `REJECT_THRESHOLD = 4.0`.  
> Raise scores above 7 to trigger **ACCEPTED**; drop below 4 for **REJECTED**; anything in-between gives **REVISION**.

If the DB file (`peerreview.mv.db`) already exists from a previous run, the seed is skipped.  
**Delete `peerreview.mv.db`** before re-running to apply new seed values:
```bash
rm Original/peerreview.mv.db
```

To change scores **live** (while the system is running), use the H2 console:
```sql
UPDATE reviewers SET score = 8.0 WHERE name = 'Alice';
UPDATE reviewers SET score = 7.5 WHERE name = 'Bob';
```

### Optimized – in-memory only

Reviewer data is hardcoded in `Optimized/src/main/java/Database.java`,  
inside `fetchReviewers()`:

```java
// Optimized/src/main/java/Database.java  ← line ~12
list.add(new Reviewer("Alice", 7.5, false, 1));  // name, score, hasConflict, currentWorkload
list.add(new Reviewer("Bob",   6.8, false, 0));
list.add(new Reviewer("Carol", 7.2, false, 2));
list.add(new Reviewer("David", 5.0, false, 1));
```

Edit these values, then recompile (`mvn compile`) and re-run.

### Decision thresholds (both systems)

| Constant | Default | Location |
|----------|---------|----------|
| `ACCEPT_THRESHOLD` | `7.0` | `Original/src/main/java/EvaluationManager.java` · `Optimized/src/main/java/DecisionTable.java` |
| `REJECT_THRESHOLD` | `4.0` | same files |
| `CONSENSUS_TOLERANCE` (max std dev) | `1.5` | same files |

Change these constants and recompile to shift the decision boundaries.

---

## Project Structure

```
├── README.md
│
├── Original/                        # Task 1 – Baseline
│   ├── pom.xml
│   ├── peerreview.mv.db             # H2 database file (created on first run)
│   └── src/main/java/
│       ├── Main.java
│       ├── UI.java
│       ├── SubmissionController.java
│       ├── Validator.java
│       ├── Database.java            # ← seed reviewer scores here
│       ├── ReviewerManager.java
│       ├── Reviewer.java
│       ├── EvaluationManager.java   # ← change ACCEPT/REJECT thresholds here
│       ├── NotificationService.java
│       └── Data.java
│
└── Optimized/                       # Task 5 – Optimized
    ├── pom.xml
    └── src/main/java/
        ├── Main.java
        ├── UI.java
        ├── SubmissionController.java
        ├── Validator.java
        ├── Database.java            # ← edit fetchReviewers() to change scores
        ├── ReviewerManager.java
        ├── Reviewer.java
        ├── EvaluationManager.java
        ├── DecisionTable.java       # ← change ACCEPT/REJECT thresholds here
        ├── NotificationService.java
        ├── Outcome.java
        └── Data.java
```
