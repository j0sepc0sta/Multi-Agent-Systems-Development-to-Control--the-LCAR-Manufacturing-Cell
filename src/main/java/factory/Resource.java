package factory;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import modbus.*;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class Resource extends Agent {
    private IMqttClient mqttClient;
    private static String broker,topic;
    String skillname, skill1name, fileName;
    public static String ip;
    public static int port,timeout;
    public static final Logger logger = LoggerFactory.getLogger(Resource.class);
    private final Random rand = new Random();
    private static final List<ChangeListener> listeners = new ArrayList<>();
    public static void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }
    //public  class MyListener implements ChangeListener {
    //    @Override
    //    public void onValueChanged(String variableName, boolean newValue) {
     //   }
    //}

    public interface ChangeListener {
        void onValueChanged(String variableName, boolean newValue);
    }
/*
    public static void addChangeListener(ChangeListener listener) {
        if (listener != null) {
            listeners.add(listener); // Ensure we add only non-null listeners
        }
    }



    public static void addChangeListener(ChangeListener listener) {
        if (listener != null) {
            synchronized (listeners) {
                listeners.add(listener); // Ensure we add only non-null listeners
                System.out.println("Added listener: " + listener);
            }
        } else {
            System.out.println("Attempted to add null listener");
        }
    }

 */public static void addChangeListener(ChangeListener listener) {
    if (listener != null) {
        synchronized (listeners) {
            System.out.println("Adding listener: " + listener);
            try {
                listeners.add(listener); // Ensure we add only non-null listeners
                System.out.println("Listener added successfully. Total listeners: " + listeners.size());
            } catch (Exception e) {
                logger.error("Exception while adding listener: " + e.getMessage());
            }
        }
    } else {
        System.out.println("Attempted to add null listener");
    }
}



    public static void notifyListeners(String variableName, boolean newValue) {
        for (ChangeListener listener : new ArrayList<>(listeners)) {
            listener.onValueChanged(variableName, newValue);
        }
    }
    @Override
    protected void setup() {
        String client = getLocalName() + "-" + UUID.randomUUID();
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Resource");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        fileName = getLocalName() + ".json";

        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)));
            JSONObject jsonObject = new JSONObject(content);
            if (jsonObject.has("modbus")) {
                JSONObject modbusObject = jsonObject.getJSONObject("modbus");
                ip = modbusObject.getString("ip");
                port = modbusObject.getInt("port");
                timeout = modbusObject.getInt("timeout");
            }
            if (jsonObject.has("mqtt")) {
                JSONObject mqttObject = jsonObject.getJSONObject("mqtt");
                broker = mqttObject.getString("broker");
                topic = mqttObject.getString("topic");
            }
            if (jsonObject.has("skills")) {
                Object skillsObj = jsonObject.get("skills");
                if (skillsObj instanceof JSONArray) {
                    JSONArray skills = (JSONArray) skillsObj;
                    for (int i = 0; i < skills.length(); i++) {
                        JSONObject skill = skills.getJSONObject(i);
                        ServiceDescription skillSd = new ServiceDescription();
                        skillSd.setType("Skill");
                        skillSd.setName(skill.getString("skill"));
                        skillname = skill.getString("skill");
                        dfd.addServices(skillSd);
                    }
                } else if (skillsObj instanceof JSONObject) {
                    JSONObject skills = (JSONObject) skillsObj;
                    ServiceDescription skillSd = new ServiceDescription();
                    skillSd.setType("Skill");
                    skillSd.setName(skills.getString("skill"));
                    skill1name = skills.getString("skill");
                    dfd.addServices(skillSd);
                }
            }
            DFService.register(this, dfd);
            mqttClient = new MqttClient(broker, client, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10); // Set your desired timeout
            mqttClient.connect(options);
        } catch (FIPAException | IOException | JSONException | MqttException e) {
            logger.error(e.getMessage(), e);
        }

        ModbusPA.modbusPA(false);
        ModbusPB.modbusPB(false);
        ModbusIA.modbusIA(false);
        ModbusIB.modbusIB(false);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> ReadPlc.ReadPlc(), 1, 1, TimeUnit.SECONDS);

        addBehaviour(new ProductRequest());
    }



    private class ProductRequest extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msgCFP = myAgent.receive(mt);
            //String productType = null;
            if (msgCFP != null) {
                String contentmsg = msgCFP.getContent();
                ACLMessage reply = msgCFP.createReply();
                int randomPrice1 = rand.nextInt(100) + 1;
                reply.setContent(contentmsg + ":" + randomPrice1);
                send(reply);
            } else {
                block();
            }

            MessageTemplate mtt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msgg = myAgent.receive(mtt);
            if (msgg != null) {
                String productType = null;
                String contentmsg = msgg.getContent();
                if (msgg.getContent().startsWith("sold:")) {
                    String[] parts = msgg.getContent().split(":");
                    if (parts.length == 2) {
                        productType = parts[1];
                    }
                    ACLMessage replyy = msgg.createReply();

                    switch (getLocalName()) {
                        case "PunchingA":
                            System.out.println("Received message2 in " + getLocalName() + ": " + contentmsg);
                            addChangeListener(new ChangeListener() {
                                boolean m25PreviousValue = false;
                                @Override
                                public void onValueChanged(String variableName, boolean newValue) {
                                    if (variableName.equals("M25")) {
                                        if (newValue) {
                                            System.out.println("m25Value changed to true!");
                                            ModbusPA.modbusPA(true);
                                        } else {
                                            System.out.println("m25Value changed to false!");
                                            if (m25PreviousValue) {
                                                System.out.println("M25 changed from true to false! Doing something...");
                                                Resource.removeChangeListener(this);
                                                replyy.setPerformative(ACLMessage.INFORM);
                                                replyy.setContent("Done:" + parts[1]);
                                                send(replyy);
                                                ModbusPA.modbusPA(false);
                                            }
                                        }
                                        m25PreviousValue = newValue;
                                    }
                                }
                            });
                            break;

                        case "PunchingB":
                            System.out.println("Received message2 in " + getLocalName() + ": " + contentmsg);
                            addChangeListener(new ChangeListener() {
                                boolean m0PreviousValue = false;
                                @Override
                                public void onValueChanged(String variableName, boolean newValue) {
                                    if (variableName.equals("M0")) {
                                        if (newValue) {
                                            System.out.println("M0 changed to true!");
                                            ModbusPB.modbusPB(true);
                                        } else {
                                            System.out.println("M0 changed to false!");
                                            if (m0PreviousValue) {
                                                System.out.println("M0 changed from true to false! Doing something...");
                                                Resource.removeChangeListener(this);
                                                replyy.setPerformative(ACLMessage.INFORM);
                                                replyy.setContent("Done:" + parts[1]);
                                                send(replyy);
                                                ModbusPB.modbusPB(false);
                                            }
                                        }
                                        m0PreviousValue = newValue;
                                    }
                                }
                            });
                            break;

                        case "IndexedA":
                            System.out.println("Received message2 in " + getLocalName() + ": " + contentmsg);
                            addChangeListener(new ChangeListener() {
                                boolean m16PreviousValue = false;
                                @Override
                                public void onValueChanged(String variableName, boolean newValue) {
                                    if (variableName.equals("M16")) {
                                        if (newValue) {
                                            System.out.println("M16 changed to true!");
                                            ModbusIA.modbusIA(true);
                                        } else {
                                            System.out.println("M16 changed to false!");
                                            if (m16PreviousValue) {
                                                System.out.println("M16 changed from true to false! Doing something...");
                                                Resource.removeChangeListener(this);
                                                replyy.setPerformative(ACLMessage.INFORM);
                                                replyy.setContent("Done:" + parts[1]);
                                                send(replyy);
                                                ModbusIA.modbusIA(false);
                                            }
                                        }
                                        m16PreviousValue = newValue;
                                    }
                                }
                            });
                            break;

                        case "IndexedB":
                            System.out.println("Received message2 in " + getLocalName() + ": " + contentmsg);
                            addChangeListener(new ChangeListener() {
                                boolean m4PreviousValue = false;
                                @Override
                                public void onValueChanged(String variableName, boolean newValue) {
                                    if (variableName.equals("M4")) {
                                        if (newValue) {
                                            System.out.println("M4 changed to true!");
                                            ModbusIB.modbusIB(true);
                                        } else {
                                            System.out.println("M4 changed to false!");
                                            if (m4PreviousValue) {
                                                System.out.println("M4 changed from true to false! Doing something...");
                                                Resource.removeChangeListener(this);
                                                replyy.setPerformative(ACLMessage.INFORM);
                                                replyy.setContent("Done:" + parts[1]);
                                                send(replyy);
                                                ModbusIB.modbusIB(false);
                                            }
                                        }
                                        m4PreviousValue = newValue;
                                    }
                                }
                            });
                            break;

                        case "ABBRobot":
                            System.out.println("Received in " + getLocalName());
                            try {
                                Thread.sleep(10000);//5 seconds
                            } catch (InterruptedException e) {
                                logger.error(e.getMessage());
                            }
                            replyy.setPerformative(ACLMessage.INFORM);
                            replyy.setContent("Done:" + parts[1]);
                            send(replyy);
                            break;

                        case "Inspection":
                            try {
                                mqttClient.subscribe(topic, (receivedTopic, message) -> {
                                    String receivedData = new String(message.getPayload());
                                    System.out.println(receivedTopic);
                                    System.out.println("Received data: " + receivedData);
                                    if (receivedData.equals("Good")) {
                                        replyy.setPerformative(ACLMessage.INFORM);
                                        replyy.setContent("Good:" + parts[1]);
                                    } else if (receivedData.equals("Bad")) {
                                        replyy.setPerformative(ACLMessage.INFORM);
                                        replyy.setContent("Bad:" + parts[1]);
                                    }
                                    send(replyy);
                                });
                            } catch (MqttException e) {
                                logger.error(e.getMessage(), e);
                            }
                            break;

                        default:
                            break;
                    }

                } else {
                    block();
                }
            } else {
                block();
            }
        }
    }


    @Override
    protected void takeDown() {
        try {
            mqttClient.disconnect();
            mqttClient.close();
            DFService.deregister(this);
        } catch (MqttException | FIPAException fe) {
            logger.error(fe.getMessage());
        }
        System.out.println(getLocalName() + " Goodbye!");
        this.doDelete();


    }
}
