package osu.sladcik.devices.motionSensor;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static osu.sladcik.devices.motionSensor.MotionSensorDevice.setSendMsg;

public class MotionSensorFrame extends JFrame {
    private JButton btn;
    private JPanel panel1;
    private JLabel outputLabel;
    private JLabel actualTime;
    private int state = 0;
    private static boolean notifyAgent = false;
    private static String msg;
    private static int output;
    MotionSensorFrame(){
        setContentPane(panel1);
        setTitle("Zařízení čidlo");
        setSize(200, 100);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        outputLabel.setOpaque(true);
        updateGui();

        btn.addActionListener(e -> {
            if (getState() == 1){
                setState(0);
                setSendMsg(true);
                btn.setText("Detekovat pohyb");
            } else {
                setState(1);
                setSendMsg(true);
                btn.setText("Nedetekovat pohyb");
            }
            setMsg(";"+getState());
            setNotifyAgent(true);
            System.out.println(getState());
        });
    }

    private void updateGui(){
        Thread thread = new Thread(() -> {
            while (true){
                GregorianCalendar calendar = new GregorianCalendar();
                int actualHour = calendar.get(Calendar.HOUR);
                int actualMinutes = calendar.get(Calendar.MINUTE);
                actualTime.setText(actualHour+":"+actualMinutes);
                if (getOutput() == 1)
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

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }

    public static boolean isNotifyAgent() {
        return notifyAgent;
    }

    public static void setNotifyAgent(boolean notifyAgent) {
        MotionSensorFrame.notifyAgent = notifyAgent;
    }

    public static String getMsg() {
        return msg;
    }

    public static void setMsg(String msg) {
        MotionSensorFrame.msg = msg;
    }

    public static int getOutput() {
        return output;
    }

    public static void setOutput(int output) {
        MotionSensorFrame.output = output;
    }
}
