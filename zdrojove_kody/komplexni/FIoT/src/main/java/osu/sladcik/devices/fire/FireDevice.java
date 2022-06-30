package osu.sladcik.devices.fire;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;


public class FireDevice extends Agent {
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
                System.out.println("msg was sent "+ msg.getContent());
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                recieveFromAgent();
                if (FireFrame.notifyAgent){
                    notifyAgent(FireFrame.msg);
                    FireFrame.notifyAgent = false;
                }
            }
        });
    }
    private void notifyAgent( String message){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(getLocalName()+";"+message);
        msg.addReceiver(new AID(targetAgentName, AID.ISLOCALNAME));
        send(msg);
        System.out.println("message was send");
    }
    private void recieveFromAgent(){
        ACLMessage msg = receive();
        if (msg != null){
            if (msg.getContent().contains("accept")){
                System.out.println("acceptMsg "+msg.getContent());
                String[] acceptMsg = msg.getContent().split(";");
                FireFrame.output = Integer.parseInt(acceptMsg[2]);
            } else {
                targetAgentName = msg.getContent();
                System.out.println("msg "+msg.getContent());
            }
        }
    }
}
