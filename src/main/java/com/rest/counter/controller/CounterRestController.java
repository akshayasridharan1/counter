package com.rest.counter.controller;

import com.rest.counter.model.Paragraph;
import com.rest.counter.service.CounterService;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static io.restassured.RestAssured.given;

@RestController
@RequestMapping("/counter-api")
public class CounterRestController {

    @Autowired
    private Environment environment;

    @Autowired
    private CounterService counterService;

    @GetMapping("/load")
    public ResponseEntity<Paragraph> loadParagraph() {
         return new ResponseEntity<>(new Paragraph(environment.getProperty("counter.load.paragraph")), HttpStatus.OK);
    }

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@RequestBody String searchText) throws ParseException {

        //form load paragraph rest-api call
        String port= environment.getProperty("server.port");
        String loadParagraph = "http://localhost:"+port+"/counter-api/load";

        //load paragraph rest-api response
        Response response = doGetRequest(loadParagraph);
        String text = response.jsonPath().getString("text");
        JSONArray jsonArray = getJsonArray(searchText);
        JSONObject outputJsonObject = getJsonObject(text, jsonArray);

        return new ResponseEntity<>(outputJsonObject.toJSONString(), HttpStatus.OK);
    }

    @PostMapping(value ="/highest/top/{value}", consumes = "text/csv")
    public ResponseEntity<String> highestCount(@PathVariable String value, @RequestBody String content) {
        Map<String, Integer> result = counterService.buildWordMap(content, value);
        String csvString="";
        for(Map.Entry entry : result.entrySet())
        {
            csvString = csvString.concat(" "+entry.getKey()+"|"+entry.getValue());
        }
        return new ResponseEntity<>(csvString, HttpStatus.OK);
    }


    private JSONObject getJsonObject(String text, JSONArray jsonArray) {
        //word counter
        Iterator<String> iterator = jsonArray.iterator();
        JSONArray responseJsonArray = new JSONArray();
        while(iterator.hasNext()) {
            String word = iterator.next();
            int count = counterService.countWords(word, text);
            JSONObject responseJsonObj = new JSONObject();
            responseJsonObj.put(word, count);
            responseJsonArray.add(responseJsonObj);
        }
        JSONObject outputJsonObject = new JSONObject();
        outputJsonObject.put("counts", responseJsonArray);
        return outputJsonObject;
    }

    private JSONArray getJsonArray(@RequestBody String searchText) throws ParseException {
        //convert string to JsonArray
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(searchText);
        return (JSONArray) jsonObject.get("searchText");
    }

    private static Response doGetRequest(String endpoint) {
        RestAssured.defaultParser = Parser.JSON;
        return given()
                .auth()
                .preemptive()
                .basic("user", "welcome123")
                .when().get(endpoint);
    }


}

