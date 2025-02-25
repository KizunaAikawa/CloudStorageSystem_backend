package ryu.cloudstoragesystem_backend.file;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Component
public class ShareCodePool {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int MAX_LENGTH = 6;
    private static final Random RANDOM = new Random();
    private Set<String> codes = new HashSet<>();

    private String generate() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MAX_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public synchronized String get() {
        String code;
        do {
            code = generate();
        } while (codes.contains(code));
        codes.add(code);
        return code;
    }

    public synchronized void release(String code) {
        codes.remove(code);
    }
}
