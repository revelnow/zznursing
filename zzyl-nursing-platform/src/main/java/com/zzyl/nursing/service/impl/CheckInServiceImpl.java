package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.CodeGenerator;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.IdCardUtil;
import com.zzyl.common.utils.bean.BeanUtils;
import com.zzyl.nursing.domain.*;
import com.zzyl.nursing.dto.CheckInApplyDto;
import com.zzyl.nursing.dto.CheckInConfigDto;
import com.zzyl.nursing.dto.CheckInContractDto;
import com.zzyl.nursing.dto.CheckInElderDto;
import com.zzyl.nursing.mapper.*;
import com.zzyl.nursing.vo.CheckInConfigVo;
import com.zzyl.nursing.vo.CheckInDetailVo;
import com.zzyl.nursing.vo.CheckInElderVo;
import com.zzyl.nursing.vo.ElderFamilyVo;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zzyl.nursing.service.ICheckInService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 入住Service业务层处理
 *
 * @author alexis
 * @date 2026-03-22
 */
@Service
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements ICheckInService
{
    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private BedMapper bedMapper;

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private CheckInConfigMapper checkInConfigMapper;

    /**
     * 查询入住
     *
     * @param id 入住主键
     * @return 入住
     */
    @Override
    public CheckIn selectCheckInById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询入住列表
     *
     * @param checkIn 入住
     * @return 入住
     */
    @Override
    public List<CheckIn> selectCheckInList(CheckIn checkIn)
    {
        return checkInMapper.selectCheckInList(checkIn);
    }

    /**
     * 新增入住
     *
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int insertCheckIn(CheckIn checkIn)
    {
        return save(checkIn) ? 1 : 0;
    }

    /**
     * 修改入住
     *
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int updateCheckIn(CheckIn checkIn)
    {
        return updateById(checkIn) ? 1 : 0;
    }

    /**
     * 批量删除入住
     *
     * @param ids 需要删除的入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除入住信息
     *
     * @param id 入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInById(Long id)
    {
        return removeById(id) ? 1 : 0;
    }

    /**
     * @param checkInApplyDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyCheckIn(CheckInApplyDto checkInApplyDto) {

        //判断老人是否已入住
        LambdaQueryWrapper<Elder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Elder::getIdCardNo, checkInApplyDto.getCheckInElderDto().getIdCardNo())
                .eq(Elder::getStatus, 1);
        Elder elder = elderMapper.selectOne(queryWrapper);
        if(ObjectUtils.isNotEmpty( elder)){
            throw new BaseException("该老人已入住");
        }

        //更新床位状态 已入住
        Bed bed = updateBedStatus(checkInApplyDto.getCheckInConfigDto().getBedId());

        //保存或更新老人数据
        elder = updateElder(checkInApplyDto.getCheckInElderDto(), bed);

        //新增签约办理
        String contractNo = "HT" + CodeGenerator.generateContractNumber();
        insertCheckContract(checkInApplyDto, contractNo,  elder);

        //新增入住数据
        CheckIn checkIn = insertCheckInInfo(elder, checkInApplyDto);

        // 新增入住配置信息
        insertCheckInConfig(checkIn.getId(), checkInApplyDto);
    }

    /**
     * 获取入住详情
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckInDetailVo getDetail(Long id) {
        CheckInDetailVo checkInDetailVo = new CheckInDetailVo();
        CheckInElderVo checkInElderVo=new CheckInElderVo();
        CheckInConfigVo checkInConfigVo=new CheckInConfigVo();


        //获取家人信息
        CheckIn checkIn = checkInMapper.selectById(id);
        BeanUtils.copyProperties(checkIn, checkInConfigVo);
        String family = checkIn.getRemark();
        if (family != null && !family.isEmpty()) {
            List<ElderFamilyVo> elderFamilyVoList = JSON.parseArray(family, ElderFamilyVo.class);
            checkInDetailVo.setElderFamilyVoList(elderFamilyVoList);
        }

        //获取老人信息
        Elder elder = elderMapper.selectById(checkIn.getElderId());
        if(ObjectUtils.isEmpty(elder)){
            throw new BaseException("未找到老人信息");
        }
        int age = IdCardUtil.getAgeByIdCard(elder.getIdCardNo());
        checkInElderVo.setAge(age);
        BeanUtils.copyProperties(elder, checkInElderVo );
        checkInDetailVo.setCheckInElderVo(checkInElderVo);



        //获取入住配置信息
        CheckInConfig checkInConfig = checkInConfigMapper.selectOne(
                new LambdaQueryWrapper<CheckInConfig>()
                        .eq(CheckInConfig::getCheckInId, checkIn.getId())
        );

        if (checkInConfig != null) {
            BeanUtils.copyProperties(checkInConfig, checkInConfigVo);
        }
        checkInDetailVo.setCheckInConfigVo(checkInConfigVo);

        //获取合同 信息
        LambdaQueryWrapper<Contract> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Contract::getElderId, elder.getId());
        Contract contract = contractMapper.selectOne(queryWrapper);
        checkInDetailVo.setContract(contract);


        return checkInDetailVo;
    }

    /**
     * 新增入住配置
     * @param checkInApplyDto
     */
    private void insertCheckInConfig(Long checkInId, CheckInApplyDto checkInApplyDto) {
        CheckInConfig checkInConfig = new CheckInConfig();
        BeanUtils.copyProperties(checkInApplyDto.getCheckInConfigDto(), checkInConfig);
        checkInConfig.setCheckInId(checkInId);
        checkInConfigMapper.insert(checkInConfig);
    }

    private CheckIn insertCheckInInfo(Elder elder, CheckInApplyDto checkInApplyDto) {
        CheckIn checkIn = new CheckIn();
        checkIn.setElderId(elder.getId());
        checkIn.setElderName(elder.getName());
        checkIn.setIdCardNo(elder.getIdCardNo());
        checkIn.setNursingLevelName(checkInApplyDto.getCheckInConfigDto().getNursingLevelName());
        checkIn.setStartDate(checkInApplyDto.getCheckInConfigDto().getStartDate());
        checkIn.setEndDate(checkInApplyDto.getCheckInConfigDto().getEndDate());
        checkIn.setBedNumber(elder.getBedNumber());
        checkIn.setRemark(JSON.toJSONString(checkInApplyDto.getElderFamilyDtoList()));
        checkIn.setStatus(0);
        checkInMapper.insert(checkIn);
        return checkIn;
    }


/**
 * 新增合同信息
 * @param checkInApplyDto
 * @param contractNo
 * @param elder
 */
private void insertCheckContract(CheckInApplyDto checkInApplyDto, String contractNo, Elder elder) {
    Contract contract = new Contract();
    // 属性拷贝
    BeanUtils.copyProperties(checkInApplyDto.getCheckInContractDto(), contract);
    contract.setContractNumber(contractNo);
    contract.setElderId(elder.getId());
    contract.setElderName(elder.getName());
    // 状态、开始时间、结束时间
    // 签约时间小于等于当前时间，合同生效中
    LocalDateTime checkInStartTime = checkInApplyDto.getCheckInConfigDto().getStartDate();
    LocalDateTime checkInEndTime = checkInApplyDto.getCheckInConfigDto().getEndDate();
    Integer status = checkInStartTime.isAfter(LocalDateTime.now()) ? 0 : 1;
    contract.setStatus(status);
    contract.setStartDate(checkInStartTime);
    contract.setEndDate(checkInEndTime);
    contractMapper.insert(contract);

}

/**
 * 更新老人数据
 * @param checkInElderDto
 * @param bed
 */
private Elder updateElder(CheckInElderDto checkInElderDto, Bed bed) {
    Elder elder = new Elder();
    elder.setBedId(bed.getId());
    elder.setBedNumber(bed.getBedNumber());
    elder.setStatus(1);
    BeanUtils.copyProperties(checkInElderDto, elder);

    //如果老人已存在，则更新数据；如果不存在，则新增数据
    LambdaQueryWrapper<Elder> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(Elder::getIdCardNo, checkInElderDto.getIdCardNo()).ne(Elder::getStatus, 0);
    Elder existingElder = elderMapper.selectOne(queryWrapper);
    if(ObjectUtils.isNotEmpty(existingElder)){
        elder.setId(existingElder.getId());
        elderMapper.updateElder(elder);
    }else{
        elder.setCreateTime(DateUtils.getNowDate());
        elderMapper.insert(elder);
    }
    return elder;
}

/**
 * 更新床位状态 已入住
 * @param bedId
 */
private Bed updateBedStatus(Long bedId) {
    Bed bed = bedMapper.selectBedById(bedId);
    bed.setBedStatus(1);
    bedMapper.updateBed(bed);
    return bed;

}
}
