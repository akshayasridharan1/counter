package com.rest.counter.service;

import org.springframework.stereotype.Service;

@Service
public class CounterServiceImpl implements CounterService {

    @Override
    public int countWords(String word, String a[])
    {
        int count = 0;
        for (int i = 0; i < a.length; i++)
        {
            if (word.toLowerCase().contains(a[i].toLowerCase()))
                count++;
        }
        return count;
    }
}
