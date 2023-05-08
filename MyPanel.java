import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MyPanel extends JPanel implements MouseListener {

	Timer timer;
	
	ArrayList<Point> points = new ArrayList<Point>();
	ArrayList<Triangle> arrows = new ArrayList<Triangle>();
	ArrayList<Edge> edges = new ArrayList<Edge>();
	ArrayList<Face> faces = new ArrayList<Face>();
	ArrayList<DCEL> dcel = new ArrayList<DCEL>();
	ArrayList<Lespede> lespede = new ArrayList<Lespede>();
	ArrayList<Integer> aux = new ArrayList<Integer>();
	ArrayList<Integer> selectedPoints = new ArrayList<Integer>();
	BufferedImage arrowImgBuff;
	Image arrowImg;
	
	int windowW, windowH;
	int titleSize = 100, pointSize = 6, hoverPointSize = 14, arrowSize = 20, squareSize = 20;
	int step = 0;
	int startPoint, endPoint, hoverPoint = -1;
	int faceCount = 1, faceX = 0, faceY = 0;
	int xM, yM;
	Point hPoint = null, lPoint = null;
	Point[] sPoint = new Point[2];
	boolean drawLine = false;
	boolean valid = false;
	boolean formFace = false;
	
	MyPanel() {
		this.setLayout(null);
		addMouseListener(this);
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if(step < 4) {
					if(lPoint == null)
						lPoint = hPoint;
					hPoint = isMouseOnPoint(MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y);
					if(hoverPoint == -1)
						hoverPoint = isMouseOnPoint(MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y, 0);
					if(hPoint != null)
						setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					else
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					repaint();
				}
				else timer.cancel();
			}
		}, 100, 100);
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if(step == 0) {
			try { 
				arrowImgBuff = ImageIO.read(Main.class.getResource("/res/triangle.png"));
				arrowImg = arrowImgBuff.getScaledInstance(arrowSize, arrowSize, Image.SCALE_SMOOTH);
			} catch(IOException ex) {
				ex.printStackTrace();
			}
			g2d.setPaint(Color.white);
			g2d.fillRect(0, 0, this.getBounds().width, this.getBounds().height);
			drawCenteredString(g2d, "Creeaza PSLG", new Rectangle(0, 0, windowW, titleSize), new Font("Sans", Font.BOLD, 21), Color.black);
		}
		if(step == 1) {
			g2d.setPaint(Color.black);
			g2d.fillOval(points.get(points.size() - 1).x - pointSize / 2, points.get(points.size() - 1).y - pointSize / 2, pointSize, pointSize);
			drawCenteredString(g2d, "v" + points.size(), new Rectangle(points.get(points.size() - 1).x - pointSize / 2, points.get(points.size() - 1).y - pointSize * 2, 20, 20), new Font("Sans", Font.BOLD, 13), Color.black);
		}
		if(hPoint != null) {
			if(points.get(hoverPoint).degree < 2)
				g2d.setPaint(Color.red);
			else
				g2d.setPaint(Color.black);
			g2d.fillOval(hPoint.x - hoverPointSize / 2, hPoint.y - hoverPointSize / 2, hoverPointSize, hoverPointSize);
		}
		if(lPoint != null && hPoint == null && step < 4) {
			if(lPoint != sPoint[0]) {
				g2d.setPaint(Color.white);
				g2d.fillOval(lPoint.x - hoverPointSize / 2, lPoint.y - hoverPointSize / 2, hoverPointSize, hoverPointSize);
			
				g2d.setPaint(Color.black);
				g2d.fillOval(lPoint.x - pointSize / 2, lPoint.y - pointSize / 2, pointSize, pointSize);
			}
			
			if(step == 1) {
				aux.clear();
				aux = getPointEdges(hoverPoint);
				hoverPoint = -1;
				for(int i = 0; i < aux.size(); i += 2)
					g2d.drawLine(points.get(aux.get(i)).x, points.get(aux.get(i)).y, points.get(aux.get(i + 1)).x, points.get(aux.get(i + 1)).y);
			}
			
			lPoint = null;
		}
		if(drawLine) {
			g2d.setPaint(Color.white);
			g2d.fillOval(sPoint[0].x - hoverPointSize / 2, sPoint[0].y - hoverPointSize / 2, hoverPointSize, hoverPointSize);
			g2d.setPaint(Color.black);
			g2d.fillOval(sPoint[0].x - pointSize / 2, sPoint[0].y - pointSize / 2, pointSize, pointSize);
			
			g2d.setPaint(Color.black);
			g2d.setStroke(new BasicStroke(2));
			g2d.drawLine(sPoint[0].x, sPoint[0].y, sPoint[1].x, sPoint[1].y);
			
			Edge e = new Edge(startPoint, endPoint);
			edges.add(e);
			dcel.add(new DCEL(edges.size(), startPoint, endPoint));
			sPoint[0].degree++; sPoint[1].degree++;
			
			drawTriangle(g2d, new Point(sPoint[0].x, sPoint[0].y), new Point(sPoint[1].x, sPoint[1].y));
			
			if(sPoint[0].degree >= 2) {
				g2d.setPaint(Color.white);
				g2d.fillOval(sPoint[0].x - pointSize / 2, sPoint[0].y - pointSize / 2, pointSize, pointSize);
				g2d.setPaint(Color.black);
				g2d.fillOval(sPoint[0].x - pointSize / 2, sPoint[0].y - pointSize / 2, pointSize, pointSize);
			}
			if(sPoint[1].degree >= 2) {
				g2d.setPaint(Color.white);
				g2d.fillOval(sPoint[1].x - pointSize / 2, sPoint[1].y - pointSize / 2, pointSize, pointSize);
				g2d.setPaint(Color.black);
				g2d.fillOval(sPoint[1].x - pointSize / 2, sPoint[1].y - pointSize / 2, pointSize, pointSize);
			}
			sPoint[0] = null; sPoint[1] = null; drawLine = false;
		}
		if(step == 1) {
			boolean a = true;
			for(int i = 0; i < points.size(); i++) {
				if(points.get(i).degree < 2) {
					g2d.setPaint(Color.white);
					g2d.fillOval(points.get(i).x - pointSize / 2, points.get(i).y - pointSize / 2, pointSize, pointSize);
					g2d.setPaint(Color.red);
					g2d.fillOval(points.get(i).x - pointSize / 2, points.get(i).y - pointSize / 2, pointSize, pointSize);
					a = false;
				}
			}
			if(valid == true && a == false) {
				g2d.setPaint(Color.white);
				g2d.fillRect(0, 0, windowW, titleSize);
				drawCenteredString(g2d, "Creeaza PSLG", new Rectangle(0, 0, windowW, titleSize), new Font("Sans", Font.BOLD, 21), Color.black);
			}
			if(valid == false && a == true) {
				g2d.setPaint(Color.white);
				g2d.fillRect(0, 0, windowW, titleSize);
				drawCenteredString(g2d, "Apasa ENTER pentru a ordona punctele", new Rectangle(0, 0, windowW, titleSize), new Font("Sans", Font.BOLD, 21), Color.black);
			}
			valid = a;
		}
		if(step == 2) {
			sortVertices(g2d);
			g2d.setPaint(Color.white);
			g2d.fillRect(0, 0, windowW, titleSize);
			drawCenteredString(g2d, "Numeroteaza fetele", new Rectangle(0, 0, windowW, titleSize), new Font("Sans", Font.BOLD, 21), Color.black);
			step = 3;
		}
		if(step == 3) {
			if(selectedPoints.size() > 0 && !formFace)
				g2d.fillOval(points.get(selectedPoints.get(selectedPoints.size() - 1)).x - hoverPointSize / 2, points.get(selectedPoints.get(selectedPoints.size() - 1)).y - hoverPointSize / 2, hoverPointSize, hoverPointSize);
			if(formFace) {
				if(!faceExists(selectedPoints)) {
					g2d.setPaint(Color.white);
					g2d.fillRect(0, 0, windowW, titleSize);
					drawCenteredString(g2d, "Finalizeaza pas", new Rectangle(0, 0, windowW, titleSize), new Font("Sans", Font.BOLD, 21), Color.black);
					
					faceCount++;
					faceX /= selectedPoints.size(); faceY /= selectedPoints.size();
					g2d.setPaint(Color.black);
					g2d.drawRect(faceX - squareSize / 2, faceY - squareSize / 2, squareSize, squareSize);
					drawCenteredString(g2d, faceCount + "", new Rectangle(faceX - squareSize / 2, faceY + squareSize / 4, squareSize, squareSize), new Font("Sans", Font.BOLD, 15), Color.black);
				
					for(int i = 0; i < selectedPoints.size(); i++) {
						g2d.setPaint(Color.white);
						g2d.fillOval(points.get(selectedPoints.get(i)).x - hoverPointSize / 2, points.get(selectedPoints.get(i)).y - hoverPointSize / 2, hoverPointSize, hoverPointSize);
						g2d.setPaint(Color.black);
						g2d.fillOval(points.get(selectedPoints.get(i)).x - pointSize / 2, points.get(selectedPoints.get(i)).y - pointSize / 2, pointSize, pointSize);
					}
				
					Face f = new Face(selectedPoints, faceX, faceY, faceCount);
					faces.add(f);
					
					selectedPoints.clear();
					faceX = 0; faceY = 0;
					formFace = false;
				}
				else {
					for(int i = 0; i < selectedPoints.size(); i++) {
						g2d.setPaint(Color.white);
						g2d.fillOval(points.get(selectedPoints.get(i)).x - hoverPointSize / 2, points.get(selectedPoints.get(i)).y - hoverPointSize / 2, hoverPointSize, hoverPointSize);
						g2d.setPaint(Color.black);
						g2d.fillOval(points.get(selectedPoints.get(i)).x - pointSize / 2, points.get(selectedPoints.get(i)).y - pointSize / 2, pointSize, pointSize);
					}
					
					selectedPoints.clear();
					faceX = 0; faceY = 0;
					formFace = false;
				}
			}
		}
		if(step == 4) {
			g2d.setPaint(Color.black);
			g2d.setStroke(new BasicStroke(1));
			ArrayList<Integer> arr = new ArrayList<Integer>();
			ArrayList<Integer> in = new ArrayList<Integer>();
			ArrayList<Integer> out = new ArrayList<Integer>();
			for(int i = 0; i < points.size(); i++) {
				g2d.drawLine(0, points.get(i).y, windowW, points.get(i).y);
				if(i < points.size() - 1)
					drawCenteredString(g2d, "L" + (i + 1), new Rectangle(windowW - 50, points.get(i).y - pointSize * 3 / 2, 50, 20), new Font("Sans", Font.BOLD, 13), Color.black);
				Lespede l = new Lespede(points.get(i).y);
				
				if(i == 0)
					arr.addAll(points.get(i).edgesOut);
				else {
					in.clear(); out.clear();
					in.addAll(points.get(i).edgesIn);
					out.addAll(points.get(i).edgesOut);
					
					arr.addAll(arr.indexOf(in.get(0)), out);
					for(int j = 0; j < in.size(); j++)
						arr.remove(in.get(j));
				}
				
				l.edges.addAll(arr);
				lespede.add(l);
			}
			
			g2d.setPaint(Color.white);
			g2d.fillRect(0, 0, windowW, titleSize);
			drawCenteredString(g2d, "Apasa click pentru a crea punctul M", new Rectangle(0, 0, windowW, titleSize), new Font("Sans", Font.BOLD, 21), Color.black);
			step = 5;
		}
		if(step == 5) {
			g2d.setPaint(Color.green);
			g2d.fillOval(xM - pointSize / 2, yM - pointSize / 2, pointSize, pointSize);
			locatePoint(g2d, xM, yM);
		}
	}
	
	Point isMouseOnPoint(int x, int y) {
		for(int i = 0; i < points.size(); i++) {
			if(x >= points.get(i).x - pointSize && x <= points.get(i).x + pointSize && y >= points.get(i).y - pointSize && y <= points.get(i).y + pointSize)
				return points.get(i);
		}
		return null;
	}
	
	int isMouseOnPoint(int x, int y, int d) {
		for(int i = 0; i < points.size(); i++) {
			if(x >= points.get(i).x - pointSize && x <= points.get(i).x + pointSize && y >= points.get(i).y - pointSize && y <= points.get(i).y + pointSize)
				return i;
		}
		return -1;
	}
	
	boolean isPointInArea(int x, int y, int radius) {
		for(int i = 0; i < points.size(); i++) {
			if(x >= points.get(i).x - radius && x <= points.get(i).x + radius && y >= points.get(i).y - radius && y <= points.get(i).y + radius)
				return true;
		}
		return false;
	}
	
	int distanceBetweenPoints(Point p1, Point p2) {
		return (int)Math.sqrt((int)Math.abs((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y)));
	}
	
	void drawTriangle(Graphics2D g2d, Point p1, Point p2) {
		int x = p2.x - p1.x;
		int y = p2.y - p1.y;
		
		int d = -(int) Math.toDegrees(Math.atan2(y, x));
		if(d < 0) d = 360 + d;
		
		Triangle t = new Triangle((p1.x + p2.x) / 2, (p1.y + p2.y) / 2, d);
		arrows.add(t);
		AffineTransform trans = new AffineTransform();
		trans.translate((p1.x + p2.x) / 2 - (float)(arrowSize / 2) * Math.sin(Math.toRadians(d)), (p1.y + p2.y) / 2 - (float)(arrowSize / 2) * Math.cos(Math.toRadians(d)));
		trans.rotate(Math.toRadians(360 - d));
		g2d.drawImage(arrowImg, trans, this);
	}
	
	ArrayList<Integer> getPointEdges(int p) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		
		for(Edge e: edges) {
			if(e.startPoint == p || e.endPoint == p) {
				a.add(e.startPoint); a.add(e.endPoint);
			}
		}
		
		return a;
	}
	
	boolean edgeExists(int p1, int p2) {
		if(p1 == p2) return false;
		for(int i = 0; i < edges.size(); i++)
			if((edges.get(i).startPoint == p1 && edges.get(i).endPoint == p2) || (edges.get(i).startPoint == p2 && edges.get(i).endPoint == p1))
				return true;
		return false;
	}
	
	int edgeExists(int p1, int p2, int d) {
		if(p1 == p2) return -1;
		for(int i = 0; i < edges.size(); i++)
			if((edges.get(i).startPoint == p1 && edges.get(i).endPoint == p2) || (edges.get(i).startPoint == p2 && edges.get(i).endPoint == p1))
				return i;
		return -1;
	}
	
	void formFaces() {
		if(valid)
			step = 2;
	}
	
	boolean faceExists(ArrayList<Integer> arr) {
		for(int i = 0; i < faces.size(); i++) {
			boolean a = true;
			for(int j = 0; j < arr.size(); j++)
				if(!faces.get(i).points.contains(arr.get(j)))
					a = false;
			if(a) return true;
		}
		return false;
	}
	
	void sortVertices() {
		if(valid) {
			step = 2;
			repaint();
		}
	}
	
	void changeEdge(int startPoint, int endPoint) {
		for(int i = 0; i < edges.size(); i++) {
			if(edges.get(i).startPoint == startPoint && edges.get(i).endPoint == endPoint) {
				int a = edges.get(i).startPoint;
				edges.get(i).startPoint = edges.get(i).endPoint;
				edges.get(i).endPoint = a;
			}
		}
	}
	
	void sortVertices(Graphics2D g2d) {
		g2d.setPaint(Color.white);
		g2d.fillRect(0, 100, windowW, windowH);
		boolean ok = false;
		do {
			ok = true;
			for(int i = 0; i < points.size() - 1; i++) {
				if(points.get(i).y < points.get(i + 1).y) {
					Point p = points.get(i);
					points.set(i, points.get(i + 1));
					points.set(i + 1, p);
					
					for(int j = 0; j < edges.size(); j++) {
						if(edges.get(j).startPoint == i) edges.get(j).startPoint = i + 1;
						else if(edges.get(j).startPoint == i + 1) edges.get(j).startPoint = i;
						if(edges.get(j).endPoint == i) edges.get(j).endPoint = i + 1; 
						else if(edges.get(j).endPoint == i + 1) edges.get(j).endPoint = i;
					}
					
					for(int j = 0; j < dcel.size(); j++) {
						if(dcel.get(j).v1 == i) dcel.get(j).v1 = i + 1;
						else if(dcel.get(j).v1 == i + 1) dcel.get(j).v1 = i;
						if(dcel.get(j).v2 == i) dcel.get(j).v2 = i + 1;
						else if(dcel.get(j).v2 == i + 1) dcel.get(j).v2 = i;
					}
					
					ok = false;
				}
			}
		}
		while(!ok);
		for(int i = 0; i < edges.size(); i++) {
			if(points.get(edges.get(i).startPoint).y < points.get(edges.get(i).endPoint).y)
				edges.set(i, new Edge(edges.get(i).endPoint, edges.get(i).startPoint));
			
			int x = points.get(edges.get(i).endPoint).x - points.get(edges.get(i).startPoint).x;
			int y = points.get(edges.get(i).endPoint).y - points.get(edges.get(i).startPoint).y;
			
			edges.get(i).startAngle = -(int) Math.toDegrees(Math.atan2(y, x));
			if(edges.get(i).startAngle < 0) edges.get(i).startAngle = 360 + edges.get(i).startAngle;
			
			edges.get(i).endAngle = edges.get(i).startAngle + 180;
		}
		
		for(int i = 0; i < dcel.size(); i++) {
			if(points.get(dcel.get(i).v1).y < points.get(dcel.get(i).v2).y)
				dcel.set(i, new DCEL(dcel.get(i).e, dcel.get(i).v2, dcel.get(i).v1));
			
			int x = points.get(dcel.get(i).v2).x - points.get(dcel.get(i).v1).x;
			int y = points.get(dcel.get(i).v2).y - points.get(dcel.get(i).v1).y;;
			
			dcel.get(i).startAngle = -(int) Math.toDegrees(Math.atan2(y, x));
			if(dcel.get(i).startAngle < 0) dcel.get(i).startAngle = 360 + dcel.get(i).startAngle;
			
			dcel.get(i).endAngle = dcel.get(i).startAngle + 180;
		}
		
		ArrayList<Integer> in = new ArrayList<Integer>();
		ArrayList<Integer> out = new ArrayList<Integer>();
		for(int i = 0; i < points.size(); i++) {
			in.clear(); out.clear();
			for(int j = 0; j < dcel.size(); j++) {
				if(dcel.get(j).v2 == i) {
					in.add(dcel.get(j).e);
				}
				else if(dcel.get(j).v1 == i)
					out.add(dcel.get(j).e);
			}
			
			do {
				ok = true;
				for(int k = 0; k < in.size() - 1; k++) {
					if(dcel.get(in.get(k) - 1).startAngle > dcel.get(in.get(k + 1) - 1).startAngle) {
						int a = in.get(k);
						in.set(k, in.get(k + 1));
						in.set(k + 1, a);
						ok = false;
					}
				}
			} while(!ok);
			
			do {
				ok = true;
				for(int k = 0; k < out.size() - 1; k++) {
					if(dcel.get(out.get(k) - 1).endAngle < dcel.get(out.get(k + 1) - 1).endAngle) {
						int a = out.get(k);
						out.set(k, out.get(k + 1));
						out.set(k + 1, a);
						ok = false;
					}
				}
			} while(!ok);
			
			points.get(i).edgesIn.addAll(in);
			points.get(i).edgesOut.addAll(out);
		}
		
		g2d.setPaint(Color.black);
		g2d.setStroke(new BasicStroke(1));
		for(int i = 0; i < points.size(); i++) {
			g2d.fillOval(points.get(i).x - pointSize / 2, points.get(i).y - pointSize / 2, pointSize, pointSize);
			drawCenteredString(g2d, "v" + (i + 1), new Rectangle(points.get(i).x - pointSize / 2, points.get(i).y - pointSize * 2, 20, 20), new Font("Sans", Font.BOLD, 13), Color.black);
		}
		
		g2d.setStroke(new BasicStroke(2));
		for(int i = 0; i < edges.size(); i++) {			
			g2d.drawLine(points.get(edges.get(i).startPoint).x, points.get(edges.get(i).startPoint).y, points.get(edges.get(i).endPoint).x, points.get(edges.get(i).endPoint).y);
			drawTriangle(g2d, new Point(points.get(edges.get(i).startPoint).x, points.get(edges.get(i).startPoint).y), new Point(points.get(edges.get(i).endPoint).x, points.get(edges.get(i).endPoint).y));
			drawCenteredString(g2d, (i + 1) + "", new Rectangle((points.get(edges.get(i).startPoint).x + points.get(edges.get(i).endPoint).x) / 2, (points.get(edges.get(i).startPoint).y + points.get(edges.get(i).endPoint).y) / 2 - 15, 30, 30), new Font("Sans", Font.BOLD, 13), Color.black);
		}
	}
	
	void locatePoint(Graphics2D g2d, int x, int y) {
		int l = 0, e1 = 0, e2 = 0, f = 0;
		for(int i = 0; i < lespede.size() - 1; i++) {
			if(y <= lespede.get(i).y && y >= lespede.get(i + 1).y) {
				l = (i + 1);
				break;
			}
		}
		Polygon p = new Polygon();
		
		if(l != 0) {
			for(int i = 0; i < lespede.get(l - 1).edges.size() - 1; i++) {
				int startX1 = points.get(dcel.get(lespede.get(l - 1).edges.get(i) - 1).v1).x;
				int startY1 = points.get(dcel.get(lespede.get(l - 1).edges.get(i) - 1).v1).y;
				int startX2 = points.get(dcel.get(lespede.get(l - 1).edges.get(i + 1) - 1).v1).x;
				int startY2 = points.get(dcel.get(lespede.get(l - 1).edges.get(i + 1) - 1).v1).y;
				
				int endX1 = points.get(dcel.get(lespede.get(l - 1).edges.get(i) - 1).v2).x;
				int endY1 = points.get(dcel.get(lespede.get(l - 1).edges.get(i) - 1).v2).y;
				int endX2 = points.get(dcel.get(lespede.get(l - 1).edges.get(i + 1) - 1).v2).x;
				int endY2 = points.get(dcel.get(lespede.get(l - 1).edges.get(i + 1) - 1).v2).y;
				
				p.reset();
				if(startX1 == startX2 && startY1 == startY2)
					p.addPoint(startX1, startY1);
				else {
					p.addPoint(startX1, startY1);
					p.addPoint(startX2, startY2);
				}
				
				if(endX1 == endX2 && endY1 == endY2)
					p.addPoint(endX1, endY1);
				else {
					p.addPoint(endX1, endY1);
					p.addPoint(endX2, endY2);
				}
				
				if(p.contains(new java.awt.Point(xM, yM))) {
					e1 = lespede.get(l - 1).edges.get(i); e2 = lespede.get(l - 1).edges.get(i + 1);
				}
			}
		}
		
		for(int i = 0; i < faces.size(); i++) {
			p.reset();
			for(int j = 0; j < faces.get(i).points.size(); j++)
				p.addPoint(points.get(faces.get(i).points.get(j)).x, points.get(faces.get(i).points.get(j)).y);
			if(p.contains(new java.awt.Point(xM, yM))) {
				f = i + 2;
				break;
			}
		}
		
		String msg = "";
		if(l != 0) {
			if(f != 0)
				msg = "Punctul M se afla in lespedea " + l + ", intre laturile " + e1 + " si " + e2 + ", in fata " + f;
			else
				msg = "Punctul M se afla in lespedea " + l + ", in exteriorul structurii";
		}
		else
			msg = "Punctul M se afla in exteriorul oricarei lespezi";
		
		g2d.setPaint(Color.white);
		g2d.fillRect(0, 0, windowW, 100);
		drawCenteredString(g2d, msg, new Rectangle(0, 0, windowW, 100), new Font("Sans", Font.BOLD, 21), Color.black);
	}
	
	void start() {
		step = 4;
		repaint();
	}
	
	int determinant(Point A, Point B, Point C) {
		return A.x * B.y + B.x * C.y + C.x * A.y - B.y * C.x - C.y * A.x - A.y * B.x;
	}
	
	void drawCenteredString(Graphics2D g2d, String message, Rectangle r, Font font, Color color) {
		FontMetrics metrics = g2d.getFontMetrics(font);
		
		int x = r.x + (r.width - metrics.stringWidth(message)) / 2;
		int y = r.y + (r.height - metrics.getHeight()) / 2;
		
		g2d.setFont(font);
		g2d.setPaint(color);
		g2d.drawString(message, x, y);
	}

	void print(String message) {
		System.out.println(message);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(step == 0 || step == 1) {
			if(step == 0) step = 1;
			if(sPoint[0] == null) {
				sPoint[0] = isMouseOnPoint(MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y);
				startPoint = isMouseOnPoint(MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y, 0);
				if(sPoint[0] == null) {
					if(!isPointInArea(e.getX(), e.getY(), 36)) {
						points.add(new Point(e.getX(), e.getY()));
						repaint();
					}
				}
			}
			else {
				sPoint[1] = isMouseOnPoint(MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y);
				endPoint = isMouseOnPoint(MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y, 0);
				if(sPoint[1] != null) {
					if(sPoint[0] != sPoint[1] && !edgeExists(startPoint, endPoint)) {
						drawLine = true;
						repaint();
					}
				}
				else {
					hPoint = sPoint[0];
					sPoint[0] = null;
				}
			}
		}
		if(step == 3) {
			int p = isMouseOnPoint(MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x, MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y, 0);
			if(p != -1) {
				if(selectedPoints.contains(p))
					formFace = true;
				else {
					faceX += points.get(p).x; faceY += points.get(p).y;
					selectedPoints.add(p);
				}
				repaint();
			}
		}
		if(step == 5) {
			xM = MouseInfo.getPointerInfo().getLocation().x - getLocationOnScreen().x;
			yM = MouseInfo.getPointerInfo().getLocation().y - getLocationOnScreen().y;
			repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
