package ru.job4j.site.component.safety;

import org.springframework.stereotype.Component;

@Component
public class StringEraseXssInspector implements Inspector<String> {

    @Override
    public String defuse(String sequence) {
        StringBuilder result = new StringBuilder();
        if (sequence != null) {
            for (int i = 0; i < sequence.length(); i++) {
                char symbol = sequence.charAt(i);
                if (symbol != '<' && symbol != '>' && symbol != '&' && symbol != '/') {
                    result.append(symbol);
                }
            }
            return result.toString();
        } else {
            return null;
        }
    }
}
