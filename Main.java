import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Main();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	Main() {
		initialize();
	}
	
	private void initialize() {
		MyFrame frame = new MyFrame();
		frame.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if(frame.panel.step == 1)
						frame.panel.sortVertices();
					else if(frame.panel.step == 3)
						frame.panel.start();
				}
			}
		});
	}
}
