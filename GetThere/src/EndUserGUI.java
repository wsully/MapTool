import java.awt.BasicStroke;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

///**
//* Created by Lumbini on 11/7/2015.
// * */
//

public class EndUserGUI extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 2270760135813536905L;
	private static LinkedList<Map> maps = new LinkedList<Map>();
	private static LinkedList<Node> currentStartNodes = new LinkedList<Node>();
	private static LinkedList<Edge> currentStartEdges = new LinkedList<Edge>();
	private static LinkedList<Node> currentEndNodes = new LinkedList<Node>();
	private static LinkedList<Edge> currentEndEdges = new LinkedList<Edge>();
	private String[] startRooms;
	private String[] endRooms = new String[100];
//	private String buildingSelectedSTART;   //track which building is selected to start in.
//	private String buildingSelectedEND;
	private String currentMapName;
	private BufferedImage currentMapFile;

	private JTextArea directions;

	private JFrame frame;		//Creates the main frame for the GUI
	private JPanel uiPanel;		//Panel to hold the interface buttons
	private JPanel mapPanel;	//Panel to hold the map
	private Image mapImage;		//Represents the map to be chosen
	private Image pathImage;	//Image that draws the path on the map

	//Labels on the GUI
	private JLabel startPoint;
	private JLabel buildingStart;
	private JLabel roomStart;
	private JLabel endPoint;
	private JLabel buildingEnd;
	private JLabel roomEnd;
	private JLabel floorStart;

	//Combo Boxes on the GUI
	private JComboBox startBuildingSEL;
	private JComboBox startRoomSEL;
	private JComboBox endBuildingSEL;
	private JComboBox endRoomSEL;
	private JComboBox startFloorSEL;

	//Buttons on the UI
	private JButton searchButton;
	Graphics g;
	Boolean updatePath = false;
	private JButton leftArrow;
	private JButton rightArrow;

	//Start-End Nodes
	private Node startNode;
	private Node endNode;
	private LinkedList<Node> listPath = new LinkedList<Node>();
	private String pathDire; 
	private Djikstra pathCalc;

	//List of buildings to be shown to the user
	//private String[] buildings = { "Stratton Hall"}; 
	private String buildingSelectedSTART;	//track which building is selected to start in.
	private String roomSelectedSTART;
	private String floorSelectedSTART;
	private String buildingSelectedEND;		//track which building is selected to end in.
	//private Map selected;					//track which map to display
	public ImageIcon mapIcon;


	//Test map nodes
	// Node one = new Node(1, 12, "Exit");
	Node two = new Node(3, 12);
	Node three = new Node(3, 13, "101");
	Node four = new Node(9, 12);
	Node five = new Node(9, 13, "102");
	Node six = new Node(15, 12);
	Node seven = new Node (15, 13, "103");
	Node eight = new Node (19, 12);
	//Node nine = new Node (19, 13, "104");
	Node ten = new Node (26, 12);
	Node eleven = new Node (26, 13, "104");
	Node twelve = new Node (28, 12);
	Node thirteen = new Node (28, 20, "Exit");
	Node fourteen = new Node (28, 2);
	Node fifteen = new Node (26, 2);
	Node sixteen = new Node (26, 3, "105");
	Node seventeen = new Node (19, 2);
	//Node eighteen = new Node (19, 3, "105");
	Node nineteen = new Node (9, 2);
	Node twenty = new Node (3, 2);
	Node twentyone = new Node (3, 3, "107");
	Node twentytwo = new Node (9, 6);
	Node twentythree = new Node (11, 6, "106");

	//Test map edges
	// Edge a = new Edge (one, two, 2*25);
	Edge b = new Edge (two, three, 1*25);
	Edge c = new Edge (two, four, 6*25);
	Edge d = new Edge (four, five, 1*25);
	Edge e = new Edge (four, six, 6*25);
	Edge f = new Edge (six, seven, 1*25);
	Edge gee = new Edge (six, eight, 4*25);
	//Edge h = new Edge (eight, nine, 1*25);
	Edge i = new Edge (eight, ten, 7*25);
	Edge j = new Edge (ten, eleven, 1*25);
	Edge k = new Edge (ten, twelve, 2*25);
	Edge l = new Edge (twelve, thirteen, 8*25);
	Edge m = new Edge (twelve, fourteen, 10*25);
	Edge n = new Edge (fourteen, fifteen, 2*25);
	Edge o = new Edge (fifteen, sixteen, 1*25);
	Edge p = new Edge (fifteen, seventeen, 7*25);
	// Edge q = new Edge (seventeen, eighteen, 1*25);
	Edge r = new Edge (seventeen, nineteen, 10*25);
	Edge s = new Edge (nineteen, twenty, 6*25);
	Edge t = new Edge (twenty, twentyone, 1*25);
	Edge u = new Edge (nineteen, twentytwo, 4*25);
	Edge v = new Edge (twentytwo, twentythree, 2*25);
	Edge w = new Edge (twentytwo, four, 6*25);
	Node[] testNodes = new Node[]{ two, three, four, five, six, seven, eight, ten, eleven, twelve,
			thirteen, fourteen, fifteen, sixteen, seventeen, nineteen, twenty, 
			twentyone, twentytwo, twentythree};
	private String currentMap;
	//	private String[] roomsLibrary = {"7", "8", "9"};
	//	private String[] roomsAtwaterKent = {"10", "11", "12"};

	//Variable to track which room list to display
	private String[] roomsSelected = {};
	private int floor = -1;

	protected enum BUILDINGS {
		STRATTONHALL, PROJECTCENTRE, LIBRARY, ATWATERKENT;
		public static BUILDINGS getEnum(String s){
			if(s.equals("Stratton Hall")){
				return STRATTONHALL;
			}else if(s.equals("Project Centre")){
				return PROJECTCENTRE;
			}else if(s.equals("Library")){
				return LIBRARY;
			}else if (s.equals("Atwater Kent")){
				return ATWATERKENT;
			}
			throw new IllegalArgumentException("No Enum specified for this string");
		}
	}


	/**
	 * Create the application.
	 */
	public EndUserGUI(){
		initialize();
	}

	//Launch the application. 

	public static void main(String[] args) {
		maps = (LinkedList<Map>) deserialize("MapList");
		
		EventQueue.invokeLater(new Runnable() {
			EndUserGUI window = new EndUserGUI();
			public void run() {
				try {
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
//			System.out.println("Deserialized map...");
//			System.out.println("Name: " + m.getMapName());
		return m;
		}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		//Frame operations
		frame = new JFrame();
		frame.setBounds(100, 100, 1030, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		frame.setTitle("Get There");
		frame.setResizable(false);
		frame.setVisible(true);

		//currentMap = mapList[3];
		//Panel Operations
		uiPanel = new JPanel();
		frame.getContentPane().add(uiPanel);
		uiPanel.setLayout(null);

		mapPanel = new JPanel();
		mapPanel.setBounds(5, 5, 750, 620);
		uiPanel.add(mapPanel);
		mapPanel.add(new MyGraphics());

		//Creating Labels
		startPoint = new JLabel("FROM");
		startPoint.setBounds(780, 6, 132, 29);

		buildingStart = new JLabel("Select Building:");
		buildingStart.setBounds(762, 26, 132, 29);

		roomStart = new JLabel("Select Room:");
		roomStart.setBounds(900, 26, 132, 29);

		//		floorStart = new JLabel("Select Floor:");
		//		floorStart.setBounds(762, 110, 132, 29);

		endPoint = new JLabel("TO");
		endPoint.setBounds(780, 72, 132, 29);

		buildingEnd = new JLabel("Select Building:");
		buildingEnd.setBounds(762, 92, 132, 29);

		roomEnd = new JLabel("Select Room:");
		roomEnd.setBounds(900, 92, 132, 29);


		//Add Labels to the uiPanel
		uiPanel.add(startPoint);
		uiPanel.add(buildingStart);
		//uiPanel.add(floorStart);
		uiPanel.add(roomStart);
		uiPanel.add(endPoint);
		uiPanel.add(buildingEnd);
		uiPanel.add(roomEnd);
		
		startRoomSEL = new JComboBox();
		startRoomSEL.setBounds(893, 50, 132, 29);
		startRoomSEL.setEditable(false);
		startRoomSEL.setVisible(true);

		//Construct Combo boxes to select start point
		startBuildingSEL = new JComboBox();
		startBuildingSEL.setBounds(755, 50, 132, 29);
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
				startRooms = new String[currentStartNodes.size()];
				currentStartEdges = maps.get(indexOfCurrentMap).getEdges();
				currentMapFile = maps.get(indexOfCurrentMap).getImage();
				startRoomSEL.removeAllItems();
				for(int i = 0; i < currentStartNodes.size(); ++i){
					startRooms[i] = currentStartNodes.get(i).getName();
					if(startRooms[i] != "")
						startRoomSEL.addItem(startRooms[i]);
				}
				uiPanel.repaint();
		        frame.repaint();
			}
		});
		
		for (int i = 0; i < maps.size(); i++) {
	        if(maps.get(i).getMapName() != null)
	        	startBuildingSEL.addItem(maps.get(i).getMapName());
	    }

		endRoomSEL = new JComboBox();
		endRoomSEL.setBounds(893, 116, 132, 29);
		endRoomSEL.setEditable(false);
		endRoomSEL.setVisible(true);

		//Construct Combo boxes to select end point
		endBuildingSEL = new JComboBox();
		endBuildingSEL.setBounds(755, 116, 132, 29);
		endBuildingSEL.setEditable(false);
		endBuildingSEL.setVisible(true);
		endBuildingSEL.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int indexOfCurrentMap;
				JComboBox cb = (JComboBox)e.getSource();
				buildingSelectedEND = (String)cb.getSelectedItem();
				for(indexOfCurrentMap = 0; indexOfCurrentMap < maps.size(); ++indexOfCurrentMap){
					if(buildingSelectedEND.equals(maps.get(indexOfCurrentMap).getMapName()))
						break;
				}
				currentMapName = maps.get(indexOfCurrentMap).getMapName();
				currentEndNodes = maps.get(indexOfCurrentMap).getNodes();
				currentEndEdges = maps.get(indexOfCurrentMap).getEdges();
				currentMapFile = maps.get(indexOfCurrentMap).getImage();
				endRoomSEL.removeAllItems();
				for(int i = 0; i < currentEndNodes.size(); ++i){
					endRooms[i] = currentEndNodes.get(i).getName();
					if(endRooms[i] != "")
						endRoomSEL.addItem(endRooms[i]);
				}
				uiPanel.repaint();
		        frame.repaint();
			}
		});
		
		for (int i = 0; i < maps.size(); i++) {
	           if(maps.get(i).getMapName() != null)
	        	   endBuildingSEL.addItem(maps.get(i).getMapName());
	    }

		

		//Add Combo Boxes to UIPanel

		uiPanel.add(startBuildingSEL);
		uiPanel.add(startRoomSEL);
		//uiPanel.add(startFloorSEL);
		uiPanel.add(endBuildingSEL);
		uiPanel.add(endRoomSEL);

		//Construct button and add button to uiPanel
		searchButton = new JButton ("Search");
		searchButton.setBounds(820, 150, 132, 29);
		uiPanel.add(searchButton);


		leftArrow = new JButton("<<");
		leftArrow.setBounds(275, 630, 80, 29);
		uiPanel.add(leftArrow);

		rightArrow = new JButton(">>");
		rightArrow.setBounds(365, 630, 80, 29);
		uiPanel.add(rightArrow);

		JLabel instructions = new JLabel("How to get there?");
		instructions.setBounds(762, 180, 132, 29);
		uiPanel.add(instructions);

		directions = new JTextArea();
		directions.setBounds(762, 210, 255, 450);
		directions.setEditable(false);
		JScrollPane scrollDire = new JScrollPane(directions);
		scrollDire.setBounds(762, 210, 255, 450);
		scrollDire.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		uiPanel.add(scrollDire);


		//Construct buttons and add action listener
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				int i;
				if(startFloorSEL.getSelectedItem() == "Test"){
					updatePath = true;
				}else updatePath = false;
				uiPanel.setVisible(true);
				frame.setVisible(true);
				GeneralPath path = null;
				pathCalc = new Djikstra();

				int startInt = Integer.parseInt((String)startRoomSEL.getSelectedItem()) -1;		//need to look up the nodes in the text file
				int endInt = Integer.parseInt((String)endRoomSEL.getSelectedItem()) -1;
				for (i=0; i<testNodes.length; i++){
					if(startRoomSEL.getSelectedItem() == testNodes[i].getName()){
						startNode = testNodes[i];
					}
					if(endRoomSEL.getSelectedItem() == testNodes[i].getName()){
						endNode = testNodes[i];
					}
				}

				System.out.println(startBuildingSEL.getSelectedItem());
				System.out.println(floor);
				listPath = pathCalc.navigate(startNode, endNode);
				directions.setText(pathCalc.gpsInstructions(pathCalc.navigate(startNode, endNode)));
				System.out.println("check List: " + listPath.size());
				repaint();
				revalidate();
			}
		});

		uiPanel.setVisible(true);
		frame.setVisible(true);
	}

	public class MyGraphics extends JComponent implements MouseMotionListener{

		private static final long serialVersionUID = 1L;
		private static final int SquareWidth = 5;
		MyGraphics() {
			setPreferredSize(new Dimension(760, 666));
			addMouseMotionListener(this);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(currentMapFile, 0, 0, this);
			repaint();
			revalidate();
			
			GeneralPath path = null;

			repaint();
			revalidate();
			
			g.drawImage(mapImage, 0, 0, this);
			
			for (int i = 0; i < currentStartNodes.size(); i++){
				((Graphics2D)g).draw(new Rectangle (currentStartNodes.get(i).getX(), currentStartNodes.get(i).getY(), SquareWidth, SquareWidth));
			}

			for (int i = 0; i < currentStartEdges.size(); i++){
				((Graphics2D)g).draw(new Line2D.Double(currentStartEdges.get(i).getNode1().getX(), currentStartEdges.get(i).getNode1().getY(),currentStartEdges.get(i).getNode2().getX(),currentStartEdges.get(i).getNode2().getY() ));
			}

			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			BasicStroke s = new BasicStroke(
					5.5f, 
					BasicStroke.CAP_ROUND, 
					BasicStroke.JOIN_ROUND);
			g2d.setStroke(s);
			g2d.setColor(Color.BLACK);
			if (path==null && updatePath == true) {
				removeAll();
				int i;
				path = new GeneralPath();
				//                path.moveTo(0, 0);
				//                path.lineTo(100, 100);
				//                path.lineTo(300, 400);
				JLabel start = new JLabel("Start");
				JLabel end = new JLabel("End");
				start.setBounds((listPath.getFirst().getX()*25)-12, (listPath.getFirst().getY()*25)-60, 100, 100);
				end.setBounds((listPath.getLast().getX()*25)-8, (listPath.getLast().getY()*25)-60, 100, 100);
				path.moveTo(listPath.getFirst().getX()*25+20, listPath.getFirst().getY()*25); 
				for (i=0; i<listPath.size(); i++){
					path.lineTo(listPath.get(i).getX()*25+20,listPath.get(i).getY()*25);
				}
				this.add(start);
				this.add(end);
				g2d.draw(path);
				g2d.setStroke(new BasicStroke(3));
				g2d.setColor(Color.RED);
				g2d.draw(path);
				repaint();
				revalidate();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			int x = e.getX();
			int y = e.getY();
			System.out.println("X: " + x + " Y: " +y);

		}
		public void mousePressed(MouseEvent evt) {
			int x = evt.getX();
			int y = evt.getY();
			System.out.println("X: " + x + " Y: " +y);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
