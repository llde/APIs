import javafx.application.Platform;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Lorenzo on 21/01/2016.
 */
public class ThreadUtils {

    private static final List<WeakReference<Thread>> threadList = new ArrayList<>();

    private static final List<WeakReference<ExecutorService>> executorServiceList = new ArrayList<>();

    private static final List<String> auth = new ArrayList<>();

    public static synchronized Thread createTrackThread(Runnable run){
        Thread t = new Thread(run);
        threadList.add(new WeakReference<>(t));
        return t;
    }


    public static synchronized <T extends ExecutorService>  T  TrackExecutor(T executorservice){
        executorServiceList.add(new WeakReference<>(executorservice));
        return executorservice;
    }

    public static  synchronized void dispose(boolean isJavaFXApp){
        if(!auth.contains(Thread.currentThread().getStackTrace()[2].getClassName())) throw new RuntimeException("Classe o metodo non autorizzato");
        if(!threadList.isEmpty()){
            for(WeakReference<Thread> t: threadList){
                Thread x = t.get();
                if(x != null){
                    if(!x.isDaemon() && x.isAlive())  x.interrupt();
                }
            }
            for(WeakReference<ExecutorService> exs : executorServiceList){
                ExecutorService e = exs.get();
                if(e != null){
                    if(!e.isShutdown()){
                        e.shutdownNow();
                    }
                }
            }
        }
        if(isJavaFXApp) Platform.exit();
    }
}
