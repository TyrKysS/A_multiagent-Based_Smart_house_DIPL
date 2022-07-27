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

public class MainFrame extends JFrame {
    private JPanel mainFrame;
    private JButton windowBtn;
    private JButton radiatorBtn;
    private JLabel radiatorLabel;
    private JLabel windowLabel;
    private JLabel temperature;
    private static String temp;
    private static String statusWindow;
    private static String statusRadiator;

    public static boolean isPressedWindowBtn() {
        return isPressedWindowBtn;
    }
    public static void setPressedWindowBtn(boolean pressed) {
        isPressedWindowBtn = pressed;
    }
    private static boolean isPressedWindowBtn;
    public static boolean isIsPressedRadiatorBtn() {
        return isPressedRadiatorBtn;
    }
    public static void setIsPressedRadiatorBtn(boolean isPressedRadiatorBtn) {
        MainFrame.isPressedRadiatorBtn = isPressedRadiatorBtn;
    }
    private static boolean isPressedRadiatorBtn;

    // Uživatelské okno
    MainFrame() {
        setContentPane(mainFrame);
        setTitle("Gui Agent");
        setSize(450,300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        showRadiatorStatus();
        showWindowStatus();
        showTemp();

        // tlačítko pro okno
        windowBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPressedWindowBtn(true);
                if (isPressedWindowBtn()){
                    if (GuiAgent.isChangeStatusWindow() == false)
                        GuiAgent.setChangeStatusWindow(true);
                    else
                        GuiAgent.setChangeStatusWindow(false);
                }

            }
        });
        // tlačítko pro topení
        radiatorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setIsPressedRadiatorBtn(true);
                if (isIsPressedRadiatorBtn()){
                    if (GuiAgent.isChangeStatusRadiator() == false)
                        GuiAgent.setChangeStatusRadiator(true);
                    else
                        GuiAgent.setChangeStatusRadiator(false);
                }

            }
        });
    }

    // zobrazení stavu topení
    private void showRadiatorStatus() {
        Thread tempThread = new Thread() {
            @Override
            public void run() {
                while (true){
                    radiatorLabel.setText(getStatusRadiator());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        tempThread.start();
    }

    // zobrazení stavu okna
    public void showWindowStatus(){
        Thread tempThread = new Thread() {
            @Override
            public void run() {
                while (true){
                    windowLabel.setText(getStatusWindow());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        tempThread.start();
    }
    // zobrazení aktuální naměřené teploty
    public void showTemp(){
        Thread tempThread = new Thread() {
            @Override
            public void run() {
                while (true){
                    temperature.setText(getTemp()+" °C");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        tempThread.start();
    }
    // spuštění agenta a kontejneru
    public static void main(String[] args) {
        String location = args[0];  // před spuštěním agenta se dodává parametr IP adresy, na které běží Middleware.jar
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, location);
        p.setParameter(Profile.CONTAINER_NAME, "GUI");
        ContainerController cc = rt.createAgentContainer(p);
        AgentController ac;

        try {
            ac = cc.createNewAgent("gui", "osu.sladcik.agents.gui.GuiAgent", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
        MainFrame frame = new MainFrame();
    }

    public static String getTemp() {
        return temp;
    }

    public static void setTemp(String temp) {
        MainFrame.temp = temp;
    }

    public static String getStatusWindow() {
        return statusWindow;
    }

    public static void setStatusWindow(String statusWindow) {
        MainFrame.statusWindow = statusWindow;
    }

    public static String getStatusRadiator() {
        return statusRadiator;
    }

    public static void setStatusRadiator(String statusRadiator) {
        MainFrame.statusRadiator = statusRadiator;
    }
}
