package JVM.hotspot.src.share.vm.runtime;

import JVM.hotspot.src.share.vm.memory.AllStatic;

import java.util.ArrayList;
import java.util.List;

public class Threads extends AllStatic {
    /**
     * 所有的Java基本线程全部存储在这个List中
     */
    private static List<Thread> threadList;
    private static Thread currentThread;

    static {
        threadList = new ArrayList<>();
    }

    public static List<Thread> getThreadList(){
        return threadList;
    }

    public static JavaThread currentThread(){
        return (JavaThread) currentThread;
    }

    public static void setCurrentThread(Thread thread){
        currentThread = thread;
    }
}
