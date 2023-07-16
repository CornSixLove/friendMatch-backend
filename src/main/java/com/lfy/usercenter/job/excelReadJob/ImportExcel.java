package com.lfy.usercenter.job.excelReadJob;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;

import java.util.List;

/**
 * 导入Excel数据
 * @author lfy
 */
public class ImportExcel {

    /**
     * 读取数据
     */
    public static void main(String[] args) {

        // JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "E:\\workcode\\user-center\\src\\main\\resources\\User.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置

        //通过监听器来实现
        readByListener(fileName);
        //同步读取
        //synchronousRead(fileName);

    }

    /**
     * 使用监听器
     * @param fileName
     */
    public static void readByListener(String fileName){
        EasyExcel.read(fileName, UserTableInfo.class,new TableListener()).sheet().doRead();
    }

    /**
     * 同步读
     * 同步的返回，不推荐使用，如果数据量大会把数据放到内存里面
     */
    public static void synchronousRead(String fileName){
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<UserTableInfo> totalDataList = EasyExcel.read(fileName).head(UserTableInfo.class).sheet().doReadSync();
        for (UserTableInfo userTableInfo : totalDataList) {
            System.out.println(userTableInfo);
        }
    }
}
