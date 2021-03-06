package com.librarium.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldParser {
    //возращает неверный символ или пустую строку для проверки автора и жанра
    public static String checkAuthor(String line) {
        return checkForAllowedLatterAndSpaces(line);
    }

    public static String checkGenre(String line) {
        return checkForAllowedLatterAndSpaces(line);
    }

    //Разрешены русские, латинские буквы и проблеьные символы
    private static String checkForAllowedLatterAndSpaces(String line) {
        if (line.length() == 0) return "";
        Pattern pattern = Pattern.compile(
                "[" +
                        "а-яА-ЯёЁ" +    //буквы русского алфавита
                        "a-zA-Z" +      //буквы латинского алфавита
                        "\\s" +         //пробельные символы
                        "]" +
                        "+");                   //любое количество символов выше
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (start == 0 && end == line.length()) {
                return "";
            } else {
                if (start != 0) {
                    return Character.toString(line.charAt(0));
                } else {
                    return Character.toString(line.charAt(end));
                }
            }
        } else {
            return line.substring(0, 1);
        }
    }

    //возращает неверный символ или пустую строку для проверки названия
    public static String checkBookName(String line) {
        if (line.length() == 0) return "";
        Pattern pattern = Pattern.compile(
                "[" +
                        "а-яА-ЯёЁ" +    //буквы русского алфавита
                        "a-zA-Z" +      //буквы латинского алфавита
                        "\\s" +         //пробельные символы
                        "\\p{Punct}" +   //знаки пунктуации
                        "\\d" +          //цифры
                        "]" +
                        "+");                   //любое количество символов выше
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (start == 0 && end == line.length()) {
                return "";
            } else {
                if (start != 0) {
                    return Character.toString(line.charAt(0));
                } else {
                    return Character.toString(line.charAt(end));
                }
            }
        } else {
            return line.substring(0, 1);
        }
    }

    //возращает неверный символ или пустую строку для проверки года
    public static String checkYear(String line) {
        Pattern pattern = Pattern.compile(
                "[" +
                        "\\d" +          //цифры
                        "]" +
                        "+");
        Matcher matcher = pattern.matcher(line);
        if (line.length() < 4) {
            return " ";
        }
        if (matcher.find() && line.length() == 4) {
            int start = matcher.start();
            int end = matcher.end();
            if (start == 0 && end == line.length()) {
                return "";
            } else {
                if (start != 0) {
                    return Character.toString(line.charAt(0));
                } else {
                    return Character.toString(line.charAt(end));
                }
            }
        } else {
            return line.substring(4, 5);
        }
    }

    //возращает неверный символ или пустую строку для проверки ISBN
    public static String checkISBN(String line) {
        if (line.length() == 0) return "";
        Pattern pattern = Pattern.compile(
                "[" +
                        "\\d" +          //цифры
                        "\\-" +
                        "]" +
                        "+");                   //1 и более символ
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (start == 0 && end == line.length()) {
                return "";
            } else {
                if (start != 0) {
                    return Character.toString(line.charAt(0));
                } else {
                    return Character.toString(line.charAt(end));
                }
            }
        } else {
            return line.substring(0, 1);
        }
    }

    public static String parseISBN(String line) {
        return line.replaceAll("\\-", "");
    }

    public static boolean isBlankString(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static boolean haveCyrillicSymbols(String s) {
        for (char a : s.toCharArray()) {
            if (Character.UnicodeBlock.of(a) == Character.UnicodeBlock.CYRILLIC) return true;
        }
        return false;
    }

    public static boolean haveNotCyrillicLatinSymbols(String s) {
        for (char a : s.toCharArray()) {
            if (Character.isLetter(a) && Character.UnicodeBlock.of(a) != Character.UnicodeBlock.CYRILLIC && Character.UnicodeBlock.of(a) != Character.UnicodeBlock.BASIC_LATIN
            ) return true;
        }
        return false;
    }
}
