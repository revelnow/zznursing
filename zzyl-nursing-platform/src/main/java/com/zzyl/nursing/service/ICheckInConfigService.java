package com.zzyl.nursing.service;

import java.util.List;
import com.zzyl.nursing.domain.CheckInConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 入住信息Service接口
 * 
 * @author alexis
 * @date 2026-03-22
 */
public interface ICheckInConfigService extends IService<CheckInConfig>
{
    /**
     * 查询入住信息
     * 
     * @param id 入住信息主键
     * @return 入住信息
     */
    public CheckInConfig selectCheckInConfigById(Long id);

    /**
     * 查询入住信息列表
     * 
     * @param checkInConfig 入住信息
     * @return 入住信息集合
     */
    public List<CheckInConfig> selectCheckInConfigList(CheckInConfig checkInConfig);

    /**
     * 新增入住信息
     * 
     * @param checkInConfig 入住信息
     * @return 结果
     */
    public int insertCheckInConfig(CheckInConfig checkInConfig);

    /**
     * 修改入住信息
     * 
     * @param checkInConfig 入住信息
     * @return 结果
     */
    public int updateCheckInConfig(CheckInConfig checkInConfig);

    /**
     * 批量删除入住信息
     * 
     * @param ids 需要删除的入住信息主键集合
     * @return 结果
     */
    public int deleteCheckInConfigByIds(Long[] ids);

    /**
     * 删除入住信息信息
     * 
     * @param id 入住信息主键
     * @return 结果
     */
    public int deleteCheckInConfigById(Long id);
}
