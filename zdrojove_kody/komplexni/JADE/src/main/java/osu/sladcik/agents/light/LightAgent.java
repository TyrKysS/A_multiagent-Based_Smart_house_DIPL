package osu.sladcik.agents.light;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import static osu.sladcik.agents.light.LightFrame.*;

public class LightAgent extends Agent {
    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getContent().equals("fire")){
                    setIsFire(1);
                }
                if (msg != null && (msg.getContent().equals("true") || msg.getContent().equals("false"))){
                    System.out.println(msg.getContent());
                    setMsgContent(msg.getContent());
                    setGotMessage(true);
                    ACLMessage sendMsgBack = new ACLMessage(ACLMessage.AGREE);
                    sendMsgBack.setContent(String.valueOf(isOutputIsOn()));
                    sendMsgBack.addReceiver(new AID("gui", AID.ISLOCALNAME));
                    send(sendMsgBack);
                }
            }
        });
    }
}
