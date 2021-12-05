package JVM.hotspot.src.share.vm.runtime;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Stack;
@EqualsAndHashCode(callSuper = true)
@Data
public class JavaThread extends Thread{
    private Stack<VFrame> stack = new Stack<>();
}
