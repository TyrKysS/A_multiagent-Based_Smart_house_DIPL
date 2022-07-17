package osu.sladcik.agents.gui;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import osu.Agents;

import java.util.*;

public class GuiAgent extends Agent {
    private AMSAgentDescription[] agents;
    @Override
    protected void setup() {
        agents = null;

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (GuiFrame.isBtnPressed){
                    changeLightStatus(String.valueOf(GuiFrame.lighter));
                    if (msg != null && (msg.getContent().equals("true") || msg.getContent().equals("false"))){
                        GuiFrame.confirmLight = Boolean.parseBoolean(msg.getContent());
                    }
                    GuiFrame.isBtnPressed = false;
                }
                if (msg != null && (msg.getContent().equals("fire") || msg.getContent().equals("motion"))){
                    System.out.println(msg.getContent());
                    GuiFrame.fireState = msg.getContent().equals("fire");
                    GuiFrame.sensorState = msg.getContent().equals("motion");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void changeLightStatus(String contentMsg){
        ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
        msg.setContent(contentMsg);
        msg.addReceiver(new AID("light", AID.ISLOCALNAME));
        send(msg);
        System.out.println("zpráva byla úspěšně odeslána");
    }
}
