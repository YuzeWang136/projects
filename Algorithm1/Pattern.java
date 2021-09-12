
public class Pattern{
	private int height;
	private int width;
	private int value;
	private String name;
	public Pattern(int height, int width, int value, String name){
		this.height = height;
		this.width = width;
		this.value = value;
		this.name = name;
	}
	public int getHeight(){
		return this.height;
	}
	public int getWidth(){
		return this.width;
	}
	public int getValue(){
		return this.value;
	}
	public String getName(){
		return this.name;
	}
}