package osu.sladcik.agents.motionSensor;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import osu.sladcik.resources.ReadFromFile;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MotionSensor extends JFrame {
    private JButton btn;
    private JPanel panel1;
    private JLabel actualTime;
    public boolean state = false;
    private int actualStatus = 0;
    MotionSensor(){
        setContentPane(panel1);
        setTitle("Čidlo Agent");
        setSize(200, 100);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        updateGui();

        btn.addActionListener(e -> {
            if (state){
                state = false;
                actualStatus = 0;
                btn.setText("Detekovat pohyb");
            } else {
                state = true;
                actualStatus = 1;
                btn.setText("Nedetekovat pohyb");
            }
            System.out.println(state);
        });
    }

    private void updateGui(){
        Thread thread = new Thread(() -> {
           int[][] matrix;
            try {
                matrix = ReadFromFile.readMatrixFromFile("motionSettings.txt", 2,5);
            } catch (FileNotFoundException e) {
                System.out.println("Soubor s nastavením nebyl nalezen, využívá se výchozí nastavení");

                matrix = new int[][]{
                        //      pohyb          začátek       konec
                        // detekovan pohyb      hodina      hodina     odeslat informaci
                        {       0,              7,              11,          0},
                        {       1,              12,              20,          1},
                        {       1,              20,              7,          0}
                };
            }
            while(true){
                GregorianCalendar calendar = new GregorianCalendar();
                int actualHour = calendar.get(Calendar.HOUR);
                int actualMinutes = calendar.get(Calendar.MINUTE);
                actualTime.setText(actualHour+":"+actualMinutes);
                for (int[] ints : matrix) {
                    for (int j = 0; j < ints.length; j++) {
                        if (ints[0] == actualStatus && ints[1] < actualHour && ints[2] >= actualHour) {
                            MotionSensorAgent.sendMsg = ints[3] == 1;
                            break;
                        }
                    }
                }
            }
        });
        thread.start();
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
