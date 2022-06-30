package osu.sladcik.agents.gui;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import osu.Agents;

import java.util.*;

public class GuiAgent extends Agent {
    private AMSAgentDescription[] agents;
    @Override
    protected void setup() {
        agents = null;

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                GregorianCalendar calendar = new GregorianCalendar();
                //System.out.println(calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND));
                GuiFrame.actualTime = calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
                if (GuiFrame.isEvent){
                    System.out.println("Událost byla vytvořena na čas "+ GuiFrame.eventTime);
                    sendEventToAgents(GuiFrame.eventTime);
                    GuiFrame.isEvent = false;
                }
                if (GuiFrame.isBtnPressed){
                    changeLightStatus(String.valueOf(GuiFrame.lighter));
                    ACLMessage msg = receive();
                    if (msg != null && (msg.getContent().equals("true") || msg.getContent().equals("false"))){
                        GuiFrame.confirmLight = Boolean.parseBoolean(msg.getContent());
                    }
                    GuiFrame.isBtnPressed = false;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void sendEventToAgents(String eventTime){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(eventTime);
        List<String> listOfAgents = findAgents();
        for (String agent : listOfAgents) {
            for (String targetAgent : GuiFrame.targetAgents) {
                if (agent.equals(targetAgent)){
                    msg.addReceiver(new AID(agent, AID.ISLOCALNAME));
                    System.out.println(agent);
                }
            }
        }
        send(msg);
    }
    //TODO odešli světelnému agentovi informaci o zapnutí/vypnutí světla na dálku
    private void changeLightStatus(String contentMsg){
        ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
        msg.setContent(contentMsg);
        msg.addReceiver(new AID("light", AID.ISLOCALNAME));
        send(msg);
        System.out.println("zpráva byla úspěšně odeslána");
    }

    private List<String> findAgents(){
        List<String> tmpList = new ArrayList<>();
        List<String> ret = new ArrayList<>();
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults(new Long(-1));
            agents = AMSService.search(this, new AMSAgentDescription(), c);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
