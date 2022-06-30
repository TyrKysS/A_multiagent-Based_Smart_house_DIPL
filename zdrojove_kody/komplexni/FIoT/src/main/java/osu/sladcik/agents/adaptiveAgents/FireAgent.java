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
    private int alarmStatus = 0, alarmState = 0, output = 0;
    private int[][] matrix;
    @Override
    protected void setup() {
        agents = null;
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                System.out.println(getLocalName()+" is running");
                try {
                    matrix = Resources.readMatrixFromFile("FireSettings.txt", 4, 3);
                    System.out.println("Čtení ze souboru bylo úspěšné");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("Soubor s nastavením nebyl nalezen, využívá se výchozí nastavení");
                    matrix = new int[][]{
                            // alarmIsOn    alarmIsRinging   output
                            {   0,               0,              0  },
                            {   0,               1,              0  },
                            {   1,               0,              1  },
                            {   1,               1,              2  }
                    };
                }
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                GregorianCalendar calendar = new GregorianCalendar();
                int actualHour = calendar.get(Calendar.HOUR);
                int actualMinutes = calendar.get(Calendar.MINUTE);
                boolean isTimerOn = false;
                String actualTime = actualHour+":"+actualMinutes;
                String btnType, btnStatus;
                ACLMessage msg = receive();
                if (msg != null){
                    String[] message = msg.getContent().split(";");
                    btnType = message[1];
                    btnStatus = message[2];
                    switch (btnType){
                        case "alarmStatus":
                            alarmStatus = Integer.parseInt(btnStatus);
                            break;
                        case "alarmState":
                            alarmState = Integer.parseInt(btnStatus);
                            break;
                        case "event":
                            timer = btnStatus;
                            isTimerOn = true;
                            break;
                    }
                    if (isTimerOn && timer.equals(actualTime)){
                        alarmState = 1;
                        alarmStatus = 0;
                    }

                    boolean deviceFound = false;
                    for (int[] ints : matrix) {
                        for (int j = 0; j < ints.length; j++) {
                            if (ints[0] == alarmState && ints[1] == alarmStatus) {
                                output = ints[2];
                                deviceFound = true;
                                break;
                            }
                        }
                    }
                    if (deviceFound){
                        System.out.println(getLocalName()+" output "+output);
                        ACLMessage confirmMsg = new ACLMessage(ACLMessage.AGREE);
                        confirmMsg.addReceiver(new AID("Fire", AID.ISLOCALNAME));
                        confirmMsg.setContent(getLocalName()+";accept;"+output);
                        send(confirmMsg);

                        sendMessage(String.valueOf(output));
                    }
                }
            }
        });
    }

    private void sendMessage(String message) {
        listOfAgents = new ArrayList<>();

        findAgents();
        String value = "";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM); //vytvoření instance ACL zprávy za účelem odeslání zprávy ve formátu INFORM
        switch (message){
            case "0":
            case "1":
                value = "0";
                break;
            case "2":
                value = "1";
                break;
        }
        msg.setContent(getLocalName()+";fire;"+value);
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

    private void findAgents(){
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults(new Long(-1));
            agents = AMSService.search(this, new AMSAgentDescription(), c);
        } catch (Exception e){}
    }
}
