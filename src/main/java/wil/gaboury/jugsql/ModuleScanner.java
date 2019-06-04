package wil.gaboury.jugsql;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuleScanner {
    private static final Pattern NAME_COMMENT = Pattern.compile("\\w*--\\w*:name\\w+(?<name>[A-Za-z0-9_]).*");
    private String currentLine = null;
    private Scanner sc;
    private int lineNum = 0;

    public ModuleScanner(InputStream in) {
        this.sc = new Scanner(in);
    }

    public String getName() {
        advancePastUnimportantLines();
        Matcher m = NAME_COMMENT.matcher(getLine());
        if (!m.matches()) {
            throw new AssertionError("found sql command with no annotation on line " + lineNum + ".");
        }
        return m.group("name");
    }

    public String getStatement() {
        ArrayList<String> strs = new ArrayList<>();

        advancePastUnimportantLines();
        if (NAME_COMMENT.matcher(getLine()).matches()) {
            throw new AssertionError("second name annotation found for a single query found on line " + lineNum + ".");
        }
        while (hasNext()) {
            advancePastUnimportantLines();
            String line = getLine().trim();
            strs.add(line);
            if (line.charAt(line.length()) == ';') {
                break;
            }
        }

        return String.join(" ", strs);
    }

    //-------------------------[ helper methods ]-----------------------

    private boolean hasNext() {
        return sc.hasNext();
    }

    private String getLine() {
        return currentLine;
    }

    private String nextLine() {
        lineNum++;
        return currentLine = sc.nextLine();
    }

    private boolean advancePastUnimportantLines() {
        while (hasNext()) {
            nextLine();
            if (!getLine().replaceAll("\\w*", "").equals("")
                && !getLine().replace("\\w*--.*", "").equals("")) {
                return false;
            }
        }
        return true;
    }
}
