package org.project.portfolio.global.constants;

public interface Message {

  // HTTP Status
  String OK = "요청이 성공적으로 처리되었습니다.";
  String CREATED = "새로운 리소스가 생성되었습니다.";
  String BAD_REQUEST = "요청이 유효하지 않습니다. 다시 한번 확인해 주세요.";
  String CONFLICT = "이미 존재하는 리소스입니다.";
  String FORBIDDEN = "권한이 없어 요청을 거부했습니다";
  String NOT_FOUND = "요청한 리소스를 찾을 수 없습니다.";
  String UNSUPPORTED_MEDIA_TYPE = "지원하지 않는 미디어 타입입니다.";

  // 회원 가입 API
  String INVALID_USER_ID = "영문 대소문자 4자 이상 8자 이하이어야 합니다.";
  String INVALID_PASSWORD = "영문 대소문자 5자 이상, 숫자 5개 이상 특수문자 2자 이상이어야 합니다.";
  String INVALID_USERNAME = "한글 2자 이상 5자 이하이어야 합니다.";
  String INVALID_EMAIL = "유효하지 않은 이메일 형식입니다.";
  String INVALID_PHONE = "유효하지 않은 휴대폰 번호 형식입니다. (예: 010-1234-5678)";
  String DUPLICATE_EMAIL = "이미 등록된 이메일입니다.";
  String DUPLICATE_USER_ID = "이미 등록된 아이디입니다.";

  // 로그인 API
  String ONLY_SUPPORTED_APPLICATION_JSON = "'application/json' 형식의 데이터만 지원합니다. 확인해주세요.";
  String ALREADY_LOGIN = "이미 로그인되어 있습니다.";
  String NOT_FOUND_EMAIL = "등록된 이메일이 아닙니다.";

}
