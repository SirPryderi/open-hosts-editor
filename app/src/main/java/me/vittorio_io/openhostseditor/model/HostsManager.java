package me.vittorio_io.openhostseditor.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by vittorio on 17/07/17.
 */

public class HostsManager {
    private static final String SYSTEM_ETC_HOSTS = "/system/etc/hosts";

    private static BufferedReader getHostsBufferedReader() {
        File file = new File(SYSTEM_ETC_HOSTS);

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));

            return new BufferedReader(inputStreamReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("WTF?");
    }

    public static List<HostRule> readFromFile() throws IOException {
        List<HostRule> rules = new ArrayList<>();
        BufferedReader bufferedReader = getHostsBufferedReader();

        String string;

        while ((string = bufferedReader.readLine()) != null) {
            try {
                HostRule rule = HostRule.fromHostLine(string);
                if (rule != null)
                    rules.add(rule);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        bufferedReader.close();

        return rules;
    }

    public static void writeToFile(File file) throws IOException {
        // TODO maybe replace with a simple copy function?
        file.createNewFile();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        BufferedReader bufferedReader = getHostsBufferedReader();

        String string;

        while ((string = bufferedReader.readLine()) != null) {
            bufferedWriter.write(string);
            bufferedWriter.write("\n");
        }

        bufferedWriter.close();
        bufferedReader.close();
    }

    public static String writeToString() throws IOException {
        BufferedReader bufferedReader = getHostsBufferedReader();
        StringBuilder builder = new StringBuilder();

        String string;

        while ((string = bufferedReader.readLine()) != null) {
            builder.append(string);
            builder.append("\n");
        }

        return builder.toString();
    }

    public static void writeFromFile(final File file) {
        ExecuteAsRootBase root = new ExecuteAsRootBase() {
            @Override
            protected ArrayList<String> getCommandsToExecute() {
                ArrayList<String> commands = new ArrayList<>();

                // need to mount /system in write mode
                commands.add("mount -o remount,rw /system");
                commands.add("cp -f " + file.getAbsolutePath() + " " + SYSTEM_ETC_HOSTS);
                commands.add("mount -o remount,ro /system");
                commands.add("exit");

                return commands;
            }
        };

        root.execute();
    }

    public static void saveBackup() throws IOException {
        if (isExternalStorageWritable()) {
            writeToFile(new File(getBackupStorageDirectory(), "backup-" + getTimestamp() + ".txt"));
        } else {
            throw new RuntimeException("Storage not ready to write.");
        }
    }

    public static File saveListToTempFile(Context context, List<HostRule> rules) throws IOException {
        File temp = new File(context.getCacheDir(), "temp");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(temp));

        for (HostRule rule : rules) {
            bufferedWriter.write(rule.toString());
            bufferedWriter.write("\n");
        }

        bufferedWriter.close();

        return temp;
    }

    public static File getBackupStorageDirectory() {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStorageDirectory().toString(), "OpenHostsEditor");

        if (!file.exists() && !file.mkdirs()) {
            Log.d("Ciao", file.toString());
            throw new RuntimeException("Could not create file directory.");
        }
        return file;
    }


    /* Checks if external storage is available for read and write */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private static String getTimestamp() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.ENGLISH).format(new Date());
    }
}
