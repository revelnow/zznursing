package com.zzyl.nursing.mapper;

import java.util.List;
import com.zzyl.nursing.domain.CheckInConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入住信息Mapper接口
 * 
 * @author alexis
 * @date 2026-03-22
 */
@Mapper
public interface CheckInConfigMapper extends BaseMapper<CheckInConfig>
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
     * 删除入住信息
     * 
     * @param id 入住信息主键
     * @return 结果
     */
    public int deleteCheckInConfigById(Long id);

    /**
     * 批量删除入住信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCheckInConfigByIds(Long[] ids);
}
