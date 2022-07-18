package osu.sladcik.agents.godAgents;

import jade.core.*;
import jade.core.Runtime;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import osu.sladcik.Middleware;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GodAgent extends Agent {
    private List<String> agentListNames;
    private File file = new File("agents.json");
    @Override
    protected void setup() {
        agentListNames = new ArrayList<>();
        getAgentsFromJson(file);
        generateJsonFileWithAgents(file);
        boolean acceptMessage = false;
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                boolean findAgent = false;
                String agentName = "";
                ACLMessage msg = receive();
                if (msg != null){
                    System.out.println("got msg: " + msg.getContent());
                    String message = msg.getContent();
                    for (String agentTargetName : agentListNames) {
                        if (agentTargetName.equals(message)){
                            findAgent = true;

                            Profile p = new ProfileImpl();
                            p.setParameter(Profile.MAIN_HOST, "localhost");
                            p.setParameter(Profile.GUI, "true");
                            p.setParameter(Profile.CONTAINER_NAME, "FIoT");

                            agentName = message+"Agent";

                            AgentController ac = null;
                            try {
                                ac = Middleware.cc.createNewAgent(agentName, "osu.sladcik.agents.adaptiveAgents."+agentName, null);
                                ac.start();
                            } catch (StaleProxyException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    sendBackMsgToDevice(findAgent, message, agentName);
                }
            }
        });
    }

    private void sendBackMsgToDevice(boolean foundAgent, String targetAgent, String agentName) {
        if (foundAgent){
            ACLMessage acceptMsg = new ACLMessage(ACLMessage.AGREE);
            acceptMsg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));
            acceptMsg.setContent(agentName);
            send(acceptMsg);
        } else {
            ACLMessage refuseMsg = new ACLMessage(ACLMessage.REFUSE);
            refuseMsg.addReceiver(new AID(targetAgent, AID.ISLOCALNAME));
            refuseMsg.setContent("refuse");
            send(refuseMsg);
        }
    }

    private void getAgentNames(JSONObject agent){
        JSONObject agentObject = (JSONObject) agent.get("agent");
        String agentName = (String) agentObject.get("name");
        //System.out.println(agentName);
        agentListNames.add(agentName);
    }
    private void getAgentsFromJson(File file) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(file))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray agentList = (JSONArray) obj;
            //System.out.println(agentList);

            agentList.forEach(agent -> getAgentNames((JSONObject) agent));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void generateJsonFileWithAgents(File file) {
        JSONObject agentDetails = new JSONObject();
        agentDetails.put("name", "Fire");
        agentDetails.put("sensors", "1");
        agentDetails.put("actuators", "1");

        JSONObject agentObject = new JSONObject();
        agentObject.put("agent", agentDetails);

        JSONObject agentDetails2 = new JSONObject();
        agentDetails2.put("name", "Light");
        agentDetails2.put("sensors", "2");
        agentDetails2.put("actuators", "1");

        JSONObject agentObject2 = new JSONObject();
        agentObject2.put("agent", agentDetails2);


        JSONObject agentDetails3 = new JSONObject();
        agentDetails3.put("name", "Gui");
        agentDetails3.put("sensors", "0");
        agentDetails3.put("actuators", "1");

        JSONObject agentObject3 = new JSONObject();
        agentObject3.put("agent", agentDetails3);

        JSONObject agentDetails4 = new JSONObject();
        agentDetails4.put("name", "Motion");
        agentDetails4.put("sensors", "1");
        agentDetails4.put("actuators", "0");

        JSONObject agentObject4 = new JSONObject();
        agentObject4.put("agent", agentDetails4);

        JSONArray agentList = new JSONArray();
        agentList.add(agentObject);
        agentList.add(agentObject2);
        agentList.add(agentObject3);
        agentList.add(agentObject4);
        if (!file.exists()){
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(agentList.toJSONString());
                fileWriter.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
