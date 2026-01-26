package dk.tommer.workday.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void defaultConstructor_createsUserWithDefaultValues() {
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.getRole()).isNull();
        assertThat(user.getWorkHours()).isEqualTo(0.0);
        assertThat(user.getProfilePhotoPath()).isNull();
    }

    @Test
    void setName_validName_setsName() {
        user.setName("Test User");
        assertThat(user.getName()).isEqualTo("Test User");
    }

    @Test
    void setEmail_validEmail_setsEmail() {
        user.setEmail("test@example.com");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void setPassword_validPassword_setsPassword() {
        user.setPassword("password123");
        assertThat(user.getPassword()).isEqualTo("password123");
    }

    @Test
    void setRole_validRole_setsRole() {
        user.setRole(Role.SVEND);
        assertThat(user.getRole()).isEqualTo(Role.SVEND);
    }

    @Test
    void setWorkHours_validHours_setsWorkHours() {
        user.setWorkHours(37.5);
        assertThat(user.getWorkHours()).isEqualTo(37.5);
    }

    @Test
    void setProfilePhotoPath_validPath_setsPath() {
        user.setProfilePhotoPath("profile.jpg");
        assertThat(user.getProfilePhotoPath()).isEqualTo("profile.jpg");
    }

    @Test
    void setId_validId_setsId() {
        user.setId(1L);
        assertThat(user.getId()).isEqualTo(1L);
    }

    @Test
    void equals_sameObject_returnsTrue() {
        User user2 = user;
        assertThat(user.equals(user2)).isTrue();
    }

    @Test
    void equals_nullObject_returnsFalse() {
        assertThat(user.equals(null)).isFalse();
    }

    @Test
    void equals_differentClass_returnsFalse() {
        assertThat(user.equals("string")).isFalse();
    }

    @Test
    void equals_differentObjects_returnsFalse() {
        User user2 = new User();
        assertThat(user.equals(user2)).isFalse();
    }

    @Test
    void hashCode_differentObjects_returnsDifferentHashCode() {
        User user2 = new User();
        assertThat(user.hashCode()).isNotEqualTo(user2.hashCode());
    }

    @Test
    void toString_returnsDefaultStringRepresentation() {
        String result = user.toString();
        
        assertThat(result).contains("User@");
        assertThat(result).contains("dk.tommer.workday.entity.User");
    }

    @Test
    void constructor_withParameters_setsAllFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.SVEND);
        user.setWorkHours(37.5);
        user.setProfilePhotoPath("profile.jpg");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        assertThat(user.getRole()).isEqualTo(Role.SVEND);
        assertThat(user.getWorkHours()).isEqualTo(37.5);
        assertThat(user.getProfilePhotoPath()).isEqualTo("profile.jpg");
    }

    @Test
    void setWorkHours_negativeHours_setsNegativeHours() {
        user.setWorkHours(-5.0);
        assertThat(user.getWorkHours()).isEqualTo(-5.0);
    }

    @Test
    void setWorkHours_zeroHours_setsZeroHours() {
        user.setWorkHours(0.0);
        assertThat(user.getWorkHours()).isEqualTo(0.0);
    }

    @Test
    void setEmail_emptyEmail_setsEmptyEmail() {
        user.setEmail("");
        assertThat(user.getEmail()).isEqualTo("");
    }

    @Test
    void setName_emptyName_setsEmptyName() {
        user.setName("");
        assertThat(user.getName()).isEqualTo("");
    }

    @Test
    void setPassword_emptyPassword_setsEmptyPassword() {
        user.setPassword("");
        assertThat(user.getPassword()).isEqualTo("");
    }

    @Test
    void setProfilePhotoPath_emptyPath_setsEmptyPath() {
        user.setProfilePhotoPath("");
        assertThat(user.getProfilePhotoPath()).isEqualTo("");
    }

    @Test
    void setProfilePhotoPath_nullPath_setsNullPath() {
        user.setProfilePhotoPath(null);
        assertThat(user.getProfilePhotoPath()).isNull();
    }
}
