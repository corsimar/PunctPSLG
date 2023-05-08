public class DCEL {
	int e, v1, v2, f1, f2, p1, p2, startAngle, endAngle;
	
	DCEL(int e, int v1, int v2) {
		this.e = e; this.v1 = v1; this.v2 = v2;
	}
	
	void print() {
		System.out.println(e + ": " + (v1 + 1) + " " + (v2 + 1) + " " + startAngle + " " + endAngle);
	}
}
