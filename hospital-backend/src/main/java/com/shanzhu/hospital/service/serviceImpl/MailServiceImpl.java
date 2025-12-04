package com.shanzhu.hospital.service.serviceImpl;

import com.shanzhu.hospital.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @Override
    public void sendAppointmentReminder(String to, String patientName, String doctorName, String appointmentTime) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("【医院预约提醒】您有一份今日就诊预约");
            
            // 构建HTML邮件内容
            String htmlContent = buildHtmlContent(patientName, doctorName, appointmentTime);
            helper.setText(htmlContent, true); // true表示是HTML内容
            
            mailSender.send(message);
        } catch (MessagingException e) {
            // 如果HTML邮件发送失败，回退到纯文本邮件
            sendTextEmail(to, patientName, doctorName, appointmentTime);
        }
    }
    
    /**
     * 构建HTML邮件内容
     */
    private String buildHtmlContent(String patientName, String doctorName, String appointmentTime) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "    <meta charset='UTF-8'>" +
               "    <style>" +
               "        body { font-family: 'Microsoft YaHei', Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 0; }" +
               "        .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }" +
               "        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }" +
               "        .header h1 { margin: 0; font-size: 24px; }" +
               "        .content { padding: 30px; }" +
               "        .content p { font-size: 16px; line-height: 1.6; color: #333; }" +
               "        .info-box { background-color: #e8f5e9; border-left: 4px solid #4CAF50; padding: 15px; margin: 20px 0; }" +
               "        .info-item { margin: 10px 0; }" +
               "        .info-label { font-weight: bold; color: #2E7D32; }" +
               "        .footer { background-color: #f1f1f1; padding: 20px; text-align: center; font-size: 14px; color: #666; }" +
               "        .highlight { color: #d32f2f; font-weight: bold; }" +
               "    </style>" +
               "</head>" +
               "<body>" +
               "    <div class='container'>" +
               "        <div class='header'>" +
               "            <h1>🏥 医院预约提醒</h1>" +
               "        </div>" +
               "        <div class='content'>" +
               "            <p>尊敬的<strong>" + patientName + "</strong>，您好！</p>" +
               "            <p>感谢您选择我们的医疗服务。这是一封预约就诊提醒邮件，请您仔细阅读以下信息：</p>" +
               "            <div class='info-box'>" +
               "                <div class='info-item'><span class='info-label'>👨‍⚕️ 主治医生：</span>" + doctorName + "</div>" +
               "                <div class='info-item'><span class='info-label'>📅 就诊时间：</span>" + appointmentTime + "</div>" +
               "                <div class='info-item'><span class='info-label'>📍 就诊地点：</span>医院门诊部</div>" +
               "            </div>" +
               "            <p>请您提前<span class='highlight'>15分钟</span>到达医院，携带有效身份证件完成签到。</p>" +
               "            <p>如需取消预约或有任何疑问，请及时致电医院服务热线：<strong>400-123-4567</strong></p>" +
               "        </div>" +
               "        <div class='footer'>" +
               "            <p>本邮件由医院预约系统自动发送，请勿直接回复</p>" +
               "            <p>© 2025 医院管理系统 - 关爱您的健康</p>" +
               "        </div>" +
               "    </div>" +
               "</body>" +
               "</html>";
    }
    
    /**
     * 发送纯文本邮件（备用方案）
     */
    private void sendTextEmail(String to, String patientName, String doctorName, String appointmentTime) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("就诊提醒");
            
            String textContent = String.format(
                "尊敬的%s，您好！\n\n" +
                "您今天预约了%s医生的门诊，就诊时间为%s。\n\n" +
                "请您按时前来就诊，如有特殊情况请提前联系医院。\n\n" +
                "祝您身体健康！",
                patientName, doctorName, appointmentTime
            );
            
            helper.setText(textContent);
            mailSender.send(message);
        } catch (MessagingException e) {
            // 邮件发送失败的日志记录
            e.printStackTrace();
        }
    }
}