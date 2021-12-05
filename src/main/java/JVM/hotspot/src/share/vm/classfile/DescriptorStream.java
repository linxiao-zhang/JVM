package JVM.hotspot.src.share.vm.classfile;

import JVM.hotspot.src.share.vm.memory.ResourceObj;
import JVM.hotspot.src.share.vm.oops.DescriptorInfo;
import JVM.hotspot.src.share.vm.utilities.BasicType;

import java.util.List;

/**
 * 将方法，属性的签名转化为byte 数组,然后进行分析
 */
public class DescriptorStream extends ResourceObj {
    private byte[] container;
    private DescriptorInfo info = new DescriptorInfo();
    public DescriptorStream(String descriptor){
        container = descriptor.getBytes();
    }

    private String parseArrRefrenceType(){
        //跳过数组维度个[,开头的L,结尾的
        int size = container.length - info.getArrayDimension()-2;
        byte[] str = new byte[size];
        //跳过开头的l,结尾的
        int j = 0;
        for(int i=info.getArrayDimension()+1;i<size+1+info.getArrayDimension();i++){
            str[j++]  =container[i];
        }
        return new String(str);
    }

    public String parseRefrenceType(){
        //跳过开头的L,结尾的
        int size = container.length-2;
        byte[] str = new byte[size];
        //跳过开头的L, 结尾的
        int j = 0;
        for(int i=0;i<container.length-1;i++){
            str[j++] = container[i];
        }
        return new String(str);
    }

    public void parseParams(List<DescriptorInfo> infos){
        for (byte b : container) {
            switch (b) {
                case 'Z'://boolean
                    infos.add(new DescriptorInfo(true, BasicType.T_BOOLEAN));
                    break;
                case 'B': //byte
                    infos.add(new DescriptorInfo(true, BasicType.T_BYTE));
                    break;
                case 'C': //char
                    infos.add(new DescriptorInfo(true, BasicType.T_CHAR));
                    break;
                case 'I': //int
                    infos.add(new DescriptorInfo(true, BasicType.T_INT));
                    break;
                case 'F': //float
                    infos.add(new DescriptorInfo(true, BasicType.T_FLOAT));
                    break;
                case 'J': //long
                    infos.add(new DescriptorInfo(true, BasicType.T_LONG));
                    break;
                case 'D': //double
                    infos.add(new DescriptorInfo(true, BasicType.T_DOUBLE));
                    break;
                case 'V':
                    infos.add(new DescriptorInfo(true, BasicType.T_VOID));
                    break;
                default:
                    throw new Error("无法识别的类型");
            }
        }

    }

    private String parseArrayType(){
        for (byte b : container) {
            //取出数组维度
            if ('[' == b) {
                info.incArrayDimension();
                continue;
            }
            if (b != 'L') {
                return null;
            }

            return parseArrRefrenceType();
        }
        return null;
    }

    public DescriptorInfo parse(){
        for (byte b : container) {
            switch (b) {
                case '[': {
                    String s = parseArrayType();
                    info.setType(BasicType.T_ARRAY);
                    info.setTypeDesc(s);
                    return info;
                }
                case 'L': {
                    String s = parseRefrenceType();
                    info.setType(BasicType.T_OBJECT);
                    info.setTypeDesc(s);
                    return info;
                }
                case 'Z':
                    info.setType(BasicType.T_BOOLEAN);
                    return info;
                case 'B':
                    info.setType(BasicType.T_BYTE);
                    return info;
                case 'C':
                    info.setType(BasicType.T_CHAR);
                    return info;
                case 'I':
                    info.setType(BasicType.T_INT);
                    return info;
                case 'F':
                    info.setType(BasicType.T_FLOAT);
                    return info;
                case 'J':
                    info.setType(BasicType.T_LONG);
                    return info;
                case 'D':
                    info.setType(BasicType.T_DOUBLE);
                    return info;
                case 'V':
                    info.setType(BasicType.T_VOID);
                    return info;
                default:
                    throw new Error("无法识别的类型");
            }
        }
        return info;

    }
}
