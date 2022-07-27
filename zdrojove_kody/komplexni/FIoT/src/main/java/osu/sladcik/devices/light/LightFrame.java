package osu.sladcik.devices.light;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class LightFrame extends JFrame {

    private JPanel panel1;
    private JButton switchBtn;
    private JButton changeColorBtn;
    private JLabel lightState;
    private JLabel colorLabel;
    private JButton setLightValue;
    private JTextField lightValue;
    private int colorCounter = 0;
    private int modeCounter = 0;
    private static HashMap<Integer, Color> lightColors;
    private static int lightStatus = 0;
    private static int output;
    private static boolean notifyAgent = false;
    private static String msg;

    LightFrame(){
        setContentPane(panel1);
        setTitle("Světelné zařízení");
        setSize(450,150);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        updateGui();
        colorLabel.setOpaque(true);
        colorLabel.setBackground(Color.BLACK);
        lightState.setText("false");


        changeColorBtn.addActionListener(e -> {
            colorCounter++;
            if (colorCounter == 5)
                colorCounter = 1;
            msg = "lightColor;"+ colorCounter;
            notifyAgent = true;
        });

        switchBtn.addActionListener(e -> {
            modeCounter++;
            if (modeCounter == 5)
                modeCounter = 0;
            msg = "lightStatus;"+modeCounter;
            notifyAgent = true;
            lightState.setText(String.valueOf(lightStatus));
            switch (modeCounter){
                case 0:
                    switchBtn.setText("Zapnout - hlavní žárovka");
                    break;
                case 1:
                    switchBtn.setText("Přepnout režim - LED pásek");
                    break;
                case 2:
                    switchBtn.setText("Přepnout režim - automatický režim");
                    break;
                case 3:
                    switchBtn.setText("Přepnout režim - náhodná barva");
                    break;
                case 4:
                    switchBtn.setText("Vypnout");
                    break;
                case 5:
                    break;
            }
        });

        setLightValue.addActionListener(e -> {
            msg = "lightValue;"+lightValue.getText();
            notifyAgent = true;
        });
    }
    private void updateGui(){
        Thread thread = new Thread(() -> {
            lightColors = new HashMap<>();
            lightColors.put(0, Color.black);
            lightColors.put(1, Color.yellow);
            lightColors.put(2, Color.green);
            lightColors.put(3, Color.red);
            lightColors.put(4, Color.blue);

            while (true){
                lightState.setText(String.valueOf(output));
                for (Integer integer : lightColors.keySet()) {
                    if (integer.equals(output))
                        colorLabel.setBackground(lightColors.get(integer));
                }
            }
        });
        thread.start();
    }
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.CONTAINER_NAME, "LIGHT");
        ContainerController cc = rt.createAgentContainer(p);
        AgentController ac;

        try {
            ac = cc.createNewAgent("Light", "osu.sladcik.devices.light.LightDevice", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
        LightFrame frame = new LightFrame();
    }

    public static int getLightStatus() {
        return lightStatus;
    }

    public static void setLightStatus(int lightStatus) {
        LightFrame.lightStatus = lightStatus;
    }

    public static int getOutput() {
        return output;
    }

    public static void setOutput(int output) {
        LightFrame.output = output;
    }

    public static boolean isNotifyAgent() {
        return notifyAgent;
    }

    public static void setNotifyAgent(boolean notifyAgent) {
        LightFrame.notifyAgent = notifyAgent;
    }

    public static String getMsg() {
        return msg;
    }

    public static void setMsg(String msg) {
        LightFrame.msg = msg;
    }
}
