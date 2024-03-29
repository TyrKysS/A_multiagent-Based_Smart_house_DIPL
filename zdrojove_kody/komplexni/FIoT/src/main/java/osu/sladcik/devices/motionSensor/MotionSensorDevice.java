package osu.sladcik.devices.motionSensor;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.lang.acl.ACLMessage;

import static osu.sladcik.devices.motionSensor.MotionSensorFrame.*;


public class MotionSensorDevice extends Agent {

    private AMSAgentDescription[] agents;
    private String targetAgentName;
    private static boolean sendMsg = false;

    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent(getLocalName());
                msg.addReceiver(new AID("godAgent", AID.ISLOCALNAME));
                send(msg);
                System.out.println("msg was sent "+ msg.getContent());
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                recieveFromAgent();
                if (isNotifyAgent()){
                    notifyAgent(getMsg());
                    setNotifyAgent(false);
                }
            }
        });
    }
    private void notifyAgent(String message){
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(getLocalName()+";"+message);
        msg.addReceiver(new AID(getTargetAgentName(), AID.ISLOCALNAME));
        send(msg);
        System.out.println("message was send");
    }

    private void recieveFromAgent(){
        ACLMessage msg = receive();
        if (msg != null){
            if (msg.getContent().contains("accept")){
                System.out.println("acceptMsg "+msg.getContent());
                String[] acceptMsg = msg.getContent().split(";");
                setOutput(Integer.parseInt(acceptMsg[2]));
            } else {
                setTargetAgentName(msg.getContent());
                System.out.println("msg "+msg.getContent());
            }
        }
    }

    public String getTargetAgentName() {
        return targetAgentName;
    }

    public void setTargetAgentName(String targetAgentName) {
        this.targetAgentName = targetAgentName;
    }

    public static boolean isSendMsg() {
        return sendMsg;
    }

    public static void setSendMsg(boolean sendMsg) {
        MotionSensorDevice.sendMsg = sendMsg;
    }
}
