package osu.sladcik.devices.fire;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;

public class FireFrame extends JFrame {


    private JPanel firePanel;
    private JButton alarmStateBtn;
    private JButton alarmStatusBtn;
    private JLabel alarmState;
    private JLabel alarmStatus;
    private JLabel alarmStatusLabel;

    public static int alarmIsOn = 0;
    public static int alarmIsRinging = 0;
    public static boolean eventStart = false;

    public static int output;

    public static boolean notifyAgent = false;
    public static String msg;


    FireFrame() {
        setContentPane(firePanel);
        setTitle("Požární agent");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(340,200);
        alarmState.setText("false");
        alarmStateBtn.setText("Aktivovat čidlo");
        alarmStatus.setText("false");
        alarmStatusBtn.setText("spustit poplach");
        updateGui();

        alarmStatusBtn.addActionListener(e -> {
            switch (alarmIsRinging){
                case 0:
                    alarmIsRinging = 1;
                    alarmStatus.setText("true");
                    alarmStatusBtn.setText("Vypnout poplach");
                    msg = "alarmStatus;1";
                    notifyAgent = true;
                    break;
                case 1:
                    alarmIsRinging = 0;
                    alarmStatus.setText("false");
                    alarmStatusBtn.setText("spustit poplach");
                    msg = "alarmStatus;0";
                    notifyAgent = true;
                    break;
            }
            System.out.println(alarmIsRinging);
        });
        alarmStateBtn.addActionListener(e -> {
            switch (alarmIsOn){
                case 0:
                    alarmIsOn = 1;
                    alarmState.setText("true");
                    alarmStateBtn.setText("Deaktivovat čidlo");
                    msg = "alarmState;1";
                    notifyAgent = true;
                    break;
                case 1:
                    alarmIsOn = 0;
                    alarmState.setText("false");
                    alarmStateBtn.setText("Aktivovat čidlo");
                    msg = "alarmState;0";
                    notifyAgent = true;
                    break;
            }
            System.out.println(alarmIsOn);
        });
    }

    private void updateGui() {
        Thread thread = new Thread(() -> {
            while(true){
                switch (output){
                    case 0:
                        alarmIsRinging = 0;
                        alarmStatus.setText("false");
                        alarmStatusBtn.setText("spustit poplach");
                        alarmStatusBtn.setVisible(false);
                        alarmStatus.setVisible(false);
                        alarmStatusLabel.setVisible(false);
                        break;
                    case 1:
                    case 2:
                        alarmStatusBtn.setVisible(true);
                        alarmStatus.setVisible(true);
                        alarmStatusLabel.setVisible(true);
                        break;
                }

                if (eventStart){
                    output = 1;
                    alarmState.setText("true");
                    alarmStateBtn.setText("Deaktivovat čidlo");
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
