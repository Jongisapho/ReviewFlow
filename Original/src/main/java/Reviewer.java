public class Reviewer {

    double score;
    boolean hasConflict;
    int currentWorkload;
    
    public Reviewer(){
        score = 0;
        hasConflict = false;
        currentWorkload = 0;
    }
    public void assignReview() {
        currentWorkload++;
        System.out.println("Review assigned. Current workload: " + currentWorkload);
    }

    public void submitScore(double score) {
        this.score = score;
        System.out.println("Score submitted: " + score);
    }
}
