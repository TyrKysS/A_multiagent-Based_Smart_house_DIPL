package osu.sladcik.devices.fire;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.*;

public class FireFrame extends JFrame {
    private JPanel firePanel;
    private JButton btn;
    private JTextField inputField;
    private JLabel output;
    private static int buzzer;
    private static int ledIndicator;
    private static boolean notifyAgent = false;
    private static String msg;


    FireFrame() {
        setContentPane(firePanel);
        setTitle("Požární zařízení");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(550,70);
        btn.setText("Aktivovat čidlo");
        btn.setText("Nastavit požár");
        output.setOpaque(true);
        updateGui();

        btn.addActionListener(e -> {
            setNotifyAgent(true);
            setMsg(inputField.getText());
        });
    }

    private void updateGui() {
        Thread thread = new Thread(() -> {
            while(true){
                if (getLedIndicator() == 1)
                    output.setBackground(Color.red);
                else
                    output.setBackground(Color.white);
                output.setText(String.valueOf(getBuzzer()));
            }
        });

        thread.start();
    }

    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.CONTAINER_NAME, "FIRE");
        ContainerController cc = rt.createAgentContainer(p);
        AgentController ac;

        try {
            ac = cc.createNewAgent("Fire", "osu.sladcik.devices.fire.FireDevice", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        FireFrame fireFrame = new FireFrame();
    }

    public static int getBuzzer() {
        return buzzer;
    }

    public static void setBuzzer(int buzzer) {
        FireFrame.buzzer = buzzer;
    }

    public static int getLedIndicator() {
        return ledIndicator;
    }

    public static void setLedIndicator(int ledIndicator) {
        FireFrame.ledIndicator = ledIndicator;
    }

    public static boolean isNotifyAgent() {
        return notifyAgent;
    }

    public static void setNotifyAgent(boolean notifyAgent) {
        FireFrame.notifyAgent = notifyAgent;
    }

    public static String getMsg() {
        return msg;
    }

    public static void setMsg(String msg) {
        FireFrame.msg = msg;
    }
}
