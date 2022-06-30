package osu.sladcik.agents.fire;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import osu.Agents;

import java.util.*;

public class FireAgent extends Agent {
    private AMSAgentDescription[] agents;
    private String eventStart = "";
    private String actualTime = "";
    @Override
    protected void setup() {
        agents = null;
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // Generování aktuálního času
                GregorianCalendar calendar = new GregorianCalendar();
                int actualHour = calendar.get(Calendar.HOUR);
                int actualMinutes = calendar.get(Calendar.MINUTE);
                actualTime = actualHour+":"+actualMinutes;
                recieveMessage();
                if (FireFrame.alarmIsRinging == 1){
                    sendAlarmToAllAgents();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Pokud byla nastavena udlást a nastala hodina události, aktivuj událost
                if (eventStart.equals(actualTime)){
                    FireFrame.alarmIsOn = 1;
                }
            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                if (eventStart.equals(actualTime) && !FireFrame.eventStart){
                    System.out.println("Událost byla zahájena");
                    FireFrame.eventStart = true;
                }
            }
        });
    }
    private void recieveMessage() {
        ACLMessage msg = receive();
        if (msg != null){
            eventStart = msg.getContent();
            System.out.println(msg.getContent());
            FireFrame.eventStart = false;
        }
    }

    // Odeslat polach všem přítomným agentům
    private void sendAlarmToAllAgents(){
        ACLMessage msg = new ACLMessage(ACLMessage.FAILURE);
        msg.setContent("fire");
        List<String> listOfAgents = findAgents();
        for (String agent : listOfAgents) {
            msg.addReceiver(new AID(agent, AID.ISLOCALNAME));
        }
        send(msg);
    }
    private List<String> findAgents(){
        List<String> tmpList = new ArrayList<>();
        List<String> ret = new ArrayList<>();
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults(new Long(-1));
            agents = AMSService.search(this, new AMSAgentDescription(), c);
        } catch (Exception e){}

        for (AMSAgentDescription agent : agents) {
            for (Agents agents : Agents.values()) {
                AID agentID = agent.getName();
                if ((agentID.getLocalName().equals(agents.toString())))
                    tmpList.add(agentID.getLocalName());
            }
            Set<String> finalListOfAgents = new LinkedHashSet<>(tmpList);
            ret = new ArrayList<>(finalListOfAgents);
        }
        return ret;
    }
}
