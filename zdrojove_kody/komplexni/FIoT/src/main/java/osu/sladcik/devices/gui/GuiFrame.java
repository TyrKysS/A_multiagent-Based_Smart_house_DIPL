package osu.sladcik.devices.gui;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;

public class GuiFrame extends JFrame {


    private JPanel guiPanel;
    private JLabel lightLabel;
    private JLabel fireLabel;
    private JLabel sensorLabel;
    private int lightState = 0;
    private JButton lightBtn;
    private static boolean sensorState = false;
    private static boolean fireState = false;
    private static boolean notifyAgent = false;
    private static String msg;
    private static int output;

    GuiFrame() {
        setContentPane(guiPanel);
        setTitle("Gui zařízení");
        setSize(450,300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        updateGui();

        lightBtn.addActionListener(e -> {
            if (getLightState() == 0){
                setLightState(1);
                lightBtn.setText("Zhasnout osvětlení");
            } else {
                setLightState(0);
                lightBtn.setText("Rozsvítit osvětlení");
            }
            setMsg("lightStatus;"+getLightState());
            setNotifyAgent(true);
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
                if (isSensorState()){
                    sensorLabel.setText("Detekován pohyb");
                    setSensorState(false);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if (isFireState()) {
                    fireLabel.setText("Požár je aktivní");
                    setFireState(false);
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

    public int getLightState() {
        return lightState;
    }

    public void setLightState(int lightState) {
        this.lightState = lightState;
    }

    public static int getOutput() {
        return output;
    }

    public static void setOutput(int output) {
        GuiFrame.output = output;
    }

    public static boolean isNotifyAgent() {
        return notifyAgent;
    }

    public static void setNotifyAgent(boolean notifyAgent) {
        GuiFrame.notifyAgent = notifyAgent;
    }

    public static String getMsg() {
        return msg;
    }

    public static void setMsg(String msg) {
        GuiFrame.msg = msg;
    }

    public static boolean isSensorState() {
        return sensorState;
    }

    public static void setSensorState(boolean sensorState) {
        GuiFrame.sensorState = sensorState;
    }

    public static boolean isFireState() {
        return fireState;
    }

    public static void setFireState(boolean fireState) {
        GuiFrame.fireState = fireState;
    }
}
