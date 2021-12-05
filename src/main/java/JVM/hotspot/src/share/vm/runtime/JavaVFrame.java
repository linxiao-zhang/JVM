package JVM.hotspot.src.share.vm.runtime;

import JVM.hotspot.src.share.vm.oops.MethodInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JavaVFrame extends VFrame{
    private StackValueCollection locals;
    private StackValueCollection stack = new StackValueCollection();
    private MethodInfo ownerMethod;

    public JavaVFrame(int maxLocals){
        locals = new StackValueCollection(maxLocals);
    }

    public JavaVFrame(int maxLocals, MethodInfo methodInfo){
        locals = new StackValueCollection(maxLocals);
        ownerMethod = methodInfo;
    }
}
