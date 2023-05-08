import java.util.ArrayList;

public class Point {
	int x, y, degree;
	
	ArrayList<Integer> edgesIn = new ArrayList<Integer>();
	ArrayList<Integer> edgesOut = new ArrayList<Integer>();
	
	Point(int x, int y) {
		this.x = x; this.y = y; degree = 0;
	}
	
	void print(int a) {
		a++;
		System.out.print("(" + a + ") In: ");
		for(int i: edgesIn)
			System.out.print(i + " ");
		System.out.print("(" + a + ") Out: ");
		for(int i: edgesOut)
			System.out.print(i + " ");
		System.out.println();
	}
}
