package osu.sladcik.agents.temperature;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import osu.sladcik.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TemperatureAgent extends Agent {
    private AMSAgentDescription[] agents;
    @Override
    protected void setup() {
        agents = null;
        Messages.agentIsRunning(getLocalName());
        Scanner sc = new Scanner(System.in);
        addBehaviour(new CyclicBehaviour() { // nekonečná smyčka
            @Override
            public void action() {
                System.out.print(getLocalName()+" - Zadejte aktualní teplotu ");
                double temperature = sc.nextDouble(); // Uživatelem zadaná teplota
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM); //vytvoření instance ACL zprávy za účelem odeslání zprávy ve formátu INFORM
                List<String> listOfAgents = findAgents(); // seznam příjemců
                // nastavení zprávy, která se odesílá dalším agentů ve formátu: "teplota;názevagenta"
                msg.setContent(String.valueOf(temperature+";"+getAID().getLocalName())); //vytvoření zprávy k odeslání
                for (String agent : listOfAgents) {
                    msg.addReceiver(new AID(agent, AID.ISLOCALNAME)); //přidej příjemce
                    Messages.confirmSendMessage(getLocalName(), agent, "byl přidán příjemce");
                }
                send(msg); //rozešli zprávy všem příjemcům
            }
        });
    }
    private List<String> findAgents(){
        List<String> returnAgents = new ArrayList<>();
        try{
            SearchConstraints searchConstraints = new SearchConstraints();
            searchConstraints.setMaxResults(new Long(-1));;
            agents = AMSService.search(this, new AMSAgentDescription(), searchConstraints);
        } catch (FIPAException e){
            e.printStackTrace();
        }
        for (AMSAgentDescription agent : agents) {
            AID agentID = agent.getName();
            if (!(agentID.getLocalName().equals("ams")||agentID.getLocalName().equals("rma")||agentID.getLocalName().equals("df")))
                returnAgents.add(agentID.getLocalName());
        }
        return returnAgents;
    }
}
