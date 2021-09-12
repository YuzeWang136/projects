import javax.swing.* ;
import java.util.Date ;

import java.util.ArrayList;
import java.util.Date ;
import javax.swing.* ;
public class cutter {
	 public static void main(String[] args) {
		ArrayList<Pattern> patterns = new ArrayList<Pattern>() ;
		patterns.add(new Pattern(2,2,1,"A")) ;
		patterns.add(new Pattern(2,6,4,"B")) ;
		patterns.add(new Pattern(4,2,3,"C")) ;
		patterns.add(new Pattern(5,3,5,"D")) ;
		int width = 40 ;
		int height = 25 ;
		int pixels = 30 ;
		System.out.println(optimize(width,height,patterns));
	 }
	 public static int optimize(int width, int height, ArrayList<Pattern> patterns){
		 if(width == 1 && height == 1) return 0;
		 int value = 0, maxValue = 0;
		 System.out.println(width+ " " + height);
		 sleep(500) ;
		 for(int i = 1; i < width; i++){
			 value = optimize(i, height, patterns) + optimize(width-i, height, patterns);
			 if(maxValue < value) maxValue = value;
		 }
		 for(int i = 1; i < height; i++){
			 value = optimize(width, i, patterns) + optimize(width, height-i, patterns);
			 if(maxValue < value) maxValue = value;
		 }
		 for(Pattern i: patterns){
			 if(i.getHeight() == height && i.getWidth() == width && i.getValue() > maxValue) maxValue = i.getValue();
		 }
		 return maxValue;
	 }

}