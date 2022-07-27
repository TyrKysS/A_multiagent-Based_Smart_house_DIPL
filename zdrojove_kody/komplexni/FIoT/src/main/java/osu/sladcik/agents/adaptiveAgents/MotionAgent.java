package osu.sladcik.agents.adaptiveAgents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import osu.sladcik.Agents;
import osu.sladcik.Resources;

import java.io.FileNotFoundException;
import java.util.*;

public class MotionAgent extends Agent {

    private AMSAgentDescription[] agents;
    private List<String> listOfAgents;
    private int actualStatus = 0;

    @Override
    protected void setup() {
        agents = null;
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null){
                    if (msg.getContent().contains("Motion")){
                        String[] msgArray = msg.getContent().split(";");
                        System.out.println("msgArray");
                        setActualStatus(1);

                        int[][] matrix;
                        try {
                            matrix = Resources.readMatrixFromFile("motionSettings.txt", 3,4);
                        } catch (FileNotFoundException e) {
                            System.out.println("Soubor s nastavením nebyl nalezen, využívá se výchozí nastavení");

                            matrix = new int[][]{
                                    //      pohyb          začátek       konec
                                    // detekovan pohyb      hodina      hodina     odeslat informaci
                                    {       0,              7,              8,          0},
                                    {       1,              20,              7,          0},
                                    {       1,              9,              20,          1}
                            };
                        }

                        ACLMessage msgBack = new ACLMessage(ACLMessage.AGREE);
                        msgBack.setContent(getLocalName()+";accept;"+msgArray[2]);
                        msgBack.addReceiver(new AID(msgArray[0], AID.ISLOCALNAME));
                        send(msgBack);
                        GregorianCalendar calendar = new GregorianCalendar();
                        int actualHour = calendar.get(Calendar.HOUR);
                        for (int[] ints : matrix) {
                            for (int j = 0; j < ints.length; j++) {
                                if (ints[0] == getActualStatus() && ints[1] < actualHour && ints[2] >= actualHour) {
                                    if (ints[3] == 1){
                                        sendMessage(msgArray[2]);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });
    }
    private void sendMessage(String message) {
        listOfAgents = new ArrayList<>();
        findAgents();
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM); //vytvoření instance ACL zprávy za účelem odeslání zprávy ve formátu INFORM
        if (message.equals("1")){
            msg.setContent(getLocalName()+";motion");
            for (AMSAgentDescription agent : agents) {
                for (Agents typeAgents : Agents.values()) {
                    AID agentID = agent.getName();
                    if (agentID.getLocalName().equals(typeAgents.toString()) && !agentID.getLocalName().equals(getLocalName())){
                        listOfAgents.add(agentID.getLocalName());
                    }
                }
            }
            Set<String> finalListOfAgents = new LinkedHashSet<>(listOfAgents);

            System.out.println("finalListOfAgents");
            for (String agent : finalListOfAgents) {
                msg.addReceiver(new AID(agent, AID.ISLOCALNAME)); // Přidej příjemce
                System.out.println(agent);
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

    public int getActualStatus() {
        return actualStatus;
    }

    public void setActualStatus(int actualStatus) {
        this.actualStatus = actualStatus;
    }
}
