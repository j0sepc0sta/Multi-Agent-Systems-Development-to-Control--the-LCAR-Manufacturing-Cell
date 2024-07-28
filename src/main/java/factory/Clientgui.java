package factory;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Clientgui extends Agent {
    private JFrame frame;
    private JPanel p;
    private JTabbedPane ClientAgentPane;
    private JTextField productField;
    private JButton addButton;
    private JTextField quantityField;
    private int quantity;
    private int sentCount;
    String product;
    public static final Logger logger = LoggerFactory.getLogger(Clientgui.class);
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Client");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            logger.error(fe.getMessage(), fe);
        }
        frame = new JFrame("Client Agent");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setContentPane(p);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                try {
                    product = productField.getText().trim();
                    quantity = Integer.parseInt(quantityField.getText().trim());
                    ACLMessage acl = new ACLMessage(ACLMessage.REQUEST);
                        if (product.equalsIgnoreCase("A")) {
                            acl.setContent(product+":"+quantity);
                            acl.addReceiver(new AID("ProductA", AID.ISLOCALNAME));
                            send(acl);
                        } else if (product.equalsIgnoreCase("B")) {
                            acl.setContent(product+":"+quantity);
                            acl.addReceiver(new AID("ProductB", AID.ISLOCALNAME));
                            send(acl);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Only A or B are accepted.", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "Invalid values. " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    logger.error(e.getMessage(), e);
                }
            }
        });
        addBehaviour(new Done());
        frame.setVisible(true);

    }
    private class Done extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String content = msg.getContent();
                System.out.println(getLocalName() + " received from: " + msg.getSender().getLocalName() + " - " + content);
                productField.setText("");
                quantityField.setText("");
                JOptionPane.showMessageDialog(frame, "Order Completed", "Completed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                block();
            }
        }
    }
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            logger.error(fe.getMessage(), fe);
        }
        System.out.println(getLocalName() + " Goodbye!");
        this.doDelete();
    }
}


