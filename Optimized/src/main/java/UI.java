import java.util.Scanner;

public class UI {

    private final SubmissionController controller;

    public UI(Database db) {
        this.controller = new SubmissionController(db);
    }

    public void submitResearchOutput() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Optimized Research Submission System ===");
        System.out.print("Enter Title / Topic  : ");
        String title = scanner.nextLine();

        System.out.print("Enter Content / Abstract: ");
        String content = scanner.nextLine();

        if (title.trim().isEmpty() || content.trim().isEmpty()) {
            System.out.println("Title and Content cannot be empty.");
            scanner.close();
            return;
        }

        Data data   = new Data(title, content);
        boolean ok  = controller.submit(data);

        System.out.println(ok
            ? "\nSubmission processed successfully."
            : "\nSubmission rejected at validation stage.");

        scanner.close();
    }
}
