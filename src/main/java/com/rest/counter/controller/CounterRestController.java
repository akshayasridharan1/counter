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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.Iterator;


import static io.restassured.RestAssured.given;

@RestController
@RequestMapping("/counter-api")
public class CounterRestController {

    @Autowired
    private Environment environment;

    @Autowired
    private CounterService counterService;

    @GetMapping("/load")
    public Paragraph loadParagraph() {
         return new Paragraph(environment.getProperty("counter.load.paragraph"));
    }

    public static Response doGetRequest(String endpoint) {
        RestAssured.defaultParser = Parser.JSON;
        return given()
                .auth()
                .preemptive()
                .basic("user", "welcome123")
                .when().get(endpoint);
    }

    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public String search(@RequestBody String searchText) throws ParseException {
        //form load paragraph rest-api call
        String port= environment.getProperty("server.port");
        String loadParagraph = "http://localhost:"+port+"/counter-api/load";

        //load paragraph rest-api response
        Response response = doGetRequest(loadParagraph);
        String text = response.jsonPath().getString("text");

        //convert string to JsonArray
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(searchText);
        JSONArray jsonArray = (JSONArray) jsonObject.get("searchText");
        String searchParagraph = text.replace(".", "").replace(",", "");
        String a[] = searchParagraph.split(" ");

        //word counter
        Iterator<String> iterator = jsonArray.iterator();
        JSONArray responseJsonArray = new JSONArray();
        while(iterator.hasNext()) {
            String word = iterator.next();
            int count = counterService.countWords(word, a);

            //form jsonObject
            JSONObject responseJsonObj = new JSONObject();
            responseJsonObj.put(word, count);
            responseJsonArray.add(responseJsonObj);
        }
        JSONObject outputJsonObject = new JSONObject();
        outputJsonObject.put("counts", responseJsonArray);
        return outputJsonObject.toJSONString();
    }
}

