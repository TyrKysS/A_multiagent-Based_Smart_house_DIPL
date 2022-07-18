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
    public static int ledIndicator;
    private JPanel firePanel;
    private JButton btn;
    private JTextField inputField;
    private JLabel output;
    public static int buzzer;
    public static boolean notifyAgent = false;
    public static String msg;


    FireFrame() {
        setContentPane(firePanel);
        setTitle("Požární agent");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(340,200);
        btn.setText("Aktivovat čidlo");
        btn.setText("Nastavit požár");
        output.setOpaque(true);
        updateGui();

        btn.addActionListener(e -> {
            notifyAgent = true;
            msg = inputField.getText();
        });
    }

    private void updateGui() {
        Thread thread = new Thread(() -> {
            while(true){
                if (ledIndicator == 1)
                    output.setBackground(Color.red);
                else
                    output.setBackground(Color.white);
                output.setText(String.valueOf(buzzer));
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
}
