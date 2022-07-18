package osu.sladcik.devices.gui;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class GuiFrame extends JFrame {
    public static boolean notifyAgent = false;
    public static String msg;
    public static int output;
    private JPanel guiPanel;
    private JLabel lightLabel;
    private JLabel fireLabel;
    private JLabel sensorLabel;
    private int lightState = 0;
    private JButton lightBtn;
    public static boolean sensorState = false;
    public static boolean fireState = false;

    GuiFrame() {
        setContentPane(guiPanel);
        setTitle("Gui Agent");
        setSize(450,300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        updateGui();

        lightBtn.addActionListener(e -> {
            if (lightState == 0){
                lightState = 1;
                lightBtn.setText("Zhasnout osvětlení");
            } else {
                lightState = 0;
                lightBtn.setText("Rozsvítit osvětlení");
            }
            msg = "lightStatus;"+lightState;
            notifyAgent = true;
        });
    }

    public static void main(String[] args) {
        GuiFrame frame = new GuiFrame();

        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.CONTAINER_NAME, "GUI");
        ContainerController cc = rt.createAgentContainer(p);
        AgentController ac;

        try {
            ac = cc.createNewAgent("Gui", "osu.sladcik.devices.gui.GuiDevice", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
    private void updateGui() {
        Thread thread = new Thread(() -> {
            while (true){
                if (sensorState){
                    sensorLabel.setText("Detekován pohyb");
                    sensorState = false;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if (fireState) {
                    fireLabel.setText("Požár je aktivní");
                    fireState = false;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    fireLabel.setText("");
                    sensorLabel.setText("");
                }
            }
        });
        thread.start();
    }
}
