package JVM.hotspot.src.share.vm.classfile;

import JVM.hotspot.src.share.tools.DataTranslate;
import JVM.hotspot.src.share.tools.Stream;
import JVM.hotspot.src.share.vm.interpreter.BytecodeStream;
import JVM.hotspot.src.share.vm.oops.*;
import JVM.hotspot.src.share.vm.utilities.AccessFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassFileParser {
    private static Logger logger  = LoggerFactory.getLogger(ClassFileParser.class);


    public static int parseConstantPool(byte[] content, InstanceKlass klass, int index){
        logger.info("解析常量池:");

        byte[] u2Arr = new byte[2];
        byte[] u4Arr = new byte[4];
        byte[] u8Arr = new byte[8];
        for(int i=1;i<klass.getConstantPool().getLength();i++){
            int tag = Stream.readU1Simple(content, index);
            index += 1;

            switch(tag){
                case ConstantPool.JVM_CONSTANT_Utf8:{
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Utf8;
                    //字符串长度
                    Stream.readU2Simple(content, index, u2Arr);
                    index += 2;

                    int len = DataTranslate.byteToUnsignedShort(u2Arr);

                    //字符串内容
                    byte[] str = new byte[len];
                    Stream.readSimple(content, index, len ,str);
                    index += len;

                    klass.getConstantPool().getDataMap().put(i,new String(str));
                    logger.info("\t第" + i + "个: 类型: utf8,值: "+ klass.getConstantPool().getDataMap().get(i));
                    break;
                }
                case ConstantPool.JVM_CONSTANT_Integer:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Integer;
                    throw new Error("程序未做处理");
                case ConstantPool.JVM_CONSTANT_Float:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Float;

                    Stream.readU4Simple(content, index, u4Arr);
                    index += 4;

                    klass.getConstantPool().getDataMap().put(i,DataTranslate.byteToFloat(u4Arr));

                    logger.info("\t第" + i +" 个: 类型: Float, 值:" + klass.getConstantPool().getDataMap().get(i));
                    break;
                case ConstantPool.JVM_CONSTANT_Long:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Long;
                    throw new Error("程序未处理");
                case ConstantPool.JVM_CONSTANT_Double:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Double;
                    Stream.readU8Simple(content, index, u8Arr);
                    index += 8;
                    klass.getConstantPool().getDataMap().put(i,DataTranslate.bytesToDouble(u8Arr, false));
                    logger.info("\t第" + i +" 个:类型:Double, 值: "+ klass.getConstantPool().getDataMap().get(i));
                    klass.getConstantPool().getTag()[++i] = ConstantPool.JVM_CONSTANT_Double;
                    klass.getConstantPool().getDataMap().put(i, DataTranslate.bytesToDouble(u8Arr, false));
                    logger.info("\t第" + i + "个: 类型: Double,值：" + klass.getConstantPool().getDataMap().get(i));
                    break;
                case ConstantPool.JVM_CONSTANT_Class:{
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Class;
                    //索引
                    Stream.readU2Simple(content, index, u2Arr);
                    index += 2;
                    klass.getConstantPool().getDataMap().put(i,DataTranslate.byteToUnsignedShort(u2Arr));
                    logger.info("sds");
                    break;
                }
                case ConstantPool.JVM_CONSTANT_String:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_String;

                    // Utf8_info
                    Stream.readU2Simple(content, index, u2Arr);
                    index += 2;

                    klass.getConstantPool().getDataMap().put(i, DataTranslate.byteToUnsignedShort(u2Arr));

                    logger.info("\t第 " + i+ " 个: 类型: String，值无法获取，因为字符串的内容还未解析到");

                    break;
                case ConstantPool.JVM_CONSTANT_Fieldref:{
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Fieldref;
                    //class_info
                    Stream.readU2Simple(content, index, u2Arr);
                    index += 2;

                    int classIndex = DataTranslate.byteToUnsignedShort(u2Arr);
                    //NameandType info
                    Stream.readU2Simple(content, index, u2Arr);
                    index += 2;
                    int nameAndTypeIndex = DataTranslate.byteToUnsignedShort(u2Arr);
                    klass.getConstantPool().getDataMap().put(i,classIndex << 16 | nameAndTypeIndex);
                    break;
                }
                case ConstantPool.JVM_CONSTANT_Methodref:{
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_Fieldref;
                    //Class_info
                    Stream.readU2Simple(content, index, u2Arr);
                    index += 2;
                    int classIndex = DataTranslate.byteToUnsignedShort(u2Arr);
                    //NameAndType info
                    Stream.readU2Simple(content, index, u2Arr);
                    index += 2;
                    int nameAndTypeIndex = DataTranslate.byteToUnsignedShort(u2Arr);
                    //将classIndex与nameAndTypeIndex拼成一个，前16位是classIndex,后十六位是nameAndTypeIndex
                    klass.getConstantPool().getDataMap().put(i, classIndex<<16 | nameAndTypeIndex);
                    break;
                }
                case ConstantPool.JVM_CONSTANT_InterfaceMethodref:
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_InterfaceMethodref;
                    //Class_info
                    Stream.readU2Simple(content, index, u2Arr);
                    index += 2;

                    int classIndex = DataTranslate.byteToUnsignedShort(u2Arr);
                    //NameAndTypeinfo
                    Stream.readU2Simple(content, index, u2Arr);
                    index += 2;

                    int namedAndTypeIndex = DataTranslate.byteToUnsignedShort(u2Arr);

                    //将classIndex与namedAndType拼成一个,前16位是classIndex,后十六位是nameAndTypeIndex;
                    klass.getConstantPool().getDataMap().put(i, classIndex << 16 | namedAndTypeIndex);
                    break;
                case ConstantPool.JVM_CONSTANT_NameAndType:{
                    klass.getConstantPool().getTag()[i] = ConstantPool.JVM_CONSTANT_NameAndType;
                    //方法名
                    Stream.readU2Simple(content, index, u2Arr);
                    index += 2;

                    int methodNameIndex = DataTranslate.byteToUnsignedShort(u2Arr);

                    //方法描述符
                    Stream.readU2Simple(content, index, u2Arr);
                    index += 2;

                    int methodDescriptorIndex = DataTranslate.byteToUnsignedShort(u2Arr);
                    klass.getConstantPool().getDataMap().put(i, methodNameIndex << 16 | methodDescriptorIndex);
                    break;
                }
                default:
                    throw new Error("无法识别的常量池");
            }

        }
        return index;

    }

    public static int parseInterFace(byte[] content, InstanceKlass klass, int index){
        byte[] u2Arr = new byte[2];
        for(int i=0;i< klass.getInterfacesLength();i++){
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            int val = DataTranslate.byteToUnsignedShort(u2Arr);
            String name = klass.getConstantPool().getClassName(val);

            InterfaceInfo interfaceInfo = new InterfaceInfo(val ,name);
            klass.getInterfaceInfos().add(interfaceInfo);
        }
        return index;
    }

    private static int parseFields(byte[] content, InstanceKlass klass, int index){
        logger.info("解析属性");

        for(int i=0; i<klass.getFieldsLength();i++){
            byte[] u2Arr = new byte[2];
            FieldInfo fieldInfo = new FieldInfo();
            klass.getFields().add(fieldInfo);
            // access flag
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            fieldInfo.setAccessFlags(DataTranslate.byteToUnsignedShort(u2Arr));

            //name index
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            fieldInfo.setNameIndex(DataTranslate.byteToUnsignedShort(u2Arr));
            //descriptor index
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            fieldInfo.setDescriptorIndex(DataTranslate.byteToUnsignedShort(u2Arr));
            // attribute count
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            fieldInfo.setAttributesCount(DataTranslate.byteToUnsignedShort(u2Arr));
            //attribute
            if(fieldInfo.getAttributesCount()!=0){
                throw new Error("属性的attribute count!=0");
            }
        }
        return index;
    }

    private static int parseLineNumberTable(byte[] content, int index, String attrName, CodeAttributeInfo attributeInfo){
        byte[] u2Arr = new byte[2];
        byte[] u4Arr = new byte[4];
        LineNumberTable lineNumberTable = new LineNumberTable();
        attributeInfo.getAttributes().put(attrName,lineNumberTable);
        //attr name index
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;

        lineNumberTable.setAttrNameIndex(DataTranslate.byteToUnsignedShort(u2Arr));
        //attr len
        Stream.readU4Simple(content, index, u4Arr);
        index += 4;
        lineNumberTable.setAttrLength(DataTranslate.byteToUnsignedShort(u4Arr));
        //table length
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        lineNumberTable.setTableLength(DataTranslate.byteToUnsignedShort(u2Arr));
        lineNumberTable.initTable();
        //table
        if(lineNumberTable.getTableLength()!=0){
            for(int l=0;l<lineNumberTable.getTableLength();l++){
                LineNumberTable.Item item  = lineNumberTable.new Item();
                lineNumberTable.getTable()[l] = item;
                //start pc
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                item.setStartPc(DataTranslate.byteToUnsignedShort(u2Arr));
                // line number
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                item.setLineNumber(DataTranslate.byteToUnsignedShort(u2Arr));
            }
        }
        return index;


    }

    private static int parseLocalVariableTable(byte[] content, int index, String attrName, CodeAttributeInfo attributeInfo){
        byte[] u2Arr = new byte[2];
        byte[] u4Arr = new byte[4];
        LocalVariableTable localVariableTable = new LocalVariableTable();
        attributeInfo.getAttributes().put(attrName, localVariableTable);
        //attr name index
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;

        localVariableTable.setAttrNameIndex(DataTranslate.byteToUnsignedShort(u2Arr));
        //attr len
        Stream.readU4Simple(content, index, u4Arr);
        index += 4;
        localVariableTable.setAttrLength(DataTranslate.byteArrayToInt(u4Arr));
        //table length
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        localVariableTable.setTableLength(DataTranslate.byteToUnsignedShort(u2Arr));
        localVariableTable.initTable();
        if(localVariableTable.getTableLength()==0){
            return index;
        }
        //table
        for(int i=0;i<localVariableTable.getTableLength();i++){
            LocalVariableTable.Item item =localVariableTable.new Item();
            localVariableTable.getTable()[i] = item;
            //start pc
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            item.setStartPc(DataTranslate.byteToUnsignedShort(u2Arr));
            //length
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            item.setStartPc(DataTranslate.byteToUnsignedShort(u2Arr));
            //name index
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            item.setNameIndex(DataTranslate.byteToUnsignedShort(u2Arr));
            //descriptor index
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            item.setDescriptorIndex(DataTranslate.byteToUnsignedShort(u2Arr));
            //index
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            item.setDescriptorIndex(DataTranslate.byteToUnsignedShort(u2Arr));



        }
        return index;
    }

    public static int parseMethods(byte[] content, InstanceKlass klass, int index){
        for(int i=0;i<klass.getMethodLength();i++){
            byte[] u2Arr = new byte[2];
            byte[] u4Arr = new byte[4];

            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setBelongKlass(klass);
            klass.getMethods()[i] = methodInfo;
            //access flag
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            methodInfo.setAccessFlags(new AccessFlags(DataTranslate.byteToUnsignedShort(u2Arr)));
            //name index
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            methodInfo.setNameIndex(DataTranslate.byteToUnsignedShort(u2Arr));
            methodInfo.setMethodName((String)methodInfo.getBelongKlass().getConstantPool().getDataMap().get(methodInfo.getNameIndex()));
            logger.info("解析方法: " + methodInfo.getMethodName());
            //descriptor index
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;

            methodInfo.setDescriptorIndex(DataTranslate.byteToUnsignedShort(u2Arr));
            //attribute count
            Stream.readU2Simple(content, index, u2Arr);
            index += 2;
            methodInfo.setAttributesCount(DataTranslate.byteToUnsignedShort(u2Arr));
            methodInfo.initAttributeContainer();
            //解析方法属性
            if(methodInfo.getAttributesCount()!=1){
                throw new Error("方法的属性不止一个");
            }
            for(int j=0;j<methodInfo.getAttributesCount();j++){
                CodeAttributeInfo attributeInfo = new CodeAttributeInfo();
                methodInfo.getAttributes()[j] = attributeInfo;
                //attr name index
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                attributeInfo.setAttrNameIndex(DataTranslate.byteToUnsignedShort(u2Arr));
                //attr length
                Stream.readU4Simple(content, index, u4Arr);
                index += 4;
                attributeInfo.setAttrLength(DataTranslate.byteArrayToInt(u4Arr));
                //max stack
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                attributeInfo.setMaxStack(DataTranslate.byteToUnsignedShort(u2Arr));
                //max locals
                Stream.readU2Simple(content, index, u2Arr);
                index +=2;
                attributeInfo.setMaxLocals(DataTranslate.byteToUnsignedShort(u2Arr));
                //code length
                Stream.readU4Simple(content, index, u4Arr);
                index += 4;
                attributeInfo.setCodeLength(DataTranslate.byteArrayToInt(u4Arr));
                //code
                BytecodeStream bytecodeStream = new BytecodeStream(methodInfo, attributeInfo);
                attributeInfo.setCode(bytecodeStream);
                Stream.readSimple(content, index, attributeInfo.getCodeLength(),bytecodeStream.getCodes());
                index += attributeInfo.getCodeLength();

                //exception table length
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                attributeInfo.setExceptionTableLength(DataTranslate.byteToUnsignedShort(u2Arr));
                // attributes count
                Stream.readU2Simple(content, index, u2Arr);
                index += 2;
                attributeInfo.setAttributeCount(DataTranslate.byteToUnsignedShort(u2Arr));
                for(int k=0;k<attributeInfo.getAttributeCount();k++){
                    //attr name index
                    Stream.readU2Simple(content, index, u2Arr);
                    String attrName = (String) klass.getConstantPool().getDataMap().get(DataTranslate.byteToUnsignedShort(u2Arr));
                    if(attrName.equals("LineNumberTable")){
                        index = parseLineNumberTable(content, index, attrName, attributeInfo);
                    } else if(attrName.equals("LocalVariableTable")){
                        index = parseLocalVariableTable(content, index, attrName, attributeInfo);
                    }

                }

            }
            //判断是不是main 函数
            String methodName = (String) klass.getConstantPool().getDataMap().get(methodInfo.getNameIndex());
            String descriptorName = (String) klass.getConstantPool().getDataMap().get(methodInfo.getDescriptorIndex());
            if(methodName.equals("main") && descriptorName.equals("([Ljava/lang/String;)V")){
                logger.info("找到main函数所在的类");
                BootClassLoader.setMainKlass(klass);
            }

        }
        return index;
    }

    private static int parseSourceFile(byte[] content, int index, InstanceKlass klass){
        byte[] u2Arr = new byte[2];
        byte[] u4Arr = new byte[4];

        AttributeInfo attributeInfo = new AttributeInfo();
        klass.getAttributeInfos().add(attributeInfo);

        //name index
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        attributeInfo.setAttrNameIndex(DataTranslate.byteToUnsignedShort(u2Arr));
        //length
        Stream.readU4Simple(content, index, u4Arr);
        index += 4;
        attributeInfo.setAttrLength(DataTranslate.byteArrayToInt(u4Arr));
        attributeInfo.initContainer();
        //data
        Stream.readU2Simple(content, index, attributeInfo.getContainer());
        index += 2;
        return index;
    }

    public static InstanceKlass parseClassFile(byte[] content){
        int index = 0;
        InstanceKlass klass  = new InstanceKlass();
        byte[] u2Arr = new byte[2];
        byte[] u4Arr = new byte[4];

        //魔数4B
        Stream.readU4Simple(content,0,klass.getMagic());
        index += 4;

        //次版本号2B
        Stream.readU2Simple(content, index, klass.getMinorVersion());
        index += 2;

        //主版本号 2B
        Stream.readU2Simple(content, index, klass.getMajorVersion());
        index += 2;


        //常量池2B
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;

        klass.getConstantPool().setLength(DataTranslate.byteToUnsignedShort(u2Arr));

        klass.getConstantPool().initContainer();


        //常量池 N字节
        index  = parseConstantPool(content, klass, index);

        // 类的访问权限 2B
        Stream.readU2Simple(content, index, u2Arr);
        index +=2 ;
        klass.setAccessFlag(DataTranslate.byteToUnsignedShort(u2Arr));

        //类名 2B
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        klass.setThisClass(DataTranslate.byteToUnsignedShort(u2Arr));
        //父类名 2B
        Stream.readU2Simple(content, index, u2Arr);
        index +=2 ;
        klass.setSuperClass(DataTranslate.byteToUnsignedShort(u2Arr));

        //实现的接口个数 2B
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        klass.setInterfacesLength(DataTranslate.byteToUnsignedShort(u2Arr));
        //实现的接口
        if(klass.getInterfacesLength()!=0){
            logger.info("开始解析实现的接口信息: ");
            index = parseInterFace(content, klass , index);
        }
        //属性数量 2B
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        klass.setFieldsLength(DataTranslate.byteToUnsignedShort(u2Arr));

        //属性
        index = parseFields(content, klass, index);
        //方法数量2B
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;

        klass.setMethodLength(DataTranslate.byteToUnsignedShort(u2Arr));
        klass.initMethodsContainer();

        //方法
        index = parseMethods(content, klass, index);

        //属性数量
        Stream.readU2Simple(content, index, u2Arr);
        index += 2;
        klass.setAttributeLength(DataTranslate.byteToUnsignedShort(u2Arr));
        logger.info("开始解析类的属性, 数量:" + klass.getAttributeLength());
        //属性
        for(int i=0;i<klass.getAttributeLength();i++){
            Stream.readU2Simple(content, index, u2Arr);
            String attrName = (String) klass.getConstantPool().getDataMap().get(DataTranslate.byteToUnsignedShort(u2Arr));
            if(attrName.equals("SourceFile")){
                index = parseSourceFile(content, index, klass);
            }else{
                throw new Error("无法识别的类属性: " + attrName);
            }
        }
        return klass;

    }
}
