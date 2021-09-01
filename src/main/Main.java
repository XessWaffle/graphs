package main;

import alg.Algorithm;
import alg.DFS;
import base.Graph;
import graphic.GraphDisplay;
import util.GraphCreator;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {

    private static JFrame frame;
    private static GraphDisplay gd;
    private static Thread updateThread, algThread;
    private static Algorithm alg;

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame();

            initialize();

        });
    }

    private static void initialize() {

        frame.setBounds(10,10, 500,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Graph g = GraphCreator.generateGraphCycle(50, 3, true, true, 1, 1, 3);

        gd = new GraphDisplay(g);
        gd.setBounds(0,0 , 500, 500);
        frame.getContentPane().add(gd);

        frame.addKeyListener(new GraphCommand());

        alg = new DFS(gd);
        alg.setDelay(100);

        updateThread = new Thread(() ->{
            while(true) {
                gd.update();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    return;
                }
                gd.repaint();
            }
        });

        algThread = new Thread(() ->{
            alg.operate();
        });

        updateThread.start();

    }

    static class GraphCommand implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            System.out.println(e.getKeyChar());

            if(e.getKeyChar() == 'r'){

                int vertices = (int)(100 * Math.random()) + 3;
                int degree = (int)(6 * Math.random()) + 1;
                boolean directed = Math.random() > 0.5 ? false : true, forceConnected = Math.random() > 0.5 ? false : true;
                double maxVertexWeight = 1;
                double maxEdgeWeight = 1;
                int cycles = (int)(6 * Math.random());

                System.out.println("Created Graph: (" + vertices + ", " + degree + ", " + directed + ", " + forceConnected + ", " + maxVertexWeight + ", " + maxEdgeWeight + ", " + cycles + ")");

                Graph g = GraphCreator.generateGraphCycle(vertices, degree, directed, forceConnected, maxVertexWeight, maxEdgeWeight, cycles);

                updateThread.interrupt();

                if(algThread.isAlive())
                    algThread.interrupt();

                gd.setGraph(g);
                updateThread = new Thread(() ->{
                    while(true) {
                        gd.update();
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException exception) {
                            return;
                        }
                        gd.repaint();
                    }
                });

                gd.repaint();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                updateThread.start();

            } else if(e.getKeyChar() == 'a'){
                if(updateThread.isAlive())
                    updateThread.interrupt();

                alg.setOperate(gd.getGraph());

                algThread = new Thread(() -> {
                    alg.operate();
                });

                algThread.start();
            }
        }
    }
}
