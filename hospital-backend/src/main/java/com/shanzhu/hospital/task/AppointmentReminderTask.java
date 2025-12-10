package com.shanzhu.hospital.task;

import cn.hutool.core.date.DateUtil;
import com.shanzhu.hospital.common.R;
import com.shanzhu.hospital.entity.po.Orders;
import com.shanzhu.hospital.entity.po.Patient;
import com.shanzhu.hospital.service.MailService;
import com.shanzhu.hospital.service.OrderService;
import com.shanzhu.hospital.service.PatientUserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
@Slf4j
public class AppointmentReminderTask {

    private final OrderService orderService;
    
    private final PatientUserService patientUserService;
    
    private final MailService mailService;

    /**
     * 每天上午8点执行一次，向当日有预约的患者发送邮件提醒
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendAppointmentReminders() {
        log.info("开始执行当日预约邮件提醒任务");
        
        // 获取今天的日期
        String today = DateUtil.today();
        
        try {
            // 查询当日所有预约
            List<Orders> todayOrders = orderService.getOrdersByDate(today);
            
            int sentCount = 0;
            for (Orders order : todayOrders) {
                // 获取患者信息
                Patient patient = patientUserService.findPatientById(order.getPId());
                
                // 检查患者是否存在、账户是否有效以及邮箱是否存在
                if (patient != null && patient.getPState() == 1 && 
                    patient.getPEmail() != null && !patient.getPEmail().isEmpty()) {
                    
                    try {
                        // 发送邮件提醒
                        mailService.sendAppointmentReminder(
                                patient.getPEmail(),
                                patient.getPName(),
                                order.getDName() != null ? order.getDName() : "医生",
                                order.getOStart()
                        );
                        
                        log.info("已发送预约提醒邮件至: {}", patient.getPEmail());
                        sentCount++;
                    } catch (Exception e) {
                        log.error("发送邮件至 {} 时发生异常", patient.getPEmail(), e);
                    }
                }
            }
            
            log.info("当日预约邮件提醒任务执行完成，共发送 {} 封邮件", sentCount);
        } catch (Exception e) {
            log.error("执行当日预约邮件提醒任务时发生异常", e);
        }
    }
    
    /**
     * 测试接口：手动发送预约提醒邮件 (GET方式)
     * @param patientEmail 患者邮箱
     * @param patientName 患者姓名
     * @param doctorName 医生姓名
     * @param appointmentTime 预约时间
     * @return 发送结果
     */
    @GetMapping("/sendTestEmail")
    public R<String> sendTestEmail(
            @RequestParam String patientEmail,
            @RequestParam String patientName,
            @RequestParam String doctorName,
            @RequestParam String appointmentTime) {
        
        try {
            mailService.sendAppointmentReminder(patientEmail, patientName, doctorName, appointmentTime);
            return R.ok("测试邮件发送成功");
        } catch (Exception e) {
            log.error("发送测试邮件时发生异常", e);
            return R.error("测试邮件发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试接口：手动发送预约提醒邮件 (POST方式，JSON格式)
     * @param request 邮件信息
     * @return 发送结果
     */
    @PostMapping("/sendTestEmail")
    public R<String> sendTestEmail(@RequestBody SendTestEmailRequest request) {
        try {
            mailService.sendAppointmentReminder(
                request.getPatientEmail(), 
                request.getPatientName(), 
                request.getDoctorName(), 
                request.getAppointmentTime()
            );
            return R.ok("测试邮件发送成功");
        } catch (Exception e) {
            log.error("发送测试邮件时发生异常", e);
            return R.error("测试邮件发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试接口：发送当日预约提醒邮件
     * @return 发送结果
     */
    @GetMapping("/sendTodayReminders")
    public R<String> sendTodayReminders() {
        try {
            // 获取今天的日期
            String today = DateUtil.today();
            
            // 查询当日所有预约
            List<Orders> todayOrders = orderService.getOrdersByDate(today);
            
            int sentCount = 0;
            StringBuilder result = new StringBuilder();
            result.append("开始发送当日预约提醒邮件，共找到 ").append(todayOrders.size()).append(" 个预约\n");
            
            for (Orders order : todayOrders) {
                // 获取患者信息
                Patient patient = patientUserService.findPatientById(order.getPId());
                
                // 检查患者是否存在、账户是否有效以及邮箱是否存在
                if (patient != null && patient.getPState() == 1 && 
                    patient.getPEmail() != null && !patient.getPEmail().isEmpty()) {
                    
                    try {
                        // 发送邮件提醒
                        mailService.sendAppointmentReminder(
                                patient.getPEmail(),
                                patient.getPName(),
                                order.getDName() != null ? order.getDName() : "医生",
                                order.getOStart()
                        );
                        
                        log.info("已发送预约提醒邮件至: {}", patient.getPEmail());
                        result.append("已发送邮件至: ").append(patient.getPEmail()).append("\n");
                        sentCount++;
                    } catch (Exception e) {
                        log.error("发送邮件至 {} 时发生异常", patient.getPEmail(), e);
                        result.append("发送邮件至 ").append(patient.getPEmail()).append(" 失败: ").append(e.getMessage()).append("\n");
                    }
                } else {
                    result.append("患者 ").append(order.getPId()).append(" 信息不完整或邮箱为空\n");
                }
            }
            
            result.append("当日预约邮件提醒发送完成，共发送 ").append(sentCount).append(" 封邮件");
            return R.ok(result.toString());
        } catch (Exception e) {
            log.error("发送当日预约提醒邮件时发生异常", e);
            return R.error("发送当日预约提醒邮件失败: " + e.getMessage());
        }
    }
    
    @Data
    public static class SendTestEmailRequest {
        private String patientEmail;
        private String patientName;
        private String doctorName;
        private String appointmentTime;
    }
}