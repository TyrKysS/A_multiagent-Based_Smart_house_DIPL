package osu.sladcik.agents.GodAgents;

import jade.core.*;
import jade.core.Runtime;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import osu.sladcik.Middleware.Main;
import osu.sladcik.resource.Messages;
import osu.sladcik.resource.TypeAgents;

import java.util.HashMap;

public class GodAgent extends Agent {
    private HashMap<TypeAgents, String> typeOfAgents = new HashMap<>();
    private AMSAgentDescription[] AMSagents;
    @Override
    protected void setup() {
        Messages.agentStatus(getLocalName());
        typeOfAgents.put(TypeAgents.RADIATOR, "RadiatorAgent");
        typeOfAgents.put(TypeAgents.WINDOW, "WindowAgent");
        typeOfAgents.put(TypeAgents.TEMPERATURE, "TemperatureAgent");
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults(new Long(-1));
            AMSagents = AMSService.search(this, new AMSAgentDescription(), c);
        } catch (Exception e){ }
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null){
                    String[] message = msg.getContent().split(";");
                    for (TypeAgents agents : TypeAgents.values()) {
                        if (agents.toString().equals(message[0])){
                            System.out.println(getLocalName()+" - byla přijata zpráva od "+message[0]+", text zprávy: "+message[1]);
                            ACLMessage sendMsg = new ACLMessage(ACLMessage.INFORM);

                            Runtime rt = Runtime.instance();
                            Profile p = new ProfileImpl();
                            ContainerController cc;
                            p.setParameter(Profile.MAIN_HOST, message[2]);
                            p.setParameter(Profile.GUI, "true");
                            p.setParameter(Profile.CONTAINER_NAME, "FIoT");
                            //cc = rt.createAgentContainer(p);

                            AgentController ac = null;
                            for (TypeAgents typeAgents : typeOfAgents.keySet()) {
                                if (typeAgents.toString().equals(message[0])){
                                    try {
                                        ac = Main.cc.createNewAgent(agents.toString(), "osu.sladcik.agents.AdaptiveAgents."+typeOfAgents.get(typeAgents), null);
                                        ac.start();
                                    } catch (StaleProxyException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            try {
                                sendMsg.setContent(ac.getName());
                            } catch (StaleProxyException e) {
                                e.printStackTrace();
                            }
                            sendMsg.addReceiver(new AID(getAID(message[1]).getLocalName(), AID.ISLOCALNAME));
                            send(sendMsg);
                            System.out.println(getLocalName()+" - zpráva byla odeslána: "+getAID(message[1]).getLocalName());
                        }
                    }
                }
            }
        });
    }
}
