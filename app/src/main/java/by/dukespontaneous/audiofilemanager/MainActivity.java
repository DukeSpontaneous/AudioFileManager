package by.dukespontaneous.audiofilemanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private static final DirectoriesNavigator navigator = new DirectoriesNavigator();

    private final List<File> guiFilesList = new ArrayList<>();
    private ArrayAdapter<File> filesAdapter;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        ListView listView = findViewById(R.id.listView);

        filesAdapter = new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, guiFilesList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                final File file = getItem(position);
                ((TextView) view.findViewById(android.R.id.text1)).setText(navigator.getRelativePath(file));
                return view;
            }
        };
        listView.setAdapter(filesAdapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

                        File file = filesAdapter.getItem(position);
                        if (file.isDirectory()) {

                            navigator.goInto(file);
                            guiUpdate();
                        }
                    }
                }
        );

        boolean fsIsReadable = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fsIsReadable = checkReadPermission();
        }

        if (fsIsReadable)
            guiUpdate();
    }

    private void guiUpdate() {
        File dir = navigator.getCurrentDir();

        textView.setText(dir.getAbsolutePath());

        List<File> audioList = FileHelper.getDirectoryAudioList(dir);
        guiFilesList.clear();
        guiFilesList.addAll(audioList);
        filesAdapter.notifyDataSetChanged();
    }

    private boolean checkReadPermission() {

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getBaseContext(), "Permission was granted!",
                            Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    guiUpdate();

                } else {
                    Toast.makeText(getBaseContext(), "Permission denied!",
                            Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onBackPressed() {
        if (navigator.hasDirectories() == false) {
            super.onBackPressed();
        } else {
            navigator.goBack();
            guiUpdate();
        }
    }
}
