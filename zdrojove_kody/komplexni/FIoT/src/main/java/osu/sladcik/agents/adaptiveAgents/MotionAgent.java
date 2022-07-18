package osu.sladcik.agents.adaptiveAgents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import osu.sladcik.Agents;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MotionAgent extends Agent {

    private AMSAgentDescription[] agents;
    private List<String> listOfAgents;

    @Override
    protected void setup() {
        agents = null;
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null){
                    if (msg.getContent().contains("Motion")){
                        String[] msgArray = msg.getContent().split(";");
                        System.out.println("msgArray");

                        ACLMessage msgBack = new ACLMessage(ACLMessage.AGREE);
                        msgBack.setContent(getLocalName()+";accept;"+msgArray[2]);
                        msgBack.addReceiver(new AID(msgArray[0], AID.ISLOCALNAME));
                        send(msgBack);

                        sendMessage(msgArray[2]);
                    }
                }
            }
        });
    }
    private void sendMessage(String message) {
        listOfAgents = new ArrayList<>();
        findAgents();
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM); //vytvoření instance ACL zprávy za účelem odeslání zprávy ve formátu INFORM
        if (message.equals("1")){
            msg.setContent(getLocalName()+";motion");
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
    }
    private void findAgents(){
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults(new Long(-1));
            agents = AMSService.search(this, new AMSAgentDescription(), c);
        } catch (Exception e){}
    }
}
