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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

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
    public static boolean gotMessage = false;
    public static String msgContent;
    public static boolean outputIsOn = false;

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
            lightState.setText(String.valueOf(counter));
        });

        switchBtn.addActionListener(e -> {
            if (!(lightState.getText().isEmpty() || lightState.getText().isBlank())){
                lightState.setText("false");
                if (lightStatus == 1){
                    lightStatus = 0;
                    switchBtn.setText("Zapnout");
                } else {
                    colorLabel.setBackground(Color.YELLOW);
                    lightStatus = 1;
                    switchBtn.setText("Vypnout");
                }
                lightState.setText(String.valueOf(lightStatus));
            }
        });
    }
    private void updateGui(){
        Thread thread = new Thread(() -> {
            int[][] matrix = new int[14][4];
            try {
                matrix = ReadFromFile.readMatrixFromFile("lightSettings.txt", 14, 4);
            } catch (FileNotFoundException e) {
                System.out.println("Soubor s nastavením nebyl nalezen, využívá se výchozí nastavení");
                matrix = new int[][]{
                        //lightStatus   color   fire    output
                        {      0,        0,      0,      0      }, //vypnuto
                        {      0,        1,      0,      0      }, //vypnuto
                        {      0,        2,      0,      0      }, //vypnuto
                        {      0,        3,      0,      0      }, //vypnuto
                        {      0,        4,      0,      0      }, //vypnuto

                        {      1,        1,      0,      1      }, // žlutá barva
                        {      1,        2,      0,      2      }, // zelená barva
                        {      1,        3,      0,      3      }, // červená barva
                        {      1,        4,      0,      4      }, // modrá barva

                        {      0,        0,      1,      3      }, // požár (červená barva)
                        {      1,        1,      1,      3      }, // požár (červená barva)
                        {      1,        2,      1,      3      }, // požár (červená barva)
                        {      1,        3,      1,      3      }, // požár (červená barva)
                        {      1,        4,      1,      3      }, // požár (červená barva)
                };
            }
            lightColors = new HashMap<>();
            lightColors.put(0, Color.black);
            lightColors.put(1, Color.yellow);
            lightColors.put(2, Color.green);
            lightColors.put(3, Color.red);
            lightColors.put(4, Color.blue);

            while(true){
                if (gotMessage){
                    switch (msgContent){
                        case "true":
                            lightStatus = 1;
                            counter = 1;
                            isFire = 0;
                            switchBtn.setText("Vypnout");
                            outputIsOn = true;
                            break;
                        case "false":
                            lightStatus = 0;
                            counter = 0;
                            isFire = 0;
                            switchBtn.setText("Zapnout");
                            outputIsOn = false;
                            break;
                    }
                    gotMessage = false;
                }
                for (int[] ints : matrix) {
                    for (int j = 0; j < ints.length; j++) {
                        if (ints[0] == lightStatus && ints[1] == counter && ints[2] == isFire) {
                            for (Integer integer : lightColors.keySet()) {
                                if (integer.equals(ints[3]))
                                    colorLabel.setBackground(lightColors.get(integer));
                            }
                        }
                    }
                }
                isFire = 0;
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
        LightFrame frame = new LightFrame();
    }

}
