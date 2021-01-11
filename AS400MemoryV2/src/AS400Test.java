import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.SystemPool;
import com.ibm.as400.access.SystemStatus;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Enumeration;

/**
 * Created with IntelliJ IDEA.
 * User: sashah
 * Date: 7/15/15
 * Time: 9:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class AS400Test {
    public static void main(String[] args) throws InterruptedException, IOException, ObjectDoesNotExistException, ErrorCompletingRequestException, AS400SecurityException {
        if (args.length < 2)
            System.out.println("Please provide warning and critical depth percentage, its required to execute program");
        else {
            Float warnDepth = Float.parseFloat(args[0]);
            Float criticalDepth = Float.parseFloat(args[1]);
            Float currentDepth;
            String statusInfo = "OK";
            ExitCode ext = ExitCode.OK;
            AS400 system = new AS400("172.16.1.49", "NAGIOS", "NAGIOS400");
//        Command c = new Command();
//        c.setSystem(system);
//        c.setPath("QWCRSSTS");

//        ConnectionPoolBeanInfo

//        SystemPool pool1 = new SystemPool(system, 1);
//        SystemPool pool2 = new SystemPool(system, 2);
//        SystemPool pool3 = new SystemPool(system, 3);
//        SystemPool pool4 = new SystemPool(system, 4);
//        System.out.println(pool1.getSize() / 1024);
//        System.out.println(pool2.getSize() / 1024);
//        System.out.println(pool3.getSize() / 1024);
//        System.out.println(pool4.getSize() / 1024);


            SystemStatus status = new SystemStatus(system);
            System.out.println("Jobs in the System: " + status.getJobsInSystem());
            System.out.println("Max Jobs in the System: " + status.getMaximumJobsInSystem());
            System.out.println("Active Jobs:" + status.getActiveJobsInSystem());
            System.out.println("Maximum Unprotected Storage Used: " + status.getMaximumUnprotectedStorageUsed());
            System.out.println("Current Unprotected Storage Used: " + status.getCurrentUnprotectedStorageUsed());

            Enumeration<SystemPool> pools = status.getSystemPools();
            while (pools.hasMoreElements()) {
                SystemPool pool = pools.nextElement();
                DecimalFormat twoDForm = new DecimalFormat("#.##");
                System.out.println(Double.valueOf(twoDForm.format((double) pool.getSize() / 1024)) + "," + pool.getName());
            }
//        c.refresh();
            // Create the program path name.
//        QSYSObjectPathName programName = new QSYSObjectPathName ("QSYS",
//                "QWCRSSTS", "PGM");
            currentDepth = Float.valueOf(((status.getCurrentUnprotectedStorageUsed() * 100) / status.getMaximumUnprotectedStorageUsed()));
            if (currentDepth >= criticalDepth) {
                statusInfo = "Critical [" + currentDepth + "] Queue Depth";
                ext = ExitCode.CRITICAL;
            } else if (currentDepth >= warnDepth && currentDepth < criticalDepth) {
                statusInfo = "Warning [" + currentDepth + "] Queue Depth";

                if (ext != null && (ext != ExitCode.CRITICAL)) {
                    ext = ExitCode.WARN;
                }
            }
            System.out.println("Status: " + statusInfo + ", Exit Code: " + ext);
            System.exit(ext.ordinal());
        }
    }

    public enum ExitCode
    {
        OK,
        WARN,
        CRITICAL,
        UNKNOWN
    }
}
