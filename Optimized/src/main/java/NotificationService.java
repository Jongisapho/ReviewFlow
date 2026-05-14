public class NotificationService {

    public void sendNotification(String submissionId, Outcome outcome) {
        String message = switch (outcome) {
            case ACCEPTED -> "Submission [" + submissionId + "] has been ACCEPTED.";
            case REJECTED -> "Submission [" + submissionId + "] has been REJECTED.";
            case REVISION -> "Submission [" + submissionId + "] requires REVISION.";
        };
        System.out.println("\nNotification → Researcher: " + message);
    }
}
