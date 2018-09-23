package BoardGame;
import java.awt.image.BufferedImage;

public abstract class Piece {
	
	private String name = "Name not yet defined";
	
	public abstract BufferedImage getImage(int size);
	
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	

}
