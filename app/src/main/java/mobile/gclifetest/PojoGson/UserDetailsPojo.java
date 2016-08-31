package mobile.gclifetest.pojoGson;

import java.util.List;

public class UserDetailsPojo {

	List<FlatDetailsPojo> gclife_registration_flatdetails;
	String username;
	String email;
	String mobile;
	String created_at;
	String otp;
	String active;
	String otpflag;
	String gender;
	String emergency_contct_no;
	String occupation;
	String dob;

	public String getProfile_url() {
		return profile_url;
	}

	public void setProfile_url(String profile_url) {
		this.profile_url = profile_url;
	}

	public boolean isPrivacy() {
		return privacy;
	}

	String profile_url;
	boolean privacy;

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmergency_contct_no() {
		return emergency_contct_no;
	}

	public void setEmergency_contct_no(String emergency_contct_no) {
		this.emergency_contct_no = emergency_contct_no;
	}

	public void setPrivacy(Boolean privacy) {
		this.privacy = privacy;
	}

	public String getGenderName() {
		return gender;
	}

	public void setGenderName(String genderName) {
		this.gender = genderName;
	}

	public String getEmeNum() {
		return emergency_contct_no;
	}

	public void setEmeNum(String emeNum) {
		this.emergency_contct_no = emeNum;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public boolean getPrivacy() {
		return privacy;
	}

	public void setPrivacy(boolean privacy) {
		this.privacy = privacy;
	}

	int id;

	public UserDetailsPojo(int uId, String uname, String emai, String mobileNu,
			String createdAt, String otp, List<FlatDetailsPojo> details,
			String activ, String otpFlg) {
		this.username = uname;
		this.email = emai;
		this.mobile = mobileNu;
		this.created_at = createdAt;
		this.otp = otp;
		this.id = uId;
		this.active = activ;
		this.otpflag = otpFlg;
		this.gclife_registration_flatdetails = details;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getOtpflag() {
		return otpflag;
	}

	public void setOtpflag(String otpflag) {
		this.otpflag = otpflag;
	}

	public List<FlatDetailsPojo> getGclife_registration_flatdetails() {
		return gclife_registration_flatdetails;
	}

	public void setGclife_registration_flatdetails(
			List<FlatDetailsPojo> gclife_registration_flatdetails) {
		this.gclife_registration_flatdetails = gclife_registration_flatdetails;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
