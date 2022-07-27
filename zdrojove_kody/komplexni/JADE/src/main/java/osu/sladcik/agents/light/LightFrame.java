package osu.sladcik.agents.light;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import osu.sladcik.resources.ReadFromFile;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class LightFrame extends JFrame {

    private JPanel panel1;
    private JButton switchBtn;
    private JButton changeColorBtn;
    private JLabel lightState;
    private JLabel colorLabel;
    private JTextField lightValue;
    private JButton setLightValue;
    private int counter = 0;
    private static HashMap<Integer, Color> lightColors;
    private static int lightStatus = 0;
    private static int isFire = 0;
    private static int actualLightValue = 0;
    private static boolean gotMessage = false;
    private static String msgContent;
    private static boolean outputIsOn = false;

    LightFrame(){
        setContentPane(panel1);
        setTitle("Světelný Agent");
        setSize(450,150);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        updateGui();
        colorLabel.setOpaque(true);
        colorLabel.setBackground(Color.BLACK);
        lightState.setText("false");

        setLightValue.addActionListener(e -> {
            setActualLightValue(Integer.parseInt(lightValue.getText()));
        });

        changeColorBtn.addActionListener(e -> {
            counter++;
            if (counter == 5)
                counter = 1;
            //lightState.setText(String.valueOf(counter));
            System.out.println("changeColorBtn "+counter);
        });

        switchBtn.addActionListener(e -> {
            lightStatus++;
            if (getLightStatus() == 5)
                setLightStatus(0);
            lightState.setText(String.valueOf(lightStatus));
            switch (getLightStatus()){
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
    }
    private void updateGui(){
        Thread thread = new Thread(() -> {
            int[][] matrix;
            try {
                matrix = ReadFromFile.readMatrixFromFile("lightSettings.txt", 62, 6);
            } catch (FileNotFoundException e) {
                System.out.println("Soubor s nastavením nebyl nalezen, využívá se výchozí nastavení");

                matrix = new int[][]{
                    //    tlačítka                    senzor                       LED diody + žárovka
                    // režim   barva       vstup2_min        vstup2_max    požár      výstup
                    {    0,       0,            0,            800,         0,        0       },
                    {    0,       1,            0,            800,         0,        0       },
                    {    0,       2,            0,            800,         0,        0       },
                    {    0,       3,            0,            800,         0,        0       },
                    {    1,       0,            0,              5,         0,        1       },
                    {    1,       1,            0,              5,         0,        1       },
                    {    1,       2,            0,              5,         0,        1       },
                    {    1,       3,            0,              5,         0,        1       },
                    {    1,       0,            5,            800,         0,        0       },
                    {    1,       1,            5,            800,         0,        0       },
                    {    1,       2,            5,            800,         0,        0       },
                    {    1,       3,            5,            800,         0,        0       },

                    {    2,       0,            0,             60,         0,        0       },
                    {    2,       1,            0,             60,         0,        1       },
                    {    2,       2,            0,             60,         0,        2       },
                    {    2,       3,            0,             60,         0,        3       },
                    {    2,       4,            0,             60,         0,        4       },
                    {    2,       0,           60,            800,         0,        0       },
                    {    2,       1,           60,            800,         0,        0       },
                    {    2,       2,           60,            800,         0,        0       },
                    {    2,       3,           60,            800,         0,        0       },

                    {    3,       0,            0,              5,         0,        1       },
                    {    3,       1,            0,              5,         0,        1       },
                    {    3,       2,            0,              5,         0,        1       },
                    {    3,       3,            0,              5,         0,        1       },
                    {    3,       0,           10,             20,         0,       75       },
                    {    3,       1,           10,             20,         0,       75       },
                    {    3,       2,           10,             20,         0,       75       },
                    {    3,       3,           10,             20,         0,       75       },
                    {    3,       0,           20,             30,         0,       50       },
                    {    3,       1,           20,             30,         0,       50       },
                    {    3,       2,           20,             30,         0,       50       },
                    {    3,       3,           20,             30,         0,       50       },
                    {    3,       0,           30,             40,         0,       25       },
                    {    3,       1,           30,             40,         0,       25       },
                    {    3,       2,           30,             40,         0,       25       },
                    {    3,       3,           30,             40,         0,       25       },
                    {    3,       0,           40,            800,         0,        0       },

                    {    4,       1,            0,            800,         0,        5       },
                    {    4,       2,            0,            800,         0,        5       },
                    {    4,       3,            0,            800,         0,        5       },
                    {    4,       4,            0,            800,         0,        5       },
                    {    4,       0,            0,            800,         0,        5       },

                    {    5,       0,            0,            800,         0,        0       },

                    {    0,       0,            0,            800,         1,        3       },
                    {    1,       0,            0,            800,         1,        3       },
                    {    1,       1,            0,            800,         1,        3       },
                    {    1,       2,            0,            800,         1,        3       },
                    {    1,       3,            0,            800,         1,        3       },
                    {    1,       4,            0,            800,         1,        3       },
                    {    2,       1,            0,            800,         1,        3       },
                    {    2,       2,            0,            800,         1,        3       },
                    {    2,       3,            0,            800,         1,        3       },
                    {    2,       4,            0,            800,         1,        3       },
                    {    3,       1,            0,            800,         1,        3       },
                    {    3,       2,            0,            800,         1,        3       },
                    {    3,       3,            0,            800,         1,        3       },
                    {    3,       4,            0,            800,         1,        3       },
                    {    4,       1,            0,            800,         1,        3       },
                    {    4,       2,            0,            800,         1,        3       },
                    {    4,       3,            0,            800,         1,        3       },
                    {    4,       4,            0,            800,         1,        3       },
                };

            }
            lightColors = new HashMap<>();
            lightColors.put(0, Color.black);
            lightColors.put(1, Color.yellow);
            lightColors.put(2, Color.green);
            lightColors.put(3, Color.red);
            lightColors.put(4, Color.blue);

            while(true){
                if (isGotMessage()){
                    switch (getMsgContent()){
                        case "true":
                            setLightStatus(1);
                            setIsFire(0);
                            setOutputIsOn(true);
                            counter = 1;
                            switchBtn.setText("Vypnout");
                            break;
                        case "false":
                            setLightStatus(0);
                            setIsFire(0);
                            setOutputIsOn(false);
                            counter = 0;
                            switchBtn.setText("Zapnout");
                            break;
                    }
                    gotMessage = false;
                }
                for (int[] ints : matrix) {
                    for (int j = 0; j < ints.length; j++) {
                        if (ints[0] == getLightStatus() && ints[1] == counter && ints[2] <= getActualLightValue() && ints[3] > getActualLightValue() && ints[4] == getIsFire()) {
                            if (ints[5] == 5){
                                int max = 4;
                                int min = 1;
                                int range = max - min + 1;
                                int result = (int)(Math.random() * range) + min;
                                colorLabel.setBackground(lightColors.get(result));
                            } else {
                                for (Integer integer : lightColors.keySet()) {
                                    if (integer.equals(ints[5]))
                                        colorLabel.setBackground(lightColors.get(integer));
                                }
                            }
                            colorLabel.setText(String.valueOf(ints[5]));
                        }
                    }
                }
                setIsFire(0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    public static void main(String[] args) {
        LightFrame frame = new LightFrame();
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.CONTAINER_NAME, "LIGHT");
        ContainerController cc = rt.createAgentContainer(p);
        AgentController ac;

        try {
            ac = cc.createNewAgent("light", "osu.sladcik.agents.light.LightAgent", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public static int getLightStatus() {
        return lightStatus;
    }

    public static void setLightStatus(int lightStatus) {
        LightFrame.lightStatus = lightStatus;
    }

    public static int getIsFire() {
        return isFire;
    }

    public static void setIsFire(int isFire) {
        LightFrame.isFire = isFire;
    }

    public static int getActualLightValue() {
        return actualLightValue;
    }

    public static void setActualLightValue(int actualLightValue) {
        LightFrame.actualLightValue = actualLightValue;
    }

    public static boolean isGotMessage() {
        return gotMessage;
    }

    public static void setGotMessage(boolean gotMessage) {
        LightFrame.gotMessage = gotMessage;
    }

    public static String getMsgContent() {
        return msgContent;
    }

    public static void setMsgContent(String msgContent) {
        LightFrame.msgContent = msgContent;
    }

    public static boolean isOutputIsOn() {
        return outputIsOn;
    }

    public static void setOutputIsOn(boolean outputIsOn) {
        LightFrame.outputIsOn = outputIsOn;
    }
}
