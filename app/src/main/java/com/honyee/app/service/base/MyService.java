package com.honyee.app.service.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honyee.app.dto.base.MyPage;
import org.springframework.data.domain.Pageable;

/**
 * service增强
 */
public class MyService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
    public <P> IPage<P> toPage(MyPage myPage) {
        return PageDTO.of(myPage.getPage(), myPage.getSize());
    }

}
