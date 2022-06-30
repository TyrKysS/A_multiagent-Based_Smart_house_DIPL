package osu.sladcik.devices.radiator;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import osu.sladcik.resource.Messages;
import osu.sladcik.resource.TypeAgents;

public class Device extends Agent {
    private boolean connectToAgent = false;
    private boolean setRadiator = false;
    private boolean firstMessage = true;
    private String[] targetAgent;

    @Override
    protected void setup() {
        if (!connectToAgent){
            Messages.agentStatus(getLocalName());
            addBehaviour(new OneShotBehaviour() {
                @Override
                public void action() {
                    sendInfoToGodAgent(getAID().getName());
                }
            });
        }
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                String message = recieveMessageFromAgent();
                if (!message.isEmpty() && firstMessage) {
                    Messages.recieveMessage(getLocalName(), message);
                    targetAgent = getAgentName(message);
                } else if(!message.isEmpty() && message.contains(";")){
                    String[] splitMessage = message.split(";");
                    if (splitMessage[1].equals(targetAgent[0])){
                        setRadiator = Boolean.parseBoolean(splitMessage[0]);
                        Messages.sendMessageStatus(getLocalName(), setRadiator);
                    }
                }
            }
        });
    }

    private String recieveMessageFromAgent() {
        String ret = "";
        ACLMessage msg = receive();
        if (msg != null){
            ret = msg.getContent();
        }
        return ret;
    }

    private void sendInfoToGodAgent(String name) {
        String[] splitName = getAgentName(name);
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(Main.NAME+";"+splitName[0]+";"+splitName[1]);
        msg.addReceiver(new AID(String.valueOf(TypeAgents.GOD), AID.ISLOCALNAME));
        send(msg);
    }

    private String[] getAgentName(String name) {
        String[] removeJadeName = name.split("/");
        String tmp = removeJadeName[0];
        String[] splitName = tmp.split("@");
        return splitName;
    }
}
