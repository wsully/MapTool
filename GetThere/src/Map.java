import java.awt.Image;
import java.util.LinkedList;

public class Map {
	
	private Image mapImage;
	private LinkedList<Node> nodes;
	private LinkedList<Edge> edges;
	private String mapName;
	
	public Map(Image mapImage, String mapName){
		this.mapImage = mapImage;
		this.mapName = mapName;
	} 
	
	public Image getImage(){
		return this.mapImage;
	}
	
	public void setImage(Image mapImage){
		this.mapImage = mapImage;
	}
	
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