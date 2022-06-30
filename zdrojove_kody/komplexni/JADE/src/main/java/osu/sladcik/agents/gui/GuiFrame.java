package osu.sladcik.agents.gui;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GuiFrame extends JFrame {
    private JPanel guiPanel;
    private JLabel timerLabel;
    private JSlider timeEventSlider;
    private JButton setTimeEventBtn;
    private JLabel timeEventSliderText;
    private JButton lightBtn;
    private JLabel lightLabel;
    private JLabel eventlabel;
    private JLabel timerDescription;
    private JCheckBox checkLight;
    private JCheckBox checkFire;
    public static boolean confirmLight = false;
    public static boolean lighter = false;
    public static boolean isBtnPressed = false;
    public static String actualTime;
    public static boolean isEvent = false;
    public static String eventTime = "";
    public static List<String> targetAgents;

    GuiFrame() {
        setContentPane(guiPanel);
        setTitle("Gui Agent");
        setSize(450,300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        timeEventSlider.setMinimum(1);
        timeEventSlider.setMaximum(1440);
        updateGui();

        lightBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBtnPressed = true;
                if (lighter)
                    lighter = false;
                else
                    lighter = true;

                if (confirmLight)
                    lightBtn.setText("Rozsvítit světlo");
                else
                    lightBtn.setText("Zhasnout světlo");
            }
        });

        checkLight.addActionListener(e -> {

        });

        checkFire.addActionListener(e -> {

        });
        setTimeEventBtn.addActionListener(e -> {
            if (!(timeEventSliderText.getText().isEmpty() || timeEventSliderText.getText().isBlank())){
                targetAgents = new ArrayList<>();
                if (checkLight.isSelected())
                    targetAgents.add("light");
                if (checkFire.isSelected())
                    targetAgents.add("fire");
                System.out.println(timeEventSliderText.getText());
                eventTime = timeEventSliderText.getText();
                isEvent = true;
            }
        });
    }

    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.CONTAINER_NAME, "GUI");
        ContainerController cc = rt.createAgentContainer(p);
        AgentController ac;

        try {
            ac = cc.createNewAgent("gui", "osu.sladcik.agents.gui.GuiAgent", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        GuiFrame frame = new GuiFrame();

    }
    private void updateGui() {
        Thread thread = new Thread(() -> {
            while (true){
                timerLabel.setText(actualTime);
                int t = timeEventSlider.getValue();
                int hours = t / 60;
                int minutes = t % 60;
                timeEventSliderText.setText(hours+":"+minutes);
            }
        });
        thread.start();
    }
}
