package mobile.gclifetest.PojoGson;

public class FlatDetailsPojo {
	String membertypeid, avenue_name, societyid, buildingid, flat_type,
			ownertypeid, member_type, flat_number, tenurestart, tenureend,
			 updated_at, relationshipid, created_at,status;
	int id, user_id,gclife_registration_id;
	public FlatDetailsPojo(int id, int userId, String avenueName,
			String societyName, String buildingNum, String flatType,
			String ownerType, String memberType, String flatNum,
			String relationShipDateStr, String liscenseDateStr,
			int gclifeRegId, String updatedAt, String relaId,
			String createdAt, String memId,String status) {
		this.id = id;
		this.user_id = userId;
		this.avenue_name = avenueName;
		this.societyid = societyName;
		this.buildingid = buildingNum;
		this.flat_type = flatType;
		this.ownertypeid = ownerType;
		this.member_type = memberType;
		this.flat_number = flatNum;
		this.tenurestart = relationShipDateStr;
		this.tenureend = liscenseDateStr;
		this.gclife_registration_id = gclifeRegId;
		this.updated_at = updatedAt;
		this.relationshipid = relaId;
		this.created_at = createdAt;
		this.membertypeid = memId;
		this.status=status;
	}

	public String getMembertypeid() {
		return membertypeid;
	}

	public void setMembertypeid(String membertypeid) {
		this.membertypeid = membertypeid;
	}

	public int getGclife_registration_id() {
		return gclife_registration_id;
	}

	public void setGclife_registration_id(int gclife_registration_id) {
		this.gclife_registration_id = gclife_registration_id;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getRelationshipid() {
		return relationshipid;
	}

	public void setRelationshipid(String relationshipid) {
		this.relationshipid = relationshipid;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getAvenue_name() {
		return avenue_name;
	}

	public void setAvenue_name(String avenue_name) {
		this.avenue_name = avenue_name;
	}

	public String getSocietyid() {
		return societyid;
	}

	public void setSocietyid(String societyid) {
		this.societyid = societyid;
	}

	public String getBuildingid() {
		return buildingid;
	}

	public void setBuildingid(String buildingid) {
		this.buildingid = buildingid;
	}

	public String getFlat_type() {
		return flat_type;
	}

	public void setFlat_type(String flat_type) {
		this.flat_type = flat_type;
	}

	public String getOwnertypeid() {
		return ownertypeid;
	}

	public void setOwnertypeid(String ownertypeid) {
		this.ownertypeid = ownertypeid;
	}

	public String getMember_type() {
		return member_type;
	}

	public void setMember_type(String member_type) {
		this.member_type = member_type;
	}

	public String getFlat_number() {
		return flat_number;
	}

	public void setFlat_number(String flat_number) {
		this.flat_number = flat_number;
	}

	public String getTenurestart() {
		return tenurestart;
	}

	public void setTenurestart(String tenurestart) {
		this.tenurestart = tenurestart;
	}

	public String getTenureend() {
		return tenureend;
	}

	public void setTenureend(String tenureend) {
		this.tenureend = tenureend;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}