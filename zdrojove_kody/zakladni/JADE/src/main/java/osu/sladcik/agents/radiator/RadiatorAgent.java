package osu.sladcik.agents.radiator;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import osu.sladcik.Messages;

import javax.swing.*;

public class RadiatorAgent extends Agent {
    public boolean isRadiator() {
        return radiator;
    }

    public void setRadiator(boolean radiator) {
        this.radiator = radiator;
    }

    private boolean radiator;
    @Override
    protected void setup() {
        Messages.agentIsRunning(getLocalName());
        addBehaviour(new CyclicBehaviour() { // Nekonečný cyklus
            @Override
            public void action() {
                ACLMessage msg = receive(); //vytvoření instance ACL zprávy za účelem získávání zpráv z venku (mimo agenta)
                if (msg != null){
                    String[] message = msg.getContent().split(";");
                    Messages.getMessage(getLocalName(),message[1], message[0]); //výpis do konzole text obdržené zprávy
                    /*
                     zpráva obsahuje hodnotu a příjemce, středníkem je rozdělena.
                     vytváří se pole, kde nultá pozice je zpráva a první pozice je jméno odesilatele
                     Pokud zpráva obsahuje hodnoty 100 nebo 200 -> agent obržel zprávu od GUI a má za úkol zapnout/vypnout topení
                     Jestliže tak učiní odešle zpět zprávu GUI o tom, v jakém je nyní stavu

                     Pokud na vstup příjde jakákoli jiná hodnota, agent tuto hodnotu bere jako aktuální tpelotu
                     Jestli že je hodnota z intervalo 5 >= X <= 25, zapne se topení, v opačném případě topení se vypíná
                      */

                    if(message[0].equals("100") || message[0].equals("200")){
                        if (message[0].equals("100")){
                            setRadiator(true);
                            sendMessage(isRadiator());
                        } else {
                            setRadiator(false);
                            sendMessage(isRadiator());
                        }
                    } else {
                        double tmpTemp = Double.parseDouble(message[0]);
                        if ((tmpTemp >= 5 && isRadiator() == false) || (tmpTemp <=25 && isRadiator() == false)){
                            setRadiator(true);
                            sendMessage(isRadiator());
                        } else {
                            setRadiator(false);
                            sendMessage(isRadiator());
                        }

                    }
                    // vypíše na konzoli a zároveň zobrazí informační hlášku o změně stavu
                    Messages.showActualStatus(getLocalName(), String.valueOf(isRadiator()));
                    infoBox("Topení je zapnuto "+isRadiator(), "Tepelný Agent");
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
