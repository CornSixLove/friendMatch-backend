package com.lfy.usercenter.job.excelReadJob;


import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 将excel数据导入数据库
 */
public class ImportUserCenter {
    public static void main(String[] args) {

        String fileName = "E:\\workcode\\user-center\\src\\main\\resources\\User.xlsx";
        List<UserTableInfo> userInfoList = EasyExcel.read(fileName).head(UserTableInfo.class).sheet().doReadSync();

        //按照username进行分组（把stream改成parallelStream就是并行流）
        //filter(userInfo -> StringUtils.isNotBlank(userInfo.getUsername()))***防止因为空数据报错
        Map<String, List<UserTableInfo>> listMap = userInfoList.stream()
                .filter(userInfo -> StringUtils.isNotEmpty(userInfo.getUsername()))
                .collect(Collectors.groupingBy(UserTableInfo::getUsername));

        //查看重复数据的编号
        for (Map.Entry<String, List<UserTableInfo>> stringListEntry : listMap.entrySet()) {
            if(stringListEntry.getValue().size()>1){
                System.out.println("username="+stringListEntry.getKey());
            }
        }

        //取不重复的数据
        System.out.println("不重复的数据"+listMap.keySet().size());
    }
}
