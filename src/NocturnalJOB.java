
import java.util.*;
import java.util.concurrent.*;

/**
 * Lo scopo di questa classe è creare un nuovo tipo di future che ha la possibilità
 * come i CompletableFuture di eseguire un CompletitionListener alla fine del calcolo sia esso
 * completato o fallito, ma mantenendo il controllo sopra la computazione dei FutureTask.
 * È simile come implementazione al (@code RenderJOB) che non fa parte della Public API(pur essendo la classe public),
 * ma dell' Iternal API. A differenza dei RenderJOB supporta anche i Callable di tipi generici.
 **/
public class NocturnalJOB<V> extends FutureTask<V> {

    @FunctionalInterface
    public interface CompletitionListener<T>{
        void done(NocturnalJOB<T> fut);
    }

    private int defaultpriority = 0;
    private volatile boolean completed = false;
    private volatile List<CompletitionListener<V>> listeners;

    /**
     * Creates a {@code NocturnalJOB} that will, upon running, execute the
     * given {@code Runnable}, and arrange that {@code get} will return the
     * given result on successful completion.
     *
     * @param runnable the runnable task
     * @param result   the result to return on successful completion. If
     *                 you don't need a particular result, consider using
     *                 constructions of the form:
     *                 {@code Future<?> f = new FutureTask<Void>(runnable, null)}
     * @throws NullPointerException if the runnable is null
     */
    public NocturnalJOB(Runnable runnable, V result) {
        super(runnable, result);
    }

    /**
     * Creates a {@code NocturnalJOB} that will, upon running, execute the
     * given {@code Callable}.
     *
     * @param callable the callable task
     * @throws NullPointerException if the callable is null
     */
    public NocturnalJOB(Callable<V> callable) {
        super(callable);
    }



    public  List<CompletitionListener<V>> getCompletionListener() {
        return listeners;
    }

    public void setCompletionListener(CompletitionListener<V> cl, Integer priority) {
        if(completed == true) {
            cl.done(this);
            return;
        }
        if(listeners == null){
            listeners = new ArrayList<>();
        }
        listeners.add(cl);
    }

    @Override
    public void run() {
        super.run();
        if (listeners != null) {
            try {
                listeners.forEach((listener) -> listener.done(this));
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        completed = true;
    }

    public void resolve(){

    }

    protected void runListener(){
    }

}
