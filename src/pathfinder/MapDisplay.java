package pathfinder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays a Map in a simple Jframe
 */
public class MapDisplay extends JPanel {

    // map to be displayed
    private Map map;

    public MapDisplay(Map map) {
        setPreferredSize(new Dimension(200, 200));
        this.map = map;
//        ActionListener repaint = new ActionListener() {
//            public void actionPerformed(ActionEvent ae) {
//                generator.update();
//                repaint();
//            }
//        };
//        Timer timer = new Timer(20, repaint);
//        timer.start();
    }

    public void paintComponent(Graphics g) {
        map.draw(g);
    }
}
