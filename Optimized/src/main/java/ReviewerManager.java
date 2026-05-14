import java.util.*;

public class ReviewerManager {

    private static final int MAX_WORKLOAD = 3;

    private final Database db;

    public ReviewerManager(Database db) {
        this.db = db;
    }

    public List<Reviewer> assignReviewers(String submissionId) {

        List<Reviewer> all = db.fetchReviewers();

        List<Reviewer> eligible = checkWorkload(filterConflicts(all));

        for (Reviewer r : eligible) {
            r.incrementWorkload();
        }

        db.persistAssignments(eligible);

        System.out.println("Assigned " + eligible.size() + " reviewer(s) to submission " + submissionId);
        return eligible;
    }


    private List<Reviewer> filterConflicts(List<Reviewer> reviewers) {
        List<Reviewer> filtered = new ArrayList<>();
        for (Reviewer r : reviewers) {
            if (!r.hasConflict) filtered.add(r);
        }
        System.out.println("   Conflict filter: " + filtered.size() + " eligible");
        return filtered;
    }

    private List<Reviewer> checkWorkload(List<Reviewer> reviewers) {
        List<Reviewer> available = new ArrayList<>();
        for (Reviewer r : reviewers) {
            if (r.currentWorkload < MAX_WORKLOAD) available.add(r);
        }
        System.out.println("   Workload filter: " + available.size() + " available");
        return available;
    }
}
