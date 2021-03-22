package com.rest.counter.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CounterServiceImpl implements CounterService {

    @Override
    public int countWords(String word, String text)
    {
        return StringUtils.countMatches(text.toLowerCase(), word.toLowerCase());
    }

    public Map<String, Integer> buildWordMap(String content, String topCount) {
        Map<String, Integer> wordMap = new HashMap<>();
        Pattern pattern = Pattern.compile("\\s+");
        String line = content.toLowerCase();
        line = line.replace(".", "").replace(",", "");
        String[] words = pattern.split(line);
        for (String word : words) {
            if (wordMap.containsKey(word)) {
                wordMap.put(word, (wordMap.get(word) + 1));
            } else {
                wordMap.put(word, 1);
            }
        }
        return wordMap.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .limit(Integer.valueOf(topCount))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
