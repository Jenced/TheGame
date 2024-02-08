package gui;

import map.Map;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.Timer;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

public class MainWindow {
    
    static JLabel AtTurn;

    public MainWindow(Map m) {

        // komplette Größe des Bildschirms
        final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
        final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

        JFrame window = new JFrame();
        window.setTitle("TheGame");
        window.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 3
        window.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));// zentriert
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);// fullsize
        window.setUndecorated(true);//entfernt topbar von window (fullscreen)
        window.setFocusable(true);//für KeyListener
        window.setFocusTraversalKeysEnabled(false);

        // Zeichenebenen anelegen
        JLayeredPane layer = new JLayeredPane();
        layer.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // bar vor Spielfeld initialisieren
        Bar b = new Bar(WIDTH, HEIGHT);
        // Element, Ebenenwert (je höher, desto weiter oben)
        layer.add(b, 2000);
        //CloseButton; schließt Bar
        JButton close = b.closeBarButton();
        layer.add(close);
        //OpenButton; öffnet Bar, wenn Bar offen: OpenButton nicht aktiviert und unsichtbar, wenn geschlossen: sichtbar
        JButton open = b.openBarButton();
        open.setVisible(false);
        open.setEnabled(false);
        layer.add(open);
        
        
        // Spielfeld
        Tilemap tM = new Tilemap(WIDTH, HEIGHT, m, b);
        layer.add(tM, 1000);
        
        
        // Actin Listener für open und close Buttons
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                b.setVisible(false);
                b.setEnabled(false);
                close.setVisible(false);
                close.setEnabled(false);
                open.setVisible(true);
                open.setEnabled(true);
                tM.limitCamera();
            }
        });
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                b.setVisible(true);
                b.setEnabled(true);
                open.setVisible(false);
                open.setEnabled(false);
                close.setVisible(true);
                close.setEnabled(true);
                tM.barOpened();
            }
        });
       
        // Anzeige wer am Zug ist    
        AtTurn = b.AtTurn();
        layer.add(AtTurn);
        layer.setLayer(AtTurn, 9999);
        

        // ----------------------------------------------------//
        // Timer für Zeichnen der Map -> Zeichnung jetzt unabhängig von StatBar
        Timer t = new Timer(10, e -> tM.repaint());
        
        
        //MenuBar
        EscapeMenu eM = new EscapeMenu();
        layer.add(eM, 3000);
        layer.setLayer(eM,9999);
        JButton eB = new JButton();
        eB.setSize(100,200);
        eB.setLocation(0,0);
        eB.setText("(ノಠ益ಠ)ノ彡┻━┻");
        eB.setVisible(true);
        eB.setEnabled(true);
        eM.add(eB);
        eB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.dispose();
            }
        });
        layer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESC"), "blub");
        window.addKeyListener(new KeyListener() {
            boolean eMopen = false;
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !eMopen) {
                    t.stop();
                    eM.setVisible(true);
                    eM.setEnabled(true);
                    close.setEnabled(false);
                    open.setEnabled(false);
                    eMopen = true;
                }else if(e.getKeyCode() == KeyEvent.VK_ESCAPE && eMopen){
                    eM.setVisible(false);
                    eM.setEnabled(false);
                    close.setEnabled(true);
                    open.setEnabled(true);
                    eMopen = false;
                    t.restart();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        window.addMouseListener(tM);
        window.addMouseMotionListener(tM);

        layer.setVisible(true);
        window.add(layer);
        // immer unter allen Darstellungselementen
        window.setVisible(true);
        window.pack();

        t.start();
    }
    
}
