package JVM.hotspot.src.share.vm.oops;

import lombok.Data;

@Data
public class AttributeInfo {
    private int attrNameIndex;
    private int attrLength;
    private byte[] container;

    public void initContainer(){
        container  = new byte[attrLength];
    }
}
