import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

public class Map {
	
	private BufferedImage mapImage;
	private LinkedList<Node> nodes;
	private LinkedList<Edge> edges;
	private String mapName;
	
	public Map(String mapPath, String mapName){
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File(mapPath));
		} catch (IOException e) {}
		this.mapImage = img;
		this.mapName = mapName;
		nodes = new LinkedList<Node>();
		edges = new LinkedList<Edge>();
	} 
	
	
	
	
	public Map(BufferedImage img, String mapName){
		this.mapImage = img;
		this.mapName = mapName;
		nodes = new LinkedList<Node>();
		edges = new LinkedList<Edge>();
	} 
	
	public BufferedImage getImage(){
		return this.mapImage;
	}
	
	//public void setImage(Image mapImage){
	//	this.mapImage = mapImage;
	//}
	
	public LinkedList<Node> getNodes(){
		return this.nodes;
	}
	
	public LinkedList<Edge> getEdges(){
		return this.edges;
	}
	
	public String getMapName(){
		return this.mapName;
	}
	
	public void setMapName(String mapName){
		this.mapName = mapName;
	}
	
	public void addNode(Node node){
		this.nodes.add(node);
	}
	
	public void addEdge(Edge edge){
		this.edges.add(edge);
	}
	
	public void deleteNode(Node node){
		for(int i = 0; i < nodes.size(); i++){
			if(node.equals(nodes.get(i))){
				nodes.remove(i);
			}
		}
	}
	
	public void deleteEdge(Edge edge){
		for(int i = 0; i < edges.size(); i++){
			if(edge.equals(edges.get(i))){
				edges.remove(i);
			}
		}
	}
}