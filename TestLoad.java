import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import java.net.URL;

public class TestLoad {
    public static void main(String[] args) throws Exception {
        Platform.startup(() -> {
            try {
                URL url = new URL("file://" + System.getProperty("user.dir") + "/target/classes/com/project/coursework2/main-layout.fxml");
                FXMLLoader.load(url);
                System.out.println("SUCCESS");
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
