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
    private int counter = 0;
    private static HashMap<Integer, Color> lightColors;
    public static int lightStatus = 0;
    public static int isFire = 0;
    public static boolean outputIsOn = false;

    public static int output;
    public static boolean notifyAgent = false;
    public static String msg;

    LightFrame(){
        setContentPane(panel1);
        setTitle("Light Agent");
        setSize(450,100);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        updateGui();
        colorLabel.setOpaque(true);
        colorLabel.setBackground(Color.BLACK);
        lightState.setText("false");


        changeColorBtn.addActionListener(e -> {
            counter++;
            if (counter == 5)
                counter = 1;
            msg = "color;"+counter;
            notifyAgent = true;
            //lightState.setText(String.valueOf(counter));
        });

        switchBtn.addActionListener(e -> {
            if (!(lightState.getText().isEmpty() || lightState.getText().isBlank())){
                lightState.setText("false");
                if (lightStatus == 1){
                    lightStatus = 0;
                    counter = 0;
                    switchBtn.setText("Zapnout");
                    msg = "lightStatus;0";
                } else {
                    colorLabel.setBackground(Color.YELLOW);
                    lightStatus = 1;
                    counter = 1;
                    switchBtn.setText("Vypnout");
                    msg = "lightStatus;1";
                }
                notifyAgent = true;
                lightState.setText(String.valueOf(lightStatus));
            }
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
                switch (output){
                    case 0:
                        isFire = 0;
                        switchBtn.setText("Zapnout");
                        outputIsOn = false;
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        isFire = 0;
                        switchBtn.setText("Vypnout");
                        outputIsOn = true;
                        break;
                }
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
}
