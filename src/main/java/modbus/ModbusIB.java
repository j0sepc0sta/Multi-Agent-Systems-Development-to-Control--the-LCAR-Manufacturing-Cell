package modbus;

import factory.Resource;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;

public class ModbusIB {
    public static void modbusIB(boolean status) {
        String connection = "modbus-tcp:tcp://" + Resource.ip + ":" + Resource.port + "?timeout=" + Resource.timeout;
        try (PlcConnection plcConnection = PlcDriverManager.getDefault().getConnectionManager().getConnection(connection)) {
            if (!plcConnection.getMetadata().isReadSupported()) {
                Resource.logger.info("Connection status with TCP/IP: {}", plcConnection.isConnected() ? "CONNECTED" : "DISCONNECTED");
                return;
            }
            PlcWriteRequest.Builder writeBuilder = plcConnection.writeRequestBuilder();
            writeBuilder.addTagAddress("M84", "coil:85", status);
            PlcWriteRequest writeRequest = writeBuilder.build();
            PlcWriteResponse writeResponse = writeRequest.execute().get();
            if (writeResponse.getResponseCode("M84") == PlcResponseCode.OK) {
                System.out.println("Successfully wrote to AgentIB:"+status);
            } else {
                System.out.println("Failed to write to AgentIB. Response code: " + writeResponse.getResponseCode("M84"));
            }
        } catch (PlcConnectionException e) {
            Resource.logger.error("Error establishing PLC connection:", e.getMessage());
            //e.printStackTrace();
        } catch (Exception e) {
            Resource.logger.error("Error during PLC connection closure:", e.getMessage());
            //e.printStackTrace();
        }
    }
}
