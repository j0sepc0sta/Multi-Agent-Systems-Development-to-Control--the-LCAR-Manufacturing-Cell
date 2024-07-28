package factory;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Product extends Agent {
    private static final Logger LOGGER = Logger.getLogger(Product.class.getName());
    private AID bestSeller;
    private int bestPrice;
    private int offersReceived = 0;
    private String productType;
    private int step = 0;
    private int step1 = 0;
    private long lastOfferTime = 0;
    private boolean offerSent = false;
    private int cycles = 0;
    private int targetCycles = 0;
    private long startTime;
    boolean danildo = false;
    boolean jpc = false;
    long endTime;
    long duration;
    private List<String> partASequence = new ArrayList<>();
    private List<String> partBSequence = new ArrayList<>();

    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Product");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            LOGGER.log(Level.SEVERE, "An exception occurred during registration", fe);
        }

        partASequence = loadSequenceFromFile(getLocalName() + ".json");
        partBSequence = loadSequenceFromFile(getLocalName() + ".json");

        addBehaviour(new ProductRequest());
    }

    private List<String> loadSequenceFromFile(String fileName) {
        List<String> sequence = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)));
            JSONObject jsonObject = new JSONObject(content);

            if (jsonObject.has("sequences")) {
                JSONArray sequences = jsonObject.getJSONArray("sequences");
                for (int i = 0; i < sequences.length(); i++) {
                    JSONObject sequenceObject = sequences.getJSONObject(i);
                    sequence.add(sequenceObject.getString("sequence"));
                }
            }
        } catch (IOException | org.json.JSONException e) {
            LOGGER.log(Level.SEVERE, "Error during file reading", e);
        }
        return sequence;
    }

    protected void PartA() {
        if (step == 0 && !danildo) {
            startTime = System.currentTimeMillis(); // Registrar o tempo inicial
            danildo = true;
        }

        if (step < partASequence.size()) {
            String skill = partASequence.get(step);
            Search(skill, "A");
        } else {
            step = 0;
            cycles++;
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            LOGGER.log(Level.INFO, " (PARCIAL) It took " + duration + " milliseconds to make " + targetCycles + " quantities of Part A");
            if (cycles == targetCycles) {
                endTime = System.currentTimeMillis();
                duration = endTime - startTime;
                LOGGER.log(Level.INFO, " (TOTAL) It took " + duration + " milliseconds to make " + targetCycles + " quantities of Part A");
                ACLMessage acl = new ACLMessage(ACLMessage.CONFIRM);
                acl.setContent("Finish:A");
                acl.addReceiver(new AID("Client", AID.ISLOCALNAME));
                send(acl);
                cycles = 0;
                danildo = true;
            } else {
                PartA();
            }
        }
    }

    protected void PartB() {
        if (step1 == 0 && !jpc) {
            startTime = System.currentTimeMillis(); // Registrar o tempo inicial
            jpc = true;
        }

        if (step1 < partBSequence.size()) {
            String skill = partBSequence.get(step1);
            Search(skill, "B");
        } else {
            step1 = 0;
            cycles++;
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            LOGGER.log(Level.INFO, " (PARCIAL) It took " + duration + " milliseconds to make " + targetCycles + " quantities of Part B");
            if (cycles == targetCycles) {
                endTime = System.currentTimeMillis();
                duration = endTime - startTime;
                LOGGER.log(Level.INFO, " (TOTAL) It took " + duration + " milliseconds to make " + targetCycles + " quantities of Part B");
                ACLMessage acl = new ACLMessage(ACLMessage.CONFIRM);
                acl.setContent("Finish:B");
                acl.addReceiver(new AID("Client", AID.ISLOCALNAME));
                send(acl);
                cycles = 0;
            } else {
                PartB();
            }
        }
    }

    private class ProductRequest extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msgCFP = myAgent.receive(mt);
            if (msgCFP != null) {
                productType = msgCFP.getContent();
                System.out.println(getLocalName() + " received from: " + msgCFP.getSender().getLocalName() + " - " + productType);
                offersReceived = 0; // Reset the offers received
                offerSent = false; // Reset the offer sent flag
                Negotiation(msgCFP, productType);
            } else {
                block();
            }

            MessageTemplate mttT = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msgrequest = myAgent.receive(mttT);
            if (msgrequest != null) {
                String content = msgrequest.getContent();
                String[] parts = content.split(":");
                if (parts.length == 2) {
                    productType = parts[0];
                    targetCycles = Integer.parseInt(parts[1]); // Set the target number of cycles
                    System.out.println(getLocalName() + " received from: " + msgrequest.getSender().getLocalName() + " - Product: " + productType + ", Quantity: " + targetCycles);
                    if (productType.equalsIgnoreCase("A")) {
                        PartA();
                    } else if (productType.equalsIgnoreCase("B")) {
                        PartB();
                    }
                }
            } else {
                block();
            }

            MessageTemplate mtt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msgg = myAgent.receive(mtt);
            if (msgg != null) {
                String content = msgg.getContent();
                System.out.println(getLocalName() + " received message: '" + content + "' from " + msgg.getSender().getLocalName());
                if (content.startsWith("Done:")) {
                    String receivedProductType = content.split(":")[1];
                    if (receivedProductType.equalsIgnoreCase("A")) {
                        step++;
                        PartA();
                    } else if (receivedProductType.equalsIgnoreCase("B")) {
                        step1++;
                        PartB();
                    } else {
                        block();
                    }
                } else if (content.startsWith("Good:")) {
                    String receivedProductType = content.split(":")[1];
                    if (receivedProductType.equalsIgnoreCase("A")) {
                        step++;
                        PartA();
                    } else if (receivedProductType.equalsIgnoreCase("B")) {
                        step1++;
                        PartB();
                    } else {
                        block();
                    }
                } else  if (content.startsWith("Bad:")) {
                    String receivedProductType = content.split(":")[1];
                    if (receivedProductType.equalsIgnoreCase("A")) {
                        step++;
                        PartA();
                    } else if (receivedProductType.equalsIgnoreCase("B")) {
                        step1++;
                        PartB();
                    } else {
                        block();
                    }
                } else {
                    System.out.println(getLocalName() + " received non-'done' message: " + content);
                }
            } else {
                block();
            }
        }
    }

    private void Negotiation(ACLMessage msg, String productType) {
        if (productType.contains(":")) {
            String[] parts = productType.split(":");
            if (parts.length == 2) {
                try {
                    int price = Integer.parseInt(parts[1]);
                    if (bestSeller == null || price < bestPrice) {
                        bestPrice = price;
                        bestSeller = msg.getSender();
                    }
                    offersReceived++;
                    addBehaviour(new WaitForOffersBehaviour(2000));
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage());
                }
            } else {
                LOGGER.log(Level.SEVERE, "Invalid product type format: " + productType);
            }
        }
    }

    private void Search(String skillType, String productType) {
        try {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Skill");
            sd.setName(skillType);
            template.addServices(sd);
            DFAgentDescription[] result = DFService.search(this, template);
            System.out.println("Agents providing the '" + skillType + "' service:");
            for (DFAgentDescription agentDesc : result) {
                AID provider = agentDesc.getName();
                System.out.println(provider.getLocalName());
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                cfp.addReceiver(provider);
                cfp.setContent(productType);
                send(cfp);
            }
        } catch (FIPAException fe) {
            LOGGER.log(Level.SEVERE, "Search not working", fe);
        }
    }

    private class WaitForOffersBehaviour extends WakerBehaviour {
        public WaitForOffersBehaviour(long timeout) {
            super(Product.this, timeout);
        }

        @Override
        public void onWake() {
            if (!offerSent) {
                ACLMessage soldMsg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                soldMsg.addReceiver(bestSeller);
                soldMsg.setContent("sold:" + productType);
                System.out.println(getLocalName() + " sent 'sold' message to " + bestSeller.getLocalName());
                send(soldMsg);
                offerSent = true;
                bestSeller = null;
            }
        }
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            LOGGER.log(Level.SEVERE, "It's not possible to deregister DF Service", fe);
        }
        System.out.println(getLocalName() + " Goodbye!");
        this.doDelete();
    }
}
