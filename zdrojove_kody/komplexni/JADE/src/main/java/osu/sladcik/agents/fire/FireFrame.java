package osu.sladcik.agents.fire;

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

public class FireFrame extends JFrame {
    private JPanel firePanel;
    private JTextField inputField;
    private JButton btn;
    private JLabel output;

    public static int inputValue = 700;
    public static int outputAlarm = 0;

    private int outputLed;

    // GUI
    FireFrame() {
        setContentPane(firePanel);
        setTitle("Požární agent");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(340,100);
        updateGui();
        output.setOpaque(true);
        output.setBackground(Color.white);

        btn.addActionListener(e -> {
            inputValue = Integer.parseInt(inputField.getText());
        });

    }

    // po stisku tlačítka aktualizace GUI
    private void updateGui() {
        Thread thread = new Thread(() -> {
            int[][] matrix;
            try {
                matrix = ReadFromFile.readMatrixFromFile("fireSettings.txt", 4, 3);
            } catch (FileNotFoundException e) {
                System.out.println("Soubor s nastavením nebyl nalezen, využívá se výchozí nastavení");

                matrix = new int[][] {
                    //            senzor plamene          LED        Bzučák
                    //     vstup_min     vstup_max      výstup      výstup2
                    {     0,            100,            1,       1000    },
                    {   100,            300,            1,          0    },
                    {   300,           1000,            0,          0    }
                };
            }
            while(true){
                for (int[] ints : matrix) {
                    for (int j = 0; j < ints.length; j++) {
                        if (ints[0] <= inputValue && ints[1] > inputValue) {
                            outputLed = ints[2];
                            outputAlarm = ints[3];
                            break;
                        }
                    }
                }

                if (outputLed == 1){
                    output.setBackground(Color.red);
                } else {
                    output.setBackground(Color.white);
                }
                output.setText(String.valueOf(outputAlarm));

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
        FireFrame fireFrame = new FireFrame();
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.CONTAINER_NAME, "FIRE");
        ContainerController cc = rt.createAgentContainer(p);
        AgentController ac;

        try {
            ac = cc.createNewAgent("fire", "osu.sladcik.agents.fire.FireAgent", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

    }
}
