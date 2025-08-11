/**
 * Write a description of CSVMin here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import edu.duke.*;
import org.apache.commons.csv.*;
import java.io.*;

public class CSVMin {
    public CSVRecord coldestHourInFile(CSVParser parser) {
        //start with largestSoFar as nothing
        CSVRecord lowestSoFar = null;
        //For each row (currentRow) in the CSV File
        for (CSVRecord currentRow : parser) {
            // use method to compare two records
            String currentRowStr = currentRow.get("TemperatureF");
            double currentRowDouble = Double.parseDouble(currentRowStr);
            if (currentRowDouble != -9999) {
                lowestSoFar = getLowestOfTwo(currentRow, lowestSoFar);
            }
        }
        //The largestSoFar is the answer
        return lowestSoFar;
    }
    
    public void testColdestHourInFile() {
        FileResource fr = new FileResource();
        CSVRecord lowest = coldestHourInFile(fr.getCSVParser());
        System.out.println("coldest temperature was " + lowest.get("TemperatureF") + " at " + lowest.get("DateUTC"));
    }

    public String fileWithColdestTemperature() {
        CSVRecord lowestSoFar = null;
        DirectoryResource dr = new DirectoryResource();
        String fileName = null;
        // iterate over files
        for (File f : dr.selectedFiles()) {
            FileResource fr = new FileResource(f);
            // use method to get largest in file.
            CSVRecord currentRow = coldestHourInFile(fr.getCSVParser());
            // use method to compare two records
            if ( lowestSoFar == null ) {
                fileName = f.getName();
                lowestSoFar = currentRow;
            }
            String currentRowStr = currentRow.get("TemperatureF");
            String lowestSoFarStr = lowestSoFar.get("TemperatureF");
            double currentRowDouble = Double.parseDouble(currentRowStr);
            double lowestSoFarDouble = Double.parseDouble(lowestSoFarStr);
            if ( currentRowDouble < lowestSoFarDouble ){
                fileName = f.getName();
                lowestSoFar = currentRow;
            }
        }
        return fileName;
    }
    
    public void testFileWithColdestTemperature() {
        String fileName = fileWithColdestTemperature();
        System.out.println("Coldest day was in file " + fileName);
        FileResource fr = new FileResource("nc_weather\\" + fileName.substring(8, 12) + "\\" + fileName);
        CSVRecord lowest = coldestHourInFile(fr.getCSVParser());
        System.out.println("Coldest temperature on that day was " + lowest.get("TemperatureF"));
        System.out.println("All the Temperatures on the coldest day were:");
        for (CSVRecord currentRow : fr.getCSVParser()) {
            System.out.println(currentRow.get("DateUTC")+ ": "+currentRow.get("TemperatureF")); 
        }
    }
    
    public CSVRecord lowestHumidityInFile(CSVParser parser) {
        //start with largestSoFar as nothing
        CSVRecord lowestSoFar = null;
        //For each row (currentRow) in the CSV File
        for (CSVRecord currentRow : parser) {
            // use method to compare two records
            String currentRowStr = currentRow.get("Humidity");
            if (!currentRowStr.equals("N/A")) {
                lowestSoFar = getLowestHumidity(currentRow, lowestSoFar);
            }
        }
        //The largestSoFar is the answer
        return lowestSoFar;
    }
    
    public void testLowestHumidityInFile() {
        FileResource fr = new FileResource();
        CSVParser parser = fr.getCSVParser();
        CSVRecord csv = lowestHumidityInFile(parser);
        System.out.println("Lowest Humidity was " + csv.get("Humidity") + " at " + csv.get("DateUTC"));
    }
    
    public CSVRecord lowestHumidityInManyFiles() {
        CSVRecord lowestSoFar = null;
        DirectoryResource dr = new DirectoryResource();
        for (File f : dr.selectedFiles()) {
            FileResource fr = new FileResource(f);
            CSVRecord currentRow = lowestHumidityInFile(fr.getCSVParser());
            if ( lowestSoFar == null ) {
                lowestSoFar = currentRow;
            }
            else {
                String currentRowStr = currentRow.get("Humidity");
                String lowestSoFarStr = lowestSoFar.get("Humidity");
                if (currentRowStr != "N/A" && lowestSoFarStr != "N/A") {
                    lowestSoFar = getLowestHumidity(currentRow, lowestSoFar);
                }
            }
        }
        return lowestSoFar;
    }
    
    public void testLowestHumidityInManyFiles() {
        CSVRecord lowestHum = lowestHumidityInManyFiles();
        System.out.println("Lowest Humidity was " + lowestHum.get("Humidity") + " at " + lowestHum.get("DateUTC"));
    }
    
    public double averageTemperatureInFile(CSVParser parser) {
        double sum = 0;
        int count = 0;
        //For each row (currentRow) in the CSV File
        for (CSVRecord currentRow : parser) {
            String currentRowStr = currentRow.get("TemperatureF");
            double currentRowDouble = Double.parseDouble(currentRowStr);
            if (currentRowDouble != -9999) {
                sum += currentRowDouble;
                count++;
            }
        }
        if(count == 0){
            return -1;
        }
        else {
            return sum/count;
        }
    }
    
    public void testAverageTemperatureInFile() {
        FileResource fr = new FileResource();
        CSVParser parser = fr.getCSVParser();
        System.out.println("Average temperature in file is " + averageTemperatureInFile(parser));
    }
    
    public double averageTemperatureWithHighHumidityInFile(CSVParser parser, int value) {
        double sum = 0;
        int count = 0;
        //For each row (currentRow) in the CSV File
        for (CSVRecord currentRow : parser) {
            String currentRowTemp = currentRow.get("TemperatureF");
            String currentRowHum = currentRow.get("Humidity");
            double currentRowTempDouble = Double.parseDouble(currentRowTemp);
            double currentRowHumDouble = Double.parseDouble(currentRowHum);
            if (currentRowHumDouble>=value && currentRowTempDouble != -9999) {
                sum += currentRowTempDouble;
                count++;
            }
        }
        if(count == 0){
            return -1;
        }
        else {
            return sum/count;
        }
    }
    
    public void testAverageTemperatureWithHighHumidityInFile() {
        FileResource fr = new FileResource();
        CSVParser parser = fr.getCSVParser();
        double result = averageTemperatureWithHighHumidityInFile(parser, 80);
        if(result == -1) {
            System.out.println("No temperatures with that humidity");
        }
        else {
            System.out.println("Average Temp when high Humidity is " + result);
        }
    }

    public CSVRecord getLowestOfTwo (CSVRecord currentRow, CSVRecord lowestSoFar) {
        //If largestSoFar is nothing
        if (lowestSoFar == null) {
            lowestSoFar = currentRow;
        }
        //Otherwise
        else {
            double currentTemp = Double.parseDouble(currentRow.get("TemperatureF"));
            double lowestTemp = Double.parseDouble(lowestSoFar.get("TemperatureF"));
            //Check if currentRow’s temperature > largestSoFar’s
            if (currentTemp < lowestTemp) {
                //If so update largestSoFar to currentRow
                lowestSoFar = currentRow;
            }
        }
        return lowestSoFar;
    }

    public CSVRecord getLowestHumidity (CSVRecord currentRow, CSVRecord lowestSoFar) {
        //If largestSoFar is nothing
        if (lowestSoFar == null) {
            lowestSoFar = currentRow;
        }
        //Otherwise
        else {
            int currentHum = Integer.parseInt(currentRow.get("Humidity"));
            int lowestHum = Integer.parseInt(lowestSoFar.get("Humidity"));
            if (currentHum < lowestHum) {
                //If so update largestSoFar to currentRow
                lowestSoFar = currentRow;
            }
        }
        return lowestSoFar;
    }
}
