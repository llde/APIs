/**
 * Created by lorenzo on 17/11/15.
 */

import java.util.concurrent.*;

public class GreyFoxExecutor extends ThreadPoolExecutor {

    private volatile boolean running;
    private volatile boolean nestedlistener = true;

    public GreyFoxExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        running = true;
    }

    public GreyFoxExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        running = true;
    }



    public GreyFoxExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        running = true;
    }


    public GreyFoxExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        running = true;
    }

    public boolean IsSuspended(){
        return !running;
    }

    public static GreyFoxExecutor newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        return new GreyFoxExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                threadFactory);
    }




    /**
     * Creates a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available.  These pools will typically improve the performance
     * of programs that execute many short-lived asynchronous tasks.
     * Calls to {@code execute} will reuse previously constructed
     * threads if available. If no existing thread is available, a new
     * thread will be created and added to the pool. Threads that have
     * not been used for sixty seconds are terminated and removed from
     * the cache. Thus, a pool that remains idle for long enough will
     * not consume any resources. Note that pools with similar
     * properties but different details (for example, timeout parameters)
     * may be created using {@link ThreadPoolExecutor} constructors.
     *
     * @return the newly created thread pool
     */
    public static GreyFoxExecutor newCachedThreadPool() {
        return new GreyFoxExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>());
    }

    /**
     * Creates a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available, and uses the provided
     * ThreadFactory to create new threads when needed.
     * @param threadFactory the factory to use when creating new threads
     * @return the newly created thread pool
     * @throws NullPointerException if threadFactory is null
     */
    public static GreyFoxExecutor newCachedThreadPool(ThreadFactory threadFactory) {
        return new GreyFoxExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                threadFactory);
    }

    @Override
    protected <T> NocturnalJOB<T> newTaskFor(Runnable runnable, T value) {
        return new NocturnalJOB<>(runnable, value);
        //Ricreare la possibilità di creare un listener generale per l'esecutore

    }

    @Override
    protected <T> NocturnalJOB<T> newTaskFor(Callable<T> callable) {
        return new NocturnalJOB<>(callable);
    }

    @Override
    public NocturnalJOB<Void> submit(Runnable task) {
        if (task == null) throw new NullPointerException();
        NocturnalJOB<Void> ftask = newTaskFor(task, null);
        super.execute(ftask);
        return ftask;
    }

    @Override
    public <T> NocturnalJOB<T> submit(Runnable task, T result) {
        if (task == null) throw new NullPointerException();
        NocturnalJOB<T> ftask = newTaskFor(task, null);
        super.execute(ftask);
        return ftask;
    }

    @Override
    public <T> NocturnalJOB<T> submit(Callable<T> task) {
        if (task == null) throw new NullPointerException();
        NocturnalJOB<T> ftask = newTaskFor(task);
         super.execute(ftask);
        return ftask;
    }

    @SuppressWarnings("unchecked")
    public <T> NocturnalJOB<T> submit(Callable<T> task, NocturnalJOB.CompletitionListener<T> listener){
        if (task == null) throw new NullPointerException();
        NocturnalJOB<T> ftask = newTaskFor(task);
        if(task.getClass().equals(NocturnalJOB.class) && nestedlistener == true) {
            ((NocturnalJOB<T>) task).setCompletionListener(listener, 0);
        }
        else ftask.setCompletionListener(listener, 0);
        super.execute(ftask);
        return ftask;
    }

    @SuppressWarnings("unchecked")
    public <T> NocturnalJOB<T> submit(Runnable task, T value, NocturnalJOB.CompletitionListener<T> listener){
        if (task == null) throw new NullPointerException();
        NocturnalJOB<T> ftask = newTaskFor(task, value);
        if(task.getClass().equals(NocturnalJOB.class)  && nestedlistener == true) {
            ((NocturnalJOB<T>)  task).setCompletionListener(listener,0);
        }
        else ftask.setCompletionListener(listener, 0);
        super.execute(ftask);
        return ftask;
    }

    public void setNested(boolean nest) { nestedlistener = nest;}


}

