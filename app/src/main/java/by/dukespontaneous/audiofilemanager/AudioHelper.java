package by.dukespontaneous.audiofilemanager;

import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AudioHelper {

    public static List<File> getDirectoryAudioList(File directory, ContentResolver context) {
        if (!directory.exists() || !directory.isDirectory())
            throw new IllegalArgumentException();

        Map<String, Set<String>> subContent = new HashMap<>();
        for (File file : directory.listFiles())
            if (file.isDirectory())
                subContent.put(file.toString(), new HashSet<String>());

        List<File> localAudioFiles = new LinkedList<>();
        List<File> localAudioDirs = new LinkedList<>();

        Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.DATA};

        try (Cursor cursor = context.query(
                contentUri,
                projection,
                MediaStore.Audio.Media.IS_MUSIC + " != 0",
                null,
                null);) {

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            while (cursor.moveToNext()) {
                String audio = cursor.getString(column_index);
                String aPath = audio.substring(0, audio.lastIndexOf(File.separator));

                if (aPath.startsWith(directory.toString())) {
                    boolean isSubContent = false;
                    for (String key : subContent.keySet()) {
                        if (aPath.startsWith(key)) {
                            subContent.get(key).add(aPath);
                            isSubContent = true;
                            break;
                        }
                    }
                    if (!isSubContent)
                        localAudioFiles.add(new File(audio));
                }
            }

        } catch (RuntimeException ex) {
        }

        // Анализ поддиректорий с аудио-файлами
        for (Map.Entry<String, Set<String>> entry : subContent.entrySet()) {
            int audioSubDirsSetSize = entry.getValue().size();
            String absolutePath;
            switch (audioSubDirsSetSize) {
                case 0:
                    continue;
                case 1:
                    absolutePath = entry.getValue().iterator().next();
                    localAudioDirs.add(new File(absolutePath));
                    break;
                default:
                    absolutePath = entry.getKey();
                    localAudioDirs.add(new File(absolutePath));
                    break;
            }
        }

        List<File> result = new LinkedList<>();
        result.addAll(localAudioDirs);
        result.addAll(localAudioFiles);

        return result;
    }
}
