public class Main {
    public static void main(String[] args) {
        Database db = new Database();
        UI ui = new UI(db);
        ui.submitResearchOutput();
        db.shutdown();
    }
}
