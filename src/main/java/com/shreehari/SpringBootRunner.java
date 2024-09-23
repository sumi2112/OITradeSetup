package com.shreehari;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class SpringBootRunner {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(SpringBootRunner.class);
        Runtime rt = Runtime.getRuntime();
        rt.exec("rundll32 url.dll,FileProtocolHandler "
                + "https://api.upstox.com/v2/login/authorization/dialog?response_type=code&client_id=a01f8052-4d15-43dc-b0ea-695bba0e0ba7&redirect_uri=http://localhost:8080/auth/accessToken");

    }

    //Way to parse command line arguments and provide them to the futher function call with distinction
    //in name of the arguments.
    private static ArgumentParser parseArgs() {
        ArgumentParser parser = ArgumentParsers.newFor("SpringBootRunner").build();

        parser.addArgument("--runType")
                .dest("runType")
                .type(String.class)
                .required(true);

        parser.addArgument("--runDate")
                .dest("runDate")
                .type(String.class)
                .required(false);
        return parser;
    }
}
