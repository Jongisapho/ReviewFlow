import java.util.Scanner;
public class UI {
    SubmissionController controller;
    boolean isValid;

    public UI(Database db){
        controller = new SubmissionController(db);
    }

    public void submitResearchOutput() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Research Submission System ===");
        System.out.print("Enter Title / Topic: ");
        String title = scanner.nextLine();

        System.out.print("Enter Content / Abstract: ");
        String content = scanner.nextLine();

        if (title.trim().isEmpty() || content.trim().isEmpty()) {
            System.out.println("Title and Content cannot be empty!");
            scanner.close();
            return;
        }

        Data data = new Data(title, content);
        isValid = controller.submit(data);

        if (isValid) {
            System.out.println("\nThe Data provided is valid.");
        } else {
            System.out.println("\nThe Data provided is invalid ");
        }

        scanner.close();

    }
}
