import java.util.*;

public class EvaluationManager {

    private final Database            db;
    private final DecisionTable       decisionTable;
    private final NotificationService notificationService;

    public EvaluationManager(Database db) {
        this.db                  = db;
        this.decisionTable       = new DecisionTable();
        this.notificationService = new NotificationService();
    }

    public void startEvaluation(String submissionId, List<Reviewer> reviewers) {
        System.out.println("\n--- Evaluation started for: " + submissionId + " ---");

        double[] scores = collectScores(reviewers);

        db.persistScores(reviewers);


        Outcome outcome = decisionTable.evaluate(scores);
        System.out.println("   Decision      : " + outcome);

        notificationService.sendNotification(submissionId, outcome);
    }

    private double[] collectScores(List<Reviewer> reviewers) {
        double[] scores = new double[reviewers.size()];
        for (int i = 0; i < reviewers.size(); i++) {
            scores[i] = reviewers.get(i).score;
            System.out.println("   Score from " + reviewers.get(i).name
                               + ": " + scores[i]);
        }
        return scores;
    }
}
