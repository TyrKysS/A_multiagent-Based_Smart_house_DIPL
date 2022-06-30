package osu.sladcik.agents.window;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import osu.sladcik.Messages;

import javax.swing.*;

public class WindowAgent extends Agent {
    private boolean window;

    public boolean isWindow() {
        return window;
    }

    public void setWindow(boolean window) {
        this.window = window;
    }

    @Override
    protected void setup() {
        Messages.agentIsRunning(getLocalName()); // kontrolní hláška, zda agent funguje
        addBehaviour(new CyclicBehaviour() { // Nekonečný cyklus
            @Override
            public void action() {
                ACLMessage msg = receive(); //vytvoření instance ACL zprávy za účelem získávání zpráv z venku (mimo agenta)
                if(msg != null){
                    //TODO odstranit pole message
                    String[] message = msg.getContent().split(";");
                    Messages.getMessage(getLocalName(),message[1], message[0]); //výpis do konzole text obdržené zprávy
                     /*
                     zpráva obsahuje hodnotu a příjemce, středníkem je rozdělena.
                     vytváří se pole, kde nultá pozice je zpráva a první pozice je jméno odesilatele
                     Pokud zpráva obsahuje hodnoty 100 nebo 200 -> agent obržel zprávu od GUI a má za úkol otevřít/zavřít okno
                     Jestliže tak učiní odešle zpět zprávu GUI o tom, v jakém je nyní stavu

                     Pokud na vstup příjde jakákoli jiná hodnota, agent tuto hodnotu bere jako teplotní
                     Jestli že je hodnota >= 25, otevře okno, v opačném případě okno zavírá
                      */
                    if(message[0].equals("100") || message[0].equals("200")){
                        if (message[0].equals("100")){
                            setWindow(true);
                            sendMessage(isWindow());
                        } else {
                            setWindow(false);
                            sendMessage(isWindow());
                        }
                    } else {
                        double tmpTemp = Double.parseDouble(message[0]);
                        if (tmpTemp >= 25 && isWindow() == false){
                            setWindow(true);
                            sendMessage(isWindow());
                        } else {
                            setWindow(false);
                            sendMessage(isWindow());
                        }
                    }
                    // vypíše na konzoli a zároveň zobrazí informační hlášku o změně stavu
                    Messages.showActualStatus(getLocalName(), String.valueOf(isWindow()));
                    infoBox("Okno je otevřeno "+isWindow(), "Window Agent");
                }
            }
        });
    }
    // zasílání zpráv o svém aktuálním stavu GUI agentovi, kde dále informuje na konzoli, že zpráva byla úspěšně odeslána
    private void sendMessage(boolean windowStatus) {
        String target = "gui";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(String.valueOf(windowStatus+";"+getAID().getLocalName()));
        msg.addReceiver(new AID(target, AID.ISLOCALNAME));
        send(msg);
        Messages.confirmSendMessage(getLocalName(), target, msg.getContent());
    }

    // okno zobrazující aktuální stav agenta
    private static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
}
