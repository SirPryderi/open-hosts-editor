package me.vittorio_io.openhostseditor.model;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by Vittorio on 17/07/17.
 * <p>
 * This class handles the execution of commands requiring super user access.
 * <p>
 * Original class taken from: http://muzikant-android.blogspot.it/2011/02/how-to-get-root-access-and-execute.html
 */

@SuppressWarnings("WeakerAccess")
public abstract class ExecuteAsRootBase {
    public static boolean isRootAvailable() {
        for (String pathDir : System.getenv("PATH").split(":")) {
            if (new File(pathDir, "su").exists()) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public static boolean canRunRootCommands() {
        boolean returnValue;
        Process suProcess;

        try {
            suProcess = Runtime.getRuntime().exec("su");

            OutputStreamWriter os = new OutputStreamWriter(new BufferedOutputStream(suProcess.getOutputStream()));
            BufferedReader osRes = new BufferedReader(new InputStreamReader(new BufferedInputStream(suProcess.getInputStream())));

            // Getting the id of the current user to check if this is root
            os.write("id\n");
            os.flush();


            String currUid = osRes.readLine();

            boolean exitSu;

            if (null == currUid) {
                returnValue = false;
                exitSu = false;
                Log.d("ROOT", "Can't get root access or denied by user");
            } else if (currUid.contains("uid=0")) {
                returnValue = true;
                exitSu = true;
                Log.d("ROOT", "Root access granted");
            } else {
                returnValue = false;
                exitSu = true;
                Log.d("ROOT", "Root access rejected: " + currUid);
            }

            if (exitSu) {
                os.write("exit\n");
                os.flush();
            }
        } catch (Exception e) {
            // Can't get root !
            // Probably broken pipe exception on trying to write to output stream (os) after su failed, meaning that the device is not rooted

            returnValue = false;
            Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
        }

        return returnValue;
    }

    public final boolean execute() {
        boolean returnValue = false;

        try {
            ArrayList<String> commands = getCommandsToExecute();
            if (null != commands && commands.size() > 0) {
                Process suProcess = Runtime.getRuntime().exec("su");

                DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());

                // Execute commands that require root access
                for (String currCommand : commands) {
                    os.writeBytes(currCommand + "\n");
                    os.flush();
                }

                os.writeBytes("exit\n");
                os.flush();

                try {
                    int suProcessReturnValue = suProcess.waitFor();
                    // Root access granted  or denied
                    returnValue = 255 != suProcessReturnValue;
                } catch (Exception ex) {
                    Log.e("ROOT", "Error executing root action", ex);
                }
            }
        } catch (IOException | SecurityException ex) {
            Log.w("ROOT", "Can't get root access", ex);
        } catch (Exception ex) {
            Log.w("ROOT", "Error executing internal operation", ex);
        }

        return returnValue;
    }

    protected abstract ArrayList<String> getCommandsToExecute();
}
