package validator;

import com.simplifier.validation.Validator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValidatorTest {

    @Test(expected = Exception.class)
    public void testInvalidId() throws Exception {
        String logs = "2019-04-02T17:51:41.579Z,st,Excel,getCell,,,excel.xlsx,Sheet1,,,,,,[[3]],,,,\n" +
                "2019-04-02T17:51:44.697Z,st,Chrome,clickTextField,,,,,,,INPUT,text,SingleLine,,,,,\n" +
                "2019-04-02T17:53:41.579Z,st,Excel,getCell,,,excel.xlsx,Sheet1,A2,,,,,[[3]],,,,\n";
        Validator.validateForIdName(logs);
    }

    @Test(expected = Exception.class)
    public void testInvalidName() throws Exception {
        String logs = "2019-04-02T17:51:41.579Z,st,Excel,getCell,,,excel.xlsx,Sheet1,A1,,,,,[[3]],,,,\n" +
                "2019-04-02T17:51:44.697Z,st,Chrome,clickTextField,,,,,,,INPUT,text,,,,,,\n" +
                "2019-04-02T17:53:41.579Z,st,Excel,getCell,,,excel.xlsx,Sheet1,A2,,,,,[[3]],,,,\n";
        Validator.validateForIdName(logs);
    }


    @Test
    public void testValidation() throws Exception {
        String logs = "2019-04-09T20:48:19.192Z,st,Chrome,paste,,6,,,,,INPUT,text,Name_Last,hello,,,,\n" +
                "2019-04-09T20:48:19.963Z,st,Chrome,editField,,,,,,,INPUT,text,Name_Last,hello world,,,,\n" +
                "2019-04-09T20:48:11.819Z,st,Chrome,copy,,hello world,,,,,INPUT,text,Name_Last,hello world,,,,\n" +
                "2019-04-09T20:48:19.964Z,st,Chrome,editField,,,,,,,INPUT,text,Name_Last,hello,,,,\n";

        Validator.validateForIdName(logs);
    }
}
