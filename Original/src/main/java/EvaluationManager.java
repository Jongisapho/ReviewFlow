import java.util.*;

public class EvaluationManager {
    Database db;
    double average;
    int isAccepted;  // 0 = rejected, 1 = accepted, 2 = revision
    NotificationService notificationService;
    List<Reviewer> reviewers;

    private static final double ACCEPT_THRESHOLD = 7.0;
    private static final double REJECT_THRESHOLD = 4.0;
    private static final double CONSENSUS_TOLERANCE = 1.5; // Adjusted for std dev

    public EvaluationManager(Database db){
        this.db = db;
        notificationService = new NotificationService();
        reviewers = new ArrayList<>();
    }

    public void startEvaluation(List<Reviewer> reviewerList) {
        this.reviewers = reviewerList;
        for (Reviewer reviewer : reviewerList) {
            db.saveScore(reviewer);
        }
        average = calculateAverage();
        boolean hasConsensus = checkConsensus();
        isAccepted = applyRules(hasConsensus);
        
        if (isAccepted == 1) {
            notificationService.notifyAcceptance();
        } else if (isAccepted == 0) {
            notificationService.notifyRejection();
        } else if (isAccepted == 2) {
            notificationService.notifyRevision();
        }
    }

    public double calculateAverage() {
        if (reviewers.isEmpty()) return 0.0;
        
        double sum = 0;
        for (Reviewer reviewer : reviewers) {
            sum += reviewer.score;
        }
        average = sum / reviewers.size();
        System.out.println("Average score: " + average);
        return average;
    }

    public boolean checkConsensus() {
        if (reviewers.isEmpty() || reviewers.size() == 1) {
            System.out.println("Consensus reached: true (too few reviewers)");
            return true;
        }

        double mean = average;
        double sumSquaredDiff = 0.0;

        for (Reviewer reviewer : reviewers) {
            double diff = reviewer.score - mean;
            sumSquaredDiff += diff * diff;
        }

        double variance = sumSquaredDiff / reviewers.size();
        double stdDev = Math.sqrt(variance);

        boolean consensus = stdDev <= CONSENSUS_TOLERANCE;
        
        System.out.println("Standard Deviation: " + String.format("%.2f", stdDev));
        System.out.println("Consensus reached: " + consensus);
        
        return consensus;
    }

    public int applyRules(boolean hasConsensus) {
        if (!hasConsensus) {
            System.out.println("No consensus reached. Forcing revision.");
            return 2;
        }
        if (hasConsensus && average >= ACCEPT_THRESHOLD) {
            return 1; // accepted
        } else if (average < REJECT_THRESHOLD) {
            return 0; // rejected
        } else {
            return 2; // revision
        }
    }

    public void submitScore(double score) {
        System.out.println("Score received by EvaluationManager: " + score);
    }
}