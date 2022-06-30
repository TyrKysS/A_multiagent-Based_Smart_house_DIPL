package osu.sladcik.agents;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;

public class Middleware {
    /*
    Vytváření prostředí prostřednictvím kterého mohou agenty komunikovat
    nastavení IP adresy, prostřednictvím kterého bude probíhat komunikace (localhost = lokální zařízení se svou IP adresou)
    Profile.GUI = chceme používat grafické rozhraní disponující JADE framework pro správu agentů
    Vytváří se hlavní kontejner, ve kterém běží základní agenty umožňující správu a práci s dalšími agenty
     */
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "true");
        ContainerController cc = rt.createMainContainer(p);

    }
}
