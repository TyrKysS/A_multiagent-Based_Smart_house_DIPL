package osu.sladcik.agents.gui;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.lang.acl.ACLMessage;

import static osu.sladcik.agents.gui.GuiFrame.*;


public class GuiAgent extends Agent {
    private AMSAgentDescription[] agents;
    @Override
    protected void setup() {
        agents = null;

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (isIsBtnPressed()){
                    changeLightStatus(String.valueOf(isLighter()));
                    if (msg != null && (msg.getContent().equals("true") || msg.getContent().equals("false"))){
                        setConfirmLight(Boolean.parseBoolean(msg.getContent()));
                    }
                    setIsBtnPressed(false);
                }
                if (msg != null && (msg.getContent().equals("fire") || msg.getContent().equals("motion"))){
                    System.out.println(msg.getContent());
                    setFireState(msg.getContent().equals("fire"));
                    setSensorState(msg.getContent().equals("motion"));
                } else {

                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void changeLightStatus(String contentMsg){
        ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
        msg.setContent(contentMsg);
        msg.addReceiver(new AID("light", AID.ISLOCALNAME));
        send(msg);
        System.out.println("zpráva byla úspěšně odeslána");
    }
}
