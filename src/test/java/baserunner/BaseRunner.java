package baserunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import utils.ExcelReaderUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BaseRunner extends AbstractTestNGCucumberTests {
    String featureFolder;

    public BaseRunner(String featureFolderPath){
        this.featureFolder=featureFolderPath;
    }

    @BeforeSuite
    public void beforeSuite() {
        List<File> listOfFiles= getAllFeatureFiles(featureFolder);
            listOfFiles.forEach(file -> {
            overrideFeatureFiles(file);
        });
    }

    private List<File> getAllFeatureFiles(String featureFolder) {
        Collection<File> filesList= FileUtils.listFiles(new File(featureFolder), new String[]{"feature"},true);
        return  new ArrayList<>(filesList);
    }

    private void overrideFeatureFiles(File file)   {

        List<String> linesOfFeature;
        try {
            linesOfFeature = Files.readAllLines(Path.of(file.getPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
            String scenarioName="";
            List<String> exampleFields=new ArrayList<>();

            for(int i=0;i<linesOfFeature.size();i++){
                String line=linesOfFeature.get(i);
                if(line.trim().startsWith("Scenario")) scenarioName= line.substring(line.indexOf(":")+1).trim();

                if(line.trim().startsWith("|") && line.trim().endsWith("|")) {
                    exampleFields=Arrays.asList(line.trim().split("\\|"));
                }

                if(line.trim().startsWith("#@#@")){
                    writer.write(line+"\n");
                    List<Map<String,String>> excelDataFromSheet1 = ExcelReaderUtils.readSheet(line.trim().substring(4));

                    for(Map<String,String> myMap : excelDataFromSheet1){
                        if(myMap.get("Scenario").equals(scenarioName)){
                            String fromExcelData="|";
                            for(String field: exampleFields){
                                if(!field.trim().isBlank())
                                     fromExcelData+=myMap.get(field.trim()).trim()+"|";
                            }
                            writer.write(fromExcelData+"\n");
                        }
                    }
                    String nextLine="";
                    do{
                        if(i<linesOfFeature.size()-1)
                            nextLine = linesOfFeature.get(++i);
                        else
                            break;
                    }while(nextLine.trim().startsWith("|"));

                }
                if(!line.trim().startsWith("#@#@"))
                    writer.write(line+"\n");
            }







            writer.close();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
