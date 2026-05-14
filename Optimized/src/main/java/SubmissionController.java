import java.util.*;

public class SubmissionController {

    private final Validator          validator;
    private final Database           db;
    private final ReviewerManager    reviewerManager;
    private final EvaluationManager  evaluationManager;

    private int submissionCounter = 0;

    public SubmissionController(Database db) {
        this.db               = db;
        this.validator        = new Validator();
        this.reviewerManager  = new ReviewerManager(db);
        this.evaluationManager = new EvaluationManager(db);
    }

    public boolean submit(Data data) {
        boolean valid = validator.validateFormat(data);
        if (!valid) {
            System.out.println("Validation failed — returning error to UI.");
            return false;
        }

        db.saveSubmission(data);
        String submissionId = "SUB-" + (++submissionCounter);

        List<Reviewer> assigned = reviewerManager.assignReviewers(submissionId);

        evaluationManager.startEvaluation(submissionId, assigned);

        return true;
    }
}
