package by.dukespontaneous.audiofilemanager;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.Stack;

public class DirectoriesNavigator {
    @NonNull
    private File currentDir = null;
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

    @Nullable
    public File goBack() {
        currentDir = stack.size() != 0 ? stack.pop() : null;
        return currentDir;
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
