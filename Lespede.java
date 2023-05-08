import java.util.ArrayList;

public class Lespede {
	int y;
	ArrayList<Integer> edges = new ArrayList<Integer>();
	
	Lespede(int y) {
		this.y = y;
	}
	
	void print() {
		for(int i = 0; i < edges.size(); i++)
			System.out.print(edges.get(i) + " ");
		System.out.println();
	}
}
