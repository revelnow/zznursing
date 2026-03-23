package com.zzyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;
import com.zzyl.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.CheckInConfigMapper;
import com.zzyl.nursing.domain.CheckInConfig;
import com.zzyl.nursing.service.ICheckInConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 入住信息Service业务层处理
 * 
 * @author alexis
 * @date 2026-03-22
 */
@Service
public class CheckInConfigServiceImpl extends ServiceImpl<CheckInConfigMapper, CheckInConfig> implements ICheckInConfigService
{
    @Autowired
    private CheckInConfigMapper checkInConfigMapper;

    /**
     * 查询入住信息
     * 
     * @param id 入住信息主键
     * @return 入住信息
     */
    @Override
    public CheckInConfig selectCheckInConfigById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询入住信息列表
     * 
     * @param checkInConfig 入住信息
     * @return 入住信息
     */
    @Override
    public List<CheckInConfig> selectCheckInConfigList(CheckInConfig checkInConfig)
    {
        return checkInConfigMapper.selectCheckInConfigList(checkInConfig);
    }

    /**
     * 新增入住信息
     * 
     * @param checkInConfig 入住信息
     * @return 结果
     */
    @Override
    public int insertCheckInConfig(CheckInConfig checkInConfig)
    {
        return save(checkInConfig) ? 1 : 0;
    }

    /**
     * 修改入住信息
     * 
     * @param checkInConfig 入住信息
     * @return 结果
     */
    @Override
    public int updateCheckInConfig(CheckInConfig checkInConfig)
    {
        return updateById(checkInConfig) ? 1 : 0;
    }

    /**
     * 批量删除入住信息
     * 
     * @param ids 需要删除的入住信息主键
     * @return 结果
     */
    @Override
    public int deleteCheckInConfigByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除入住信息信息
     * 
     * @param id 入住信息主键
     * @return 结果
     */
    @Override
    public int deleteCheckInConfigById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }
}
