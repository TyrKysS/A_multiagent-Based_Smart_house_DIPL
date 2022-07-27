package osu.sladcik.agents.AdaptiveAgents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.*;
import osu.sladcik.resource.Messages;
import osu.sladcik.resource.TypeAgents;

import java.util.*;

public class TemperatureAgent extends Agent {
    private AMSAgentDescription[] agents;
    private List<String> listOfAgents;
    private String recieveMessage;;
    @Override
    protected void setup() {
        agents = null;
        //TODO God agent odešle adresu svého zařízení agentovi
        //TODO odeslat stav svému zařízení
        addBehaviour(new CyclicBehaviour() { // nekonečná smyčka
            @Override
            public void action() {
                //ACLMessage sendMsg = new ACLMessage(ACLMessage.INFORM);
                //sendMsg.setContent(getLocalName());

                ACLMessage getMsg = receive();
                if (getMsg != null){
                    Messages.recieveMessage(getLocalName(), getMsg.getContent());
                    recieveMessage = getMsg.getContent();
                    String[] finalRecieveMessage = recieveMessage.split(";");
                    double temperature = Double.parseDouble(finalRecieveMessage[0]);
                    sendMessageToAllAgents(String.valueOf(temperature));
                }
            }
        });
    }

    private void sendMessageToAllAgents(String message) {
        double temperature = Double.parseDouble(message);

        if (temperature != Double.NaN){
            listOfAgents = new ArrayList<>();

            findAgents();
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM); //vytvoření instance ACL zprávy za účelem odeslání zprávy ve formátu INFORM
            msg.setContent(temperature+";"+getLocalName());
            for (AMSAgentDescription agent : agents) {
                for (TypeAgents typeAgents : TypeAgents.values()) {
                    AID agentID = agent.getName();
                    if (agentID.getLocalName().equals(typeAgents.toString())){
                        listOfAgents.add(agentID.getLocalName());
                    }
                }
            }
            Set<String> finalListOfAgents = new LinkedHashSet<>(listOfAgents);

            for (String agent : finalListOfAgents) {
                Messages.sendMessageStatus(getLocalName(), agent, msg.getContent());
                if (!agent.equals(getLocalName()))
                    msg.addReceiver(new AID(agent, AID.ISLOCALNAME)); // Přidej příjemce
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
