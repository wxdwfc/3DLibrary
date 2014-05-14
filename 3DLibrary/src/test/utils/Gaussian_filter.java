package test.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import utils.Utils;

public class Gaussian_filter {
	
	//kernel width
	public static int Width = 4;
	
	
	public static double square(double a){
		return a * a;
	}
	/**
	 * return the gaussian kernel
	 * @param sigma 
	 * @return
	 */
	public static double[] getKernel(double sigma){
		
		if(sigma < 0.01){
			sigma = 0.01;
		}
		
		double k[] = new double[(int)(sigma * Width) + 1];		

		for(int i = 0;i < k.length;++i){
			
			k[i] = Math.exp((-0.5) * square(i/sigma));

		}
		
		normal(k);
		

		
		return k;
	}
	
	private static void normal(double[] k){
		int len = k.length;
	    double sum = 0;
		for (int i = 1; i < len; i++) {
		     sum += Math.abs(k[i]);
		}
		sum = 2*sum + Math.abs(k[0]);
		sum = 1. / sum;
		for (int i = 0; i < len; i++) {
		 k[i] *= sum;
		}
		
	}
	
	
	public static BufferedImage blur(BufferedImage image,double[] kernel){
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		int len = kernel.length;
		
		BufferedImage result = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		BufferedImage temp   = new BufferedImage(height,width,BufferedImage.TYPE_INT_RGB);
		
		convolve(image,temp,kernel);
		convolve(temp,result,kernel);
		
		
		return result;
	}
	
	private static void convolve(BufferedImage src,BufferedImage rst,double[] mask){
		int width = src.getWidth();
		int height = src.getHeight();
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double sum_r = mask[0] * ImageUtil.getR(src.getRGB(x,y));
				double sum_g = mask[0] * ImageUtil.getG(src.getRGB(x,y));
				double sum_b = mask[0] * ImageUtil.getB(src.getRGB(x,y));
				
				for (int i = 1; i < mask.length; i++) {
					sum_r += mask[i] *(
						ImageUtil.getR((src.getRGB(Utils.max(x-i,0), y)) +
								ImageUtil.getR(src.getRGB(Utils.min(x+i, width-1), y))) );
					sum_g += mask[i] * (
							ImageUtil.getG((src.getRGB(Utils.max(x-i,0), y)) +
									ImageUtil.getG(src.getRGB(Utils.min(x+i, width-1), y))) );
					sum_b += mask[i] * (
							ImageUtil.getB((src.getRGB(Utils.max(x-i,0), y)) +
									ImageUtil.getB(src.getRGB(Utils.min(x+i, width-1), y))) );
				
				//end for
				}
				
				int n_p = new Color((int)sum_r,(int)sum_g,(int)sum_b).getRGB();
				rst.setRGB(y, x, n_p);
			}
		}
		
		//end func
		
	}
	
	private static void convolve(double[][]src,double[][] dst,int width,int height,double[] mask){
		
		for(int y = 0;y < height;++y){
			
			for(int x = 0;x < width;++x){
				double sum = mask[0] * src[x][y];
				for(int i = 1;i < mask.length;++i){
					sum += mask[i] * ( src[Utils.max(0,x-i)][y] + src[Utils.min(x+i,width-1)][y]);
					
				}
				dst[y][x] = sum;
				
			}
		}
		
	}
	
	public static double[][] blur_single(double[][] mat,int width,int height,double sigma){
		
		double[][] temp = new double[height][width];
		double[][] result = new double[width][height];
		
		double[] mask = getKernel(sigma);
		convolve(mat,temp,width,height,mask);
		convolve(temp,result,height,width,mask);
		
		return result;
	}
}
