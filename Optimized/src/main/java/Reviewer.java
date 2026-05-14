public class Reviewer {

    String name;
    double score;
    boolean hasConflict;
    int currentWorkload;

    public Reviewer() {
        this.score = 0.0;
        this.hasConflict = false;
        this.currentWorkload = 0;
    }

    public Reviewer(String name, double score, boolean hasConflict, int currentWorkload) {
        this.name = name;
        this.score = score;
        this.hasConflict = hasConflict;
        this.currentWorkload = currentWorkload;
    }

    void incrementWorkload() {
        this.currentWorkload++;
    }
}
