package osu.sladcik.agents.motionSensor;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;

public class MotionSensor extends JFrame {
    private JButton btn;
    private JPanel panel1;
    public boolean state = false;
    MotionSensor(){
        setContentPane(panel1);
        setTitle("Pohybový Agent");
        setSize(200, 100);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        btn.addActionListener(e -> {
            if (state){
                state = false;
                MotionSensorAgent.sendMsg = true;
                btn.setText("Detekovat pohyb");
            } else {
                state = true;
                MotionSensorAgent.sendMsg = true;
                btn.setText("Nedetekovat pohyb");
            }
            System.out.println(state);
        });
    }

    public static void main(String[] args) {
        MotionSensor sensor = new MotionSensor();

        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.CONTAINER_NAME, "MOTION");
        ContainerController cc = rt.createAgentContainer(p);
        AgentController ac;

        try {
            ac = cc.createNewAgent("motion", "osu.sladcik.agents.motionSensor.MotionSensorAgent", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
