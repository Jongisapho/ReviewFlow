/**
 * DecisionTable
 *
 * ┌──────────────┬───────────┬──────────┬───────────┐
 * │  Consensus   │ Avg ≥ 7.0 │ Avg < 4.0│  Outcome  │
 * ├──────────────┼───────────┼──────────┼───────────┤
 * │    false     │     -     │    -     │ REVISION  │
 * │    true      │    true   │    -     │ ACCEPTED  │
 * │    true      │   false   │   true   │ REJECTED  │
 * │    true      │   false   │  false   │ REVISION  │
 * └──────────────┴───────────┴──────────┴───────────┘
 */
public class DecisionTable {

    private static final double ACCEPT_THRESHOLD    = 7.0;
    private static final double REJECT_THRESHOLD    = 4.0;
    private static final double CONSENSUS_TOLERANCE = 1.5;  // max std deviation

    public Outcome evaluate(double[] scores) {
        if (scores == null || scores.length == 0) return Outcome.REVISION;

        double average   = computeAverage(scores);
        boolean consensus = hasConsensus(scores, average);

        System.out.printf("   Average score : %.2f%n", average);
        System.out.printf("   Consensus     : %b%n",   consensus);

        // Decision table lookup
        if (!consensus)              return Outcome.REVISION;
        if (average >= ACCEPT_THRESHOLD) return Outcome.ACCEPTED;
        if (average <  REJECT_THRESHOLD) return Outcome.REJECTED;
        return Outcome.REVISION;
    }
    private double computeAverage(double[] scores) {
        double sum = 0;
        for (double s : scores) sum += s;
        return sum / scores.length;
    }

    private boolean hasConsensus(double[] scores, double mean) {
        if (scores.length == 1) return true;
        double sumSq = 0;
        for (double s : scores) {
            double d = s - mean;
            sumSq += d * d;
        }
        double stdDev = Math.sqrt(sumSq / scores.length);
        System.out.printf("   Std deviation : %.2f%n", stdDev);
        return stdDev <= CONSENSUS_TOLERANCE;
    }
}
