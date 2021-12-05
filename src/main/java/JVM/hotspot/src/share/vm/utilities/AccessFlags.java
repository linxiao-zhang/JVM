package JVM.hotspot.src.share.vm.utilities;

public class AccessFlags {
    private int flag;
    public AccessFlags(int flag){
        this.flag = flag;
    }
    public boolean isStatic(){
        return (flag & BasicType.JVM_ACC_STATIC) != 0;
    }
}
