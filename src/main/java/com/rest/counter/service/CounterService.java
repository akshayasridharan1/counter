package com.rest.counter.service;

import java.util.Map;

public interface CounterService {

    int countWords(String word, String text);

    Map<String, Integer> buildWordMap(String content, String topCount);

    }
