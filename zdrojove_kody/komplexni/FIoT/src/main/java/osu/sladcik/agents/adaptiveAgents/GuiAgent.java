package osu.sladcik.agents.adaptiveAgents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import osu.sladcik.Agents;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GuiAgent extends Agent {
    private AMSAgentDescription[] agents;
    private List<String> listOfAgents;
    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println("Agent is running");
            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();

                if (msg != null && msg.getContent().contains("lightStatus")){
                    String[] message = msg.getContent().split(";");

                    ACLMessage confirmMsg = new ACLMessage(ACLMessage.AGREE);
                    confirmMsg.addReceiver(new AID(message[0], AID.ISLOCALNAME));
                    confirmMsg.setContent(getLocalName()+";accept;"+message[2]);
                    send(confirmMsg);
                    sendMessage("lightStatus;"+message[2]);
                }
                if (msg != null && (msg.getContent().contains("fire") || msg.getContent().contains("motion"))){
                    String[] message = msg.getContent().split(";");

                    ACLMessage confirmMsg = new ACLMessage(ACLMessage.AGREE);
                    confirmMsg.addReceiver(new AID("Gui", AID.ISLOCALNAME));
                    confirmMsg.setContent(getLocalName()+";"+message[1]);
                    send(confirmMsg);
                }
            }
        });
    }

    private void sendMessage(String message) {
        listOfAgents = new ArrayList<>();

        findAgents();
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM); //vytvoření instance ACL zprávy za účelem odeslání zprávy ve formátu INFORM
        msg.setContent(getLocalName()+";"+message);
        for (AMSAgentDescription agent : agents) {
            for (Agents typeAgents : Agents.values()) {
                AID agentID = agent.getName();
                if (agentID.getLocalName().equals(typeAgents.toString()) && !agentID.getLocalName().equals(getLocalName())){
                    listOfAgents.add(agentID.getLocalName());
                }
            }
        }
        Set<String> finalListOfAgents = new LinkedHashSet<>(listOfAgents);

        System.out.println("finalListOfAgents");
        for (String agent : finalListOfAgents) {
            msg.addReceiver(new AID(agent, AID.ISLOCALNAME)); // Přidej příjemce
            System.out.println(agent);
        }
        send(msg); //rozešli zprávy všem příjemcům
    }
    private void findAgents(){
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults(new Long(-1));
            agents = AMSService.search(this, new AMSAgentDescription(), c);
        } catch (Exception e){}
    }
}
