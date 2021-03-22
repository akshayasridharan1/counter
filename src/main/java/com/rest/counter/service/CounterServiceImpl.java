package com.rest.counter.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CounterServiceImpl implements CounterService {

    @Override
    public int countWords(String word, String text)
    {
        int count = StringUtils.countMatches(text.toLowerCase(), word.toLowerCase());

        return count;
    }
}
