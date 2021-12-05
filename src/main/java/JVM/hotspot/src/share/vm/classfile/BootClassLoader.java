package JVM.hotspot.src.share.vm.classfile;

import JVM.hotspot.src.share.vm.oops.InstanceKlass;
import JVM.hotspot.src.share.vm.oops.Klass;
import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.HashMap;

public class BootClassLoader {
    public static final String SUFFIX = ".class";
    private static String searchPath = "/Users/zhanglinxiao/JVM/target/classes/";

    private static HashMap<String, InstanceKlass> classLoaderData = new HashMap<>();

    private static InstanceKlass mainKlass = null;

    public static InstanceKlass getMainKlass(){
        return mainKlass;
    }
    public static void setMainKlass(InstanceKlass mainKlass){
        BootClassLoader.mainKlass = mainKlass;
    }

    public static InstanceKlass loadKlass(String name){
        return loadKlass(name ,true);
    }

    public static InstanceKlass loadKlass(String name, Boolean resolve){
        InstanceKlass klass = findLoadedKlass(name);
        if(klass!=null){
            return klass;
        }

        klass = readAndParse(name);

        if(resolve){
            resolveKlass();
        }
        return klass;
    }


    private static InstanceKlass readAndParse(String name) {
        String tmpName = name.replace('.', '/');
        String filePath = searchPath + tmpName + SUFFIX;

        // 读取字节码文件
        byte[] content = FileUtil.readBytes(new File(filePath));

        // 解析字节码文件
        InstanceKlass klass = ClassFileParser.parseClassFile(content);

        // 存入
        classLoaderData.put(name, klass);

        return klass;
    }

    private static void resolveKlass() {
    }

    public static InstanceKlass findLoadedKlass(String name) {
        return classLoaderData.get(name);
    }

    public static InstanceKlass loadMainKlass(String name) {
        if (null != mainKlass) {
            return mainKlass;
        }

        return loadKlass(name);
    }


}
