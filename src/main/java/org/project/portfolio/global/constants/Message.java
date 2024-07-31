package org.project.portfolio.global.constants;

public interface Message {

  // HTTP Status
  String CREATED = "새로운 리소스가 생성되었습니다.";
  String BAD_REQUEST = "요청이 유효하지 않습니다. 다시 한번 확인해 주세요.";

  // 회원 가입 API
  String INVALID_USER_ID = "영문 대소문자 4자 이상 8자 이하이어야 합니다.";
  String INVALID_PASSWORD = "영문 대소문자 5자 이상, 숫자 5개 이상 특수문자 2자 이상이어야 합니다.";
  String INVALID_USERNAME = "한글 2자 이상 5자 이하이어야 합니다.";
  String INVALID_EMAIL = "유효하지 않은 이메일 형식입니다.";
  String INVALID_PHONE = "유효하지 않은 휴대폰 번호 형식입니다. (예: 010-1234-5678)";
}
