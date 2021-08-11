import javafx.scene.paint.Color;

public class Tile {

	private String id;
	private int x;
	private int y;
	private Color [][] borderInfo ; // Color Array for each direction (first line of colors)
	
	
	public Tile(String id, int x, int y, Color [][] borderInfo ) {
		
		this.id = id;
		this.x = x;
		this.y = y;
		this.borderInfo = borderInfo;
	}
	
	
	public String getId() {
		return this.id;
	}
	
	public Color [][] getBorderInfo(){
		return this.borderInfo;
	}
	
}
