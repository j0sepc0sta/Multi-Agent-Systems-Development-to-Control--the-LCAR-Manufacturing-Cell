package modbus;
import factory.Resource;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;

public class ReadPlc {
    public static boolean m0Value,m25Value,m1Value, m4Value, m12Value, m16Value, m24Value, m26Value, m85Value, m86Value, m87Value, m88Value,m89Value,m90Value;
    public static boolean newM0Value,newM1Value,newM4Value,newm12Value,newm16Value,newm24Value,newm25Value,newm26Value;
    public static boolean newm85Value,newm86Value, newm87Value,newm88Value;
    public static void ReadPlc() {
        String connection = "modbus-tcp:tcp://" + Resource.ip + ":" + Resource.port + "?timeout=" + Resource.timeout;
        try (PlcConnection plcConnection = PlcDriverManager.getDefault().getConnectionManager().getConnection(connection)) {
            if (!plcConnection.getMetadata().isReadSupported()) {
                Resource.logger.info("Connection status with TCP/IP: {}", plcConnection.isConnected() ? "CONNECTED" : "DISCONNECTED");
                return;
            }
            PlcReadRequest.Builder readBuilder = plcConnection.readRequestBuilder();
            readBuilder.addTagAddress("M0", "coil:1");//PB_S_T_1
            readBuilder.addTagAddress("M1", "coil:2");//PB_S_T_2
            readBuilder.addTagAddress("M4", "coil:5");//IB_S_T_1
            readBuilder.addTagAddress("M12", "coil:13");//IB_S_T_4
            readBuilder.addTagAddress("M16", "coil:17");//IA_S_T_1
            readBuilder.addTagAddress("M24", "coil:25");//IA_S_T_4
            readBuilder.addTagAddress("M25", "coil:26");//PA_S_T_1
            readBuilder.addTagAddress("M26", "coil:27");//PA_S_T_2
            readBuilder.addTagAddress("M85", "coil:86");//PA_OK
            readBuilder.addTagAddress("M86", "coil:87");//PB_OK
            readBuilder.addTagAddress("M87", "coil:88");//IA_OK
            readBuilder.addTagAddress("M88", "coil:89");//IB_OK
            readBuilder.addTagAddress("M89", "coil:90");//PB_Ciclo
            readBuilder.addTagAddress("M90", "coil:91");//PA_Ciclo
            PlcReadRequest readRequest = readBuilder.build();
            PlcReadResponse readResponse = readRequest.execute().get();
            m0Value = readResponse.getBoolean("M0");
            //m0Value=false;
            Resource.notifyListeners("M0", m0Value);
            m1Value = readResponse.getBoolean("M1");
            Resource.notifyListeners("M1", m1Value);
            m4Value = readResponse.getBoolean("M4");
            Resource.notifyListeners("M4", m4Value);
            m12Value = readResponse.getBoolean("M12");
            Resource.notifyListeners("M12", m12Value);
            m16Value = readResponse.getBoolean("M16");
            Resource.notifyListeners("M16", m16Value);
            m24Value = readResponse.getBoolean("M24");
            Resource.notifyListeners("M24", m24Value);
            m25Value = readResponse.getBoolean("M25");
            Resource.notifyListeners("M25", m25Value);
            m26Value = readResponse.getBoolean("M26");
            Resource.notifyListeners("M26", m26Value);
            m85Value = readResponse.getBoolean("M85");
            Resource.notifyListeners("M85", m85Value);
            m86Value = readResponse.getBoolean("M86");
            Resource.notifyListeners("M86", m86Value);
            m87Value = readResponse.getBoolean("M87");
            Resource.notifyListeners("M87", m87Value);
            m88Value = readResponse.getBoolean("M88");
            Resource.notifyListeners("M88", m88Value);
            m89Value = readResponse.getBoolean("M89");
            Resource.notifyListeners("M89", m89Value);
            m90Value = readResponse.getBoolean("M90");
            Resource.notifyListeners("M90", m90Value);
        } catch (PlcConnectionException e) {
            Resource.logger.error("Error establishing PLC connection:", e.getMessage());
        } catch (Exception e) {
            Resource.logger.error("Error during PLC connection closure:", e.getMessage());
        }
    }
}
