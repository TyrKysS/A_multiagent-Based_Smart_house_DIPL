package osu.sladcik.agents.adaptiveAgents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import osu.sladcik.Resources;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class LightAgent extends Agent {
    private int lightStatus = 0, color = 0, fire = 0, output = 0;
    private String timer;
    private int matrix[][];
    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                try {
                    matrix = Resources.readMatrixFromFile("LightSettings.txt", 14, 4);
                } catch (FileNotFoundException e) {
                    System.out.println("Soubor s nastavením nebyl nalezen, využívá se výchozí nastavení");
                    matrix = new int[][]{
                            //lightStatus   color   fire    output
                            {      0,        0,      0,      0      }, //vypnuto
                            {      0,        1,      0,      0      }, //vypnuto
                            {      0,        2,      0,      0      }, //vypnuto
                            {      0,        3,      0,      0      }, //vypnuto
                            {      0,        4,      0,      0      }, //vypnuto

                            {      1,        1,      0,      1      }, // žlutá barva
                            {      1,        2,      0,      2      }, // zelená barva
                            {      1,        3,      0,      3      }, // červená barva
                            {      1,        4,      0,      4      }, // modrá barva

                            {      0,        0,      1,      3      }, // požár (červená barva)
                            {      1,        1,      1,      3      }, // požár (červená barva)
                            {      1,        2,      1,      3      }, // požár (červená barva)
                            {      1,        3,      1,      3      }, // požár (červená barva)
                            {      1,        4,      1,      3      }, // požár (červená barva)
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
                String deviceSource, agentStatus;
                ACLMessage msg = receive();
                if (msg != null){
                    String[] message = msg.getContent().split(";");
                    deviceSource = message[1];
                    agentStatus = message[2];
                    switch (deviceSource){
                        case "lightStatus":
                            lightStatus = Integer.parseInt(agentStatus);
                            color = Integer.parseInt(agentStatus);
                            break;
                        case "color":
                            color = Integer.parseInt(agentStatus);
                            break;
                        case "fire":
                            fire = Integer.parseInt(agentStatus);
                            break;
                        case "event":
                            timer = agentStatus;
                            isTimerOn = true;
                            break;
                    }
                    if (isTimerOn && timer.equals(actualTime)){
                        lightStatus = 1;
                        color = 1;
                    }
                    boolean deviceFound = false;
                    for (int[] ints : matrix) {
                        for (int j = 0; j < ints.length; j++) {
                            if (ints[0] == lightStatus && ints[1] == color && ints[2] == fire) {
                                output = ints[3];
                                deviceFound = true;
                                break;
                            }
                        }
                    }
                    if (deviceFound){
                        System.out.println(getLocalName()+" output "+output);
                        ACLMessage confirmMsg = new ACLMessage(ACLMessage.AGREE);
                        confirmMsg.addReceiver(new AID("Light", AID.ISLOCALNAME));
                        confirmMsg.setContent(getLocalName()+";accept;"+output);
                        send(confirmMsg);
                    }
                }
            }
        });
    }
}
