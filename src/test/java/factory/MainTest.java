package factory;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    public void JadeInitializationTime() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        long startTime = System.currentTimeMillis();
        Main.main(new String[]{});
        long endTime = System.currentTimeMillis();

        System.setOut(originalOut);

        long totalTime = endTime - startTime;
        System.out.println("Tempo total de inicialização do Jade: " + totalTime + " ms");

    }
}
