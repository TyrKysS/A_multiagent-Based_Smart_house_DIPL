package osu.sladcik.Middleware;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import osu.sladcik.resource.TypeAgents;

public class Main {
    /**
     * sjednotit posílání zpráv: odesílatek;zpráva
     * zjistit proč se teplota zadává přes middleware
     * doupravit GUI agenta tak, aby mohl komunikovat
     */


    public static ContainerController cc;
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "true");
        p.setParameter(Profile.CONTAINER_NAME, "FIoT");
        cc = rt.createMainContainer(p);
        AgentController ac;
        try {
            ac = cc.createNewAgent(String.valueOf(TypeAgents.GOD),"osu.sladcik.agents.GodAgents.GodAgent", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
        try {
            ac = cc.createNewAgent("ObserverAgent","osu.sladcik.agents.ObserverAgents.ObserverAgent", null);
            ac.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }


    }
}
