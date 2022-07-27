package osu.sladcik.devices.temperature;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {
    private static String location;
    private static String deviceName;
    public static final String NAME = "TEMPERATURE";
    public static void main(String[] args) {
        location = args[0];
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, location);
        ContainerController cc = rt.createAgentContainer(p);
        AgentController ac;
        deviceName = "device_"+NAME;
        try {
            ac = cc.createNewAgent(deviceName, "osu.sladcik.devices.temperature.Device", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
