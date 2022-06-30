package osu.sladcik.resource;

import jade.core.AID;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Messages {
    private static List<String> listOfOnlineAgents = new ArrayList<>();

    public static void sendMessageStatus(String agentName, String reciever, String message){
        System.out.println(agentName+" - byla odeslána zpráva - "+reciever+" - text zprávy - "+message);
    }
    public static void sendMessageStatus(String agentName, String message){
        System.out.println(agentName+" - status byl úspěšně odeslán, text zprávy - "+message);
    }
    public static void sendMessageStatus(String agentName, boolean state){
        System.out.println(agentName+" - status byl úspěšně změněn na - "+state);
    }
    public static void agentStatus(String agentName){
        System.out.println(agentName+" - byl spuštěn");
    }
    // potvrzení, že zpráva byla přijata
    public static void recieveStatusOfAgents(String agentName, String senderAgent) {
        System.out.println(agentName+" - potvrzení bylo přijato  od "+senderAgent);
    }
    public static void recieveMessage(String agentName, String senderAgent, String message) {
        System.out.println(agentName+" - zpráva byla přijata - "+senderAgent+" - text zprávy - "+message);
    }
    public static void recieveMessage(String agentName, String message) {
        System.out.println(agentName+" - zpráva byla přijata, text zprávy - "+message);
    }
    // okno zobrazující aktuální stav agenta
    public static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void agentNotFound(String localName, String targetAgent) {
        System.out.println(localName+" - agent nebyl nalezen - "+targetAgent);
    }

    public static void MessageNotSend(String agentName, String targetAgent) {
        System.out.println(agentName+" - zprávu nebylo možné odeslat - "+targetAgent);
    }

}
