package by.dukespontaneous.audiofilemanager;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FileHelper {

    private static final String[] audioExtensions =
            {".mp3", ".mp2", ".wav", ".flac", ".ogg", ".au", ".snd", ".mid", ".midi", ".kar",
                    ".mga", ".aif", ".aiff", ".aifc", ".m3u", ".oga", ".spx"};

    public static List<File> getDirectoryAudioList(File directory) {
        if (!directory.exists() || !directory.isDirectory())
            throw new IllegalArgumentException();

        File[] dirs = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        List<File> result = new LinkedList<>();

        for (File dir : dirs) {
            List<File> aFiles = FileHelper.getAllFiles(dir, audioExtensions);
            if (aFiles.size() > 0) {
                File aDir = FileHelper.deepestCommonDirectory(aFiles);
                result.add(aDir);
            }
        }

        File[] aFiles = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                for (String extension : audioExtensions)
                    if (name.endsWith(extension))
                        return true;

                return false;
            }
        });

        for (File aFile : aFiles)
            result.add(aFile);

        return result;
    }

    public static File deepestCommonDirectory(List<File> paths) {
        if (paths.size() == 1)
            return new File(paths.get(0).getParent());

        String[][] way = new String[paths.size()][];

        int minPathLength = Integer.MAX_VALUE;
        for (int i = 0; i < way.length; ++i) {
            way[i] = paths.get(i).getAbsolutePath().split(File.separator);
            if (minPathLength > way[i].length)
                minPathLength = way[i].length;
        }

        int pl;
        PL:
        for (pl = 0; pl < minPathLength; ++pl)
            for (int w = 0; w < way.length - 1; ++w)
                if (way[w][pl].equals(way[w + 1][pl]) == false)
                    break PL;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < pl; ++i) {
            String dir = way[0][i];
            if (dir.equals(""))
                continue;
            stringBuilder.append(File.separator).append(dir);
        }
        String result = stringBuilder.toString();
        if (result == "")
            throw new IllegalArgumentException();

        return new File(result);
    }

    public static List<File> getAllFiles(File directory, final String[] extensions) {
        List<File> files = new LinkedList<>();

        File[] dirs = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        for (File d : dirs)
            files.addAll(getAllFiles(d, extensions));

        File[] f = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                for (String extension : extensions)
                    if (name.endsWith(extension))
                        return true;

                return false;
            }
        });
        files.addAll(Arrays.asList(f));

        return files;
    }
}