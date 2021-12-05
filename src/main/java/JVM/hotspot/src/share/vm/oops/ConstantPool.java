package JVM.hotspot.src.share.vm.oops;

import lombok.Data;

import java.util.HashMap;

@Data
public class ConstantPool {
    public static final int JVM_CONSTANT_Utf8 = 1;
    public static final int JVM_CONSTANT_Unicode = 2;   /* unused */
    public static final int JVM_CONSTANT_Integer = 3;
    public static final int JVM_CONSTANT_Float = 4;
    public static final int JVM_CONSTANT_Long = 5;
    public static final int JVM_CONSTANT_Double = 6;
    public static final int JVM_CONSTANT_Class = 7;
    public static final int JVM_CONSTANT_String = 8;
    public static final int JVM_CONSTANT_Fieldref = 9;
    public static final int JVM_CONSTANT_Methodref = 10;
    public static final int JVM_CONSTANT_InterfaceMethodref = 11;
    public static final int JVM_CONSTANT_NameAndType = 12;
    public static final int JVM_CONSTANT_MethodHandle = 15; /* JSR 292 */
    public static final int JVM_CONSTANT_MethodType = 16;   /* JSR 292 */
    public static final int JVM_CONSTANT_InvokeDynamic = 18;    /* JSR 292 */
    public static final int JVM_CONSTANT_ExternalMax = 18;  /* Last tag found in classfiles */

    private Klass klass;
    private int length;


    //index -> type
    private int[] tag;
    //index -> value
    private HashMap<Integer, Object> dataMap;

    public void initContainer(){
        this.tag = new int[length];
        this.dataMap = new HashMap<>(length);
    }

    public String getClassName(int index){
        if(0==index || index>length){
            return null;
        }
        return (String) getDataMap().get(getDataMap().get(index));
    }

    public String getSuperClassName(int index){
        if(index==0||index>length){
            return null;
        }
        return (String) getDataMap().get(index);
    }

    public String getMethodName(int index){
        if(index==0 || index>length){
            return null;
        }
        return (String) getDataMap().get(index);
    }

    public String getDescriptorName(int index){
        if(index==0 || index>length){
            return null;
        }
        return (String) getDataMap().get(index);
    }

    public String getClassNameByFieldInfo(int index){
        int data = (int) getDataMap().get(index);
        int classIndex = data >> 16;

        int classNameIndex = (int) getDataMap().get(classIndex);

        return (String) getDataMap().get(classNameIndex);
    }

    public String getClassNameByMethodInfo(int index){
        return getClassNameByFieldInfo(index);
    }

    public String getFieldName(int index){
        int data = (int) getDataMap().get(index);
        int i = data & 0xFF;
        int nameAndType = (int) getDataMap().get(i);
        i = nameAndType >> 16;
        return (String) getDataMap().get(i);
    }

    public String getMethodNameByMethodInfo(int operand) {
        // 获取Methodinfo在常量池中的index
        int i = (int) getDataMap().get(operand);
        int nameAndTypeIndex = i & 0xff;

        // 获取NameAndType的值
        int data = (int) getDataMap().get(nameAndTypeIndex);
        i = data >> 16;

        return (String) getDataMap().get(i);
    }

    public String getDescriptorNameByMethodInfo(int operand) {
        // 获取Methodinfo在常量池中的index
        int i = (int) getDataMap().get(operand);
        int nameAndTypeIndex = i & 0xff;

        // 获取NameAndType的值
        int data = (int) getDataMap().get(nameAndTypeIndex);
        i = data & 0xFF;

        return (String) getDataMap().get(i);
    }


}
