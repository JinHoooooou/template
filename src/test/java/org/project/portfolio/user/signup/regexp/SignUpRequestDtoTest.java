package org.project.portfolio.user.signup.regexp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.portfolio.global.constants.RegExp;

public class SignUpRequestDtoTest {

  @Test
  @DisplayName("userId는 영문 대소문자 4자 이상 8자 이하이어야 한다.")
  public void userId_shouldMoreThan3AndLessThan9LettersCaseInsensitive() {
    String valid = "valid";
    String validOnCaseInsensitive = "ValId";
    String invalidOnLessThan4 = "hi";
    String invalidOnMoreThan8 = "testMoreThanEightLetters";
    String invalidOnContainingNumeric = "asd123";
    String invalidOnContainingSpecialCharacter = "asd#@";

    assertThat(valid.matches(RegExp.USER_ID)).isTrue();
    assertThat(validOnCaseInsensitive.matches(RegExp.USER_ID)).isTrue();

    assertThat(invalidOnLessThan4.matches(RegExp.USER_ID)).isFalse();
    assertThat(invalidOnMoreThan8.matches(RegExp.USER_ID)).isFalse();
    assertThat(invalidOnContainingNumeric.matches(RegExp.USER_ID)).isFalse();
    assertThat(invalidOnContainingSpecialCharacter.matches(RegExp.USER_ID)).isFalse();
  }

  @Test
  @DisplayName("password는 영문 대소문자 5자 이상, 숫자 5개 이상 특수문자 2자 이상이어야 한다.")
  public void password_shouldContainingMoreThan5LettersCaseInsensitive_andMoreThan5Numerics_andMoreThan2SpecialCharacters() {
    String valid = "q1w2e3r4t5!@";
    String validOnCaseInsensitive = "Q1w2E3R4t5!$";
    String validOnOrderInsensitive = "!2%qsRez42325$";
    String validOnConsecutiveLettersInsensitive = "aaaaa11111!!";
    String invalidOnLessThan5Letters = "aaaa11111!!";
    String invalidOnLessThan5Numerics = "aaaaa1111!!";
    String invalidOnLessThan2SpecialCharacters = "aaaaa11111!";

    assertThat(valid.matches(RegExp.PASSWORD)).isTrue();
    assertThat(validOnCaseInsensitive.matches(RegExp.PASSWORD)).isTrue();
    assertThat(validOnOrderInsensitive.matches(RegExp.PASSWORD)).isTrue();
    assertThat(validOnConsecutiveLettersInsensitive.matches(RegExp.PASSWORD)).isTrue();
    assertThat(validOnConsecutiveLettersInsensitive.matches(RegExp.PASSWORD)).isTrue();

    assertThat(invalidOnLessThan5Letters.matches(RegExp.PASSWORD)).isFalse();
    assertThat(invalidOnLessThan5Numerics.matches(RegExp.PASSWORD)).isFalse();
    assertThat(invalidOnLessThan5Letters.matches(RegExp.PASSWORD)).isFalse();
    assertThat(invalidOnLessThan2SpecialCharacters.matches(RegExp.PASSWORD)).isFalse();
  }

  @Test
  @DisplayName("username은 한글 2자 이상 5자 이하이어야 한다.")
  public void username_shouldKoreanLettersAndMoreThan1AndLessThan6() {
    String valid = "진호";
    String invalidOnNotCompletedLetters = "ㅈㅎ";
    String invalidOnLessThan2Letters = "이";
    String invalidOnMoreThan5Letters = "이진호라고합니다";
    String invalidOnContainingEnglishLetters = "Lee진호";
    String invalidOnContainingSpecialCharacters = "이진호!";

    assertThat(valid.matches(RegExp.USERNAME)).isTrue();

    assertThat(invalidOnNotCompletedLetters.matches(RegExp.USERNAME)).isFalse();
    assertThat(invalidOnLessThan2Letters.matches(RegExp.USERNAME)).isFalse();
    assertThat(invalidOnMoreThan5Letters.matches(RegExp.USERNAME)).isFalse();
    assertThat(invalidOnContainingEnglishLetters.matches(RegExp.USERNAME)).isFalse();
    assertThat(invalidOnContainingSpecialCharacters.matches(RegExp.USERNAME)).isFalse();
  }

  @Test
  @DisplayName("email은 유효한 이메일 형식이어야 한다.")
  public void email_shouldValidEmailFormat() {
    String valid = "jinho@naver.com";
    String invalidOnNotContainingAtLetters = "jinhonaver.com";
    String invalidOnNotExistAfterAtLetters = "jinho@";
    String invalidOnNotContainingDotLettersAfterAtLetters = "jinho@qweqe";
    String invalidOnNotContainingMoreThan1LettersAfterDotLetters = "jinho@qweqe.z";

    assertThat(valid.matches(RegExp.EMAIL)).isTrue();

    assertThat(invalidOnNotContainingAtLetters.matches(RegExp.EMAIL)).isFalse();
    assertThat(invalidOnNotExistAfterAtLetters.matches(RegExp.EMAIL)).isFalse();
    assertThat(invalidOnNotContainingDotLettersAfterAtLetters.matches(RegExp.EMAIL)).isFalse();
    assertThat(invalidOnNotContainingDotLettersAfterAtLetters.matches(RegExp.EMAIL)).isFalse();
    assertThat(invalidOnNotContainingMoreThan1LettersAfterDotLetters.matches(RegExp.EMAIL)).isFalse();
  }

  @Test
  @DisplayName("phone은 유효한 휴대폰 번호 형식이어야 한다.")
  public void phone_shouldValidPhoneFormat() {
    String valid = "010-1234-5678";
    String invalidNotContainingHyphen1 = "01012345678";
    String invalidNotContainingHyphen2 = "010-12345678";
    String invalidNotContainingHyphen3 = "0101234-5678";
    String invalidNotStartingWith01 = "910-1234-5678";
    String invalidOnLessThan3InMiddleNumbers = "010-12-5678";
    String invalidOnMoreThan4InMiddleNumbers = "010-12345-5678";
    String invalidOnNot4InLastNumbers1 = "010-12345-567";
    String invalidOnNot4InLastNumbers2 = "010-12345-56789";

    assertThat(valid.matches(RegExp.PHONE)).isTrue();

    assertThat(invalidNotContainingHyphen1.matches(RegExp.PHONE)).isFalse();
    assertThat(invalidNotContainingHyphen2.matches(RegExp.PHONE)).isFalse();
    assertThat(invalidNotContainingHyphen3.matches(RegExp.PHONE)).isFalse();
    assertThat(invalidNotStartingWith01.matches(RegExp.PHONE)).isFalse();
    assertThat(invalidOnLessThan3InMiddleNumbers.matches(RegExp.PHONE)).isFalse();
    assertThat(invalidOnMoreThan4InMiddleNumbers.matches(RegExp.PHONE)).isFalse();
    assertThat(invalidOnNot4InLastNumbers1.matches(RegExp.PHONE)).isFalse();
    assertThat(invalidOnNot4InLastNumbers2.matches(RegExp.PHONE)).isFalse();
  }

}
