package factory;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;

public class JadeContainer {
	public static Runtime rt;
	private ContainerController container;
	private ContainerController containerproducts;
	private ContainerController containerclients;

	public JadeContainer() {
		Runtime rt = Runtime.instance();
		rt.setCloseVM(true);
		// Initialize the Resource container
		ProfileImpl profile = new ProfileImpl(false);
		profile.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		profile.setParameter(ProfileImpl.MAIN_PORT, "1099");
		profile.setParameter(ProfileImpl.CONTAINER_NAME, "Resource Agent Container");
		container = rt.createAgentContainer(profile);
		// Initialize the Product container
		ProfileImpl profileproducts = new ProfileImpl(false);
		profileproducts.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		profileproducts.setParameter(ProfileImpl.MAIN_PORT, "1099");
		profileproducts.setParameter(ProfileImpl.CONTAINER_NAME, "Product Agent Container");
		containerproducts = rt.createAgentContainer(profileproducts);
		// Initialize the Clients container
		ProfileImpl profileclients = new ProfileImpl(false);
		profileclients.setParameter(ProfileImpl.MAIN_HOST, "localhost");
		profileclients.setParameter(ProfileImpl.MAIN_PORT, "1099");
		profileclients.setParameter(ProfileImpl.CONTAINER_NAME, "Client Agent Container");
		containerclients = rt.createAgentContainer(profileclients);
	}
	public ContainerController getContainer() {
		return container;
	}
	public ContainerController getContainerproduct() {
		return containerproducts;
	}
	public ContainerController getContainerclients() {
		return containerclients;
	}
}
