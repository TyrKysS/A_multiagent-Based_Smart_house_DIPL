package osu.sladcik.devices.temperature;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import osu.sladcik.resource.Messages;
import osu.sladcik.resource.TypeAgents;

import java.util.Scanner;

public class Device extends Agent {
    private boolean sendInfo = false;
    private boolean connectToAgent = false;
    private static String recieverAgent;

    @Override
    protected void setup() {
        Messages.agentStatus(getLocalName());
        if (!connectToAgent){
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
                if (!message.isEmpty()){
                    Messages.recieveMessage(getLocalName(), message);
                    String[] name = getAgentName(message);
                    recieverAgent = name[0];
                    connectToAgent = true;
                }
                if (connectToAgent){
                    Scanner sc = new Scanner(System.in);
                    System.out.print("Zadejte aktualní teplotu ");
                    double actualTemperature = sc.nextDouble(); // Uživatelem zadaná teplota
                    sendMessageToAgent(recieverAgent, actualTemperature);
                }
            }
        });

    }

    public String recieveMessageFromAgent(){
        String ret = "";
        ACLMessage msg = receive();
        if (msg != null){
            ret = msg.getContent();
        }
        return ret;
    }
    public void sendMessageToAgent(String agent, double actualTemperature) {
        String[] agentName = getAgentName(agent);
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(String.valueOf(actualTemperature));
        msg.addReceiver(new AID(agentName[0], AID.ISLOCALNAME));
        send(msg);
    }
    public void sendInfoToGodAgent(String name){
        String[] splitName = getAgentName(name);
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(Main.NAME+";"+splitName[0]+";"+splitName[1]);
        msg.addReceiver(new AID(String.valueOf(TypeAgents.GOD), AID.ISLOCALNAME));
        send(msg);
    }
    public String[] getAgentName(String name){
        String[] removeJadeName = name.split("/");
        String tmp = removeJadeName[0];
        String[] splitName = tmp.split("@");
        return splitName;
    }

}
