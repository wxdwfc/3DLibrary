package test.utils;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.IOException;

public class _W_color implements Weight {

	private int width;
	private int height;

	private double[][] r;
	private double[][] g;
	private double[][] b;
	
	public _W_color(BufferedImage img,double sigma){
		
		
		width = img.getWidth();
		height = img.getHeight();
		
		double[][] mat = new  double[width][height];
		r = new double[width][height];
		g = new double[width][height];
		b = new double[width][height];
		
		for(int x = 0;x < width;++x){
			for(int y = 0;y < height;++y){
				int pixel = img.getRGB(x, y);
				r[x][y] = ImageUtil.getR(pixel);
				g[x][y] = ImageUtil.getG(pixel);
				b[x][y] = ImageUtil.getB(pixel);
				
			}
		}
		
		r = Gaussian_filter.blur_single(r, width, height, sigma);
		g = Gaussian_filter.blur_single(g, width, height, sigma);
		b = Gaussian_filter.blur_single(b, width, height, sigma);
		
	}
	
	@Override
	public double weight(int index0, int index1) {
		
		int x0 = index0 % width;
		int y0 = index0 / width;
		
		int x1 = index1 % width;
		int y1 = index1 / width;
		
		return colorDiff(x0,y0,x1,y1);
	}
	
	private double colorDiff(int x0,int y0,int x1,int y1){
		
		 double red = r[x0][y0] - r[x1][y1];
		 double green = g[x0][y0] - g[x1][y1];
		 double blue = b[x0][y0] - b[x1][y1];
		 
	     return Math.sqrt(red * red + green * green + blue * blue);

	}

}
