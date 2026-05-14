import java.util.*;

public class ReviewerManager {
    private static final int MAX_WORKLOAD = 3;
    Database db;
    List<Reviewer> reviewerList;

    public ReviewerManager(Database db){
        this.db = db;
        reviewerList = new ArrayList<>();
    }
    public List<Reviewer> getAvailableReviewers() {
        reviewerList = db.fetchReviewers();
        reviewerList = filterConflicts(reviewerList);
        reviewerList = checkWorkload(reviewerList);
        return reviewerList;
    }

    public List<Reviewer> filterConflicts(List<Reviewer> reviewers) {
        List<Reviewer> filtered = new ArrayList<>();
        for (Reviewer reviewer : reviewers) {
            if (!reviewer.hasConflict) {
                filtered.add(reviewer);
            }
        }
        System.out.println("After conflict filter: " + filtered.size() + " reviewer(s)");
        return filtered;
    }

    public List<Reviewer> checkWorkload(List<Reviewer> reviewers) {
        List<Reviewer> available = new ArrayList<>();
        for (Reviewer reviewer : reviewers) {
            if (reviewer.currentWorkload < MAX_WORKLOAD) {
                available.add(reviewer);
            }
        }
        System.out.println("After workload check: " + available.size() + " reviewer(s)");
        return available;
    }
}