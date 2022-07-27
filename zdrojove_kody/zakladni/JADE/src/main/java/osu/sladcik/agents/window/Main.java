package osu.sladcik.agents.window;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
        String location = args[0]; // před spuštěním agenta se dodává parametr IP adresy, na které běží Middleware.jar
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, location); // nastavování umístění, kde agent poběží
        p.setParameter(Profile.CONTAINER_NAME, "window"); //nastavování kontejneru, ve kterém agent poběží
        ContainerController cc = rt.createAgentContainer(p); //vytváření agenta
        AgentController ac;

        try {
            // zvolení třídy, kde se agent nachází
            ac = cc.createNewAgent("window", "osu.sladcik.agents.window.WindowAgent", null);
            ac.start(); // spuštění agenta
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
