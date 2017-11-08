package by.dukespontaneous.audiofilemanager.underway;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

public class FileHelper {

    public static String deepestCommonDirectory(Set<String> directories) {
        String[] commonParts = null;
        for(String path : directories) {
            String[] parts = path.trim().split(File.separator);

            if(commonParts == null) {
                commonParts = parts;
            } else {
                for(int i = 0; i < (parts.length < commonParts.length ? parts.length : commonParts.length); ++i) {
                    if(commonParts[i].equals(parts[i]) == false) {
                        commonParts = Arrays.copyOf(commonParts, i);
                        break;
                    }
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(String cp : commonParts) {
            if (cp.equals(""))
                continue;
            stringBuilder.append(File.separator).append(cp);
        }

        return stringBuilder.toString();
    }
}
