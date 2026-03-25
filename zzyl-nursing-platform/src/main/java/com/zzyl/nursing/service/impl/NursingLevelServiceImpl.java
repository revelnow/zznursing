package com.zzyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.vo.NursingLevelVo;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingLevelMapper;
import com.zzyl.nursing.domain.NursingLevel;
import com.zzyl.nursing.service.INursingLevelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import static com.zzyl.common.constant.CacheConstants.Nursing_Level_All;

/**
 * 护理等级Service业务层处理
 * 
 * @author alexis
 * @date 2025-06-02
 */
@Service
public class NursingLevelServiceImpl extends ServiceImpl<NursingLevelMapper, NursingLevel> implements INursingLevelService {
    @Autowired
    private NursingLevelMapper nursingLevelMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 查询护理等级
     *
     * @param id 护理等级主键
     * @return 护理等级
     */
    @Override
    public NursingLevel selectNursingLevelById(Long id) {
        return getById(id);
    }

    /**
     * 查询护理等级列表
     *
     * @param nursingLevel 护理等级
     * @return 护理等级
     */
    @Override
    public List<NursingLevel> selectNursingLevelList(NursingLevel nursingLevel) {
        return nursingLevelMapper.selectNursingLevelList(nursingLevel);
    }

    /**
     * 新增护理等级
     *
     * @param nursingLevel 护理等级
     * @return 结果
     */
    @Override
    public int insertNursingLevel(NursingLevel nursingLevel) {
        nursingLevel.setCreateTime(DateUtils.getNowDate());
        boolean flag = save(nursingLevel);
        //删除缓存
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 删除缓存
     */
    private void deleteCache() {
        redisTemplate.delete(Nursing_Level_All);
    }

    /**
     * 修改护理等级
     *
     * @param nursingLevel 护理等级
     * @return 结果
     */
    @Override
    public int updateNursingLevel(NursingLevel nursingLevel) {
        boolean flag = updateById(nursingLevel);
        //删除缓存
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 批量删除护理等级
     *
     * @param ids 需要删除的护理等级主键
     * @return 结果
     */
    @Override
    public int deleteNursingLevelByIds(Long[] ids) {
        boolean flag = removeByIds(Arrays.asList(ids));
        //删除缓存
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 删除护理等级信息
     *
     * @param id 护理等级主键
     * @return 结果
     */
    @Override
    public int deleteNursingLevelById(Long id) {
        boolean flag = removeById(id);
        //删除缓存
        deleteCache();
        return flag ? 1 : 0;
    }

    /**
     * 查询护理等级Vo列表
     *
     * @param nursingLevel 条件
     * @return 结果
     */
    @Override
    public List<NursingLevelVo> selectNursingLevelVoList(NursingLevel nursingLevel) {
        return nursingLevelMapper.selectNursingLevelVoList(nursingLevel);
    }

    @Override
    public List<NursingLevel> listAll() {
        // 从缓存中获取护理等级列表

        List<NursingLevel> nursingLevels = (List<NursingLevel>) redisTemplate.opsForValue().get(Nursing_Level_All);
        if (ObjectUtils.isNotEmpty(nursingLevels)) {
            return nursingLevels;
        }
        //查询不到在数据库中查询
        LambdaQueryWrapper<NursingLevel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NursingLevel::getStatus, 1);
        //将数据存入缓存当中
        redisTemplate.opsForValue().set(Nursing_Level_All, list(queryWrapper));

        return list(queryWrapper);
    }
}


