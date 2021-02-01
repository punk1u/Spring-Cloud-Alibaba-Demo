package tech.punklu.contentcenter;

import org.springframework.web.client.RestTemplate;

public class Test {

    public static void main1(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0;i < 1000000; i ++){
            String object = restTemplate.getForObject("http://localhost:8082/getInstances", String.class);
            System.out.println(object);
        }
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0;i < 1000000; i ++){
            String object = restTemplate.getForObject("http://localhost:8082/test-a", String.class);
            System.out.println(object);
        }
    }
}
