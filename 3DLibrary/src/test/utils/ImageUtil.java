package test.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO; 


public class ImageUtil {

	
	public static BufferedImage openImage(String imgPath) throws FileNotFoundException, IOException{
		
		return ImageIO.read(new FileInputStream(imgPath));
		
	}
	
	public static int[] getRGB(int pixel){
		
		int rgb[] = new int[3];
		rgb[0] = ((0xff0000 & pixel) >> 16) & 0xff;
		rgb[1] = ((0xff00   & pixel) >>  8) & 0xff;
		rgb[2] =  pixel & 0xff;
		return rgb;
	}
	
	public static int getR(int pixel){
		return ((0xff0000 & pixel) >> 16) & 0xff;
	}
	
	public static int getG(int pixel){
		
		return ((0xff00   & pixel) >>  8) & 0xff;
	}
	
	public static int getB(int pixel){
		return pixel & 0xff;
	}

	public static BufferedImage toBinaryImage(BufferedImage origin){
		int width = origin.getWidth();
		int height = origin.getHeight();
		BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);  
		for(int i= 0 ; i < width ; ++i){  
		   for(int j = 0 ; j < height; ++j){  
		        int rgb = origin.getRGB(i, j);  
		        grayImage.setRGB(i, j, rgb);  
		        }  
		    }  
		return grayImage;
	}
	
	
	public static int getAlpha(int pixel){
		
		return ((0xff000000 & pixel) >> 24) & 0xff;
		
	}
	
	public static int setAlpha(int pixel,int alpha){
		
		return (alpha << 24) & (0x00ffffff & pixel);
	}
	
	
	public static void writeImage(BufferedImage src,String filename,String fmt) throws IOException{
		File out = new File(filename);
		ImageIO.write(src, fmt, out);  
	}
	
	public static BufferedImage grayImage(BufferedImage image) {  
		    
		      
		    int width = image.getWidth();  
		    int height = image.getHeight();  
		      
		    BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);  
		    for(int i= 0 ; i < width ; i++){  
		        for(int j = 0 ; j < height; j++){  
		        int rgb = image.getRGB(i, j);  
		        		grayImage.setRGB(i, j, rgb & 0xff000000 | to_grey(rgb));  
		        }  
		    } 
		    return grayImage;
	}
	
	
	/**
	 * 
	 * @param rgb - rgb value for the pixel 
	 * @return converted gray value
	 */
	public static int to_grey(int rgb){
		
		
		int red = rgb & 0xff0000 >> 16;
		int gre = rgb & 0x00ff00 >> 8;
		int blu = rgb & 0x0000ff ;
		System.out.println(red + ' ' + gre +' '+ blu);
		return (int)((float)(red + gre + blu) / 3);
	}
	
	
	/**
	 * 
	 * @return the random generated rgb color
	 */
	public static int random_color(){
		
		Random rand = new Random();
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		Color randomColor = new Color(r, g, b);
		return randomColor.getRGB();
		
		
	}
}