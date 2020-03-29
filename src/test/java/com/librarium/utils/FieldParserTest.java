package com.librarium.utils;

import com.librarium.service.BookService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

public class FieldParserTest {
    @Test
    public void checkAuthor_EmptyString_returnEmptyString() {
        String actual = FieldParser.checkAuthor("");
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void checkAuthor_StringAllNotMatch_returnFirstLetter() {
        String actual = FieldParser.checkAuthor("ééééé");
        String expected = "é";
        assertEquals(expected, actual);
    }

    @Test
    public void checkAuthor_StringWithLatinCyrillic_returnEmptyString() {
        String actual = FieldParser.checkAuthor("asdfghjkas фывапрол");
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void checkAuthor_StringWithFirstNumber_returnNumber() {
        String actual = FieldParser.checkAuthor("1asdfghjkas фывапрол");
        String expected = "1";
        assertEquals(expected, actual);
    }

    @Test
    public void checkAuthor_StringWithFrench_returnFirstFrenchLetter() {
        String actual = FieldParser.checkAuthor("adsfghj фаыуацф étr 2345 ./@#$%^&");
        String expected = "é";
        assertEquals(expected, actual);
    }

    @Test
    public void checkAuthor_StringWithLettersSymbols_returnFirstSymbol() {
        String actual = FieldParser.checkAuthor("sfdgfhgj . ads---fdgh");
        String expected = ".";
        assertEquals(expected, actual);
    }

    @Test
    public void checkAuthor_StringWithLettersNumber_returnFirstNumber() {
        String actual = FieldParser.checkAuthor("sfdgfhgj 2134 adsfdgh");
        String expected = "2";
        assertEquals(expected, actual);
    }

    @Test
    public void checkGenre_StringAllNotMatch_returnFirstLetter() {
        String actual = FieldParser.checkGenre("ééééé");
        String expected = "é";
        assertEquals(expected, actual);
    }

    @Test
    public void checkGenre_StringWithFirstNumber_returnNumber() {
        String actual = FieldParser.checkGenre("1asdfghjkas фывапрол");
        String expected = "1";
        assertEquals(expected, actual);
    }

    @Test
    public void checkGenre_EmptyString_returnEmptyString() {
        String actual = FieldParser.checkGenre("");
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void checkGenre_StringWithLatinCyrillic_returnEmptyString() {
        String actual = FieldParser.checkGenre("asdfghj абвагмжфв");
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void checkGenre_StringWithFrench_returnFirstFrenchLetter() {
        String actual = FieldParser.checkGenre("adsfghj фаыуацф étr 2345 ./@#$%^&");
        String expected = "é";
        assertEquals(expected, actual);
    }

    @Test
    public void checkGenre_StringWithLettersSymbols_returnFirstSymbol() {
        String actual = FieldParser.checkGenre("sadfgh.2435465afggdaw");
        String expected = ".";
        assertEquals(expected, actual);
    }

    @Test
    public void checkGenre_StringWithLettersNumbers_returnFirstNumber() {
        String actual = FieldParser.checkGenre("sadfgh1234567sdfg");
        String expected = "1";
        assertEquals(expected, actual);
    }


    @Test
    public void checkBookName_EmptyString_returnEmptyString() {
        String actual = FieldParser.checkBookName("");
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void checkBookName_StringWithNumbersLatinCyrillicSymbols_returnEmptyString() {
        String actual = FieldParser.checkBookName("adsfghj фаыуацф 2345 ./@#$%^&");
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void checkAuthor_StringNotMatch_returnEmptyString() {
        String actual = FieldParser.checkAuthor("ééééé");
        String expected = "é";
        assertEquals(expected, actual);
    }

    @Test
    public void checkBookName_StringWithFrenchLetter_returnFirstFrenchLetter() {
        String actual = FieldParser.checkBookName("adsfghj фаыуацф étr 2345 ./@#$%^&");
        String expected = "é";
        assertEquals(expected, actual);
    }

    @Test
    public void checkBookName_StringNotMatch_returnFirstFrenchLetter() {
        String actual = FieldParser.checkBookName("éééééé");
        String expected = "é";
        assertEquals(expected, actual);
    }

    @Test
    public void checkBookName_StringWithFirstFrenchLetter_returnFirstFrenchLetter() {
        String actual = FieldParser.checkBookName("éadsfghj фаыуацф tr 2345 ./@#$%^&");
        String expected = "é";
        assertEquals(expected, actual);
    }

    @Test
    public void checkYear_EmptyString_returnWhitespace() {
        String actual = FieldParser.checkYear("");
        String expectedErrSymbol = " ";
        assertEquals(expectedErrSymbol, actual);
    }

    @Test
    public void checkYear_String4Number_returnEmptyString() {
        String actual = FieldParser.checkYear("1234");
        String expectedErrSymbol = "";
        assertEquals(expectedErrSymbol, actual);
    }

    @Test
    public void checkYear_String4NumberAndLetter_returnEmptyString() {
        String actual = FieldParser.checkYear("1234A");
        String expectedErrSymbol = "A";
        assertEquals(expectedErrSymbol, actual);
    }

    @Test
    public void checkYear_String4NumberAndSymbol_return5Letter() {
        String actual = FieldParser.checkYear("124=4");
        String expectedErrSymbol = "4";
        assertEquals(expectedErrSymbol, actual);
    }

    @Test
    public void checkYear_String5orMoreNumber_return5NumberString() {
        String year = "1234";
        String expectedErrSymbol = "5";
        for (int i = 5; i < 10; i++) {
            year += String.valueOf(i);
            String actualErrSymbol = FieldParser.checkYear(year);
            assertEquals(expectedErrSymbol, actualErrSymbol);
        }
    }

    @Test
    public void checkYear_3NumberAndFirstSymbol_returnFirstSymbolString() {
        String actual = FieldParser.checkYear("=244");
        String expectedErrSymbol = "=";
        assertEquals(expectedErrSymbol, actual);
    }

    @Test
    public void checkYear_3NumberAndTailSymbol_returnFirstSymbolString() {
        String actual = FieldParser.checkYear("244=");
        String expectedErrSymbol = "=";
        assertEquals(expectedErrSymbol, actual);
    }


    @Test
    public void checkYear_StringLessThan4Number_returnWhiteSpace() {
        String year = "";
        String expectedErrSymbol = " ";
        for (int i = 1; i < 4; i++) {
            year += String.valueOf(i);
            String actualErrSymbol = FieldParser.checkYear(year);
            assertEquals(expectedErrSymbol, actualErrSymbol);
        }
    }

    @Test
    public void checkISBN_EmptyString_returnEmptyString() {
        String actual = FieldParser.checkISBN("");
        String expectedErrSymbol = "";
        assertEquals(expectedErrSymbol, actual);
    }

    @Test
    public void checkISBN_StringFillByNumbers_returnEmptyString() {
        String actual = FieldParser.checkISBN("12345678");
        String expectedErrSymbol = "";
        assertEquals(expectedErrSymbol, actual);
    }

    @Test
    public void checkISBN_StringFillByNumbersWithDashes_returnEmptyString() {
        String actual = FieldParser.checkISBN("2345767-345678-324567");
        String expectedErrSymbol = "";
        assertEquals(expectedErrSymbol, actual);
    }

    @Test
    public void checkISBN_StringFillByNumbersWithDashesAndLatterInHead_returnFirstLatter() {
        String actual = FieldParser.checkISBN("A2345767-345678-324567");
        String expectedErrSymbol = "A";
        assertEquals(expectedErrSymbol, actual);
    }

    @Test
    public void checkISBN_StringFillByNumbersWithDashesAndLatterInTail_returnFirstLatter() {
        String actual = FieldParser.checkISBN("2345767-345678-324567A");
        String expectedErrSymbol = "A";
        assertEquals(expectedErrSymbol, actual);
    }

    @Test
    public void checkISBN_StringFillByNumbersWithDashesAndSymbols_returnFirstSymbol() {
        String actual = FieldParser.checkISBN("234//...5767-345678-324567");
        String expectedErrSymbol = "/";
        assertEquals(expectedErrSymbol, actual);
    }

    @Test
    public void parseISBN_EmptyString_returnEmptyString() {
        String actual = FieldParser.parseISBN("");
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void parseISBN_StringContainsOnlyDashes_returnEmptyString() {
        String actual = FieldParser.parseISBN("----------");
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void parseISBN_StringContainsNumbersAndDash_returnNumberWithoutDashes() {
        String actual = FieldParser.parseISBN("23456-2345678-234566");
        String expected = "234562345678234566";
        assertEquals(expected, actual);
    }

    @Test
    public void parseISBN_StringContainsOnlyNumbers_returnNotChangedNumbers() {
        String actual = FieldParser.parseISBN("23456");
        String expected = "23456";
        assertEquals(expected, actual);
    }

    @Test
    public void checkISBN_StringNotMatch_returnFirstFrenchLetter() {
        String actual = FieldParser.checkISBN("éééé");
        String expected = "é";
        assertEquals(expected, actual);
    }

    @Test
    public void isBlankString_fullString_returnFalse() {
        boolean actual = FieldParser.isBlankString("qwertryetur");
        assertFalse(actual);

    }

    @Test
    public void isBlankString_emptyString_returnTrue() {
        boolean actual = FieldParser.isBlankString("");
        assertTrue(actual);
    }

    @Test
    public void isBlankString_stringContainsWhitespaces_returnTrue() {
        boolean actual = FieldParser.isBlankString("       ");
        assertTrue(actual);
    }

    @Test
    public void isBlankString_stringContainsNewLine_returnTrue() {
        boolean actual = FieldParser.isBlankString("\n\n");
        assertTrue(actual);
    }

    @Test
    public void haveCyrillicSymbols_emptyString_returnFalse() {
        boolean result = FieldParser.haveCyrillicSymbols("");
        assertFalse(result);
    }

    @Test
    public void haveCyrillicSymbols_stringContains1CyrillicInCenterAndLatin_returnTrue() {
        boolean result = FieldParser.haveCyrillicSymbols("asШdfgh");
        assertTrue(result);
    }

    @Test
    public void haveCyrillicSymbols_stringContains1CyrillicInTailAndLatin_returnTrue() {
        boolean result = FieldParser.haveCyrillicSymbols("asdfghШ");
        assertTrue(result);
    }

    @Test
    public void haveCyrillicSymbols_stringContains1CyrillicInHeadAndLatin_returnTrue() {
        boolean result = FieldParser.haveCyrillicSymbols("Шasdfgh");
        assertTrue(result);
    }

    @Test
    public void haveCyrillicSymbols_stringContainsAllCyrillic_returnTrue() {
        boolean result = FieldParser.haveCyrillicSymbols("абвгд");
        assertTrue(result);
    }

    @Test
    public void haveCyrillicSymbols_stringContainsOnlyLatin_returnFalse() {
        boolean result = FieldParser.haveCyrillicSymbols("abcdef");
        assertFalse(result);
    }

    @Test
    public void haveNotCyrillicLatinSymbols_emptyString_returnFalse() {
        boolean result = FieldParser.haveNotCyrillicLatinSymbols("");
        assertFalse(result);
    }

    @Test
    public void haveNotCyrillicLatinSymbols_stringContainsOnlyCyrillic_returnFalse() {
        boolean result = FieldParser.haveNotCyrillicLatinSymbols("абв где");
        assertFalse(result);
    }

    @Test
    public void haveNotCyrillicLatinSymbols_stringContainOnlyLatin_returnFalse() {
        boolean result = FieldParser.haveNotCyrillicLatinSymbols("abcd e f g");
        assertFalse(result);
    }

    @Test
    public void haveNotCyrillicLatinSymbols_stringContainsLatinAndCyrillic_returnFalse() {
        boolean result = FieldParser.haveNotCyrillicLatinSymbols("abcd апаьe фцвf аывпg");
        assertFalse(result);
    }

    @Test
    public void haveNotCyrillicLatinSymbols_stringContainsOnlyFrench_returnTrue() {
        boolean result = FieldParser.haveNotCyrillicLatinSymbols(" langue étrangère à");
        assertTrue(result);
    }

    @Test
    public void haveNotCyrillicLatinSymbols_stringContainsFrenchAndCyrillic_returnTrue() {
        boolean result = FieldParser.haveNotCyrillicLatinSymbols(" langue étrangère Абв гд à");
        assertTrue(result);
    }
}