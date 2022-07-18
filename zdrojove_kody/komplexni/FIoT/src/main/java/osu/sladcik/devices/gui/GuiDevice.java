package osu.sladcik.devices.gui;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;


public class GuiDevice extends Agent {
    private String targetAgentName;
    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent(getLocalName());
                msg.addReceiver(new AID("godAgent", AID.ISLOCALNAME));
                send(msg);
                System.out.println("msg was send "+ msg.getContent());
            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                recieveFromAgent();
                if (GuiFrame.notifyAgent){
                    notifyAgent(GuiFrame.msg);
                    GuiFrame.notifyAgent = false;
                }
            }
        });
    }
    private void notifyAgent(String message) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(getLocalName()+";"+message);
        msg.addReceiver(new AID(targetAgentName, AID.ISLOCALNAME));
        send(msg);
        System.out.println("message was send");
    }

    private void recieveFromAgent() {
        ACLMessage msg = receive();
        if (msg != null){
            if (msg.getContent().contains("accept")){
                System.out.println("acceptMsg "+msg.getContent());
                String[] acceptMsg = msg.getContent().split(";");
                GuiFrame.output = Integer.parseInt(acceptMsg[2]);
            } else if (msg.getContent().contains("fire") || msg.getContent().contains("motion")){
                String[] message = msg.getContent().split(";");
                switch (message[1]){
                    case "fire":
                        GuiFrame.fireState = true;
                        break;
                    case "motion":
                        GuiFrame.sensorState = true;
                        break;
                }
            } else {
                targetAgentName = msg.getContent();
                System.out.println("msg "+msg.getContent());
            }
        }
    }
}
