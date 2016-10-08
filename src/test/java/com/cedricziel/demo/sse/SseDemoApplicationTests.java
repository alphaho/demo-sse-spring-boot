package com.cedricziel.demo.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SseDemoApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate template;
    private URL base;
    private URL stream;
    private URL chat;
    private String host;

    @Before
    public void setUp() throws Exception {
        this.host = "http://localhost:" + port;
        this.base = new URL(host + "/");
        this.stream = new URL(host + "/stream");
        this.chat = new URL(host + "/chat");
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void shouldReceiveMessageFromSseEmitter() throws Exception {
        scheduleMessageSender();
        scheduleStopper();

        createListener();
    }

    private void createListener() throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse httpResponse = client.execute(HttpHost.create(host), new HttpGet(stream.toString()));
        HttpEntity httpEntity = httpResponse.getEntity();

        try (InputStream inputStream = httpEntity.getContent();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String string;
            while ((string = reader.readLine()) != null) {
                if (!Objects.equals(string, "")) {
                    System.out.println(string);
                }
            }
        }
    }

    private void scheduleMessageSender() {
        ObjectMapper objectMapper = new ObjectMapper();
        new Thread(
                () -> {
                    try {
                        Thread.sleep(2000);
                        final Message message = new Message("client", "Hey there");
                        template.postForEntity(chat.toString(), message, Message.class);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        ).start();
    }

    private void scheduleStopper() {
        new Thread(() -> {
            try {
                Thread.sleep(20000);
                final Message message = new Message("stopper", "stop");
                template.postForEntity(chat.toString(), message, Message.class);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
