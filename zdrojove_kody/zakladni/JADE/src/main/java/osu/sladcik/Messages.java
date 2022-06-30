package osu.sladcik;

public class Messages {
    public static void agentIsRunning(String agentName){
        System.out.println(agentName+" - byl spuštěn");
    }
    public static void agentNotFound(String agentName, String targetAgentName){
        System.out.println(agentName+" - "+targetAgentName+" agent nebyl nalezen");
    }
    public static void confirmRecieveMessage(String agentName, String sourceAgentName, String message){
        System.out.println(agentName+" - potvrzující zpráva od "+sourceAgentName+": "+message);
    }
    public static void confirmSendMessage(String agentName, String targetAgent, String message){
        System.out.println(agentName+" - zpráva byla úspěšně odeslána: "+targetAgent+": "+message);
    }
    public static void showActualStatus(String agentName, String status){
        switch (agentName){
            case "window":
                System.out.println(agentName+" - je otevřeno: "+status);
                break;
            case "radiator":
                System.out.println(agentName+" - je spuštěno: "+status);
                break;
        }
    }
    public static void getMessage(String agentName, String sourceAgent, String message){
        System.out.println(agentName+" - zpráva byla úspěšně přijata: "+sourceAgent+": "+message);
    }
}
