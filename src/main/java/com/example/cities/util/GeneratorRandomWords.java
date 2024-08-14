package com.example.cities.util;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class GeneratorRandomWords implements Generator {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int WORD_LENGTH = 10;
    private static final Random RANDOM = new Random();

    @Override
    public String generateRandomWord(char lastCharacter) {
        StringBuilder builder = new StringBuilder(WORD_LENGTH);
        builder.append(lastCharacter);
        for (int i = 1; i < WORD_LENGTH; i++) {
            int index = RANDOM.nextInt(ALPHABET.length());
            builder.append(ALPHABET.charAt(index));
        }
        return builder.toString();
    }
}
