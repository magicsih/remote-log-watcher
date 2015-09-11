package app;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RemoteDebuggerApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(RemoteDebuggerApplication.class, args);
    }
}
