package osu.sladcik.agents.adaptiveAgents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import osu.sladcik.Agents;
import osu.sladcik.Resources;

import java.io.FileNotFoundException;
import java.util.*;

public class FireAgent extends Agent {
    private AMSAgentDescription[] agents;
    private List<String> listOfAgents;
    private String timer;
    private int outputLed = 0, outputAlarm = 0;
    private int[][] matrix;
    @Override
    protected void setup() {
        agents = null;
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println(getLocalName()+" is running");
                try {
                    matrix = Resources.readMatrixFromFile("FireSettings.txt", 3, 4);
                    System.out.println("Čtení ze souboru bylo úspěšné");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("Soubor s nastavením nebyl nalezen, využívá se výchozí nastavení");
                    matrix = new int[][] {
                            //            senzor plamene          LED        Bzučák
                            //     vstup_min     vstup_max      výstup      výstup2
                            {     0,            100,            1,       1000    },
                            {   100,            300,            1,          0    },
                            {   300,           1000,            0,          0    }
                    };
                }
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                int buzzerValue;
                ACLMessage msg = receive();
                if (msg != null && msg.getContent().contains("Fire")){
                    String[] message = msg.getContent().split(";");
                    buzzerValue = Integer.parseInt(message[1]);

                    boolean deviceFound = false;
                    for (int[] ints : matrix) {
                        for (int j = 0; j < ints.length; j++) {
                            if (ints[0] <= buzzerValue && ints[1] > buzzerValue) {
                                setOutputLed(ints[2]);
                                setOutputAlarm(ints[3]);
                                deviceFound = true;
                                break;
                            }
                        }
                    }
                    if (deviceFound){
                        System.out.println(getLocalName()+" outputLed "+getOutputLed());
                        System.out.println(getLocalName()+" outputAlarm "+getOutputAlarm());
                        ACLMessage confirmMsg = new ACLMessage(ACLMessage.AGREE);
                        confirmMsg.addReceiver(new AID("Fire", AID.ISLOCALNAME));
                        confirmMsg.setContent(getLocalName()+";accept;"+getOutputLed()+";"+getOutputAlarm());
                        send(confirmMsg);

                        sendMessage(String.valueOf(getOutputAlarm()));
                    }
                }
            }
        });
    }
    private void sendMessage(String alarmState) {
        listOfAgents = new ArrayList<>();
        findAgents();
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM); //vytvoření instance ACL zprávy za účelem odeslání zprávy ve formátu INFORM
        if (alarmState.equals("1000")){
            msg.setContent(getLocalName()+";fire");
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

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public int getOutputLed() {
        return outputLed;
    }

    public void setOutputLed(int outputLed) {
        this.outputLed = outputLed;
    }

    public int getOutputAlarm() {
        return outputAlarm;
    }

    public void setOutputAlarm(int outputAlarm) {
        this.outputAlarm = outputAlarm;
    }
}
