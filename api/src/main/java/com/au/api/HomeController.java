package com.au.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@Slf4j
public class HomeController {

    @GetMapping("/user")
    public User getUser(@RequestParam Integer vuNum) throws InterruptedException {
        String generatedName = Math.abs(new Random().nextLong()) + "-" + vuNum;
        int random = getRandomNumberUsingNextInt(0, 10);
        if (random == 5) {
            Thread.sleep(1000);
        } else {
            Thread.sleep(500);
        }
        if (random == 9) {
            throw new InternalError("Generated exception");
        }
        return User.builder().name(generatedName).build();
    }

    public int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}
