package osu.sladcik;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Middleware {
    public static ContainerController cc;
    public static void main (String[]args){
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "true");
        p.setParameter(Profile.CONTAINER_NAME, "FIoT");
        cc = rt.createMainContainer(p);
        AgentController ac;
        try {
            ac = cc.createNewAgent("godAgent", "osu.sladcik.agents.godAgents.GodAgent", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}