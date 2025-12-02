package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.po.Orders;
import com.shanzhu.hospital.entity.po.Patient;
import com.shanzhu.hospital.entity.vo.DoctorPageVo;
import com.shanzhu.hospital.entity.vo.user.DoctorUserVo;
import com.shanzhu.hospital.service.DoctorUserService;
import com.shanzhu.hospital.service.OrderService;
import com.shanzhu.hospital.service.PatientUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 医生相关 控制层
 *
 */
@RestController
@RequestMapping("doctor")
@RequiredArgsConstructor
@Tag(name = "医生接口", description = "医生相关操作接口")
public class DoctorUserController {

    private final DoctorUserService doctorUserService;

    private final OrderService orderService;

    private final PatientUserService patientUserService;

    /**
     * 医生登录
     *
     * @param dId       医生id (账号)
     * @param dPassword 密码
     * @return 医生用户信息
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @Operation(summary = "医生登录", description = "使用医生账号和密码进行登录")
    public R<DoctorUserVo> login(
            @Parameter(description = "医生ID") @RequestParam(value = "dId") Integer dId,
            @Parameter(description = "医生密码") @RequestParam(value = "dPassword") String dPassword
    ) {
        //登录医生
        DoctorUserVo doctorVo = doctorUserService.login(dId, dPassword);

        if (doctorVo == null) {
            return R.error("登录失败，密码或账号错误");
        }

        return R.ok(doctorVo);
    }

    /**
     * 查看医生当天挂号
     *
     * @param dId    医生id
     * @param oStart 日期时间
     * @return 挂号数据
     */
    @RequestMapping("findOrderByNull")
    @Operation(summary = "查询医生当日挂号", description = "根据医生ID和日期查询当日挂号信息")
    public R<List<Orders>> findOrderByNull(
            @Parameter(description = "医生ID") @Param(value = "dId") Integer dId,
            @Parameter(description = "日期时间") @RequestParam(value = "oStart") String oStart) {

        return R.ok(orderService.findOrderByNull(dId, oStart));
    }

    /**
     * 查询患者信息
     *
     * @param pId 患者id
     * @return 患者信息
     */
    @RequestMapping("findPatientById")
    @Operation(summary = "根据ID查询患者", description = "通过患者ID查询患者详细信息")
    public R<Patient> findPatientById(@Parameter(description = "患者ID") int pId) {
        return R.ok(patientUserService.findPatientById(pId));
    }

    /**
     * 通过科室查询所有医生信息 - 分页
     *
     * @param pageNum     分页页面
     * @param pageSize    分页大小
     * @param dName       医生名字
     * @param arrangeDate 排班时间
     * @param dSection    科室
     * @return 医生信息
     */
    @RequestMapping("findDoctorBySectionPage")
    @Operation(summary = "按科室分页查询医生", description = "根据科室、排班日期等条件分页查询医生信息")
    public R<DoctorPageVo> findDoctorBySectionPage(
            @Parameter(description = "页码") @RequestParam(value = "pageNumber") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "size") Integer pageSize,
            @Parameter(description = "医生姓名") @RequestParam(value = "query") String dName,
            @Parameter(description = "排班日期") @RequestParam(value = "arrangeDate") String arrangeDate,
            @Parameter(description = "科室名称") @RequestParam(value = "dSection") String dSection
    ) {
        return R.ok(doctorUserService.findDoctorBySectionPage(pageNum, pageSize, dName, arrangeDate, dSection));
    }

    /**
     * 更新评价
     *
     * @param dId   医生id
     * @param dStar 评分
     * @return 结果
     */
    @RequestMapping("updateStar")
    @Operation(summary = "更新医生评分", description = "更新医生的评分信息")
    public R<Boolean> updateStar(@Parameter(description = "医生ID") Integer dId, 
                                 @Parameter(description = "评分") Double dStar) {
        if (doctorUserService.updateStar(dId, dStar)) {
            return R.ok("评价成功");
        }

        return R.error("评价失败");
    }

}
