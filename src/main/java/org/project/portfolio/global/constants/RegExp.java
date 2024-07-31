package org.project.portfolio.global.constants;

public interface RegExp {

  String USER_ID = "^[a-zA-Z]{4,8}$";
  String USERNAME = "^[가-힣]{2,5}$";
  String PASSWORD = "^(?=(?:[^A-Za-z]*[A-Za-z]){5})(?=(?:[^0-9]*\\d){5})(?=(?:[^!@#$%^&*(),.?\":{}|<>]*[!@#$%^&*(),.?\":{}|<>]){2}).*$";
  String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
  String PHONE = "^01[0-9]-[0-9]{3,4}-[0-9]{4}$";
}
