package osu.sladcik.agents.gui;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import osu.sladcik.Messages;

import static osu.sladcik.agents.gui.MainFrame.*;

public class GuiAgent extends Agent {
    public static boolean isChangeStatusWindow() {
        return changeStatusWindow;
    }
    public static void setChangeStatusWindow(boolean changeStatusWindow) {
        GuiAgent.changeStatusWindow = changeStatusWindow;
    }
    public static boolean isChangeStatusRadiator() {
        return changeStatusRadiator;
    }
    public static void setChangeStatusRadiator(boolean changeStatusRadiator) {
        GuiAgent.changeStatusRadiator = changeStatusRadiator;
    }
    private static boolean changeStatusRadiator;
    private static boolean changeStatusWindow;
    private static String reciever;
    @Override
    protected void setup() {
        Messages.agentIsRunning(getLocalName());
        addBehaviour(new CyclicBehaviour() { //cyklus neustále opakující se
            @Override
            public void action() {
                if (MainFrame.isPressedWindowBtn())
                    changeWindowStatus(); // Pokud se stisklo tlačítko okna, změň stav okna
                MainFrame.setPressedWindowBtn(false);
                if (MainFrame.isIsPressedRadiatorBtn())
                    changeRadiatorStatus(); // Pokud se stisklo tlačítko topení, změň stav topení
                MainFrame.setIsPressedRadiatorBtn(false);

                // čtení zpráv od ostatních agentů
                ACLMessage msg = receive();
                if (msg != null){
                    /*
                     zpráva obsahuje hodnotu a příjemce, středníkem je rozdělena.
                     vytváří se pole, kde nultá pozice je zpráva a první pozice je jméno odesilatele
                     na základě příjemce se upravují jednotlivé WindowLabely v GUI
                      */
                    String[] message = msg.getContent().split(";");
                    switch (message[1]){
                        case "temperature":
                            Messages.confirmRecieveMessage(getLocalName(), message[1], message[0]);
                            setTemp(message[0]);
                            break;
                        case "radiator":
                            Messages.confirmRecieveMessage(getLocalName(), message[1], message[0]);
                            setStatusRadiator(message[0]);
                            break;
                        case "window":
                            Messages.confirmRecieveMessage(getLocalName(), message[1], message[0]);
                            setStatusWindow(message[0]);
                            break;
                    }
                }
            }
        });
    }
    // manuální odesílání stavu topení závislé na uživateli
    private void changeRadiatorStatus() {
        reciever = "radiator";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        if (changeStatusRadiator){
            msg.setContent(100+";"+getLocalName());
        } else {
            msg.setContent(200+";"+getLocalName());
        }
        msg.addReceiver(new AID(reciever, AID.ISLOCALNAME));
        send(msg);
        Messages.confirmSendMessage(getLocalName(),reciever, msg.getContent());
    }
    // manuální odesílání stavu okna závislé na uživateli
    private void changeWindowStatus(){
        reciever = "window";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        if (changeStatusWindow){
            msg.setContent(100+";"+getLocalName());
        } else {
            msg.setContent(200+";"+getLocalName());
        }
        msg.addReceiver(new AID(reciever, AID.ISLOCALNAME));
        send(msg);
        Messages.confirmSendMessage(getLocalName(),reciever, msg.getContent());
    }
}
