public class Triangle {
	int x, y, rot, startEdge, endEdge;
	
	Triangle(int x, int y, int rot) {
		this.x = x; this.y = y; this.rot = rot;//this.startEdge = startEdge; this.endEdge = endEdge;
	}
	
	int[] getTriangle() {
		return new int[] { x, y, rot };
	}
}
