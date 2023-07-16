package com.lfy.usercenter.job.excelReadJob;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 导入用户Excel
 * 这个类是用来映射Excel中的数据
 * @author lfy
 */
@Data
@EqualsAndHashCode
public class UserTableInfo {
    /**
     * 用户编号
     * @ExcelProperty("**列名**") 可以使用index=num，也可以直接打列名
     */
    @ExcelProperty("用户编号")
    private String planetCode;

    /**
     * 用户昵称
     */
    @ExcelProperty("昵称")
    private String username;
}