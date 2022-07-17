package osu.sladcik.agents.gui;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;

public class GuiFrame extends JFrame {
    private JPanel guiPanel;
    private JButton lightBtn;
    private JLabel lightLabel;
    private JLabel fireLabel;
    private JLabel sensorLabel;
    public static boolean fireState = false;
    public static boolean sensorState = false;
    public static boolean confirmLight = false;
    public static boolean lighter = false;
    public static boolean isBtnPressed = false;

    GuiFrame() {
        setContentPane(guiPanel);
        setTitle("Gui Agent");
        setSize(450,300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        updateGui();

        lightBtn.addActionListener(e -> {
            isBtnPressed = true;
            if (lighter)
                lighter = false;
            else
                lighter = true;

            if (confirmLight)
                lightBtn.setText("Rozsvítit světlo");
            else
                lightBtn.setText("Zhasnout světlo");
        });
    }

    private void updateGui(){
        Thread thread = new Thread(() -> {
            while(true){
                if (sensorState)
                    sensorLabel.setText("Detekován pohyb");
                else if (fireState)
                    fireLabel.setText("Požár je aktivní");
                else {
                    fireLabel.setText("");
                    sensorLabel.setText("");
                }
            }

        });
        thread.start();
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
            ac = cc.createNewAgent("gui", "osu.sladcik.agents.gui.GuiAgent", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }
}
