package osu.sladcik.agents.motionSensor;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import osu.Agents;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MotionSensorAgent extends Agent {

    private static boolean sendMsg = false;
    private AMSAgentDescription[] agents;

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                if (isSendMsg()){
                    sendMsgToAllAgents();
                    System.out.println("send msg");
                    setSendMsg(false);
                }
            }
        });
    }

    private void sendMsgToAllAgents(){
        ACLMessage msg = new ACLMessage(ACLMessage.FAILURE);
        msg.setContent("motion");
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

    public static boolean isSendMsg() {
        return sendMsg;
    }

    public static void setSendMsg(boolean sendMsg) {
        MotionSensorAgent.sendMsg = sendMsg;
    }
}
