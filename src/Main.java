import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	private final double width = 16 ;
	private final double height = 10 ;
	private final double tileSize = 64;
	private final double errorAcceptance = 0.01;
	private final String title = "Tile Generator";
	private final String directory = "Advanced";
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Model model = new Model(this.width,this.height,this.tileSize,this.directory,this.errorAcceptance);
		View view = new View(model,primaryStage);
		@SuppressWarnings("unused")
		Control control = new Control(model,view);
		
		primaryStage.setTitle(title);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}


}
