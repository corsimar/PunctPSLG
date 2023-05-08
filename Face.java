import java.util.ArrayList;

public class Face {
	ArrayList<Integer> points = new ArrayList<Integer>();
	int x, y, id;
	
	Face(ArrayList<Integer> points, int x, int y, int id) {
		this.points.addAll(points); this.x = x; this.y = y; this.id = id;
	}
}
