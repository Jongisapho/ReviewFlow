import java.util.*;

public class SubmissionController {
    boolean isValid;
    Validator validator;
    Database db;
    ReviewerManager reviewerManager;
    EvaluationManager evaluationManager;

    public SubmissionController(Database db){
        this.db = db;
        this.reviewerManager = new ReviewerManager(db);
        this.evaluationManager = new EvaluationManager(db);
        this.validator = new Validator();
    }
    
    public boolean submit(Data data) {
        isValid = validator.validateFormat(data);
        if (!isValid) {
            System.out.println("Validation failed: returning error.");
            return false;
        } else {
            db.saveSubmission(data);
            List<Reviewer> filteredReviewers = reviewerManager.getAvailableReviewers();
            for (Reviewer reviewer : filteredReviewers) {
                reviewer.assignReview();
            }
            evaluationManager.startEvaluation(filteredReviewers);
            return true;
        }
    }
}
