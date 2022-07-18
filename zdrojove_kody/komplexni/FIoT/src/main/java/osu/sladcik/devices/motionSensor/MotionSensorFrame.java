package osu.sladcik.devices.motionSensor;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.*;

public class MotionSensorFrame extends JFrame {
    public static boolean notifyAgent = false;
    public static String msg;
    public static int output;
    private JButton btn;
    private JPanel panel1;
    private JLabel outputLabel;
    public int state = 0;
    MotionSensorFrame(){
        setContentPane(panel1);
        setTitle("Pohybový Agent");
        setSize(200, 100);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        outputLabel.setOpaque(true);
        updateGui();

        btn.addActionListener(e -> {
            if (state == 1){
                state = 0;
                MotionSensorDevice.sendMsg = true;

                btn.setText("Detekovat pohyb");
            } else {
                state = 1;
                MotionSensorDevice.sendMsg = true;
                btn.setText("Nedetekovat pohyb");
            }
            msg = ";"+state;
            notifyAgent = true;
            System.out.println(state);
        });
    }

    private void updateGui(){
        Thread thread = new Thread(() -> {
            while (true){
                if (output == 1)
                    outputLabel.setBackground(Color.red);
                else
                    outputLabel.setBackground(Color.white);
            }

        });
        thread.start();
    }

    public static void main(String[] args) {
        MotionSensorFrame sensor = new MotionSensorFrame();

        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.CONTAINER_NAME, "MOTION");
        ContainerController cc = rt.createAgentContainer(p);
        AgentController ac;

        try {
            ac = cc.createNewAgent("Motion", "osu.sladcik.devices.motionSensor.MotionSensorDevice", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
