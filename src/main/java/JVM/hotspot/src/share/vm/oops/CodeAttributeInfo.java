package JVM.hotspot.src.share.vm.oops;

import JVM.hotspot.src.share.vm.interpreter.BytecodeStream;
import lombok.Data;

import javax.management.Attribute;
import java.util.HashMap;

@Data
public class CodeAttributeInfo {
    private int attrNameIndex;
    private int attrLength;
    private int maxStack;
    private int maxLocals;
    private int codeLength;
    private BytecodeStream code;
    private int exceptionTableLength;
    // 如局部变量表, 操作数栈
    private int attributeCount;
    private HashMap<String, AttributeInfo> attributes = new HashMap<>();

}
