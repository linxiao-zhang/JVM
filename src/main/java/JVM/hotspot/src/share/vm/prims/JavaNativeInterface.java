package JVM.hotspot.src.share.vm.prims;

import JVM.hotspot.src.share.vm.interpreter.BytecodeInterpreter;
import JVM.hotspot.src.share.vm.oops.CodeAttributeInfo;
import JVM.hotspot.src.share.vm.oops.InstanceKlass;
import JVM.hotspot.src.share.vm.oops.MethodInfo;
import JVM.hotspot.src.share.vm.runtime.JavaThread;
import JVM.hotspot.src.share.vm.runtime.JavaVFrame;
import JVM.hotspot.src.share.vm.runtime.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaNativeInterface {
    private final static Logger logger = LoggerFactory.getLogger(JavaNativeInterface.class);
    public static InstanceKlass findClass(String name){
        return null;
    }

    public static MethodInfo getMethodID(InstanceKlass klass, String name, String descriptorName){
        MethodInfo[] methods = klass.getMethods();
        for(MethodInfo method:methods){
            String tmpName = (String) klass.getConstantPool().getDataMap().get(method.getNameIndex());
            String tmpDescriptor  = (String) klass.getConstantPool().getDataMap().get(method.getDescriptorIndex());
            if(tmpName.equals(name) && tmpDescriptor.equals(descriptorName)){
                logger.info("找到了方法: " + name + "#" + descriptorName);
                return method;
            }
        }

        logger.info("没有找到方法");
        return null;

    }

    public static void callStaticMethod(MethodInfo method){

        JavaThread thread = Threads.currentThread();
        if(!method.getAccessFlags().isStatic()){
            throw new Error("只能调用静态方法");
        }

        CodeAttributeInfo codeAttributeInfo = method.getAttributes()[0];

        //创建栈帧
        JavaVFrame frame = new JavaVFrame(codeAttributeInfo.getMaxLocals(), method);
        thread.getStack().push(frame);
        logger.info("第 " + thread.getStack().size() + " 个栈帧");

        //执行任务交给字节码解释器
        BytecodeInterpreter.run(thread,method);


    }


}
