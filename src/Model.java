import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class Model {
	
	private double width ;
	private double height ;
	private double tileSize;
	private double errorAcceptance;  
	private String directory;
	private Image [] images;
	private File [] files;
	private int nrOfImages;
	private Tile [][] playingField;
	private HashMap <String, Color[][]> tileInformation;
	private HashMap <String, Image> possibleImages;
	
	public Model(double width, double height, double tileSize , String directory, double errorAcceptance ) {
		
		this.width = width;
		this.height = height;
		this.tileSize = tileSize;
		this.errorAcceptance = errorAcceptance;
		this.directory = directory;
		this.files = new File(".\\src\\" + this.directory).listFiles();
		this.nrOfImages = this.files.length;
		this.images = new Image [this.nrOfImages];
		this.playingField = new Tile [(int) this.width][(int) this.height];
		this.tileInformation = new HashMap <String, Color[][]> ();								// Picture name / Color Array for each direction
		this.possibleImages = new HashMap<String, Image>();
		
		Initialize();
	}

	public void Initialize() {
		
		for (int i = 0; i < this.nrOfImages; i++) {					
			String path = ".\\" + this.directory + "\\" + this.files[i].getName();				// Iterating through files
			Image imageRead = new Image(path);
			ImageView iv = new ImageView(imageRead);
			
			for (int rotation = 0; rotation * 90 < 270 ; rotation ++) {
				iv.setRotate(rotation * 90);
				SnapshotParameters ssp = new SnapshotParameters();
				Image image = iv.snapshot(ssp, null);
				Color [][] edgeColors = readPictureEdge(image);
				tileInformation.put(this.files[i].getName() + rotation,edgeColors);				// saving file Information
				possibleImages.put(this.files[i].getName() + rotation, image); 					// all possible image rotations
			}
		} 
		
	}
	
	public void clearField() {
		this.playingField = new Tile [(int) this.width][(int) this.height];
	}
	
	public Color [][] readPictureEdge(Image image) {
		
		Color [][] colors = new Color [4][(int) image.getHeight()];  	// one Array for each direction. Positional: [0] North, [1] East, [2] South, [3] West
		PixelReader pw = image.getPixelReader();
		int imageHeight = (int) image.getHeight();
		
		for (int i = 0; i < imageHeight; i ++ ) {  // North
			colors[0][i] = pw.getColor(i, 0);
		}
		for (int i = 0; i < imageHeight; i ++ ) {  // East
			colors[1][i] = pw.getColor(imageHeight -1 , i);
		}
		for (int i = 0; i < imageHeight; i ++ ) {  // South
			colors[2][i] = pw.getColor(i, imageHeight -1 );
		}
		for (int i = 0; i < imageHeight; i ++ ) {  // West
			colors[3][i] = pw.getColor(0, i);
		}
		
		return colors;
	}

	public void deleteTile(int x, int y) {
		this.playingField[x][y] = null;
		
	}
	
	public void update(int x, int y) {
		
		Tile [] surroundingTiles = checkSurroundingTiles(x,y);
		ArrayList<String> possibleTiles = checkPossibleTiles(surroundingTiles);
		
		if (possibleTiles.isEmpty()) {						// No Possible Tile
			insertZeroTile(x,y);
		}else {
			insertRandomTile(possibleTiles,x,y);
		}
	}
	
	
	private void insertZeroTile(int x, int y) {
		
		createTile("zero",x,y);
		
	}

	
	private void insertRandomTile(ArrayList<String> possibleTiles , int x , int y) {
		
			int random = getRandomInt(0, possibleTiles.size() - 1);
			createTile(possibleTiles.get(random ),x,y);

	}
	
	
	public int getRandomInt ( int min , int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	
	private ArrayList<String> checkPossibleTiles(Tile [] surroundingTiles) {
		
		ArrayList <String> acceptedTiles = new ArrayList <String>() ;
		
		for ( Map.Entry<String, Color[][]> entry : this.tileInformation.entrySet()) {
				
			boolean accepted = compareTileFit( entry, surroundingTiles, this.errorAcceptance );		
			
			if (accepted) {
				acceptedTiles.add(entry.getKey());
			}	
		}
		return acceptedTiles;
	}
	
	
	@SuppressWarnings("unused")					// Same function as below but more elaborate
	private boolean compareTileFitElaborate(Entry<String, Color[][]> entry, Tile[] surroundingTiles , double errorAcceptance) {
		
		int pixelLength = entry.getValue()[0].length;																				
		int northIndex = 0, eastIndex = 1 , southIndex = 2 ,  westIndex = 3;
		
		// North Border Compare //
		
		Tile northTile = surroundingTiles[northIndex] == null ? null : surroundingTiles [northIndex].getId() == "zero" ? null : surroundingTiles [northIndex];								// North Tile
		Color [] northBorderNeighbor = northTile != null ? northTile.getBorderInfo()[southIndex] : null;							// + 2	offset		
		Color [] northBorderSelf =  entry.getValue()[northIndex];																	// Top row of pixels from current tile
		double northDeviation = northTile != null ? compareColorArrayDeviation(northBorderSelf, northBorderNeighbor, pixelLength) : 0;
		
		// East Border Information //
		
		Tile eastTile = surroundingTiles[eastIndex] == null ? null : surroundingTiles [eastIndex].getId() == "zero" ? null : surroundingTiles [eastIndex];	
		Color [] eastBorderNeighbor = eastTile != null ? eastTile.getBorderInfo()[westIndex] : null;						
		Color [] eastBorderSelf =  entry.getValue()[eastIndex];																		
		double eastDeviation = eastBorderNeighbor != null ? compareColorArrayDeviation(eastBorderSelf, eastBorderNeighbor, pixelLength) : 0;
		
		// South Border Information //
		
		Tile southTile = surroundingTiles[southIndex] == null ? null : surroundingTiles [southIndex].getId() == "zero" ? null : surroundingTiles [southIndex];	
		Color [] southBorderNeighbor = southTile != null ? southTile.getBorderInfo()[northIndex] : null;						
		Color [] southBorderSelf =  entry.getValue()[southIndex];																		
		double southDeviation = southBorderNeighbor != null ? compareColorArrayDeviation(southBorderSelf, southBorderNeighbor, pixelLength) : 0;
		
		// West Border Information //
		
		Tile westTile = surroundingTiles[westIndex] == null ? null : surroundingTiles [westIndex].getId() == "zero" ? null : surroundingTiles[westIndex];	
		Color [] westBorderNeighbor = westTile != null ? westTile.getBorderInfo()[eastIndex] : null;						
		Color [] westBorderSelf =  entry.getValue()[westIndex];																		
		double westDeviation = westBorderNeighbor != null ? compareColorArrayDeviation(westBorderSelf, westBorderNeighbor, pixelLength) : 0;
		
		if (northDeviation <= errorAcceptance && eastDeviation <= errorAcceptance && southDeviation <= errorAcceptance && westDeviation <= errorAcceptance ) {
			return true;
		}else {
			return false;
		}
		
	}
	
	
	private boolean compareTileFit(Entry<String, Color[][]> entry, Tile[] surroundingTiles , double errorAcceptance) {
		 
		int pixelLength = entry.getValue()[0].length;	
		int sides = entry.getValue().length;
		double deviation = 0;
		
		for (int i = 0; i < sides; i++ ) {
			Tile tile = surroundingTiles[i] == null ? null : surroundingTiles [i].getId() == "zero" ? null : surroundingTiles [i];		
			Color [] neighbor = tile != null ? tile.getBorderInfo()[(i+2 > 3) ? i-2 : i+2 ] : null;					// + 2 / - 2 offset		
			Color [] self =  entry.getValue()[i];																	// Top row of pixels from current tile
			deviation = tile != null ? compareColorArrayDeviation(self,neighbor, pixelLength) : 0;
			if (deviation > errorAcceptance) {return false;} else {continue;}
		}
		return true;
	}
	
	
	private double compareColorArrayDeviation(Color [] inner, Color [] outer, double pixelLength) {
		
		double arrayDeviation = 0;
		for(int colorEntry = 0; colorEntry < pixelLength; colorEntry ++) { 		// Iterating through Pixels
			
			double rx = Math.abs(inner[colorEntry].getRed() - outer[colorEntry].getRed());
			double gx = Math.abs(inner[colorEntry].getGreen() - outer[colorEntry].getGreen());
			double bx = Math.abs(inner[colorEntry].getBlue() - outer[colorEntry].getBlue());
			arrayDeviation = arrayDeviation + ((rx + gx + bx)/3);
		}
		
		return arrayDeviation / pixelLength;
	}

	
	private Tile[] checkSurroundingTiles( int x, int y) {
		
		Tile [] NESWTiles = new Tile[4];
		
		NESWTiles[0] = y-1 > - 1 ? playingField[x][y-1] : null; 				// North Tile
		NESWTiles[1]= x+1 < this.width ? playingField[x+1][y] : null ;		// East Tile
		NESWTiles[2] = y+1 < this.height  ? playingField[x][y+1] : null;;	// South Tile
		NESWTiles[3] = x-1 > -1 ? playingField[x-1][y] : null ; ;			// West Tile
		
		return NESWTiles;
	}

	public void createTile (String id, int x, int y) {
		
		Tile tile = new Tile(id,x,y,tileInformation.get(id));
		this.playingField[x][y] = tile;
		
	}
	
	
	
	
	public HashMap <String,Image> getPossibleImages(){
		return this.possibleImages;
	}
	
	public String getDirectory() {
		return this.directory;
	}
	
	public Tile getTile (int x , int y) {
		return playingField[x][y];
	}
	
	public Image [] getImage() {
		return this.images;
	}
	
	public double getWidth() {
		return this.width;
	}
	
	public double getHeight() {
		return this.height;
	}
	
	public double getTileSize() {
		return this.tileSize;
	}

}
