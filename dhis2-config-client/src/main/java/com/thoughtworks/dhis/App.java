package com.thoughtworks.dhis;

import com.google.gson.Gson;
import com.thoughtworks.dhis.configurations.IConfiguration;
import com.thoughtworks.dhis.configurations.LMISConfiguration;
import com.thoughtworks.dhis.endpoints.ApiService;
import com.thoughtworks.dhis.models.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import static java.lang.String.format;

public class App {

    public static final int randomLimit = 500;

    public static void main(String[] args) throws IOException {
        ApiService service = getService(args[0]);
        setupConfig(service);
//        submitCalculatedData(service);
//        submitTestData(service);
 
    }

    private static void submitTestData(ApiService service) {
        try {
            submitCalculatedData(service);
        } catch (Exception ex) {
            System.err.println("Submitting Calculated Data timed out");
            ex.printStackTrace();
        }

        try {
            submitDefaultData(service);
        } catch (Exception ex) {
            System.err.println("Submitting Default Data timed out");
            ex.printStackTrace();
        }
    }

    private static ApiService getService(String env) throws IOException {
        Properties dhis2Properties = new Properties();
        dhis2Properties.load(App.class.getClassLoader().getResourceAsStream("dhis2." + env + ".properties"));
        String dhis2BaseUrl = dhis2Properties.getProperty("dhis2.base_url");
        String dhis2Username = dhis2Properties.getProperty("dhis2.username");
        String dhis2Password = dhis2Properties.getProperty("dhis2.password");
        String dhis2ApiUrl = dhis2BaseUrl + "/api/";

        System.out.println(format("Accessing API at %s with login - %s/%s", dhis2ApiUrl, dhis2Username, dhis2Password));
        return new Client(dhis2ApiUrl, new User(dhis2Username, dhis2Password)).getService();
    }

    private static void setupConfig(ApiService service) throws IOException {
        CategoryCombo categoryCombo = service.searchCategoryCombos("default").getCategoryCombos().get(0);
        categoryCombo = service.getCombo(categoryCombo.getId());
        categoryCombo.setHref(null);
        categoryCombo.setCreated(null);
        categoryCombo.setLastUpdated(null);
        categoryCombo.setCategories(null);
        categoryCombo.setDimensionType(null);
        categoryCombo.setCategoryOptionCombos(null);

        IConfiguration lmisConfig = new LMISConfiguration(categoryCombo);
        Map<String, Object> data = lmisConfig.generateMetaData();

        //Gson gson = new Gson();
        //writeFile(gson, data);

        service.updateMetaData(data);
    }

    private static void writeFile(Gson gson, Map<String, Object> data) throws IOException {
        Writer writer = null;

        File myFile = new File("metaData.json");
        myFile.createNewFile();
        FileOutputStream fOut = new FileOutputStream(myFile);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
        myOutWriter.append(gson.toJson(data));
        myOutWriter.close();
        fOut.close();
    }

    private static void submitCalculatedData(ApiService service) {
        UserProfile me = service.getProfile();
        DataSet set = service.getDataSet("53218436405");
        System.out.println(set.getDataElements().size());

        for (int i = 9; i < 10; i++) {
            String period = "20140";
            String period1 = period + String.valueOf(i);
            for (DataElement element : set.getDataElements()) {

                DataValueSet valueSet = new DataValueSet();
                valueSet.setDataValues(new ArrayList<DataValue>());
                valueSet.setOrgUnit(me.getOrganisationUnits().get(0).getId());

                Random rand = new Random();
                int n = rand.nextInt(randomLimit) + 1;
                DataValue value = new DataValue();
                value.setDataElement(element.getId());
                value.setValue(String.valueOf(n));
                value.setPeriod(period1);
                valueSet.getDataValues().add(value);

                Object data = service.submitValueSet(valueSet);
                System.out.println(data);
            }

        }
        //  Object data = service.submitValueSet(valueSet);
        //System.out.println(data);
    }

    private static void submitDefaultData(ApiService service) {
        UserProfile me = service.getProfile();
        DataSet set = service.getDataSet("1ce7aa8c65e");
        System.out.println(set.getDataElements().size());
        DataValueSet valueSet = new DataValueSet();
        valueSet.setDataValues(new ArrayList<DataValue>());
        String period = "201409";
        valueSet.setOrgUnit(me.getOrganisationUnits().get(0).getId());
        for (int i = 13; i < 14; i++) {
            String period1 = period + String.valueOf(i);
            for (DataElement element : set.getDataElements()) {
                Random rand = new Random();
                int n = rand.nextInt(randomLimit) + 1;
                DataValue value = new DataValue();
                value.setDataElement(element.getId());
                value.setValue(String.valueOf(n));
                value.setPeriod(period1);
                valueSet.getDataValues().add(value);
            }
        }
        Object data = service.submitValueSet(valueSet);
        System.out.println(data);
    }

    public App() {

    }


}
