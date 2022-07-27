package osu.sladcik.agents.AdaptiveAgents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.lang.acl.ACLMessage;
import osu.sladcik.agents.AdaptiveAgents.GUI.MainFrame;
import osu.sladcik.resource.Messages;
import osu.sladcik.resource.TypeAgents;

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

    @Override
    protected void setup() {
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
                if (msg != null) {
                    /*
                     zpráva obsahuje hodnotu a příjemce, středníkem je rozdělena.
                     vytváří se pole, kde nultá pozice je zpráva a první pozice je jméno odesilatele
                     na základě příjemce se upravují jednotlivé WindowLabely v GUI
                      */
                    String[] message = msg.getContent().split(";");
                    for (TypeAgents agents : TypeAgents.values()) {
                        if (agents.toString().equals(message[1])) {
                            switch (agents) {
                                case TEMPERATURE:
                                    Messages.recieveStatusOfAgents(message[0], message[1]);
                                    MainFrame.temp = message[0];
                                    break;
                                case RADIATOR:
                                    Messages.recieveStatusOfAgents(message[0], message[1]);
                                    MainFrame.statusRadiator = message[0];
                                    break;
                                case WINDOW:
                                    Messages.recieveStatusOfAgents(message[0], message[1]);
                                    MainFrame.statusWindow = message[0];
                                    break;
                            }
                        }
                    }
                }
            }
        });
    }

    // manuální odesílání stavu topení závislé na uživateli
    private void changeRadiatorStatus() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        if (changeStatusRadiator) {
            msg.setContent(String.valueOf(100));
        } else {
            msg.setContent(String.valueOf(200));
        }
        AMSAgentDescription[] AMSagents = null;
        //TODO požádat God agenta o vytvoření observer agenta o kontrolu, jaké agenty jsou aktivní
        msg.addReceiver(new AID(TypeAgents.RADIATOR.toString(), AID.ISLOCALNAME));
        send(msg);
        //Messages.SendMessageStatus(getLocalName(), s, msg.getContent());
    }

    // manuální odesílání stavu okna závislé na uživateli
    private void changeWindowStatus() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        if (changeStatusWindow) {
            msg.setContent(String.valueOf(100));
        } else {
            msg.setContent(String.valueOf(200));
        }
        //TODO požádat God agenta o vytvoření observer agenta o kontrolu, jaké agenty jsou aktivní
        msg.addReceiver(new AID(TypeAgents.WINDOW.toString(), AID.ISLOCALNAME));
        send(msg);
        //Messages.SendMessageStatus(getLocalName(), s, msg.getContent());

    }
}
