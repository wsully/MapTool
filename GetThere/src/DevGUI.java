import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.event.MouseAdapter;
import java.awt.*;
import java.awt.geom.Line2D;
import javax.imageio.*; 


import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

import java.awt.image.BufferedImage;

///**
//* Created by Lumbini on 11/7/2015.
//* modified by Billy
// * */
//




public class DevGUI extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2270760135813536905L;
	private static LinkedList<Map> maps = new LinkedList<Map>();
	private static LinkedList<Node> currentStartNodes = new LinkedList<Node>();
	private static LinkedList<Edge> currentStartEdges = new LinkedList<Edge>();
	private static LinkedList<Node> currentEndNodes = new LinkedList<Node>();
	private static LinkedList<Edge> currentEndEdges = new LinkedList<Edge>();
	private String[] startRooms = new String[1000];
	private String[] endRooms = new String[1000];
	private String buildingSelectedSTART;   //track which building is selected to start in.
	private String buildingSelectedEND;
	private String currentMapName;
	
	private BufferedImage currentMapFile;


	String outputVar = "src/output.txt";
	String inputVar = "src/output.txt";

	boolean createNodes = false;
	boolean createEdges = false;
	boolean importPushed = true;

	public boolean developerMode = true;

	private JFrame frame;       //Creates the main frame for the GUI
	private JPanel uiPanel;     //Panel to hold the interface buttons
	private JPanel mapPanel;    //Panel to hold the map
	private Image mapImage;     //Represents the map to be chosen
	private Image pathImage;    //Image that draws the path on the map

	//Labels on the GUI
	private JLabel startPoint;
	private JLabel buildingStart;
	private JLabel roomStart;
	private JLabel endPoint;
	private JLabel buildingEnd;
	private JLabel roomEnd;

	//Combo Boxes on the GUI
	private JComboBox<String> startBuildingSEL;
//	private JComboBox startRoomSEL;
//	private JComboBox endBuildingSEL;
//	private JComboBox endRoomSEL;

	//Buttons on the UI
	private JButton searchButton;

	private Edge edge;
	private Node node1;
	private Node node2;
	private LinkedList<Edge> edgeList = new LinkedList<Edge>();
	private LinkedList<Node> nodeList = new LinkedList<Node>();
	File file1;
	Process process1;
	File output;
	Writer filewriter;

	/**
	 * Create the application.
	 */
	public DevGUI(){
		initialize();
	}


	//Launch the application. 
	public static void main(String[] args) {
		
		maps = (LinkedList<Map>) deserialize("MapList");
				
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DevGUI window = new DevGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	// saves Map object "m" in a file named "s"
	public void serialize(String s, LinkedList<Map> maplist){
		
		try {
			FileOutputStream fileOut = new FileOutputStream(s + ".ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(maplist);
			out.close();
			fileOut.close();
			System.out.println("Serialized data is saved in " + s + ".ser");
		} catch(IOException i){
			i.printStackTrace();
		}
	}
	
	// loads the map stored in file name "s"
	public static Object deserialize(String s){
		Object m = null;
		try
		{
			FileInputStream fileIn = new FileInputStream(s + ".ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			m = in.readObject();
			in.close();
			fileIn.close();
		}catch(IOException i)
		{
			i.printStackTrace();

		}catch(ClassNotFoundException c)
		{
			System.out.println("Map class not found");
			c.printStackTrace();
	
		}
//		System.out.println("Deserialized map...");
//		System.out.println("Name: " + m.getMapName());
	return m;
	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void initialize() {

		//Frame operations
		frame = new JFrame();
		frame.setBounds(100, 100, 900, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		frame.setTitle("Get There");
		frame.setResizable(false);
		frame.setVisible(true);

		//Panel Operations
		uiPanel = new JPanel();
		frame.getContentPane().add(uiPanel);
		uiPanel.setLayout(null);

		mapPanel = new JPanel();
		mapPanel.setBounds(5, 5, 750, 650);
		uiPanel.add(mapPanel);
		MouseEvents m1 = new MouseEvents();
		mapPanel.add(m1);

		//Creating Labels
//		startPoint = new JLabel("FROM");
//		startPoint.setBounds(780, 6, 132, 29);

		buildingStart = new JLabel("Select Map:");
		buildingStart.setBounds(762, 26, 132, 29);
		
		

//		roomStart = new JLabel("Select Room:");
//		roomStart.setBounds(762, 70, 132, 29);
//
//		endPoint = new JLabel("TO");
//		endPoint.setBounds(780, 126, 132, 29);
//
//		buildingEnd = new JLabel("Select Building:");
//		buildingEnd.setBounds(762, 146, 132, 29);
//
//		roomEnd = new JLabel("Select Room:");
//		roomEnd.setBounds(762, 190, 132, 29);

		//Add Labels to the uiPanel
//		uiPanel.add(startPoint);
		uiPanel.add(buildingStart);
//		uiPanel.add(roomStart);
//		uiPanel.add(endPoint);
//		uiPanel.add(buildingEnd);
//		uiPanel.add(roomEnd);
		
		//Construct Combo boxes to select start point
		startBuildingSEL = new JComboBox();
		startBuildingSEL.setBounds(762, 46, 132, 29);
		startBuildingSEL.setEditable(false);
		startBuildingSEL.setVisible(true);
		startBuildingSEL.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int indexOfCurrentMap;
				JComboBox cb = (JComboBox)e.getSource();
				buildingSelectedSTART = (String)cb.getSelectedItem();
				for(indexOfCurrentMap = 0; indexOfCurrentMap < maps.size(); ++indexOfCurrentMap){
					if(buildingSelectedSTART.equals(maps.get(indexOfCurrentMap).getMapName()))
						break;
				}
				currentMapName = maps.get(indexOfCurrentMap).getMapName();
				currentStartNodes = maps.get(indexOfCurrentMap).getNodes();
				currentStartEdges = maps.get(indexOfCurrentMap).getEdges();
				currentMapFile = maps.get(indexOfCurrentMap).getImage();
				for(int i = 0; i < currentStartNodes.size(); ++i){
					startRooms[i] = currentStartNodes.get(i).getName();
				}
				uiPanel.repaint();
		        frame.repaint();
			}
		});
		
		for (int i = 0; i < maps.size(); i++) {
	           if(maps.get(i).getMapName() != null)
	        	   startBuildingSEL.addItem(maps.get(i).getMapName());
	    }

//		startRoomSEL = new JComboBox(startRooms);
//		startRoomSEL.setBounds(762, 90, 132, 29);
//		startRoomSEL.setEditable(false);
//		startRoomSEL.setVisible(true);
//
//		//Construct Combo boxes to select end point
//		endBuildingSEL = new JComboBox();
//		endBuildingSEL.setBounds(762, 166, 132, 29);
//		endBuildingSEL.setEditable(false);
//		endBuildingSEL.setVisible(true);
//		endBuildingSEL.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e) {
//				int indexOfCurrentMap;
//				JComboBox cb = (JComboBox)e.getSource();
//				buildingSelectedEND = (String)cb.getSelectedItem();
//				for(indexOfCurrentMap = 0; indexOfCurrentMap < maps.size(); ++indexOfCurrentMap){
//					if(buildingSelectedEND.equals(maps.get(indexOfCurrentMap).getMapName()))
//						break;
//				}
//				currentEndNodes = maps.get(indexOfCurrentMap).getNodes();
//				currentEndEdges = maps.get(indexOfCurrentMap).getEdges();
//				for(int i = 0; i < currentEndNodes.size(); ++i){
//					endRooms[i] = currentEndNodes.get(i).getName();
//				}
//			}
//		});
//
//		endRoomSEL = new JComboBox(endRooms);     
//		endRoomSEL.setBounds(762, 210, 132, 29);
//		endRoomSEL.setEditable(false);
//		endRoomSEL.setVisible(true);

		//Add Combo Boxes to UIPanel
		uiPanel.add(startBuildingSEL);
//		uiPanel.add(startRoomSEL);
//		uiPanel.add(endBuildingSEL);
//		uiPanel.add(endRoomSEL);

		//Construct button and add button to uiPanel
		searchButton = new JButton ("Search: ");
		searchButton.setBounds(762, 250, 132, 29);
		uiPanel.add(searchButton);
		JButton devGUI = new JButton ("DevMode");
		devGUI.setBounds(762, 420, 132, 29);
		uiPanel.add(devGUI);
		devGUI.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				DevGUI devMode = new DevGUI();
				devMode.setDeveloperMode(true);;
			}
		});
		JButton userGUI = new JButton ("UserMode");
		userGUI.setBounds(762, 450, 132, 29);
		uiPanel.add(userGUI);
		userGUI.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				EndUserGUI userMode = new EndUserGUI();
			}
		});

		//Construct button and add action listener
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{

			}
		});

		if(developerMode)
		{

			JButton btnCreateNodes = new JButton("Create Nodes");
			btnCreateNodes.setBounds(762, 300, 132, 29);
			uiPanel.add(btnCreateNodes);
			btnCreateNodes.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					System.out.println("Create Nodes Pushed");
					createNodes = true;
					createEdges = false;
				}
			});

			//Construct button and add action listener
			JButton btnMakeNeighbors = new JButton("Make Neighbors");
			btnMakeNeighbors.setBounds(762, 330, 132, 29);
			uiPanel.add(btnMakeNeighbors);
			btnMakeNeighbors.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) 
				{
					System.out.println("Make Neighbors Pushed");
					createNodes = false;
					createEdges = true;

				}
			});

			//Construct button and add action listener
			JButton btnExport = new JButton("Export");
			btnExport.setBounds(762, 360, 132, 29);
			uiPanel.add(btnExport);
			btnExport.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) 
				{
					System.out.println("Export Pushed");
					m1.produceNodes();
					m1.produceEdges();
					
					serialize("MapList", maps);
				}
			});
			
			
			JButton btnImport = new JButton("Import");
			btnImport.setBounds(762, 390, 132, 29);
			uiPanel.add(btnImport);
			btnImport.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) 
				{
					System.out.println("Import Pushed");
					file1 = new File(inputVar);
					process1 = new Process(file1);
					nodeList.addAll(process1.getNodes());
					edgeList.addAll(process1.getEdges());
					repaint();

					int x;
					int y;
					for(int i = 0; i < nodeList.size(); i++){
						x = (nodeList.get(i).getX());
						y = (nodeList.get(i).getY());
					}
					importPushed = true;
				}
			});     
		}    
		
		uiPanel.setVisible(true);
		frame.setVisible(true);
	}
	
	
	
	public class MouseEvents extends JComponent implements MouseMotionListener{
		private static final long serialVersionUID = 1L;


		private static final int SquareWidth = 5;

		private static final int Max = 1000;

		public Rectangle[] squares = new Rectangle[Max];

		private Line2D[] lines = new Line2D[Max];

		private int squareCount = 0;
		private int lineCount = 0;

		private int currentSquareIndex = -1;
		private int currentLineIndex = -1;

		// private BufferedImage image;

		public String nodes;

		private int count = 0;
		private int startingX = 0;
		private int startingY = 0;
		// private int 
		
		int nodeIndex;



		MouseEvents() {
			setPreferredSize(new Dimension(760, 666));
			addMouseMotionListener(this);
			addMouseListener(new MouseAdapter() 
			{
				public void mousePressed(MouseEvent evt) {
					int x = evt.getX();
					int y = evt.getY();
					
					nodeIndex = getNodeIndex(x, y);
										
				}
				int staringEdgeIndex;
				int count = 0;

				public void mouseClicked(MouseEvent evt) {
					int x = evt.getX();
					int y = evt.getY();

					System.out.println("\nX: " + x + "\t Y: " + y);
					repaint();
					
					
					if(createNodes){
						if (nodeIndex < 0) {// not inside a square
							currentStartNodes.add(new Node(x, y));
							//evt.getComponent();
						}
					}
					
					if(createEdges){
					
						if(count == 0 && nodeIndex >= 0){
							staringEdgeIndex = nodeIndex;
							System.out.println(nodeIndex);
							count++;
						} else if(count > 0 && nodeIndex >= 0){
							System.out.println(nodeIndex);
							currentStartEdges.add(new Edge(currentStartNodes.get(staringEdgeIndex), 
															currentStartNodes.get(nodeIndex),
															(int) calcDistance(currentStartNodes.get(staringEdgeIndex), currentStartNodes.get(nodeIndex))));
							count = 0;
						}
					}
					
					if (evt.getClickCount() >= 2 && createNodes) {
						
						LinkedList<Edge> tempList = new LinkedList<Edge>();
						for (int i = 0; i < currentStartEdges.size(); i++){
							if(currentStartEdges.get(i).getNode1().equals(currentStartNodes.get(nodeIndex))||
							   currentStartEdges.get(i).getNode2().equals(currentStartNodes.get(nodeIndex)))
							{
								tempList.add(currentStartEdges.get(i));
							}
						}
						currentStartEdges.removeAll(tempList);
						 
						
						// currentStartEdges.removeEdgesToNode(nodeIndex);
						currentStartNodes.remove(nodeIndex);
					}
					repaint();
				}
			});
			addMouseMotionListener(this);

		}
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			ImageIcon mapIcon = new ImageIcon(currentMapName);
			ImageIcon pathIcon = new ImageIcon();
			mapImage = mapIcon.getImage();
			pathImage = pathIcon.getImage();
			GeneralPath path = null;

			int width = mapImage.getWidth(this);
			int height = mapImage.getHeight(this);
			
			
//			ImageIcon mapIcon = new ImageIcon(currentMapName);
//			ImageIcon pathIcon = new ImageIcon();
//			mapImage = mapIcon.getImage();
//			pathImage = pathIcon.getImage();
//			GeneralPath path = null;
//
//			int width = mapImage.getWidth(this);
//			int height = mapImage.getHeight(this);

			int w = width / 3;
			int h = height / 3;

			g.drawImage(currentMapFile, 0, 0, this);
			repaint();
			revalidate();

			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			BasicStroke s = new BasicStroke(
					1.5f, 
					BasicStroke.CAP_ROUND, 
					BasicStroke.JOIN_ROUND);
			g2d.setStroke(s);
			g2d.setColor(Color.BLACK);

			for (int i = 0; i < currentStartNodes.size(); i++){
				
				((Graphics2D)g).draw(new Rectangle (currentStartNodes.get(i).getX(), currentStartNodes.get(i).getY(), SquareWidth, SquareWidth));
			}

			for (int i = 0; i < currentStartEdges.size(); i++){
				((Graphics2D)g).draw(new Line2D.Double(currentStartEdges.get(i).getNode1().getX(), currentStartEdges.get(i).getNode1().getY(),currentStartEdges.get(i).getNode2().getX(),currentStartEdges.get(i).getNode2().getY() ));
			}
		}

		public double calcDistance(int x1, int y1, int x2, int y2)
		{
			return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
		}

		public double calcDistance(int x1, int y1, int x2, int y2, int scale)
		{
			return (Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2))) * scale;
		}
		
		public double calcDistance(Node n1, Node n2)
		{
			return (Math.sqrt((n1.getX()-n2.getX())*(n1.getX()-n2.getX()) + (n1.getY()-n2.getY())*(n1.getY()-n2.getY())));
		}

		public void printSquareInfo()
		{
			System.out.println(squares);
		}

		public int getSquare(int x, int y) {
			for (int i = 0; i < squareCount; i++)
				if(squares[i].contains(x,y))
					return i;
			return -1;
		}

		public int getLines(int x, int y) {
			for (int i = 0; i < lineCount; i++)
				if(lines[i].contains(x,y))
					return i;
			return -1;
		}

		public void add(int x, int y) {
			if (squareCount < Max) {
				squares[squareCount] = new Rectangle(x, y,SquareWidth,SquareWidth);
				currentSquareIndex = squareCount;
				squareCount++;
				repaint();
			}
		}
		

		public void removeLinesAtSquare(int x, int y)
		{
			for (int i = 0; i < lineCount; i++)
			{
				int sqX1 = (int) lines[i].getX1();
				int sqY1 = (int) lines[i].getY1();
				int sqX2 = (int) lines[i].getX2();
				int sqY2 = (int) lines[i].getY2();
				int tolarance = 5;

				if(((x-tolarance <=  sqX1) && (sqX1 <= x+tolarance)) && 
						((y-tolarance <=  sqY1 ) && ( sqY1 <= y+tolarance)) 
						|| 
						(((x-tolarance <=  sqX2) && (sqX2 <= x+tolarance)) && 
						((y-tolarance <=  sqY2) && (sqY2 <= y+tolarance))))
				{
					System.out.println("Remove line: " +i);

					if (i < 0 || i >= lineCount)
						return;
					lineCount--;
					lines[i] = lines[lineCount];
					if (currentLineIndex == i)
						currentLineIndex = -1;
					repaint();
				}
			}

		}




		public void remove(int n) {
			
			
			for (int i = 0; i < currentStartEdges.size(); i++)
			{
		//		if(currentStartEdges.get(i).contains(currentStartNodes.get(n)))
		//		currentStartEdges.remove(currentStartEdges.get(i));
				
			}
	
			repaint();


		}

		public void produceNodes(){
			nodes = "";
			for (int i = 0; i < currentStartNodes.size(); i++){
				nodes = nodes +"\nX: " + currentStartNodes.get(i).getX() + "  Y: " + currentStartNodes.get(i).getY();
			}
			System.out.print(nodes);
		}


		public void produceEdges(){
			for (int i = 0; i < currentStartEdges.size(); i++){
				System.out.println("\nNode n"+ i + " = new Node(" + 
									currentStartEdges.get(i).getNode1().getX() + ", " +  
									currentStartEdges.get(i).getNode1().getY() + " );");
				System.out.println("Node n"+ i + " = new Node(" + 
									currentStartEdges.get(i).getNode2().getX() + ", " +  
									currentStartEdges.get(i).getNode2().getY() + " );");
			}
		}
		
		public void removeEdgesToNode(int nodeIndex)
		{
			for (int i = 0; i < currentStartEdges.size(); i++)
			{
				if(currentStartEdges.get(i).getNode1().equals(currentStartNodes.get(nodeIndex))||
				   currentStartEdges.get(i).getNode2().equals(currentStartNodes.get(nodeIndex)))
				{
					currentStartEdges.remove(currentStartEdges.get(i));
				}
			}
			
		}
		
		
		public int getNodeIndex(int x, int y)
		{
			int thres = 10;
			for (int i = 0; i < currentStartNodes.size(); i++)
			{
				if((currentStartNodes.get(i).getX() > x-thres)&&(currentStartNodes.get(i).getX() < x+thres) 
				&& (currentStartNodes.get(i).getY() > y-thres)&&(currentStartNodes.get(i).getY() < y+thres))
				return i;
				
			}
			return -1;
		}



		public void mouseMoved(MouseEvent evt) {
			int x = evt.getX();
			int y = evt.getY();

			if (getNodeIndex(x, y) >= 0)
				setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			else
				setCursor(Cursor.getDefaultCursor());
   


		}
		
		public void mouseDragged(MouseEvent evt) {
			         int x = evt.getX();
			         int y = evt.getY();
					 
			
			           if (nodeIndex >= 0) 
						{
							currentStartNodes.get(nodeIndex).setX(x);
							currentStartNodes.get(nodeIndex).setY(y);

			            }
		}
	}
	public Boolean getDeveloperMode(){
		return developerMode;
	}
	public void setDeveloperMode(Boolean modeSelect){
		this.developerMode = modeSelect;
	}

}
