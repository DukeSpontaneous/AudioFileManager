package by.dukespontaneous.audiofilemanager;

import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.Stack;

public class DirectoriesNavigator {
    @NonNull
    private File currentDir;
    private final Stack<File> stack = new Stack<>();

    public DirectoriesNavigator(File currentDir) {
        if (!currentDir.exists() || !currentDir.isDirectory())
            throw new IllegalArgumentException();

        this.currentDir = currentDir;
    }

    public DirectoriesNavigator(String path) {
        this(new File(path));
    }

    public DirectoriesNavigator() {
        this(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath());
    }

    public File getCurrentDir() {
        return currentDir;
    }

    public void goInto(File dir) {
        if (!dir.exists() || !dir.isDirectory())
            throw new IllegalArgumentException();

        stack.push(this.currentDir);
        this.currentDir = dir;
    }

    public File goBack() {
        currentDir = stack.size() != 0 ? stack.pop() : currentDir;
        return currentDir;
    }

    public boolean hasDirectories() {
        return stack.size() > 0;
    }

    public String getRelativePath (File file) {
        String absolutePath = file.getAbsolutePath();
        if(absolutePath.startsWith(currentDir.getAbsolutePath()) == false)
            throw new IllegalArgumentException();

        String relativePath = absolutePath.substring(
                (int)currentDir.getAbsolutePath().length(),
                absolutePath.length());

        return relativePath;
    }
}
