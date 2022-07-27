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
    private static boolean fireState = false;
    private static boolean sensorState = false;
    private static boolean confirmLight = false;
    private static boolean lighter = false;
    private static boolean isBtnPressed = false;

    GuiFrame() {
        setContentPane(guiPanel);
        setTitle("Gui Agent");
        setSize(450,300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        updateGui();

        lightBtn.addActionListener(e -> {
            setIsBtnPressed(true);
            if (isLighter())
                setLighter(false);
            else
                setLighter(true);

            if (isConfirmLight())
                lightBtn.setText("Rozsvítit světlo");
            else
                lightBtn.setText("Zhasnout světlo");
        });
    }

    private void updateGui(){
        Thread thread = new Thread(() -> {
            while(true){
                if (isSensorState())
                    sensorLabel.setText("Detekován pohyb");
                else if (isFireState())
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

    public static boolean isFireState() {
        return fireState;
    }

    public static void setFireState(boolean fireState) {
        GuiFrame.fireState = fireState;
    }

    public static boolean isSensorState() {
        return sensorState;
    }

    public static void setSensorState(boolean sensorState) {
        GuiFrame.sensorState = sensorState;
    }

    public static boolean isConfirmLight() {
        return confirmLight;
    }

    public static void setConfirmLight(boolean confirmLight) {
        GuiFrame.confirmLight = confirmLight;
    }

    public static boolean isLighter() {
        return lighter;
    }

    public static void setLighter(boolean lighter) {
        GuiFrame.lighter = lighter;
    }

    public static boolean isIsBtnPressed() {
        return isBtnPressed;
    }

    public static void setIsBtnPressed(boolean isBtnPressed) {
        GuiFrame.isBtnPressed = isBtnPressed;
    }
}
