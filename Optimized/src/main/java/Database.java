import java.util.*;

public class Database {
    public Database() {
        System.out.println("Database tables ready.");
        System.out.println("Sample reviewers inserted.");
    }
    public void saveSubmission(Data data) {
        System.out.println("Submission saved: " + data.title);
    }
    public List<Reviewer> fetchReviewers() {
        List<Reviewer> list = new ArrayList<>();
        list.add(new Reviewer("Alice", 7.5, false, 1));
        list.add(new Reviewer("Bob",   6.8, false, 0));
        list.add(new Reviewer("Carol", 7.2, false, 2));
        list.add(new Reviewer("David", 5.0, false, 1));
        return list;
    }
    public void persistAssignments(List<Reviewer> reviewers) {
        System.out.println("Assignments persisted for " + reviewers.size() + " reviewer(s).");
    }
    public void persistScores(List<Reviewer> reviewers) {
        System.out.println("Scores persisted in bulk for " + reviewers.size() + " reviewer(s).");
    }
    public void shutdown() {
        System.out.println("Database shutdown complete.");
    }
}
