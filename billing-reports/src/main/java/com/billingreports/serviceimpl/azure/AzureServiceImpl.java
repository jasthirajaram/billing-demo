package com.billingreports.serviceimpl.azure;

import com.billingreports.entities.azure.Azure;
import com.billingreports.entities.azure.AzureAggregateResult;
import com.billingreports.repositories.azure.AzureRepository;
import com.billingreports.service.azure.AzureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AzureServiceImpl implements AzureService {

    @Autowired
    private AzureRepository azureRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Long getCountOfData() {

        return azureRepository.count();
    }

    @Override
    public List<Azure> getAll() {

        return azureRepository.findAll();
    }

    @Override
    public List<String> getDistinctResourceType() {

        List<String> serviceDescriptions = azureRepository.findDistinctResourceTypeBy();
        return extractUniqueResourceType(serviceDescriptions);
    }

    private List<String> extractUniqueResourceType(List<String> resourceType) {
        Set<String> uniqueServiceSet = new HashSet<>();
        List<String> uniqueServiceList = new ArrayList<>();

        for (String jsonStr : resourceType) {
            String resourceType1 = extractResourceType(jsonStr);
            if (resourceType1 != null) {
                uniqueServiceSet.add(resourceType1);
            }
        }

        uniqueServiceList.addAll(uniqueServiceSet);
        return uniqueServiceList;
    }

    private String extractResourceType(String jsonStr) {

        int startIndex = jsonStr.indexOf("ResourceType\": \"") + "ResourceType\": \"".length();
        int endIndex = jsonStr.indexOf("\"", startIndex);
        if (startIndex >= 0 && endIndex >= 0) {
            return jsonStr.substring(startIndex, endIndex);
        }
        return null; // Return null if extraction fails
    }

    @Override
    public List<Azure> getAllDataBydateRange(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        return azureRepository.findByusageDateBetween(start, end);
    }

    @Override
    public List<Azure> getAllDataByMonths(int months) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months - 1).withDayOfMonth(1);
        return azureRepository.findByusageDateBetween(startDate, endDate);
    }

    @Override
    public List<Azure> getDataByResourseTypeAndDateRange(String resourseType, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return azureRepository.findByResourceTypeAndUsageDateBetween(resourseType, start, end);
    }

    @Override
    public List<Azure> getDataByResourseTypeAndMonths(String resourseType, int months) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusMonths(months - 1).withDayOfMonth(1);

        return azureRepository.findByResourceTypeAndUsageDateBetween(resourseType, startDate, endDate);
    }
    @Override
    public List<Azure> getDataByResourseTypeAndSubscriptionNameAndDateRange(String resourseType, String subscriptionName, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return azureRepository.findByResourceTypeAndSubscriptionNameAndUsageDateBetween(resourseType, subscriptionName, start, end);
    }

    @Override
    public List<Azure> getDataByResourseTypeAndSubscriptionNameAndDuration(String resourseType, String subscriptionName, Integer months) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusMonths(months - 1).withDayOfMonth(1);

        return azureRepository.findByResourceTypeAndSubscriptionNameAndUsageDateBetween(resourseType, subscriptionName, startDate, endDate);
    }

    @Override
    public List<Azure> getDataBySubscriptionNameAndMonths(String subscriptionName, int months) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusMonths(months - 1).withDayOfMonth(1);

        return azureRepository.findBySubscriptionNameAndUsageDateBetween(subscriptionName, startDate, endDate);
    }

    @Override
    public List<Azure> getDataBySubscriptionNameAndRange(String subscriptionName, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return azureRepository.findBySubscriptionNameAndUsageDateBetween(subscriptionName, start, end);
    }

    @Override
    public List<Azure> getBillingDetails(String resourceType, String subscriptionName, String startDate, String endDate, Integer months) {
        List<Azure> billingDetails;


        System.err.println("Service method started");
        /* months == null || */
        if (resourceType.isEmpty() && subscriptionName.isEmpty() && startDate != null && endDate != null && months == 0) {

            billingDetails = getAllDataBydateRange(startDate, endDate);
        } else if (resourceType.isEmpty() && subscriptionName.isEmpty() && Objects.requireNonNull(startDate).isEmpty() && Objects.requireNonNull(endDate).isEmpty() && months > 0) {

            billingDetails = getAllDataByMonths(months);
        } else if (!resourceType.isEmpty() && subscriptionName.isEmpty() && startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty() && months == 0) {

            billingDetails = getDataByResourseTypeAndDateRange(resourceType, startDate, endDate);
        } else if (!resourceType.isEmpty() && subscriptionName.isEmpty() && Objects.requireNonNull(startDate).isEmpty() && endDate.isEmpty() && months != null && months > 0) {

            billingDetails = getDataByResourseTypeAndMonths(resourceType, months);
        } else if (resourceType.isEmpty() && !subscriptionName.isEmpty() && Objects.requireNonNull(startDate).isEmpty() && endDate.isEmpty() && months != null && months > 0) {

            billingDetails = getDataBySubscriptionNameAndMonths(subscriptionName, months);
        } else if (resourceType.isEmpty() && !subscriptionName.isEmpty() && startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty() && months == 0) {

            billingDetails = getDataBySubscriptionNameAndRange(subscriptionName, startDate, endDate);
        } else if (!resourceType.isEmpty() && !subscriptionName.isEmpty() && Objects.requireNonNull(startDate).isEmpty() && endDate.isEmpty() && months != null && months > 0) {

            billingDetails = getDataByResourseTypeAndSubscriptionNameAndDuration(resourceType, subscriptionName, months);
        } else if (!resourceType.isEmpty() && !subscriptionName.isEmpty() && startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty() && months == 0) {

            billingDetails = getDataByResourseTypeAndSubscriptionNameAndDateRange(resourceType, subscriptionName, startDate, endDate);
        } else {
            throw new IllegalArgumentException("Please give the valid date or duration");
        }
        return billingDetails;
    }

    @Override
    public List<Map<String, Double>> calculateMonthlyTotalBills(List<Azure> billingDetails) {
        Map<String, Double> monthlyTotalBillsMap = new LinkedHashMap<>();
        Map<Integer, String> monthNames = Map.ofEntries(
                Map.entry(1, "Jan"), Map.entry(2, "Feb"), Map.entry(3, "Mar"),
                Map.entry(4, "Apr"), Map.entry(5, "May"), Map.entry(6, "Jun"),
                Map.entry(7, "Jul"), Map.entry(8, "Aug"), Map.entry(9, "Sep"),
                Map.entry(10, "Oct"), Map.entry(11, "Nov"), Map.entry(12, "Dec")
        );

        for (Azure azure: billingDetails) {
            Date usageDate = azure.getUsageDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(usageDate);
            int year = calendar.get(Calendar.YEAR);
            int monthNumber = calendar.get(Calendar.MONTH) + 1; // Adding 1 to match the map keys
            String monthName = monthNames.get(monthNumber);

            double cost = azure.getCost();
            String monthYear = monthName + "-" + year;

            // If the month key exists in the map, add the cost; otherwise, put a new entry
            monthlyTotalBillsMap.put(monthYear, monthlyTotalBillsMap.getOrDefault(monthYear, 0.0) + cost);
        }

        // Sort the monthly total bills by year and month
        List<Map.Entry<String, Double>> sortedBills = new ArrayList<>(monthlyTotalBillsMap.entrySet());
        Collections.sort(sortedBills, (entry1, entry2) -> {
            String[] parts1 = entry1.getKey().split("-");
            String[] parts2 = entry2.getKey().split("-");
            int year1 = Integer.parseInt(parts1[1]);
            int year2 = Integer.parseInt(parts2[1]);
            int monthOrder1 = monthNames.entrySet().stream()
                    .filter(entry -> entry.getValue().equalsIgnoreCase(parts1[0]))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(-1);
            int monthOrder2 = monthNames.entrySet().stream()
                    .filter(entry -> entry.getValue().equalsIgnoreCase(parts2[0]))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(-1);
            if (year1 != year2) {
                return Integer.compare(year1, year2);
            }
            return Integer.compare(monthOrder1, monthOrder2);
        });

        List<Map<String, Double>> monthlyTotalBillsList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : sortedBills) {
            Map<String, Double> monthEntry = new LinkedHashMap<>();
            monthEntry.put(entry.getKey(), entry.getValue());
            monthlyTotalBillsList.add(monthEntry);
        }

        return monthlyTotalBillsList;
    }

    public List<Map<String, Object>> generateBillingPeriod(String startDate, String endDate, Integer months) {
        List<Map<String, Object>> billingPeriod = new ArrayList<>();
        Map<String, Object> periodData = new HashMap<>();

        if (months != null && months > 0) {
            LocalDate currentDate = LocalDate.now();

            // Calculate the start date as the first day of the month 'months' months ago
            LocalDate startDate1 = currentDate.minusMonths(months - 1).withDayOfMonth(1);

            // Format the dates
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
            String formattedStartDate = startDate1.format(formatter);
            String formattedEndDate = currentDate.format(formatter);

            // Generate the period data with the desired format
            periodData.put("BillingPeriod", formattedStartDate + " to " + formattedEndDate);
            billingPeriod.add(periodData);
        } else if (startDate != null && endDate != null) {
            // Logic for start date and end date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
            LocalDate parsedStartDate = LocalDate.parse(startDate);
            LocalDate parsedEndDate = LocalDate.parse(endDate);

            String formattedStartDate = parsedStartDate.format(formatter);
            String formattedEndDate = parsedEndDate.format(formatter);

            Map<String, Object> periodData1 = new HashMap<>();
            periodData1.put("BillingPeriod", formattedStartDate + " to " + formattedEndDate);
            billingPeriod.add(periodData1);
        }

        return billingPeriod;
    }




    @Override
    public List<Azure> getBillingDetailsUsingRangeAndDate(String startDate, String endDate, Integer months) {
        List<Azure> billingDetails;

        if (startDate != null && endDate != null && months == 0) {

            billingDetails = getAllDataBydateRange(startDate, endDate) ;
        }
        else if (Objects.requireNonNull(startDate).isEmpty() && Objects.requireNonNull(endDate).isEmpty() && months > 0) {

            System.out.println("Data Using Months");
            System.out.println("Service call for months before call " + months);
            billingDetails = getAllDataByMonths(months);
            System.out.println("Service call for months " + months);

        }

        else {
            throw new IllegalArgumentException("Please provide valid months or duration for top 5 services");
        }

        return billingDetails;
    }


    @Override
    public List<AzureAggregateResult> getServiceTopFiveTotalCosts(String startDate, String endDate, Integer months) {
        List<Azure> billingDetails = getBillingDetailsUsingRangeAndDate(startDate, endDate, months);

        Map<String, Double> serviceTotalCostMap = billingDetails.stream()
                .collect(Collectors.groupingBy(Azure::getResourceType, Collectors.summingDouble(Azure::getCost)));

        List<AzureAggregateResult> top5Services = serviceTotalCostMap.entrySet().stream()
                .map(entry -> new AzureAggregateResult(entry.getKey(), round(entry.getValue(), 2)))
                .sorted((b1, b2) -> Double.compare(b2.getTotalCost(), b1.getTotalCost())) // Sort in descending order
                .limit(5)
                .collect(Collectors.toList());

        return top5Services;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public List<String> getDistinctSubscriptionIds() {
        Query distinctQuery = new Query();
        List<String> distinctSubscriptionIds = mongoTemplate.getCollection("Azure")
                .distinct("SubscriptionID", distinctQuery.getQueryObject(), String.class)
                .into(new ArrayList<>())
                .stream()
                .map(element -> (String) element)
                .collect(Collectors.toList());

        return distinctSubscriptionIds;
    }

    @Override
    public List<String> getDistinctSubscriptionNames() {
        Query distinctQuery = new Query();
        List<String> distinctSubscriptionNames = mongoTemplate.getCollection("Azure")
                .distinct("SubscriptionName", distinctQuery.getQueryObject(), String.class)
                .into(new ArrayList<>())
                .stream()
                .map(element -> (String) element)
                .collect(Collectors.toList());

        return distinctSubscriptionNames;
    }

    @Override
    public List<String> getDistinctResourceTypeBySubscriptionName(String subscriptionName) {
        List<String> serviceDescriptions = azureRepository.findDistinctResourceTypeBySubscriptionName(subscriptionName);
        return extractUniqueResourceType(serviceDescriptions);
    }
}
