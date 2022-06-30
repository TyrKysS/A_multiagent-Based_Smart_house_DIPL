package osu.sladcik.agents.AdaptiveAgents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import osu.sladcik.resource.Messages;
import osu.sladcik.resource.TypeAgents;

public class RadiatorAgent extends Agent {
    private boolean radiator;
    private AMSAgentDescription[] agents;
    private String targetAgent = TypeAgents.GUI.toString();
    public boolean isRadiator() {
        return radiator;
    }

    public void setRadiator(boolean radiator) {
        this.radiator = radiator;
    }
    @Override
    protected void setup() {
        Messages.agentStatus(getLocalName()); // kontrolní hláška, zda agent funguje
        agents = null;
        addBehaviour(new CyclicBehaviour() { // Nekonečný cyklus
            @Override
            public void action() {
                ACLMessage msg = receive(); //vytvoření instance ACL zprávy za účelem získávání zpráv z venku (mimo agenta)
                if (msg != null){
                    System.out.println(msg.getContent()); //výpis do konzole text obdržené zprávy
                    /*
                     zpráva obsahuje hodnotu a příjemce, středníkem je rozdělena.
                     vytváří se pole, kde nultá pozice je zpráva a první pozice je jméno odesilatele
                     Pokud zpráva obsahuje hodnoty 100 nebo 200 -> agent obržel zprávu od GUI a má za úkol zapnout/vypnout topení
                     Jestliže tak učiní odešle zpět zprávu GUI o tom, v jakém je nyní stavu

                     Pokud na vstup příjde jakákoli jiná hodnota, agent tuto hodnotu bere jako aktuální tpelotu
                     Jestli že je hodnota z intervalo 5 >= X <= 25, zapne se topení, v opačném případě topení se vypíná
                      */
                    String[] message = msg.getContent().split(";");
                    if(message[0].equals("100") || message[0].equals("200")){
                        if (msg.getContent().equals("100")){
                            setRadiator(true);
                            sendMessage(isRadiator());
                        } else {
                            setRadiator(false);
                            sendMessage(isRadiator());
                        }
                    } else {
                        double tmpTemp = Double.parseDouble(message[0]);
                        if ((tmpTemp <= 15 && isRadiator() == false)){
                            setRadiator(true);
                            sendMessage(isRadiator());
                        } else if (tmpTemp >=16 && isRadiator() == true){
                            setRadiator(false);
                            sendMessage(isRadiator());
                        } else {
                            Messages.infoBox("Topení je již ve stavu "+isRadiator(), getLocalName());
                        }
                    }
                }
            }
        });
    }
    // zasílání zpráv o svém aktuálním stavu GUI agentovi, kde dále informuje na konzoli, že zpráva byla úspěšně odeslána
    private void sendMessage(boolean radiatorStatus) {

        boolean messageWasSent = false;
        boolean findTarget = false;
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults(new Long(-1));
            agents = AMSService.search(this, new AMSAgentDescription(), c);
        } catch (Exception e){}

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        for (AMSAgentDescription agent : agents) {
            AID agentID = agent.getName();
            if (agentID.getLocalName().equals(targetAgent))
                findTarget = true;
        }
        if (findTarget){
            msg.setContent(radiatorStatus+";"+getLocalName());
            msg.addReceiver(new AID("device_RADIATOR", AID.ISLOCALNAME));
            msg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));
            send(msg);
            messageWasSent = true;
            if (messageWasSent){
                Messages.sendMessageStatus(getLocalName(), msg.getContent());
            } else
                Messages.MessageNotSend(getLocalName(), targetAgent);
        } else {
            Messages.agentNotFound(getLocalName(), targetAgent);
            msg.setContent(radiatorStatus+";"+getLocalName());
            msg.addReceiver(new AID("device_RADIATOR", AID.ISLOCALNAME));
            send(msg);
        }
        // vypíše na konzoli a zároveň zobrazí informační hlášku o změně stavu
        Messages.sendMessageStatus(getLocalName(), isRadiator());
        Messages.infoBox("Topení je zapnuto "+isRadiator(), getLocalName());
    }
}
