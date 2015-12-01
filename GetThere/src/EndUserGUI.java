import java.awt.BasicStroke;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.StyleConstants;

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
	private static LinkedList<Map> mapsForPaths = new LinkedList<Map>();
	private static LinkedList<Node> nodesInMap = new LinkedList<Node>();

	//private static LinkedList<Edge> currentEndEdges = new LinkedList<Edge>();
	private String[] startRooms;
	private String[] endRooms = new String[100];
	//	private String buildingSelectedSTART;   //track which building is selected to start in.
	//	private String buildingSelectedEND;
	//private String currentMapName;
	private BufferedImage currentMapFile;

	private boolean startClicked = false;
	private boolean endClicked = false;
	private boolean startHoverFlag = false;
	private boolean endHoverFlag = false;


	private JTextArea directions;

	private JFrame frame;		//Creates the main frame for the GUI
	private JPanel uiPanel;		//Panel to hold the interface buttons
	private JPanel mapPanel;	//Panel to hold the map
	private Image mapImage;		

	//Represents the map to be chosen
	//private Image pathImage;	//Image that draws the path on the map

	//Labels on the GUI
	private JLabel startPoint;
	private JLabel buildingStart;
	private JLabel roomStart;
	private JLabel endPoint;
	private JLabel buildingEnd;
	private JLabel roomEnd;
	//private JLabel floorStart;

	//Combo Boxes on the GUI
	private JComboBox<String> startBuildingSEL;
	private XComboBox startRoomSEL = new XComboBox();
	private boolean startRoomSELLaunched = false;
	private JComboBox<String> endBuildingSEL;
	private XComboBox endRoomSEL = new XComboBox();
	private boolean endRoomSELLaunched = false;
	//private JComboBox startFloorSEL;

	//Buttons on the UI
	private JButton searchButton;
	Graphics g;
	boolean updatePath = false;
	private JButton leftArrow;
	private JButton rightArrow;

	//Start-End Nodes
	private Node startNode;
	private Node endNode;
	private LinkedList<Node> listPath = new LinkedList<Node>();
	private Djikstra pathCalc;

	//List of buildings to be shown to the user
	private String buildingSelectedSTART;	//track which building is selected to start in.
	private String buildingSelectedEND;		//track which building is selected to end in.
	public ImageIcon mapIcon;
	private Node hovered;
	private JTextPane mapNumber;
	private Integer totalMaps = 1;
	private int arrowCounter = 0;
	private int floor = -1;


	/**
	 * Create the application.
	 */
	public EndUserGUI(){
		initialize();
	}

	//Launch the application. 

	@SuppressWarnings("unchecked")
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

		endPoint = new JLabel("TO");
		endPoint.setBounds(780, 72, 132, 29);

		buildingEnd = new JLabel("Select Building:");
		buildingEnd.setBounds(762, 92, 132, 29);

		roomEnd = new JLabel("Select Room:");
		roomEnd.setBounds(900, 92, 132, 29);


		//Add Labels to the uiPanel
		uiPanel.add(startPoint);
		uiPanel.add(buildingStart);
		uiPanel.add(roomStart);
		uiPanel.add(endPoint);
		uiPanel.add(buildingEnd);
		uiPanel.add(roomEnd);

		startRoomSEL.setModel(new DefaultComboBoxModel(new String[]{}));
		startRoomSEL.setBounds(893, 50, 132, 29);
		startRoomSEL.setEditable(false);
		startRoomSEL.setVisible(true);
		startRoomSEL.setName("Start");
		
		
		mapNumber = new JTextPane();
		mapNumber.setBounds(360, 634, 47, 20);
		mapNumber.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
		mapNumber.setAlignmentX(StyleConstants.ALIGN_CENTER);
		mapNumber.setAlignmentY(StyleConstants.ALIGN_CENTER);
		uiPanel.add(mapNumber);
		
		//Construct Combo boxes to select start point
		startBuildingSEL = new JComboBox<String>();
		startBuildingSEL.setBounds(755, 50, 132, 29);
		startBuildingSEL.setEditable(false);
		startBuildingSEL.setVisible(true);
		startBuildingSEL.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				updatePath = false;
				mapNumber.setText("");
				startRoomSELLaunched = false;
				endRoomSELLaunched = false;
				repaint();
				revalidate();
				int indexOfCurrentMap;
				@SuppressWarnings("unchecked")
				JComboBox<String> cb = (JComboBox<String>)e.getSource();
				buildingSelectedSTART = (String)cb.getSelectedItem();
				for(indexOfCurrentMap = 0; indexOfCurrentMap < maps.size(); ++indexOfCurrentMap){
					if(buildingSelectedSTART.equals(maps.get(indexOfCurrentMap).getMapName()))
						break;
				}
				currentStartNodes = maps.get(indexOfCurrentMap).getNodes();
				startRooms = new String[currentStartNodes.size()];
				currentStartEdges = maps.get(indexOfCurrentMap).getEdges();
				currentMapFile = maps.get(indexOfCurrentMap).getImage();
				arrowCounter = 0;
				mapsForPaths = null;
				startRoomSEL.removeAllItems();
				for(int i = 0; i < currentStartNodes.size(); ++i){
					startRooms[i] = currentStartNodes.get(i).getName();
					if(startRooms[i] != "")
						startRoomSEL.addItem(startRooms[i]);
				}
				startHoverFlag = false;
				uiPanel.repaint();
				frame.repaint();
			}
		});

		for (int i = 0; i < maps.size(); i++) {
			if(maps.get(i).getMapName() != null)
				startBuildingSEL.addItem(maps.get(i).getMapName());
		}

		endRoomSEL.setModel(new DefaultComboBoxModel(new String[]{}));
		endRoomSEL.setBounds(893, 116, 132, 29);
		endRoomSEL.setEditable(false);
		endRoomSEL.setVisible(true);
		endRoomSEL.setName("End");
		

		//Construct Combo boxes to select end point
		endBuildingSEL = new JComboBox<String>();
		endBuildingSEL.setBounds(755, 116, 132, 29);
		endBuildingSEL.setEditable(false);
		endBuildingSEL.setVisible(true);
		endBuildingSEL.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				updatePath = false;
				int indexOfCurrentMap;
				@SuppressWarnings("unchecked")
				JComboBox<String> cb = (JComboBox<String>)e.getSource();
				buildingSelectedEND = (String)cb.getSelectedItem();
				for(indexOfCurrentMap = 0; indexOfCurrentMap < maps.size(); ++indexOfCurrentMap){
					if(buildingSelectedEND.equals(maps.get(indexOfCurrentMap).getMapName()))
						break;
				}
				currentEndNodes = maps.get(indexOfCurrentMap).getNodes();
				currentMapFile = maps.get(indexOfCurrentMap).getImage();
				endRoomSEL.removeAllItems();
				arrowCounter = 0;
				mapsForPaths = null;
				for(int i = 0; i < currentEndNodes.size(); ++i){
					endRooms[i] = currentEndNodes.get(i).getName();
					if(endRooms[i] != "")
						endRoomSEL.addItem(endRooms[i]);
				}
				endHoverFlag = false;
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
		uiPanel.add(endBuildingSEL);
		uiPanel.add(endRoomSEL);

		//Construct button and add button to uiPanel
		searchButton = new JButton ("Search");
		searchButton.setBounds(820, 150, 132, 29);
		uiPanel.add(searchButton);


		leftArrow = new JButton("<<");
		leftArrow.setBounds(275, 630, 80, 29);
		uiPanel.add(leftArrow);
		if(arrowCounter == 0){
			leftArrow.setEnabled(false);
		}

		

		rightArrow = new JButton(">>");
		rightArrow.setBounds(412, 630, 80, 29);
		uiPanel.add(rightArrow);
		rightArrow.setEnabled(false);

		JLabel instructions = new JLabel("How to get there?");
		instructions.setBounds(762, 180, 132, 29);
		uiPanel.add(instructions);

		directions = new JTextArea();
		directions.setBounds(762, 210, 255, 450);
		directions.setFont(new Font("Helvetica Neue", Font.PLAIN, 13));
		directions.setLineWrap(true);
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
				updatePath = true;
				uiPanel.setVisible(true);
				frame.setVisible(true);
				pathCalc = new Djikstra();
				if(!startClicked && !endClicked){
					for (i = 0; i < currentStartNodes.size(); i++){
						if(startRoomSEL.getSelectedItem() == currentStartNodes.get(i).getName())
							startNode = currentStartNodes.get(i);
					}
					for(i = 0; i < currentEndNodes.size(); i++){
						if(endRoomSEL.getSelectedItem() == currentEndNodes.get(i).getName())
							endNode = currentEndNodes.get(i);
					}
				}
				if(startClicked && !endClicked){
					directions.setText("Please Select End point");
					updatePath = false;
				}
				if(updatePath){
					System.out.println(startBuildingSEL.getSelectedItem());
					System.out.println(floor);
					listPath = pathCalc.navigate(startNode, endNode);
					mapsForPaths = new LinkedList<Map>();
					System.out.println(listPath.size());
					for (i = 0; i < listPath.size(); i++){
						//String fullNodeLocation = listPath.get(i).getName();
						//int dotLoc = fullNodeLocation.indexOf(".");
						//System.out.println(dotLoc);
						//System.out.println(fullNodeLocation);
						//String nodeLocation = fullNodeLocation.substring(0, dotLoc);
						//						if(fullNodeLocation != startBuildingSEL.getSelectedItem()){
						//							for(int j = 0; j <maps.size(); j++){
						//								maps.get(j).getNodes();
						//							}
						//						}
						for (int j = 0; j < maps.size(); j++){
							nodesInMap = maps.get(j).getNodes();
							for(int k = 0; k<nodesInMap.size(); k++){
								if(listPath.get(i) == nodesInMap.get(k)){
									if(!mapsForPaths.contains(maps.get(j))){
										mapsForPaths.add(maps.get(j));
									}
								}
							}
						}
						currentMapFile = mapsForPaths.getFirst().getImage();
						totalMaps = mapsForPaths.size();

						if(mapsForPaths.size() > 1){
							rightArrow.setEnabled(true);
							mapNumber.setText(String.valueOf(1) + " of " + String.valueOf(totalMaps));
						}
					}
					directions.setText(pathCalc.gpsInstructions(pathCalc.navigate(startNode, endNode)));
					System.out.println("check List: " + listPath.size());
					repaint();
					revalidate();
				}
			}
		});

		leftArrow.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(mapsForPaths!= null){
					if(arrowCounter != 0){
						rightArrow.setEnabled(true);
						arrowCounter -= 1;
						mapNumber.setText(String.valueOf(arrowCounter + 1) + " of " + String.valueOf(totalMaps));
						currentMapFile = mapsForPaths.get(arrowCounter).getImage();
					}else if (arrowCounter == 0){
						leftArrow.setEnabled(false);
					}
				}
			}
		});
		rightArrow.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(mapsForPaths != null){
					if(arrowCounter != totalMaps-1){
						leftArrow.setEnabled(true);
						arrowCounter += 1;
						mapNumber.setText(String.valueOf(arrowCounter + 1) + " of " + String.valueOf(totalMaps));
						currentMapFile = mapsForPaths.get(arrowCounter).getImage();
					}else if (arrowCounter == totalMaps-1){
						rightArrow.setEnabled(false);
					}
				}
			}
		});
		uiPanel.setVisible(true);
		frame.setVisible(true);
	}

	public class MyGraphics extends JComponent implements MouseMotionListener{

		private static final long serialVersionUID = 1L;
		private static final int SquareWidth = 5;
		private static final int CircleDiam = 10;

		MyGraphics() {
			setPreferredSize(new Dimension(760, 666));
			addMouseMotionListener(this);
			addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent evt) {
					int x = evt.getX();
					int y = evt.getY();
					System.out.println("Click " + x + "..."+ y);
					if(!startClicked){
						startNode = findClosestNode(x,y);
						System.out.println("Closest start node has x = " + startNode.getX() + " and y = "+ startNode.getY());
						startClicked = true;
					}
					else if(!endClicked){
						endNode = findClosestNode(x,y);
						System.out.println("Closest end node has x = " + endNode.getX() + " and y = "+ endNode.getY());
						endClicked = true;
					}
					else{
						System.out.println("Start and end nodes have already been selected");
					}

				}

				private Node findClosestNode(int x, int y) {
					double shortestDistance = Double.MAX_VALUE;
					double previousShortestDistance;
					Node result = null;
					for(int i = 0; i < currentStartNodes.size(); i++){
						previousShortestDistance = shortestDistance;
						Node temp = currentStartNodes.get(i);
						shortestDistance = Math.min(Math.sqrt((temp.getX()-x)*(temp.getX()-x) + (temp.getY()-y)*(temp.getY()-y)), shortestDistance);
						if(previousShortestDistance != shortestDistance){
							result = temp;
						}
					}
					return result;
				}});
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

			//g.drawImage(mapImage, 0, 0, this);

			//			for (int i = 0; i < currentStartNodes.size(); i++){
			//				((Graphics2D)g).draw(new Rectangle (currentStartNodes.get(i).getX()-SquareWidth/2, currentStartNodes.get(i).getY()-SquareWidth/2, SquareWidth, SquareWidth));
			//			}
			//
			//			for (int i = 0; i < currentStartEdges.size(); i++){
			//				((Graphics2D)g).draw(new Line2D.Double(currentStartEdges.get(i).getNode1().getX(), currentStartEdges.get(i).getNode1().getY(),currentStartEdges.get(i).getNode2().getX(),currentStartEdges.get(i).getNode2().getY() ));
			//			}
			//			

			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			BasicStroke s = new BasicStroke(
					3f, 
					BasicStroke.CAP_ROUND, 
					BasicStroke.JOIN_ROUND);
			g2d.setStroke(s);

			if (path==null && updatePath == true) {
				removeAll();
				int i;
				path = new GeneralPath();
				path.moveTo(listPath.getFirst().getX(), listPath.getFirst().getY()); 
				for (i=1; i<listPath.size(); i++){
					if(mapsForPaths.get(arrowCounter).getNodes().contains(listPath.get(i-1)) && 
							mapsForPaths.get(arrowCounter).getNodes().contains(listPath.get(i))){
						path.lineTo(listPath.get(i).getX(),listPath.get(i).getY());
						g2d.draw(path);
					}
					else{
						path.moveTo(listPath.get(i).getX(), listPath.get(i).getY());
					}
					if(mapsForPaths.get(arrowCounter).getNodes().contains(listPath.get(i-1)) != 
							mapsForPaths.get(arrowCounter).getNodes().contains(listPath.get(i))){
						g.setColor(Color.BLACK);
						g.fillOval(listPath.get(i).getX()-(CircleDiam+3)/2, listPath.get(i).getY()-(CircleDiam+3)/2, CircleDiam+3, CircleDiam+3);
						g.setColor(Color.WHITE);
						g.fillOval(listPath.get(i).getX()-CircleDiam/2, listPath.get(i).getY()-CircleDiam/2, CircleDiam, CircleDiam);
					}
				}

				g2d.setColor(Color.BLACK);
				g2d.draw(path);
				g2d.setStroke(new BasicStroke(2));
				g2d.setColor(Color.BLUE);
				g2d.draw(path);

				if(mapsForPaths.get(arrowCounter).getNodes().contains(startNode)){
					g.setColor(Color.BLACK);
					g.fillOval(startNode.getX()-(CircleDiam+3)/2, startNode.getY()-(CircleDiam+3)/2, CircleDiam+3, CircleDiam+3);
					g.setColor(Color.GREEN);
					g.fillOval(startNode.getX()-CircleDiam/2, startNode.getY()-CircleDiam/2, CircleDiam, CircleDiam);
				}

				if(mapsForPaths.get(arrowCounter).getNodes().contains(endNode)){
					g.setColor(Color.BLACK);
					g.fillOval(endNode.getX()-(CircleDiam+3)/2, endNode.getY()-(CircleDiam+3)/2, CircleDiam+3, CircleDiam+3);
					g.setColor(Color.RED);
					g.fillOval(endNode.getX()-CircleDiam/2, endNode.getY()-CircleDiam/2, CircleDiam, CircleDiam);
				}

				repaint();
				revalidate();
			}
			if (hovered != null){
				if(startNode != null){

					g.setColor(Color.BLACK);
					g.fillOval(startNode.getX()-(CircleDiam+3)/2, startNode.getY()-(CircleDiam+3)/2, CircleDiam+3, CircleDiam+3);
					g.setColor(Color.GREEN);
					g.fillOval(startNode.getX()-CircleDiam/2, startNode.getY()-CircleDiam/2, CircleDiam, CircleDiam);

				}
				if(endNode != null){
					g.setColor(Color.BLACK);
					g.fillOval(startNode.getX()-(CircleDiam+3)/2, startNode.getY()-(CircleDiam+3)/2, CircleDiam+3, CircleDiam+3);
					g.setColor(Color.GREEN);
					g.fillOval(startNode.getX()-CircleDiam/2, startNode.getY()-CircleDiam/2, CircleDiam, CircleDiam);

					g.setColor(Color.BLACK);
					g.fillOval(endNode.getX()-(CircleDiam+3)/2, endNode.getY()-(CircleDiam+3)/2, CircleDiam+3, CircleDiam+3);
					g.setColor(Color.RED);
					g.fillOval(endNode.getX()-CircleDiam/2, endNode.getY()-CircleDiam/2, CircleDiam, CircleDiam);

				}
			}
			if(startClicked){

				g.setColor(Color.BLACK);
				g.fillOval(startNode.getX()-(CircleDiam+3)/2, startNode.getY()-(CircleDiam+3)/2, CircleDiam+3, CircleDiam+3);
				g.setColor(Color.GREEN);
				g.fillOval(startNode.getX()-CircleDiam/2, startNode.getY()-CircleDiam/2, CircleDiam, CircleDiam);

			}
			if(endClicked){
				g.setColor(Color.BLACK);
				g.fillOval(startNode.getX()-(CircleDiam+3)/2, startNode.getY()-(CircleDiam+3)/2, CircleDiam+3, CircleDiam+3);
				g.setColor(Color.GREEN);
				g.fillOval(startNode.getX()-CircleDiam/2, startNode.getY()-CircleDiam/2, CircleDiam, CircleDiam);
				g.setColor(Color.BLACK);
				g.fillOval(endNode.getX()-(CircleDiam+3)/2, endNode.getY()-(CircleDiam+3)/2, CircleDiam+3, CircleDiam+3);
				g.setColor(Color.RED);
				g.fillOval(endNode.getX()-CircleDiam/2, endNode.getY()-CircleDiam/2, CircleDiam, CircleDiam);
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
			//System.out.println("X: " + x + " Y: " +y);

		}
		public void mousePressed(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			System.out.println("X: " + x + " Y: " +y);
		}


	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	public class XComboBox extends JComboBox {

		private ListSelectionListener listener;

		public XComboBox() {
			uninstall();
			install();
		}

		@Override
		public void updateUI() {
			uninstall();
			super.updateUI();
			install();
		}

		private void uninstall() {
			if (listener == null) return;
			getPopupList().removeListSelectionListener(listener);
			listener = null;
		}

		protected void install() {
			listener = new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {

					if (e.getValueIsAdjusting()) return;

					JList list = getPopupList();
					hovered = getNodeByName(String.valueOf(list.getSelectedValue()));
					if (hovered != null){
						//System.out.println("--> " + hovered.getX() + "---" + hovered.getY());
						System.out.println(getPopupName());
						if(!startHoverFlag){
							startHoverFlag = true;
							return;
							}
						if(!endHoverFlag){
								endHoverFlag = true;
								return;
								}
						if(getPopupName().equals("Start")){
							startClicked = true;
							startNode = hovered;
							System.out.println("START SELECTED");
						}
						else if(getPopupName().equals("End")){
							endClicked = true;
							endNode = hovered;
							System.out.println("END SELECTED");
						}
					}
				}

				private Node getNodeByName(String name) {
                    for(int i = 0; i < currentStartNodes.size(); i++){
                        if(currentStartNodes.get(i).getName().equals(name)){
                            return currentStartNodes.get(i);
                        }
                    }
                    for(int j = 0; j < currentEndNodes.size(); j++){
                        if(currentEndNodes.get(j).getName().equals(name)){
                            return currentEndNodes.get(j);
                        }
                    }
                    return null;
                }
			};
			getPopupList().addListSelectionListener(listener);
		}

		private JList getPopupList() {
			ComboPopup popup = (ComboPopup) getUI().getAccessibleChild(this, 0);
			return popup.getList();

		}

		private String getPopupName() {
			JComboBox jcb = (JComboBox) getUI().getAccessibleChild(this, 0).getAccessibleContext().getAccessibleParent();
			return jcb.getName();
	}
}
}