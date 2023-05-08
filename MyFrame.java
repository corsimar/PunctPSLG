import java.awt.Dimension;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MyFrame extends JFrame {
	
	MyPanel panel;
	
	MyFrame() {
		this.setMinimumSize(new Dimension(1200, 800));
		this.pack();
		panel = new MyPanel();
		panel.setBounds(this.getContentPane().getBounds());
		panel.windowW = this.getContentPane().getBounds().width;
		panel.windowH = this.getContentPane().getBounds().height;
		this.getContentPane().add(panel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
}
