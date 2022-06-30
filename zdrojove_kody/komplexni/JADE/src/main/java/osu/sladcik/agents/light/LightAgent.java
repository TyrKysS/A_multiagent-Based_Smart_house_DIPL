package osu.sladcik.agents.light;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class LightAgent extends Agent {
    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getContent().equals("fire")){
                    LightFrame.isFire = 1;
                }
                if (msg != null && (msg.getContent().equals("true") || msg.getContent().equals("false"))){
                    System.out.println(msg.getContent());
                    LightFrame.msgContent = msg.getContent();
                    LightFrame.gotMessage = true;
                    ACLMessage sendMsgBack = new ACLMessage(ACLMessage.AGREE);
                    sendMsgBack.setContent(String.valueOf(LightFrame.outputIsOn));
                    sendMsgBack.addReceiver(new AID("gui", AID.ISLOCALNAME));
                    send(sendMsgBack);
                }
            }
        });
    }
}
