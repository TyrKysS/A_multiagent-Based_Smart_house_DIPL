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

public class WindowAgent extends Agent {
    private boolean window;
    private AMSAgentDescription[] agents;
    private String targetAgent = TypeAgents.GUI.toString();

    public boolean isWindow() {
        return window;
    }

    public void setWindow(boolean window) {
        this.window = window;
    }
    @Override
    protected void setup() {
        Messages.agentStatus(getLocalName()); // kontrolní hláška, zda agent funguje
        agents = null;
         addBehaviour(new CyclicBehaviour() { // Nekonečný cyklus
             @Override
             public void action() {
                 ACLMessage msg = receive(); //vytvoření instance ACL zprávy za účelem získávání zpráv z venku (mimo agenta)
                 if(msg != null){
                     System.out.println(msg.getContent()); //výpis do konzole text obdržené zprávy
                     /*
                     zpráva obsahuje hodnotu a příjemce, středníkem je rozdělena.
                     vytváří se pole, kde nultá pozice je zpráva a první pozice je jméno odesilatele
                     Pokud zpráva obsahuje hodnoty 100 nebo 200 -> agent obržel zprávu od GUI a má za úkol otevřít/zavřít okno
                     Jestliže tak učiní odešle zpět zprávu GUI o tom, v jakém je nyní stavu

                     Pokud na vstup příjde jakákoli jiná hodnota, agent tuto hodnotu bere jako teplotní
                     Jestli že je hodnota >= 25, otevře okno, v opačném případě okno zavírá
                      */
                     String[] message = msg.getContent().split(";");
                     if(message[0].equals("100") || message[0].equals("200")){
                         if (msg.getContent().equals("100")){
                             setWindow(true);
                             sendMessage(isWindow());
                         } else {
                             setWindow(false);
                             sendMessage(isWindow());
                         }
                     } else {
                         double tmpTemp = Double.parseDouble(message[0]);
                         if (tmpTemp >= 25 && isWindow() == false){
                             setWindow(true);
                             sendMessage(isWindow());
                         } else {
                             setWindow(false);
                             sendMessage(isWindow());
                         }
                     }
                 }
             }
         });
    }
    // zasílání zpráv o svém aktuálním stavu GUI agentovi, kde dále informuje na konzoli, že zpráva byla úspěšně odeslána
    private void sendMessage(boolean windowStatus) {

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
            if (agentID.getLocalName().equals("GUI"))
                findTarget = true;
        }
        if (findTarget){
            msg.setContent(windowStatus+";"+getLocalName());
            msg.addReceiver(new AID("device_WINDOW", AID.ISLOCALNAME));
            msg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));
            send(msg);
            messageWasSent = true;
            if (messageWasSent){
                Messages.sendMessageStatus(getLocalName(), msg.getContent());
            } else
                Messages.MessageNotSend(getLocalName(), targetAgent);
        } else {
            Messages.agentNotFound(getLocalName(), targetAgent);
            msg.setContent(windowStatus+";"+getLocalName());
            msg.addReceiver(new AID("device_WINDOW", AID.ISLOCALNAME));
            send(msg);
        }
        // vypíše na konzoli a zároveň zobrazí informační hlášku o změně stavu
        Messages.sendMessageStatus(getLocalName(), isWindow());
        Messages.infoBox("Okno je otevřeno "+isWindow(), getLocalName());
    }
}
