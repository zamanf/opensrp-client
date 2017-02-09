package util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jason Rogena - jrogena@ona.io
 * @since 09/02/2017
 */
public abstract class Comparison {
    private static final String TYPE_STRING = "string";
    private static final String TYPE_NUMERIC = "numeric";
    private static final String DEFAULT_STRING = "";
    private static final String DEFAULT_NUMERIC = "0";
    private static String functionRegex;
    private static HashMap<String, Comparison> comparisons;

    public abstract boolean compare(String a, String type, String b);

    public abstract String getFunctionName();

    public static boolean doComparison(String value, JSONObject comparison) throws Exception {
        if (functionRegex == null || comparisons == null) {
            functionRegex = "";
            comparisons = new HashMap<>();

            LessThanComparison lessThanComparison = new LessThanComparison();
            functionRegex += lessThanComparison.getFunctionName();
            comparisons.put(lessThanComparison.getFunctionName(), lessThanComparison);

            LessThanEqualToComparison lessThanEqualToComparison = new LessThanEqualToComparison();
            functionRegex += "|" + lessThanEqualToComparison.getFunctionName();
            comparisons.put(lessThanEqualToComparison.getFunctionName(), lessThanEqualToComparison);

            EqualToComparison equalToComparison = new EqualToComparison();
            functionRegex += "|" + equalToComparison.getFunctionName();
            comparisons.put(equalToComparison.getFunctionName(), equalToComparison);

            GreaterThanComparison greaterThanComparison = new GreaterThanComparison();
            functionRegex += "|" + greaterThanComparison.getFunctionName();
            comparisons.put(greaterThanComparison.getFunctionName(), greaterThanComparison);

            GreaterThanEqualToComparison greaterThanEqualToComparison = new GreaterThanEqualToComparison();
            functionRegex += "|" + greaterThanEqualToComparison.getFunctionName();
            comparisons.put(greaterThanEqualToComparison.getFunctionName(), greaterThanEqualToComparison);

            RegexComparison regexComparison = new RegexComparison();
            functionRegex += "|" + regexComparison.getFunctionName();
            comparisons.put(regexComparison.getFunctionName(), regexComparison);
        }

        String type = comparison.getString("type").toLowerCase();
        String ex = comparison.getString("ex");

        Pattern pattern = Pattern.compile("(" + functionRegex + ")\\((.*)\\)");
        Matcher matcher = pattern.matcher(ex);
        if (matcher.find()) {
            String functionName = matcher.group(1);
            String b = matcher.group(2);
            return comparisons.get(functionName).compare(value, type, b);
        }

        return false;
    }
}
