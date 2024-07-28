package factory;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        Main mainInstance = new Main();
        mainInstance.container();
    }
    private void container(){
        ContainerController mainContainer = new MainContainer().getContainer();
        JadeContainer jadeContainer = new JadeContainer();
        ContainerController containerresource = jadeContainer.getContainer();
        if (containerresource != null) {
            try {
                AgentController punchingA = containerresource.createNewAgent("PunchingA", "factory.ResourceSimulator", null);//"factory.ResourceV4"
                punchingA.start();
                AgentController punchingB = containerresource.createNewAgent("PunchingB", "factory.ResourceSimulator", null);
                punchingB.start();
                AgentController indexedA = containerresource.createNewAgent("IndexedA", "factory.ResourceSimulator", null);
                indexedA.start();
                AgentController indexedB = containerresource.createNewAgent("IndexedB", "factory.ResourceSimulator", null);
                indexedB.start();
                AgentController ABBRobot = containerresource.createNewAgent("ABBRobot", "factory.ResourceSimulator", null);
                ABBRobot.start();
                AgentController inspection = containerresource.createNewAgent("Inspection", "factory.ResourceSimulator", null);
                inspection.start();
            } catch (StaleProxyException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        } else {
            logger.log(Level.SEVERE, "Container Resource is null");
        }
        ContainerController containerproduct = jadeContainer.getContainerproduct();
        if (containerproduct != null) {
            try {
                AgentController productA = containerproduct.createNewAgent("ProductA", "factory.Product", null);
                productA.start();
                AgentController productB = containerproduct.createNewAgent("ProductB", "factory.Product", null);
                productB.start();
            } catch (StaleProxyException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        } else {
            logger.log(Level.SEVERE, "Container Product is null");
        }
        ContainerController containerclient = jadeContainer.getContainerclients();
        if (containerclient != null) {
            try {
                AgentController client = containerclient.createNewAgent("Client", "factory.Clientgui", null);
                client.start();
            } catch (StaleProxyException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        } else {
            logger.log(Level.SEVERE, "Container Client is null");
        }
    }

}
