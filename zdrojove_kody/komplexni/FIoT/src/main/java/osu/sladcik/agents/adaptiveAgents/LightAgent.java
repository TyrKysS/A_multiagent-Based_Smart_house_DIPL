package osu.sladcik.agents.adaptiveAgents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import osu.sladcik.Resources;

import java.io.FileNotFoundException;

public class LightAgent extends Agent {
    private int lightStatus = 0, lightColor = 1, actualLightValue = 2, isFire = 0, output = 0;
    private int matrix[][];
    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                try {
                    matrix = Resources.readMatrixFromFile("LightSettings.txt", 62, 6);
                } catch (FileNotFoundException e) {
                    System.out.println("Soubor s nastavením nebyl nalezen, využívá se výchozí nastavení");

                    matrix = new int[][]{
                            //    tlačítka                    senzor                       LED diody + žárovka
                            // režim   barva       vstup2_min        vstup2_max    požár      výstup
                            {    0,       0,            0,            800,         0,        0       },
                            {    0,       1,            0,            800,         0,        0       },
                            {    0,       2,            0,            800,         0,        0       },
                            {    0,       3,            0,            800,         0,        0       },
                            {    1,       0,            0,              5,         0,        1       },
                            {    1,       1,            0,              5,         0,        1       },
                            {    1,       2,            0,              5,         0,        1       },
                            {    1,       3,            0,              5,         0,        1       },
                            {    1,       0,            5,            800,         0,        0       },
                            {    1,       1,            5,            800,         0,        0       },
                            {    1,       2,            5,            800,         0,        0       },
                            {    1,       3,            5,            800,         0,        0       },

                            {    2,       0,            0,             60,         0,        0       },
                            {    2,       1,            0,             60,         0,        1       },
                            {    2,       2,            0,             60,         0,        2       },
                            {    2,       3,            0,             60,         0,        3       },
                            {    2,       4,            0,             60,         0,        4       },
                            {    2,       0,           60,            800,         0,        0       },
                            {    2,       1,           60,            800,         0,        0       },
                            {    2,       2,           60,            800,         0,        0       },
                            {    2,       3,           60,            800,         0,        0       },

                            {    3,       0,            0,              5,         0,        1       },
                            {    3,       1,            0,              5,         0,        1       },
                            {    3,       2,            0,              5,         0,        1       },
                            {    3,       3,            0,              5,         0,        1       },
                            {    3,       0,           10,             20,         0,       75       },
                            {    3,       1,           10,             20,         0,       75       },
                            {    3,       2,           10,             20,         0,       75       },
                            {    3,       3,           10,             20,         0,       75       },
                            {    3,       0,           20,             30,         0,       50       },
                            {    3,       1,           20,             30,         0,       50       },
                            {    3,       2,           20,             30,         0,       50       },
                            {    3,       3,           20,             30,         0,       50       },
                            {    3,       0,           30,             40,         0,       25       },
                            {    3,       1,           30,             40,         0,       25       },
                            {    3,       2,           30,             40,         0,       25       },
                            {    3,       3,           30,             40,         0,       25       },
                            {    3,       0,           40,            800,         0,        0       },

                            {    4,       1,            0,            800,         0,        5       },
                            {    4,       2,            0,            800,         0,        5       },
                            {    4,       3,            0,            800,         0,        5       },
                            {    4,       4,            0,            800,         0,        5       },
                            {    4,       0,            0,            800,         0,        5       },

                            {    5,       0,            0,            800,         0,        0       },

                            {    0,       0,            0,            800,         1,        3       },
                            {    1,       0,            0,            800,         1,        3       },
                            {    1,       1,            0,            800,         1,        3       },
                            {    1,       2,            0,            800,         1,        3       },
                            {    1,       3,            0,            800,         1,        3       },
                            {    1,       4,            0,            800,         1,        3       },
                            {    2,       1,            0,            800,         1,        3       },
                            {    2,       2,            0,            800,         1,        3       },
                            {    2,       3,            0,            800,         1,        3       },
                            {    2,       4,            0,            800,         1,        3       },
                            {    3,       1,            0,            800,         1,        3       },
                            {    3,       2,            0,            800,         1,        3       },
                            {    3,       3,            0,            800,         1,        3       },
                            {    3,       4,            0,            800,         1,        3       },
                            {    4,       1,            0,            800,         1,        3       },
                            {    4,       2,            0,            800,         1,        3       },
                            {    4,       3,            0,            800,         1,        3       },
                            {    4,       4,            0,            800,         1,        3       },
                    };

                }
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                ACLMessage msgBack = new ACLMessage(ACLMessage.AGREE);
                if (msg != null && (msg.getContent().contains("Light"))){
                    String[] message = msg.getContent().split(";");
                    switch (message[1]){
                        case "lightStatus":
                            lightStatus = Integer.parseInt(message[2]);
                            break;
                        case "lightColor":
                            lightColor = Integer.parseInt(message[2]);
                            break;
                        case "lightValue":
                            actualLightValue = Integer.parseInt(message[2]);
                            break;
                    }
                    for (int[] ints : matrix) {
                        for (int j = 0; j < ints.length; j++) {
                            if (ints[0] == lightStatus && ints[1] == lightColor && ints[2] <= actualLightValue && ints[3] > actualLightValue && ints[4] == isFire) {
                                if (ints[5] == 5){
                                    int max = 4;
                                    int min = 1;
                                    int range = max - min + 1;
                                    int result = (int)(Math.random() * range) + min;
                                }
                                output = ints[5];
                            }
                        }
                    }
                                msgBack.addReceiver(new AID(message[0], AID.ISLOCALNAME));
                                msgBack.setContent(getLocalName()+";accept;"+output);
                                send(msgBack);

                }

                if (msg != null && msg.getContent().contains("GuiAgent")){
                    String[] message = msg.getContent().split(";");
                    lightStatus = Integer.parseInt(message[2]);
                    boolean deviceFound = false;
                    for (int[] ints : matrix) {
                        for (int j = 0; j < ints.length; j++) {
                            if (ints[0] == lightStatus && ints[1] == lightColor && ints[2] <= actualLightValue && ints[3] > actualLightValue && ints[4] == isFire) {
                                output = ints[5];
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
