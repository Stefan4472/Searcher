package pathfinder;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

/**
 * Displays a Map in a simple Jframe
 */
public class MapDisplay extends JPanel {

    // map to be displayed
    private Map map;
    // path of nodes to highlight
    private List<LocationNode> path;

    public MapDisplay(Map map) {
        setPreferredSize(new Dimension(200, 200));
        this.map = map;
        path = new Stack<>();
//        ActionListener repaint = new ActionListener() {
//            public void actionPerformed(ActionEvent ae) {
//                generator.update();
//                repaint();
//            }
//        };
//        Timer timer = new Timer(20, repaint);
//        timer.start();
    }

    public void setPath(List<LocationNode> path) {
        this.path = path;
    }

    public void paintComponent(Graphics g) {
        map.drawClip(g, null, path);
    }
}
