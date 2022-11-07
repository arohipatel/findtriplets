package com.example.exercise.controller;

import com.example.exercise.domain.AttemptDataRepository;
import com.example.exercise.domain.AttemptsData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
public class FindTripletsController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();

    private final AttemptDataRepository attemptDataRepository;
    private final HashMap<String, String> attempts;

    @RequestMapping(value = "/find-tripplets", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getTriplets(@RequestParam("file-path") String filePath) {
        log.debug("Inside getTriplets filePath: {}", filePath);

        File file = new File(filePath);
        List<List<Integer>> listTriplets;

        try {
            FileInputStream inputStream = new FileInputStream(file);

            byte[] byteArray = new byte[((int) file.length())];
            inputStream.read(byteArray);
            String inputString = new String(byteArray);
            List<String> listNumbers = Arrays.asList(inputString.split(","));

            List<Integer> listInteger = listNumbers.stream().map(value -> {
                value = value.trim();
                return Integer.valueOf(value);
            }).collect(Collectors.toList());
            log.debug("fileString: {}", inputString);
            listInteger = listInteger.stream().distinct().collect(Collectors.toList());
            listTriplets = findTriplets(listInteger);
            log.debug("Leaving getTriplets");

            if (CollectionUtils.isEmpty(listTriplets)) {
                log.debug("No triplets found");
                return ResponseEntity.ok("No triplets found for matching condition for the given input");
            }
            try {
                String json = objectMapper.writeValueAsString(listTriplets);
                log.debug("Generated json: {}", json);
                attempts.put(inputString, json);
                return ResponseEntity.ok(json);
            } catch (JsonProcessingException exception) {
                log.error("Error while converting list to json. Error Message: {}", exception.getMessage(), exception);
            }
        } catch (Exception exception) {
            log.error("Error occurred while getting triplets. Error message: {}", exception.getMessage(), exception);
        }
        return ResponseEntity.ok("Runtime error");
    }

    @RequestMapping(value = "/xml-tripplets", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getXmlTriplets(@RequestParam("file-path") String filePath) {
        log.debug("Inside getXmlTriplets filePath: {}", filePath);

        File file = new File(filePath);
        List<List<Integer>> listTriplets;

        try {
            FileInputStream inputStream = new FileInputStream(file);

            byte[] byteArray = new byte[((int) file.length())];
            inputStream.read(byteArray);
            String inputString = new String(byteArray);
            List<String> listNumbers = Arrays.asList(inputString.split(","));

            List<Integer> listInteger = listNumbers.stream().map(value -> {
                value = value.trim();
                return Integer.valueOf(value);
            }).collect(Collectors.toList());
            log.debug("fileString: {}", inputString);
            listInteger = listInteger.stream().distinct().collect(Collectors.toList());
            listTriplets = findTriplets(listInteger);
            log.debug("Leaving getTriplets");

            if (CollectionUtils.isEmpty(listTriplets)) {
                log.debug("No triplets found");
                return ResponseEntity.ok("No triplets found for matching condition for the given input");
            }
            try {
                String xml = xmlMapper.writeValueAsString(listTriplets);
                log.debug("Generated xml: {}", xml);
                attempts.put(inputString, xml);
                return ResponseEntity.ok(xml);
            } catch (JsonProcessingException exception) {
                log.error("Error while converting list to json. Error Message: {}", exception.getMessage(), exception);
            }
        } catch (Exception exception) {
            log.error("Error occurred while getting triplets. Error message: {}", exception.getMessage(), exception);
        }
        return ResponseEntity.ok("Runtime error");
    }

    @RequestMapping(value = "/get-attempts", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, String>> getAttempts() {
        log.debug("Inside getAttempts attempts size: {}", attempts.size());

        log.debug("Leaving getAttempts");
        return ResponseEntity.ok(attempts);
    }

    @RequestMapping(value = "/save-attempts", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> saveAttempts() {
        log.debug("Inside saveAttempts attempts size: {}", attempts);
        List<AttemptsData> listAttempts = new ArrayList<>();
        for (Map.Entry<String, String> attemptEntry : attempts.entrySet()) {
            AttemptsData attemptsData = new AttemptsData();
            attemptsData.setInput(attemptEntry.getKey());
            attemptsData.setOutput(attemptEntry.getValue());
            attemptsData.setCreatedAt(LocalDateTime.now());
            listAttempts.add(attemptsData);
        }
        log.debug("Creating DB entries for listAttempts: {}", listAttempts);
        attemptDataRepository.saveAll(listAttempts);
        log.debug("Leaving saveAttempts");
        return ResponseEntity.ok("Saved "+ listAttempts.size() +" entries to DB");
    }


    private List<List<Integer>> findTriplets(List<Integer> list) {

        log.debug("Inside findTriplets list size {}", list.size());
        List<List<Integer>> listTriplets = new LinkedList<>();
        List<Integer> triplet = new ArrayList<>();

        for (int i = 0; i < list.size() - 2; i++) {
            int sum = 0;
            for (int j = i + 1; j < list.size() - 1; j++) {
                for (int k = j + 1; j < list.size(); j++) {
                    triplet.clear();
                    triplet.add(list.get(i));
                    triplet.add(list.get(j));
                    triplet.add(list.get(k));
                    sum = triplet.stream().mapToInt(value -> value).sum();
                    if (sum == 0) {
                        log.debug("Found the match triplet: {}", triplet);
                        listTriplets.add(new ArrayList<>(triplet));
                    }

                }
            }
        }

        log.debug("Leaving findTriplets listTriplets size: {}", listTriplets);
        return listTriplets;
    }
}

