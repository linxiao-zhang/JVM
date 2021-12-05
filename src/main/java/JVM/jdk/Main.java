package JVM.jdk;

import JVM.hotspot.src.share.vm.classfile.BootClassLoader;
import JVM.hotspot.src.share.vm.oops.InstanceKlass;
import JVM.hotspot.src.share.vm.oops.MethodInfo;
import JVM.hotspot.src.share.vm.prims.JavaNativeInterface;
import JVM.hotspot.src.share.vm.runtime.JavaThread;
import JVM.hotspot.src.share.vm.runtime.Threads;


public class Main {
    public static void main(String[] args) {
        startJVM();

    }
    public static void startJVM(){
        // 通过AppClassLoader加载main函数所在的类
        InstanceKlass mainKlass = BootClassLoader.loadMainKlass("JVM.example.HelloWorld");

        // 找到main方法
        MethodInfo mainMethod = JavaNativeInterface.getMethodID(mainKlass,"main", "([Ljava/lang/String;)V");

        // 创建线程
        JavaThread thread = new JavaThread();

        Threads.getThreadList().add(thread);
        Threads.setCurrentThread(thread);

        // 执行main方法
        assert mainMethod != null;
        JavaNativeInterface.callStaticMethod(mainMethod);
    }
}
