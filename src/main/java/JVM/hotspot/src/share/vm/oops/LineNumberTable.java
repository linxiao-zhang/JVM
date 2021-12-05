package JVM.hotspot.src.share.vm.oops;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LineNumberTable extends AttributeInfo{
    private int tableLength;
    private Item[] table;

    public void initTable(){
        this.table = new Item[this.tableLength];
    }

    @Data
    public class Item{
        private int startPc;
        private int lineNumber;
    }
}
