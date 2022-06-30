package osu.sladcik.devices.gui;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.lang.acl.ACLMessage;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class GuiDevice extends Agent {
    private AMSAgentDescription[] agents;
    private String targetAgentName;
    @Override
    protected void setup() {
        agents = null;

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
                if (GuiFrame.notifyAgent){
                    notifyAgent(GuiFrame.msg);
                    GuiFrame.notifyAgent = false;
                }
            }
        });

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

    private void changeLightStatus(String valueOf) {

    }

    private void sendEventToAgents(String eventTime) {

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
            } else {
                targetAgentName = msg.getContent();
                System.out.println("msg "+msg.getContent());
            }
        }
    }






/*

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
 */
}
