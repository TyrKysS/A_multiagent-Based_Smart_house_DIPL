package osu.sladcik.agents.ObserverAgents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAException;
import osu.sladcik.resource.Messages;
import osu.sladcik.resource.TypeAgents;

import java.util.ArrayList;
import java.util.List;

public class ObserverAgent extends Agent {
    private AMSAgentDescription[] agents;
    private List<String> listOfAgents = new ArrayList<>();
    @Override
    protected void setup() {
        Messages.agentStatus(getLocalName());
        agents = null;
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults(new Long(-1));
            agents = AMSService.search(this, new AMSAgentDescription(), c);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                for (AMSAgentDescription agent : agents) {
                    for (TypeAgents typeAgents : TypeAgents.values()) {
                        AID agentID = agent.getName();
                        if (agentID.getLocalName().equals(typeAgents.toString())){
                            listOfAgents.add(agentID.getLocalName());
                        }
                    }
                }
                doDelete();
            }
        });
    }
}
