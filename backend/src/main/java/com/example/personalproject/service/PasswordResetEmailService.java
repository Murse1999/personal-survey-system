package com.example.personalproject.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetEmailService {

  private final ObjectProvider<JavaMailSender> mailSenderProvider;
  private final boolean mailDevMode;
  private final boolean mailEnabled;
  private final String mailFrom;
  private final String mailUsername;

  public PasswordResetEmailService(
    ObjectProvider<JavaMailSender> mailSenderProvider,
    @Value("${app.mail.dev-mode:false}") boolean mailDevMode,
    @Value("${app.mail.enabled:false}") boolean mailEnabled,
    @Value("${app.mail.from:}") String mailFrom,
    @Value("${spring.mail.username:}") String mailUsername) {
    this.mailSenderProvider = mailSenderProvider;
    this.mailDevMode = mailDevMode;
    this.mailEnabled = mailEnabled;
    this.mailFrom = mailFrom;
    this.mailUsername = mailUsername;
  }

  public String sendPasswordResetCode(String email, String code) {
    if (mailDevMode) {
      return "本機示範驗證碼：" + code + "（僅供測試，正式環境請設定寄信服務）";
    }

    if (!mailEnabled) {
      throw new IllegalStateException("目前尚未設定寄信服務");
    }

    JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
    if (mailSender == null) {
      throw new IllegalStateException("目前尚未設定寄信服務");
    }

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("問卷平台密碼重設驗證碼");
    message.setText(
      "您好，您正在重設問卷平台密碼。\n\n"
        + "您的驗證碼是：" + code + "\n"
        + "驗證碼 10 分鐘內有效，若不是您本人操作，請忽略這封信。"
    );

    String senderAddress = mailFrom.isBlank() ? mailUsername : mailFrom;
    if (!senderAddress.isBlank()) {
      message.setFrom(senderAddress);
    }

    try {
      mailSender.send(message);
    } catch (MailException exception) {
      throw new IllegalStateException("驗證信寄送失敗");
    }

    return "如果這個 Email 已註冊，驗證碼會寄到你的信箱";
  }
}
