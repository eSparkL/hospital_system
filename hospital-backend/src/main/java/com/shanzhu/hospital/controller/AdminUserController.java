package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.po.Doctor;
import com.shanzhu.hospital.entity.vo.DoctorPageVo;
import com.shanzhu.hospital.entity.vo.OrdersPageVo;
import com.shanzhu.hospital.entity.vo.PatientPageVo;
import com.shanzhu.hospital.entity.vo.user.AdminUserVo;
import com.shanzhu.hospital.service.AdminUserService;
import com.shanzhu.hospital.service.DoctorUserService;
import com.shanzhu.hospital.service.OrderService;
import com.shanzhu.hospital.service.PatientUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 管理员相关 控制层
 *
 */
@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
@Tag(name = "管理员接口", description = "管理员相关操作接口")
public class AdminUserController {

    private final AdminUserService adminService;

    private final DoctorUserService doctorUserService;

    private final PatientUserService patientUserService;

    private final OrderService orderService;

    /**
     * 管理员登录
     *
     * @param aId       管理员id （账号）
     * @param aPassword 管理员密码
     * @return 返回管理员登录信息
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "使用管理员账号和密码进行登录")
    public R<AdminUserVo> login(@Parameter(description = "管理员ID") @RequestParam("aId") int aId, 
                                @Parameter(description = "管理员密码") @RequestParam("aPassword") String aPassword) {
        //登录管理员
        AdminUserVo adminVo = adminService.login(aId, aPassword);

        if (adminVo == null) {
            return R.error("登录失败，密码或账号错误");
        }

        return R.ok(adminVo);
    }

    /**
     * 查询所有医护人员信息 - 分页
     *
     * @param pageNum  分页页码
     * @param pageSize 分页大小
     * @param query    查询条件
     * @return 医护人员信息
     */
    @RequestMapping("findAllDoctors")
    @Operation(summary = "分页查询所有医生", description = "根据页码和查询条件分页查询医生信息")
    public R<DoctorPageVo> findDoctorPage(
            @Parameter(description = "页码") @RequestParam(value = "pageNumber") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "size") Integer pageSize,
            @Parameter(description = "查询关键字") @RequestParam(value = "query") String query
    ) {
        return R.ok(doctorUserService.findDoctorPage(pageNum, pageSize, query));
    }

    /**
     * 通过id查找医生
     *
     * @param dId 医生id（账号）
     * @return 医生信息
     */
    @RequestMapping("findDoctor")
    @Operation(summary = "根据ID查询医生", description = "通过医生ID查询医生详细信息")
    public R<Doctor> findDoctor(@Parameter(description = "医生ID") @RequestParam(value = "dId") int dId) {
        return R.ok(doctorUserService.findDoctor(dId));
    }

    /**
     * 添加医生
     *
     * @param doctor 医生信息
     * @return 结果
     */
    @RequestMapping("addDoctor")
    @Operation(summary = "添加医生", description = "添加新的医生信息")
    public R addDoctor(@Parameter(description = "医生信息对象") Doctor doctor) {
        if (BooleanUtils.isTrue(doctorUserService.addDoctor(doctor))) {
            return R.ok("添加医生成功");
        }

        return R.error("添加医生失败");
    }

    /**
     * 删除医生
     *
     * @param dId 医生账号
     * @return 结果
     */
    @RequestMapping("deleteDoctor")
    @Operation(summary = "删除医生", description = "逻辑删除医生，将医生状态设置为离职")
    public R deleteDoctor(@Parameter(description = "医生ID") @RequestParam(value = "dId") int dId) {
        if (BooleanUtils.isTrue(doctorUserService.deleteDoctor(dId))) {
            return R.ok("删除医生成功");
        }

        return R.error("删除医生失败");
    }

    /**
     * 修改医生信息
     *
     * @param doctor 医生信息
     * @return 结果
     */
    @RequestMapping("modifyDoctor")
    @Operation(summary = "更新医生信息", description = "更新医生信息")
    public R updateDoctor(@Parameter(description = "医生信息对象") Doctor doctor) {
        if (BooleanUtils.isTrue(doctorUserService.updateDoctor(doctor))) {
            return R.ok("修改医生成功");
        }

        return R.ok("修改医生失败");
    }

    /**
     * 查询患者信息 - 分页
     *
     * @param pageNum  分页页码
     * @param pageSize 分页大小
     * @param query    查询条件
     * @return 患者数据
     */
    @RequestMapping("findAllPatients")
    @Operation(summary = "分页查询所有患者", description = "根据页码和查询条件分页查询患者信息")
    public R<PatientPageVo> findPatientPage(
            @Parameter(description = "页码") @RequestParam(value = "pageNumber") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "size") Integer pageSize,
            @Parameter(description = "查询关键字") @RequestParam(value = "query") String query
    ) {
        return R.ok(patientUserService.findPatientPage(pageNum, pageSize, query));
    }

    /**
     * 删除患者
     *
     * @param pId 账号id
     * @return 结果
     */
    @RequestMapping("deletePatient")
    @Operation(summary = "删除患者", description = "逻辑删除患者，将患者状态设置为删除")
    public R deletePatient(@Parameter(description = "患者ID") @RequestParam(value = "pId") int pId) {
        if (BooleanUtils.isTrue(patientUserService.deletePatient(pId))) {
            return R.ok("删除患者成功");
        }

        return R.error("删除患者失败");
    }

    /**
     * 查询挂号信息 - 分页
     *
     * @param pageNum  分页页数
     * @param pageSize 分页大小
     * @param query    查询条件
     * @return 挂号列表
     */
    @RequestMapping("findAllOrders")
    @Operation(summary = "分页查询所有挂号信息", description = "根据页码和查询条件分页查询挂号信息")
    public R<OrdersPageVo> findOrdersPages(
            @Parameter(description = "页码") @RequestParam(value = "pageNumber") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "size") Integer pageSize,
            @Parameter(description = "查询关键字") @RequestParam(value = "query") String query
    ) {
        return R.ok(orderService.findOrdersPages(pageNum, pageSize, query));
    }

    /**
     * 删除挂号单
     *
     * @param oId 挂号单id
     * @return 结果
     */
    @RequestMapping("deleteOrder")
    @Operation(summary = "删除挂号单", description = "删除指定ID的挂号单")
    public R deleteOrder(@Parameter(description = "挂号单ID") @RequestParam(value = "oId") Integer oId) {
        if (BooleanUtils.isTrue(orderService.deleteOrder(oId))) {
            return R.ok("删除挂号单成功");
        }

        return R.error("删除挂号单失败");
    }

}
