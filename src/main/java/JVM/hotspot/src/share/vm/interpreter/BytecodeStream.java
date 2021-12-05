package JVM.hotspot.src.share.vm.interpreter;

import JVM.hotspot.src.share.vm.oops.CodeAttributeInfo;
import JVM.hotspot.src.share.vm.oops.MethodInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BytecodeStream extends BaseBytecodeStream{
    public BytecodeStream(MethodInfo belongMethod, CodeAttributeInfo belongCode){
        this.belongMethod = belongMethod;
        this.belongCode = belongCode;
        this.length = belongCode.getCodeLength();
        this.index = 0;
        this.codes = new byte[this.length];
    }
}
