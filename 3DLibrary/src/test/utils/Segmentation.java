package test.utils;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;


public class Segmentation {

	private BufferedImage image = null;
	private int width;
	private int height;
	private _Set[] components = null;
	private int k; //parameters
	private int min_size;
	private String result;
	
	
	
	//---------------
	
	private static final int benchmark = 128;
	private static final int benchK    = 150;
	
	//---------------
	
	private static Comparator<_Edge> weight_compre =  new Comparator<_Edge>(){  
        public int compare(_Edge o1, _Edge o2) {  
            // TODO Auto-generated method stub
        	if(o1.weight > o2.weight)
        		return 1;
        	else if(o1.weight == o2.weight)
        		return 0;
        	return -1;
        }
	};
	
	private static int[][] test_set = {
			
			{8,2},{9,1},{0,7},{6,7},{7,2},{3,4},{4,9},{4,6},{3,5}
			
	};
	
	public Segmentation(BufferedImage img,int p_k,int min,String r){
		
		
		this.result = r;
		this.image = img;
		this.width = img.getWidth();
		this.height = img.getHeight();
		this.k = p_k;
		this.min_size = min;
		
		components = new _Set[width * height];
		for(int i = 0;i < width * height;++i){
			components[i] = new _Set(i);
		}

		
		
	}
	
	public void process(Weight w_f){
		
		//add the edges
		 //Queue<_Edge> priorityQueue =  new PriorityQueue<_Edge>(width * height,weight_compre);  
		 List<_Edge> edges = new ArrayList<_Edge>(width * height * 4);
		 
		 for(int y = 0;y < height;++y){
			 for(int x = 0;x < width;++x){
				 if(x < width - 1){
					 
					int a = y * width + x;
					int b = y * width + x + 1;
					_Edge e = new _Edge(a,b,w_f.weight(a, b));
					edges.add(e);
					
				 }
				 if(y < height - 1){
					 int a = y * width + x;
					 int b = y * width + x + width;
					 _Edge e = new _Edge(a,b,w_f.weight(a, b));
					edges.add(e);
					 
				 }
				 if ((x < width-1) && (y < height-1)) {
					 
					 int a = y * width + x;
					 int b = (y+1) * width + (x+1);
					 _Edge e = new _Edge(a,b,w_f.weight(a, b));
					edges.add(e);
				 }
				 
				 if ((x < width-1) && (y > 0)) {
					 int a = y * width + x;
					 int b = (y-1) * width + (x+1);
					 _Edge e = new _Edge(a,b,w_f.weight(a, b));
					edges.add(e);
					 
				 }
				 //end for
			 }
			 
		 }

		 Collections.sort(edges,weight_compre);

		 //process
		 for(int i = 0;i < edges.size();++i){
			 
			 _Edge e = edges.get(i);
			 
			 int v1 = e.v1;
			 int v2 = e.v2;

			 
			 if(this.components[v1].isConnect(this.components[v2])){
				 continue;
			 }
			 
			 //MInt
			 
			_Set r1 = this.components[v1].findRoot();
			_Set r2 = this.components[v2].findRoot();
			

			double a1 = r1.max + (double)k / r1.size;
			double a2 = r2.max + (double)k / r2.size;
			
			if(e.weight <= a1 && e.weight <= a2){
			
				r1.merge(r2, e.weight);
			}
			
			
		 }
		 
		 for (int i = 0; i < edges.size(); i++) {
			 
			 _Edge e = edges.get(i);
			 
			 int a = e.v1;
			 int b = e.v2;
			 
			 _Set r1 = this.components[a].findRoot();
			 _Set r2 = this.components[b].findRoot();
			 
			 if(r1.index == r2.index)
				 continue;
			 if(r1.size  < this.min_size || r2.size < min_size ){
				 r1.merge(r2, e.weight);
			 }
			 
		 }

	}
	
	/**
	 * label the set int the components
	 * @throws IOException 
	 */
	public void label() throws IOException{
		
		Map<Integer,Integer > mapping = new HashMap<Integer,Integer>();
		
		for(int i = 0;i < components.length;++i){

			int root = components[i].rootIndex();
			components[i].root = root;
			mapping.put(root, 0);
			
		}
		System.out.println("n mapping: " + mapping.size());
		
		for(Iterator<Integer> it = mapping.keySet().iterator();it.hasNext();){
			int key = it.next();
			mapping.put(key,ImageUtil.random_color());
		}
		
		BufferedImage result = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
		
		for(int i = 0;i < components.length;++i){
			
			int x = i % width;
			int y = i / width;
			result.setRGB(x,y,mapping.get(components[i].root));
			
		}
		ImageUtil.writeImage(result,this.result,"jpg");
		
	}
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException{
		
		String test = "test1.jpg";
	
		BufferedImage img = ImageUtil.openImage(test);

		
		int k = img.getWidth() / benchmark * benchK;
		k = 300;
		Segmentation s = new Segmentation(img,k,300,"res.jpg");
		_W_color w_f = new _W_color(img,0.9);
		
		s.process(w_f);
		s.label();
		
		System.out.println("done");

	}
	
}

class _Edge{
	int v1;
	int v2;
	double weight;
	public _Edge(int a,int b,double w){
		v1 = a;v2 = b;weight = w;
	}
}

class _Set {

	int index;
	_Set parent = null;
	int size = 0;
	double max = 0;
	int root = 0;
	
	public _Set(int i){
		index = i;
		max = 0;
		size = 1;
	}	
	
	public _Set findRoot(){
		if(this.parent == null)
			return this;
		_Set p_prime = null;

		p_prime =  parent.findRoot();
		
		this.parent = p_prime;
		
		return p_prime;
		
	}
	
	
	public int rootIndex(){
		
		return findRoot().index;
	}
	
	public _Set up(){
		if(this.parent != null)
			return parent;
		return this;
	}
	
	
	public boolean isConnect(_Set x){
		return this.findRoot().index == x.findRoot().index;
	}
	
	/**
	 *  
	 * @param next //the merging tree
	 * @param e the weight of the connecting edge
	 */
	public void merge(_Set next,double e){
		
		_Set p_root = this.findRoot();
		_Set q_root = next.findRoot();

		if(p_root.size <= q_root.size){
			p_root.parent = q_root;
			q_root.size += p_root.size; 
			q_root.max = e;
			
		}
		else{
			q_root.parent = p_root;
			p_root.size += q_root.size;
			p_root.max = e;
		}

	}
	
}