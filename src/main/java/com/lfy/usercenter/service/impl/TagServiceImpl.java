package com.lfy.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.lfy.usercenter.mapper.TagMapper;
import com.lfy.usercenter.model.domain.Tag;
import com.lfy.usercenter.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author 13155
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2023-04-23 21:55:17
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




